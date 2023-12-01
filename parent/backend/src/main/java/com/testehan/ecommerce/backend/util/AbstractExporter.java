package com.testehan.ecommerce.backend.util;

import jakarta.servlet.http.HttpServletResponse;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractExporter {

    public void setupResponseHeader(HttpServletResponse response, String entity, String extension, String contentType) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormat.format(new Date());
        String filename = new StringBuilder(entity).append("_").append(timestamp).append(".").append(extension).toString();

        response.setContentType(contentType);
        response.setHeader("Content-Disposition","attachment; filename="+filename);

    }
}
