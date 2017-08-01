/*
Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Amazon Software License (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

    http://aws.amazon.com/asl/

or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the specific language governing permissions and limitations under the License.
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
