package com.test.crypto.controller;

import com.test.crypto.common.exceptions.NotFoundException;
import com.test.crypto.common.exceptions.handler.RestErrorHandler;
import com.test.crypto.commons.model.CryptoValuesDto;
import com.test.crypto.domain.enums.SortingOrder;
import com.test.crypto.service.CryptoService;
import com.test.crypto.service.mapper.CryptoMapper;
import com.test.crypto.service.mapper.CryptoMapperImpl;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.List;

import static com.test.crypto.helper.TestHelper.getCryptoValues;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoControllerTest {

    @InjectMocks
    private CryptoController cryptoController;

    @Mock
    private CryptoService cryptoService;

    @Spy
    private CryptoMapper cryptoMapper = new CryptoMapperImpl();

    @Captor
    private ArgumentCaptor<LocalDate> localDateArgumentCaptor;

    @Captor
    private ArgumentCaptor<SortingOrder> sortingOrderArgumentCaptor;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;


    private MockMvcRequestSpecification givenController() {
        return given().standaloneSetup(cryptoController, new RestErrorHandler());
    }

    @Test
    void getCryptoListOrderedByNormalization_Return200AndAListOfCrypto_WithDefaultValues() {
        var cryptoList = List.of("XRP", "LTC");

        when(cryptoService.getCryptoListOrderedByNormalization(any(), any(), any())).thenReturn(cryptoList);

        var cryptoListResponse = givenController()
                .contentType(ContentType.JSON)
                .when()
                .get("/crypto/normalize")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(List.class);

        assertEquals(cryptoList, cryptoListResponse);

        verify(cryptoService).getCryptoListOrderedByNormalization(localDateArgumentCaptor.capture(), localDateArgumentCaptor.capture(), sortingOrderArgumentCaptor.capture());

        var dateArgumentCaptorValues = localDateArgumentCaptor.getAllValues();
        dateArgumentCaptorValues.forEach(Assertions::assertNotNull);

        var sortingOrderArgumentCaptorValue = sortingOrderArgumentCaptor.getValue();
        assertEquals(SortingOrder.DESC, sortingOrderArgumentCaptorValue);
    }

    @Test
    void getCryptoListOrderedByNormalization_Return200AndAListOfCrypto_WithGivenValues() {
        var cryptoList = List.of("XRP", "LTC");
        var startDate = LocalDate.of(2021, 1, 1);
        var endDate = LocalDate.of(2022, 2, 2);

        when(cryptoService.getCryptoListOrderedByNormalization(any(), any(), any())).thenReturn(cryptoList);

        var cryptoListResponse = givenController()
                .contentType(ContentType.JSON)
                .when()
                .get(String.format("/crypto/normalize?start=%s&end=%s&order=ASC", startDate, endDate))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(List.class);

        assertEquals(cryptoList, cryptoListResponse);

        verify(cryptoService).getCryptoListOrderedByNormalization(localDateArgumentCaptor.capture(), localDateArgumentCaptor.capture(), sortingOrderArgumentCaptor.capture());

        var dateArgumentCaptorValues = localDateArgumentCaptor.getAllValues();
        dateArgumentCaptorValues.forEach(Assertions::assertNotNull);
        assertTrue(dateArgumentCaptorValues.containsAll(List.of(startDate, endDate)));

        var sortingOrderArgumentCaptorValue = sortingOrderArgumentCaptor.getValue();
        assertEquals(SortingOrder.ASC, sortingOrderArgumentCaptorValue);
    }


    @Test
    void getCryptoListOrderedByNormalization_Return400_WhenParamsAreWrong() {
        var cryptoListResponse = givenController()
                .contentType(ContentType.JSON)
                .when()
                .get("/crypto/normalize?start=no&end=bad&order=worst")
                .statusCode();

        assertEquals(HttpStatus.BAD_REQUEST.value(), cryptoListResponse);
    }

    @Test
    void getHighestNormalizedCryptoByDate_Return200_WhenDateIsValid() {
        var cryptoSymbol = "LTE";
        var date = LocalDate.of(2021, 1, 1);

        when(cryptoService.getHighestNormalizedCryptoByDate(any(LocalDate.class))).thenReturn(cryptoSymbol);

        var cryptoResponse = givenController()
                .contentType(ContentType.JSON)
                .when()
                .get(String.format("/crypto/normalize/highest?date=%s", date))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().asString();

        assertEquals(cryptoSymbol, cryptoResponse);

        verify(cryptoService).getHighestNormalizedCryptoByDate(localDateArgumentCaptor.capture());

        LocalDate dateArgumentCaptorValue = localDateArgumentCaptor.getValue();
        assertNotNull(dateArgumentCaptorValue);
        assertEquals(date, dateArgumentCaptorValue);
    }

    @Test
    void getHighestNormalizedCryptoByDate_Return400_WhenDateIsInvalid() {
        var cryptoResponse = givenController()
                .contentType(ContentType.JSON)
                .when()
                .get("/crypto/normalize/highest?date=badTiming")
                .statusCode();

        assertEquals(HttpStatus.BAD_REQUEST.value(), cryptoResponse);
    }

    @Test
    void getHighestNormalizedCryptoByDate_Return400_WhenDateIsMissing() {
        var cryptoResponse = givenController()
                .contentType(ContentType.JSON)
                .when()
                .get("/crypto/normalize/highest")
                .statusCode();

        assertEquals(HttpStatus.BAD_REQUEST.value(), cryptoResponse);
    }

    @Test
    void getHighestNormalizedCryptoByDate_Return400_WhenCryptoNotFound() {
        var date = LocalDate.of(2021, 1, 1);

        when(cryptoService.getHighestNormalizedCryptoByDate(any(LocalDate.class))).thenThrow(new NotFoundException(""));

        var cryptoResponse = givenController()
                .contentType(ContentType.JSON)
                .when()
                .get(String.format("/crypto/normalize/highest?date=%s", date))
                .statusCode();

        assertEquals(HttpStatus.NOT_FOUND.value(), cryptoResponse);
    }

    @Test
    void getCryptoValuesBySymbol_Return200_AndTheValues() {
        var cryptoValues = getCryptoValues();

        when(cryptoService.getCryptoValuesBySymbol(anyString())).thenReturn(cryptoValues);

        var cryptoValuesResponse = givenController()
                .contentType(ContentType.JSON)
                .when()
                .get(String.format("/crypto/info/%s", cryptoValues.getSymbol()))
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .extract().as(CryptoValuesDto.class);

        assertEquals(cryptoMapper.toDto(cryptoValues), cryptoValuesResponse);

        verify(cryptoService).getCryptoValuesBySymbol(stringArgumentCaptor.capture());

        String stringArgumentCaptorValue = stringArgumentCaptor.getValue();
        assertEquals(cryptoValues.getSymbol(), stringArgumentCaptorValue);
    }

    @Test
    void getCryptoValuesBySymbol_Return400_WhenCryptoDoesNotExist() {
        var cryptoValues = getCryptoValues();

        when(cryptoService.getCryptoValuesBySymbol(anyString())).thenThrow(new NotFoundException(""));

        var cryptoValuesResponse = givenController()
                .contentType(ContentType.JSON)
                .when()
                .get(String.format("/crypto/info/%s", cryptoValues.getSymbol()))
                .statusCode();

        assertEquals(HttpStatus.NOT_FOUND.value(), cryptoValuesResponse);
    }
}