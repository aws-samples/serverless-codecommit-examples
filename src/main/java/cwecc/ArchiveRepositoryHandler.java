/*
Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Amazon Software License (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

    http://aws.amazon.com/asl/

or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

package cwecc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import org.eclipse.jgit.api.ArchiveCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.archive.ZipFormat;
import org.eclipse.jgit.lib.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

public class ArchiveRepositoryHandler
        implements RequestHandler<CodeCommitEvent, HandlerResponse> {

    private static Logger logger = LoggerFactory.getLogger(ArchiveRepositoryHandler.class);

    private final String ZIP_FORMAT = "zip";
    private final String targetS3Bucket;
    private final AmazonS3 s3Client;

    public ArchiveRepositoryHandler() {
        targetS3Bucket = System.getenv("TARGET_S3_BUCKET");
        s3Client = AmazonS3ClientBuilder.defaultClient();
        ArchiveCommand.registerFormat(ZIP_FORMAT, new ZipFormat());
    }

    @Override
    public HandlerResponse handleRequest(CodeCommitEvent event, Context context) {
        String sourceName = event.getDetail().getRepositoryName();
        String sourceRegion = event.getRegion();
        String commitId = event.getDetail().getCommitId();

        try {
            // clone source repository
            CodeCommitMetadata source = new CodeCommitMetadata(sourceName, sourceRegion);
            String sourceUrl = source.getCloneUrlHttp();
            Git git = new CloneCommandBuilder().buildCloneCommand(sourceUrl).call();

            // create and upload archive for commitId
            File file = Files.createTempFile(null, null).toFile();
            try (OutputStream out = new FileOutputStream(file)) {
                ObjectId objectId = git.getRepository().resolve(commitId);
                git.archive().setTree(objectId)
                             .setFormat(ZIP_FORMAT)
                             .setOutputStream(out)
                             .call();

                String key = sourceName + "." + commitId + "." + ZIP_FORMAT;
                s3Client.putObject(targetS3Bucket, key, file);
            }

            return HandlerResponse.success();
        } catch (IllegalStateException | GitAPIException | IOException e) {
            logger.error(e.getMessage());
            return HandlerResponse.failure(e.getMessage());
        }
    }
}
