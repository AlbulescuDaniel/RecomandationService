package com.test.crypto.utils.csv.record;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class CryptoCsvRecord {
    @CsvBindByName
    private String timestamp;
    @CsvBindByName
    private String symbol;
    @CsvBindByName
    private String price;
}
