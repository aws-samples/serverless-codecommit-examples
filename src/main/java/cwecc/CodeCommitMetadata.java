/*
// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
*/

package cwecc;

import com.amazonaws.services.codecommit.AWSCodeCommit;
import com.amazonaws.services.codecommit.AWSCodeCommitClientBuilder;
import com.amazonaws.services.codecommit.model.GetRepositoryRequest;
import com.amazonaws.services.codecommit.model.GetRepositoryResult;
import com.amazonaws.services.codecommit.model.RepositoryMetadata;

public class CodeCommitMetadata {

    private RepositoryMetadata repositoryMetadata;

    public CodeCommitMetadata(String repoName, String repoRegion) {
        AWSCodeCommitClientBuilder builder = AWSCodeCommitClientBuilder.standard();
        AWSCodeCommit client = builder.withRegion(repoRegion).build();

        GetRepositoryRequest request = new GetRepositoryRequest();
        request.withRepositoryName(repoName);

        GetRepositoryResult result = client.getRepository(request);
        repositoryMetadata = result.getRepositoryMetadata();
    }

    public String getCloneUrlHttp() {
        return repositoryMetadata.getCloneUrlHttp();
    }
}
