package com.testehan.ecommerce.backend.util;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class AmazonS3UtilTests {

    @Test
    public void listS3Folder(){
        var folderName = "test-upload";
        List<String> listKeys = AmazonS3Util.listFolder(folderName);
        listKeys.forEach(System.out::println);
    }

    @Test
    public void uploadS3File() throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("images/default-user.png").getFile());

        var folderName = "test-upload";
        var fileName = "default-user.png";

        InputStream inputStream = new FileInputStream(file);

        AmazonS3Util.uploadFile(folderName, fileName, inputStream);
    }
}
