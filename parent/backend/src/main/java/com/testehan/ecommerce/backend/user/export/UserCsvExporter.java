package com.testehan.ecommerce.backend.user.export;

import com.testehan.ecommerce.backend.util.AbstractExporter;
import com.testehan.ecommerce.common.entity.User;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class UserCsvExporter extends AbstractExporter {

    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {

        setupResponseHeader(response,"users" , "csv", "text/csv");

        ICsvBeanWriter writer = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"User ID", "Email","First Name", "Last Name","Roles","Enabled"};

        String[] fieldMapping = {"id","email","firstName","lastName","roles","enabled"};

        writer.writeHeader(csvHeader);
        for (User user : listUsers){
            writer.write(user,fieldMapping);
        }

        writer.close();
    }
}
