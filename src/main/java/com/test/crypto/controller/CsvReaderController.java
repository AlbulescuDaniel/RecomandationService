package com.test.crypto.controller;

import com.test.crypto.common.exceptions.BadRequestException;
import com.test.crypto.common.exceptions.InternalException;
import com.test.crypto.commons.ports.application.UploadApi;
import com.test.crypto.service.CryptoService;
import com.test.crypto.service.mapper.CryptoMapper;
import com.test.crypto.utils.csv.CsvParser;
import com.test.crypto.utils.csv.record.CryptoCsvRecord;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@RestController
@RequiredArgsConstructor
public class CsvReaderController implements UploadApi {

    private final CsvParser csvParser;
    private final CryptoMapper cryptoMapper;
    private final CryptoService cryptoService;

    @Override
    public ResponseEntity<Void> uploadCsv(@RequestParam("file") MultipartFile file) {
        if (!StringUtils.equals("csv", FilenameUtils.getExtension(file.getOriginalFilename()))) {
            throw new BadRequestException("The file is not a CSV.");
        }

        try {
            BufferedReader fileReader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            var csvRecords = csvParser.parseCSVToBean(fileReader, CryptoCsvRecord.class);
            var cryptoList = cryptoMapper.fromRecordList(csvRecords);
            cryptoService.saveAll(cryptoList);
        } catch (IOException e) {
            throw new InternalException("CSV could not be uploaded.");
        } catch (RuntimeException e) {
            throw new InternalException("CSV could not be parsed.");
        }

        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
