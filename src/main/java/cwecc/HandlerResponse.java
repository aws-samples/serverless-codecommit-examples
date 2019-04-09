/*
// Copyright 2017 Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: MIT-0
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
