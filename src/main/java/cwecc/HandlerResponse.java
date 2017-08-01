/*
Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.

Licensed under the Amazon Software License (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

    http://aws.amazon.com/asl/

or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the specific language governing permissions and limitations under the License.
*/

package cwecc;

public class HandlerResponse {

    private String status;
    private String message;

    private static final String SUCCESS = "success";
    private static final String FAILURE = "failure";

    private HandlerResponse(String status) {
        this.status = status;
    }

    private HandlerResponse(String status, String message) {
        this(status);
        this.message = message;
    }

    public static HandlerResponse success() {
        return new HandlerResponse(SUCCESS);
    }

    public static HandlerResponse success(String message) {
        return new HandlerResponse(SUCCESS, message);
    }

    public static HandlerResponse failure() {
        return new HandlerResponse(FAILURE);
    }

    public static HandlerResponse failure(String message) {
        return new HandlerResponse(FAILURE, message);
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
