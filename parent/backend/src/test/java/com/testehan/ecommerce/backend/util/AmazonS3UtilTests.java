package com.testehan.ecommerce.backend.util;

import org.junit.jupiter.api.Test;

import java.util.List;

public class AmazonS3UtilTests {

    @Test
    public void listS3Folder(){
        var folderName = "test-upload";
        List<String> listKeys = AmazonS3Util.listFolder(folderName);
        listKeys.forEach(System.out::println);
    }
}
