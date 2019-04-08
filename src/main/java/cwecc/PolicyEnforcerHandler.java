/*
// Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
*/

package cwecc;

import java.io.IOException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PolicyEnforcerHandler implements RequestHandler<CodeCommitEvent, HandlerResponse> {

    private static Logger logger = LoggerFactory.getLogger(ArchiveRepositoryHandler.class);

    private final String mainBranch;
    private final String snsTopicArn;
    private final Pattern pattern;
    private final AmazonSNS snsClient;

    public PolicyEnforcerHandler() {
        mainBranch = System.getenv("MAIN_BRANCH_NAME");
        snsTopicArn = System.getenv("SNS_TOPIC_ARN");

        String messageRegex = System.getenv("MESSAGE_REGEX");
        pattern = Pattern.compile(messageRegex);

        String snsRegion = snsTopicArn.split(":")[3];
        snsClient = AmazonSNSClientBuilder.standard().withRegion(snsRegion).build();
    }

    @Override
    public HandlerResponse handleRequest(CodeCommitEvent event, Context context) {
        String sourceName = event.getDetail().getRepositoryName();
        String sourceRegion = event.getRegion();
        String commitId = event.getDetail().getCommitId();
        String oldCommitId = event.getDetail().getOldCommitId();

        try {
            logger.debug("CodeCommitEvent: " + new ObjectMapper().writeValueAsString(event));

            // clone source repository
            CodeCommitMetadata source = new CodeCommitMetadata(sourceName, sourceRegion);
            String sourceUrl = source.getCloneUrlHttp();
            Git git = new CloneCommandBuilder().buildCloneCommand(sourceUrl).call();

            // use the OldCommitId, or default to the main branch
            String toGitReference = Optional.ofNullable(oldCommitId).orElse(mainBranch);
            Repository repository = git.getRepository();
            ObjectId to = repository.resolve(toGitReference);
            ObjectId from = repository.resolve(commitId);

            // create a RevWalk and set the range of commits
            try (RevWalk walk = new RevWalk(repository)) {
                walk.markStart(walk.parseCommit(from));
                walk.markUninteresting(walk.parseCommit(to));

                // iterate the list of commits and validate each message
                for (RevCommit commit : walk) {
                    Matcher matcher = pattern.matcher(commit.getShortMessage());

                    // publish a message to the topic if the message does not match
                    if (!matcher.find()) {
                        String message = buildMessage(commit);
                        logger.info(message);
                        snsClient.publish(snsTopicArn, message);
                    }
                }

                walk.dispose();
            }

            return HandlerResponse.success();
        } catch (IllegalStateException | GitAPIException | IOException e) {
            logger.error(e.getMessage());
            return HandlerResponse.failure(e.getMessage());
        }
    }

    private String buildMessage(RevCommit commit) {
        String format = "(%s) commit message does not comply with policy (%s): %s";

        return String.format(format, commit.getId(), pattern.pattern(),
                commit.getShortMessage());
    }
}