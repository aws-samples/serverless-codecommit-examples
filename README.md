# AWS CodeCommit Serverless Samples

The samples in this repository demonstrate several uses of AWS Lambda to process Amazon CloudWatch Events in response to changes to a AWS CodeCommit Git repository.

## Requirements

### Java 8 and Gradle

The examples are written in Java 8 using the Gradle build tool.  Java 8 must be installed to build the examples, however the Gradle Wrapper is bundled in the project and does not need to be installed.  Please see the link below for more detail to install Java 8 and information on Gradle:

* [Java 8 Installation](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html)
* [Gradle]()
* [Gradle Wrapper](https://docs.gradle.org/3.3/userguide/gradle_wrapper.html)


### AWS Command Line Interface (CLI)

The examples are configured, packaged and deployed using the AWS [Serverless Application Model (SAM)](https://github.com/awslabs/serverless-application-model).  To use SAM, you must install and configure the AWS Command Line Interface (CLI).  Please see the link below for more detail to install and configure the CLI:

* [Installing the AWS Command Line Interface](http://docs.aws.amazon.com/cli/latest/userguide/installing.html)
* [Configuring the AWS CLI](http://docs.aws.amazon.com/cli/latest/userguide/cli-chap-getting-started.html)

### Amazon S3 Bucket

The AWS [Serverless Application Model (SAM)](https://github.com/awslabs/serverless-application-model) will be used to package the project in a zip archive and upload to S3 for deployment.  Before using SAM, you must create an Amazon S3 bucket in the account and region that you will use for uploading artifacts, and configure it with permissions for access using the AWS credentials configured in the prior step.  Please see the link below for more detail to create an Amazon S3 bucket:

* [Creating and Configuring an S3 Bucket](http://docs.aws.amazon.com/AmazonS3/latest/user-guide/create-configure-bucket.html)

### AWS CodeCommit Repository

Each of these examples use an existing AWS CodeCommit repository that you must create and populate prior to deploying the examples.  Please see the link below for more detail to create an AWS CodeCommit repository:

* [Create an AWS CodeCommit Repository](http://docs.aws.amazon.com/codecommit/latest/userguide/how-to-create-repository.html)

### Clone GitHub Repository

The examples below can be executed from a local workstation after cloning this Git repository locally.  Please see the link below for more detail to clone the repository:

* [Cloning a Repository](https://help.github.com/articles/cloning-a-repository/)

# Examples

Each of the examples below are packaged and deployed using the same jar, which contains all the project dependencies.  To build the jar, run the following command from the directory used to clone this repository locally:

Windows:

```
gradlew clean createDistribution
```

Unix:

```
 ./gradlew clean createDistribution
```

This will build the project classes and copy the project dependency jars into the `build/distributions` directory.  Each example has a corresponding SAM YAML template that refers to the `build/distributions` directory as the source of local code to be packaged and deployed.

JGit uses a default log level that is very chatty for these examples.  To reduce the noise, a [Logback](https://docs.gradle.org/3.3/userguide/gradle_wrapper.html) configuration file is included (`src/main/resources/logback.xml`) that specifies a default WARN log level and a DEBUG log level for classes in this project's package.

## Replicate an AWS CodeCommit Repository

This example configures an Amazon CloudWatch Event to trigger an AWS Lambda function that will replicate the commits from a source to target Git repository when the repository has been updated.

Once the project has been built using the `gradlew` command, it can be packaged and deployed using the AWS CLI and SAM.  To package the project, execute the following command in the project directory, replacing `S3_BUCKET` with the Amazon S3 bucket that you created in the requirements setup.  For more information on the CloudFormation Package command, please see this [link](http://docs.aws.amazon.com/cli/latest/reference/cloudformation/package.html).

```
aws cloudformation package --template-file replicate-template.yaml --s3-bucket S3_BUCKET -artifacts --output-template-file output-replicate-template.yaml
```

Once the package command has completed, the files in the `build/distributions` directory will have been packaged into a zip archive, uploaded to the specified Amazon S3 bucket, and an output AWS CloudFormation template will be created with the S3 location of the archive.  

*Please note, this example is intended to work with an existing AWS CodeCommit target repository that has been previously populated with a recent version of the source CodeCommit repository.*

The output AWS CloudFormation template is ready to be deployed using the following command.  Please replace `STACK_NAME` with your custom stack name, `SOURCE_REPO_NAME` with the name of the source AWS CodeCommit repository, `TARGET_REPO_NAME` with the name of the target AWS CodeCommit repository, and `TARGET_REPO_REGION` with the region of the target AWS CodeCommit repository.

```
aws cloudformation deploy --template-file output-replicate-template.yaml --stack-name STACK_NAME --capabilities CAPABILITY_IAM --parameter-overrides SourceRepositoryName=SOURCE_REPO_NAME TargetRepositoryName=TARGET_REPO_NAME TargetRepositoryRegion=TARGET_REPO_REGION
```

After the deployment is complete, push a new commit to the source AWS CodeCommit repository and verify that the new commits have been replicated to the target repository.

## Enforce Git Commit Message Policy

This example configures an Amazon CloudWatch Event to trigger an AWS Lambda function that will iterate over new commits to an existing or new branch, and check each to see if the commit messages matches a regular expression.  If the commit message does not match the regular expression, it will publish a notification to an email address using Amazon SNS.

Once the project has been built using the `gradlew` command, it can be packaged and deployed using the AWS CLI and SAM.  To package the project, execute the following command in the project directory, replacing `S3_BUCKET` with the Amazon S3 bucket that you created in the requirements setup.  For more information on the CloudFormation Package command, please see this [link](http://docs.aws.amazon.com/cli/latest/reference/cloudformation/package.html).

```
aws cloudformation package --template-file enforcer-template.yaml --s3-bucket S3_BUCKET -artifacts --output-template-file output-enforcer-template.yaml
```

Once the package command has completed, the files in the `build/distributions` directory will have been packaged into a zip archive, uploaded to the specified Amazon S3 bucket, and an output AWS CloudFormation template will be created with the S3 location of the archive.  

The output AWS CloudFormation template is ready to be deployed using the following command.  Please replace `STACK_NAME` with your custom stack name, `SOURCE_REPO_NAME` with the name of the source AWS CodeCommit repository, `EMAIL_ADDRESS` to which you would like to receive notification, `REGULAR_EXPRESSION` with the message policy regular expression used to validate commit messages, and `MAIN_BRANCH_NAME` with the branch name used as your main branch for commits (typically "master" or "mainline").

```
aws cloudformation deploy --template-file output-enforcer-template.yaml --stack-name STACK_NAME --capabilities CAPABILITY_IAM --parameter-overrides SourceRepositoryName=SOURCE_REPO_NAME NotificationEmailAddress=EMAIL_ADDRESS MessageRegex=REGULAR_EXPRESSION MainBranchName=MAIN_BRANCH_NAME
```

After the deployment is complete, you will receive a subscription confirmation email to the address that you specified.  Confirm subscription and then push a new commit to the source AWS CodeCommit repository with a commit message that will not match your regular expression.  Confirm that you receive the email with the message policy violation.

## Backup Git Archive to Amazon S3

This example configures an Amazon CloudWatch Event to trigger an AWS Lambda function that will create a zip archive of the repository files using the commitId of the CloudWatch Event and upload it a specified S3 bucket.

Once the project has been built using the `gradlew` command, it can be packaged and deployed using the AWS CLI and SAM.  To package the project, execute the following command in the project directory, replacing `S3_BUCKET` with the Amazon S3 bucket that you created in the requirements setup.  For more information on the CloudFormation Package command, please see this [link](http://docs.aws.amazon.com/cli/latest/reference/cloudformation/package.html).

```
aws cloudformation package --template-file archive-template.yaml --s3-bucket S3_BUCKET -artifacts --output-template-file output-archive-template.yaml
```

Once the package command has completed, the files in the `build/distributions` directory will have been packaged into a zip archive, uploaded to the specified Amazon S3 bucket, and an output AWS CloudFormation template will be created with the S3 location of the archive.  

The output AWS CloudFormation template is ready to be deployed using the following command.  Please replace `STACK_NAME` with your custom stack name, `SOURCE_REPO_NAME` with the name of the source AWS CodeCommit repository, and `TARGET_S3_BUCKET` with the name of the Amazon S3 bucket used to store the backup archive.

```
aws cloudformation deploy --template-file output-archive-template.yaml --stack-name STACK_NAME --capabilities CAPABILITY_IAM --parameter-overrides SourceRepositoryName=SOURCE_REPO_NAME TargetS3Bucket=TARGET_S3_BUCKET
```

After the deployment is complete, push a new commit to the source AWS CodeCommit repository and verify that a new zip archive has been created in the Amazon S3 bucket with the source repository name and commit id used as the key prefix.

## Cleanup

Each of the examples uses AWS CloudFormation to create the AWS resources in a Stack.  To cleanup each example, open the AWS Console, navigate to the AWS CloudFormation service and delete the corresponding Stack.  For further detail, please see the following link:

* [Deleting a Stack on the AWS CloudFormation Console](http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-console-delete-stack.html)