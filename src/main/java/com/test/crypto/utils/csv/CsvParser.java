package com.test.crypto.utils.csv;

import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.stereotype.Component;

import java.io.Reader;
import java.util.List;

@Component
public class CsvParser {

    /**
     * Parse the CSV file and map the rows to a provided class object.
     *
     * @param fileReader the file
     * @param clazz Class used for mapping
     * @return A list of mapped rows
     */
    public <T> List<T> parseCSVToBean(Reader fileReader, Class<T> clazz) {
        var csvToBean = new CsvToBeanBuilder<T>(fileReader)
                .withType(clazz)
                .withIgnoreLeadingWhiteSpace(true)
                .build();

        return csvToBean.parse();
    }
}
