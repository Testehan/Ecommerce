package com.testehan.ecommerce.backend.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListObjectsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import static com.testehan.ecommerce.common.constants.AmazonS3Constants.*;

public class AmazonS3Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(AmazonS3Util.class);

    public static S3Client createAmazomS3() {

        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(ACCESS_KEY_ID, SECRET_ACCESS_KEY);

        S3Client client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .build();

        return client;
    }

    public static ListObjectsRequest listAllFilesInAmazonS3(String folderName) {
        ListObjectsRequest listRequest = ListObjectsRequest.builder()
                .bucket(BUCKET_NAME).prefix(folderName).build();

        return listRequest;
    }

    public static List<String> listFolder(String folderName) {

        S3Client client = createAmazomS3();

        ListObjectsRequest listRequest = listAllFilesInAmazonS3(folderName);
        ListObjectsResponse response = client.listObjects(listRequest);
        List<S3Object> contents = response.contents();
        ListIterator<S3Object> listIterator = contents.listIterator();

        List<String> listKeys = new ArrayList<>();
        while (listIterator.hasNext()) {
            S3Object object = listIterator.next();
            listKeys.add(object.key());
        }

        return listKeys;

    }

    public static void uploadFile(String folderName, String fileName, InputStream inputStream) {

        S3Client client = createAmazomS3();
        PutObjectRequest request = putFileInAmazonS3(folderName, fileName);

        try (inputStream) {
            int contentLength = inputStream.available();
            client.putObject(request, RequestBody.fromInputStream(inputStream, contentLength));

        } catch (IOException ex) {
            LOGGER.error("Could not upload file to Amazon S3", ex);
        }
    }

    public static PutObjectRequest putFileInAmazonS3(String folderName, String fileName) {
        PutObjectRequest request = PutObjectRequest.builder().bucket(BUCKET_NAME)
                .key(folderName + "/" + fileName).acl("public-read").build();

        return request;
    }
}
