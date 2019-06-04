# Batch application

This is a batch application, it will listen from a queue to process/convert audio files from sfx, 3gp, flat, etc (extensions) to mp3 and next, save them into a S3 bucket. After the audio has been processed, sends a confirmation email.

## Version 2

* Built on Java 8 using Spring framework.
* Uses MongoDB, a no-relational database.
* Uses AWS S3 to retrieve and store audios.
* Uses the AWS API to send emails through AWS SES.
* Uses AWS SQS to retrive messages from a queue. The web application is responsible to put those messages.
* Uses Jave-core component to process/convert audios.

## Assumptions

This application was created using:

* Java 8
* Maven 3
* Spring Boot
* Ubuntu 18.4 

## Prerequisites

1) Install Java, follow this [link](https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-on-ubuntu-18-04).
2) Install Maven, follow this [link](https://linuxize.com/post/how-to-install-apache-maven-on-ubuntu-18-04/).
3) Install Git, follow this [link](https://www.liquidweb.com/kb/install-git-ubuntu-16-04-lts/)

## Instructions

1) Configure these environment variables:

|Environment variable|Example|Description|
|-|-|-|
|`CONTESTS_DATASOURCE_HOST`|localhost|The database host|
|`CONTESTS_DATASOURCE_PORT`|27017|The database port|
|`CONTESTS_DATASOURCE_DATABASE_NAME`|contests|The database name|
|`CONTESTS_DATASOURCE_USER`|root|The database user|
|`CONTESTS_DATASOURCE_PASSWORD`|12345|The database password|
|`CONTESTS_PATH_ORIGINAL_FILES`|/home/audio/original/|The path where *ORIGINAL* audio files remains|
|`CONTESTS_PATH_CONVERTED_FILES`|/home/audio/converted/|The path where *CONVERTED* audio files will be stored|
|`CONTESTS_MAIL_NOTIFICATION`|javax or AWS-SES|The service used to send emails|
|`CONTESTS_FILE_STORE`|file-system or AWS-S3|The service used to store and retrieve audios|
|`AWS_ACCESS_KEY_ID`|-|The AWS key ID for AWS SES, AWS SQS and AWS S3|
|`AWS_SECRET_ACCESS_KEY`|-|The AWS access key for AWS SES, AWS SQS and AWS S3|
|`AWS_REGION`|-|The AWS region for AWS services|
|`CONTESTS_AWS_BUCKET`|-|The bucket name for AWS S3|
|`CONTESTS_TEMPORAL_DIRECTORY`|/home/temporal/|Path to save temporal audios before to upload those to S3|

2) Go to `batch` folder and execute:

```bash
mvn clean install
java -jar ./target/batch-1.0.jar
```
