/*
Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Amazon Software License (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

    http://aws.amazon.com/asl/

or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the specific language governing permissions and limitations under the License.
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