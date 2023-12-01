package com.testehan.ecommerce.backend.category.export;

import com.testehan.ecommerce.backend.util.AbstractExporter;
import com.testehan.ecommerce.common.entity.Category;
import jakarta.servlet.http.HttpServletResponse;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.util.List;

public class CategoryCsvExporter extends AbstractExporter {

    public void export(List<Category> listCategory, HttpServletResponse response) throws IOException {

        setupResponseHeader(response,"categories" , "csv", "text/csv");

        ICsvBeanWriter writer = new CsvBeanWriter(response.getWriter(), CsvPreference.STANDARD_PREFERENCE);
        String[] csvHeader = {"Category ID", "Category Name"};

        String[] fieldMapping = {"id","name"};

        writer.writeHeader(csvHeader);
        for (Category category : listCategory){
            writer.write(category,fieldMapping);
        }

        writer.close();
    }
}
