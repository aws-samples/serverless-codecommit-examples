/*
// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
*/

package cwecc;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.RefSpec;
import org.springframework.cloud.config.server.support.AwsCodeCommitCredentialProvider;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class ReplicateRepositoryHandler
        implements RequestHandler<CodeCommitEvent, HandlerResponse> {

    private static Logger logger = Logger.getLogger(ReplicateRepositoryHandler.class);

    private final String targetUrl;
    private final AwsCodeCommitCredentialProvider credentialsProvider;

    public ReplicateRepositoryHandler() {
        String targetName = System.getenv("TARGET_REPO_NAME");
        String targetRegion = System.getenv("TARGET_REPO_REGION");

        CodeCommitMetadata target = new CodeCommitMetadata(targetName, targetRegion);
        targetUrl = target.getCloneUrlHttp();
        credentialsProvider = new AwsCodeCommitCredentialProvider();
    }

    @Override
    public HandlerResponse handleRequest(CodeCommitEvent event, Context context) {
        try {
            String sourceName = event.getDetail().getRepositoryName();
            String sourceRegion = event.getRegion();

            // clone source repository
            CodeCommitMetadata source = new CodeCommitMetadata(sourceName, sourceRegion);
            String sourceUrl = source.getCloneUrlHttp();
            Git git = new CloneCommandBuilder().buildCloneCommand(sourceUrl).call();

            // push target repository
            git.push().setCredentialsProvider(credentialsProvider)
                      .setRemote(targetUrl)
                      .setRefSpecs(new RefSpec("+refs/*:refs/*"))
                      .call();

            return HandlerResponse.success();
        } catch (IllegalStateException | GitAPIException | IOException e) {
            logger.error(e.getMessage());
            return HandlerResponse.failure(e.getMessage());
        }
    }
}