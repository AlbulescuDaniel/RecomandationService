package com.test.crypto.controller;

import com.test.crypto.common.exceptions.dto.ApiError;
import com.test.crypto.common.exceptions.handler.RestErrorHandler;
import com.test.crypto.domain.entity.CryptoEntity;
import com.test.crypto.service.CryptoService;
import com.test.crypto.service.mapper.CryptoMapper;
import com.test.crypto.service.mapper.CryptoMapperImpl;
import com.test.crypto.utils.csv.CsvParser;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CsvReaderControllerTest {

    @InjectMocks
    private CsvReaderController csvReaderController;

    @Spy
    private CsvParser csvParser = new CsvParser();

    @Spy
    private CryptoMapper cryptoMapper = new CryptoMapperImpl();

    @Mock
    private CryptoService cryptoService;

    @Captor
    private ArgumentCaptor<List<CryptoEntity>> cryptoListArgumentCaptor;

    private MockMvcRequestSpecification givenController() {
        return given().standaloneSetup(csvReaderController, new RestErrorHandler());
    }

    @Test
    void uploadFileHandler_Return200_WhenTheCsvWasUploaded() {
        File file = new File("src/test/resources/BTC_values.csv");

        var statusCode = givenController()
                .multiPart("file", file)
                .when()
                .post("/upload/csv")
                .statusCode();

        assertEquals(HttpStatus.CREATED.value(), statusCode);

        verify(cryptoService).saveAll(cryptoListArgumentCaptor.capture());

        List<CryptoEntity> cryptoListArgumentCaptorValue = cryptoListArgumentCaptor.getValue();

        assertNotNull(cryptoListArgumentCaptorValue);
        assertEquals(10, cryptoListArgumentCaptorValue.size());
        cryptoListArgumentCaptorValue.forEach(cryptoEntity -> {
            assertNotNull(cryptoEntity.getSymbol());
            assertEquals("BTC", cryptoEntity.getSymbol());
            assertNotNull(cryptoEntity.getTimestamp());
            assertNotNull(cryptoEntity.getPrice());
        });
    }

    @Test
    void uploadFileHandler_Return400_WhenTheFileIsNotCsv() {
        File file = new File("src/test/resources/BTC_values.notCsv");

        var statusCode = givenController()
                .multiPart("file", file)
                .when()
                .post("/upload/csv")
                .statusCode();

        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);

        verify(cryptoService, never()).saveAll(anyList());
    }

    @Test
    void uploadFileHandler_Return400_WhenTheFileParamIsMissing() {
        var statusCode = givenController()
                .multiPart("other", "some/file")
                .when()
                .post("/upload/csv")
                .statusCode();

        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);

        verify(cryptoService, never()).saveAll(anyList());
    }

    @Test
    void uploadFileHandler_Return500_WhenTheCsvIsCorrupted() {
        File file = new File("src/test/resources/BTC_malformed_values.csv");

        var error = givenController()
                .multiPart("file", file)
                .when()
                .post("/upload/csv")
                .then()
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .contentType(ContentType.JSON)
                .extract().as(ApiError.class);

        assertNotNull(error);
        assertEquals("CSV could not be parsed.", error.getMessage());

        verify(cryptoService, never()).saveAll(anyList());
    }
}