/*
Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Amazon Software License (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

    http://aws.amazon.com/asl/

or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

package cwecc;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.eclipse.jgit.api.CloneCommand;
import org.springframework.cloud.config.server.support.AwsCodeCommitCredentialProvider;

public class CloneCommandBuilder {

    private File directory;

    public CloneCommandBuilder() throws IOException {
        directory = Files.createTempDirectory(null).toFile();
    }

    public CloneCommand buildCloneCommand(String sourceUrl) {
        return buildCloneCommand(sourceUrl, new AwsCodeCommitCredentialProvider());
    }

    public CloneCommand buildCloneCommand(String sourceUrl,
            AwsCodeCommitCredentialProvider credentialsProvider) {

        return new CloneCommand().setDirectory(directory)
                .setURI(sourceUrl)
                .setCredentialsProvider(credentialsProvider)
                .setBare(true);
    }
}
