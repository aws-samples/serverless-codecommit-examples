/*
// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
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
