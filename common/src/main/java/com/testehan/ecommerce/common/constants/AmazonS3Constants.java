package com.testehan.ecommerce.common.constants;

public class AmazonS3Constants {
    public static final String S3_BASE_URI;

    static {
        var bucketName = System.getenv("AWS_BUCKET_NAME");
        var region = System.getenv("AWS_REGION");
        var pattern = "https://%s.s3.%s.amazonaws.com";

        S3_BASE_URI = bucketName == null ? "" : String.format(pattern, bucketName, region);

    }


    public static void main(String[] args) {
        System.out.println(S3_BASE_URI);
    }
}
