package com.test.crypto.service;

import com.test.crypto.common.exceptions.NotFoundException;
import com.test.crypto.domain.entity.CryptoValues;
import com.test.crypto.domain.enums.SortingOrder;
import com.test.crypto.repository.CryptoRepository;
import com.test.crypto.repository.CryptoValuesRepository;
import com.test.crypto.utils.CryptoNormalizer;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.LocalDate;

import static com.test.crypto.helper.TestHelper.getCryptoEntityList;
import static com.test.crypto.helper.TestHelper.getCryptoValues;
import static com.test.crypto.helper.TestHelper.getCryptoValuesDynamically;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CryptoServiceTest {

    @InjectMocks
    private CryptoService cryptoService;

    @Mock
    private CryptoRepository cryptoRepository;

    @Mock
    private CryptoValuesRepository cryptoValuesRepository;

    @Spy
    private CryptoNormalizer cryptoNormalizer = new CryptoNormalizer();


    @Test
    void getCryptoListOrderedByNormalization_ReturnListOfAscOrderedCryptos() {
        var cryptoEntityList = getCryptoEntityList();
        var cryptoValues1 = getCryptoValuesDynamically("LTE", 10d, 12d);
        var cryptoValues2 = getCryptoValuesDynamically("DOGE", 11d, 19d);

        when(cryptoRepository.findAllSymbols()).thenReturn(cryptoEntityList);
        when(cryptoValuesRepository.findPriceLimitsBySymbolInInterval(anyString(), any(Instant.class), any(Instant.class)))
                .thenReturn(cryptoValues1)
                .thenReturn(cryptoValues2);

        var cryptoListOrderedByNormalization = cryptoService.getCryptoListOrderedByNormalization(LocalDate.now(), LocalDate.now(), SortingOrder.ASC);

        assertNotNull(cryptoListOrderedByNormalization);
        assertEquals(2, cryptoListOrderedByNormalization.size());

        // Check the ordering
        assertEquals(cryptoValues1.getSymbol(), cryptoListOrderedByNormalization.get(0));
        assertEquals(cryptoValues2.getSymbol(), cryptoListOrderedByNormalization.get(1));

        verify(cryptoRepository).findAllSymbols();
        verify(cryptoValuesRepository, times(2)).findPriceLimitsBySymbolInInterval(anyString(), any(Instant.class), any(Instant.class));
    }

    @Test
    void getCryptoListOrderedByNormalization_ReturnListOfDescOrderedCryptos() {
        var cryptoEntityList = getCryptoEntityList();
        var cryptoValues1 = getCryptoValuesDynamically("LTE", 10d, 12d);
        var cryptoValues2 = getCryptoValuesDynamically("DOGE", 11d, 19d);

        when(cryptoRepository.findAllSymbols()).thenReturn(cryptoEntityList);
        when(cryptoValuesRepository.findPriceLimitsBySymbolInInterval(anyString(), any(Instant.class), any(Instant.class)))
                .thenReturn(cryptoValues1)
                .thenReturn(cryptoValues2);

        var cryptoListOrderedByNormalization = cryptoService.getCryptoListOrderedByNormalization(LocalDate.now(), LocalDate.now(), SortingOrder.DESC);

        assertNotNull(cryptoListOrderedByNormalization);
        assertEquals(2, cryptoListOrderedByNormalization.size());

        // Check the ordering
        assertEquals(cryptoValues2.getSymbol(), cryptoListOrderedByNormalization.get(0));
        assertEquals(cryptoValues1.getSymbol(), cryptoListOrderedByNormalization.get(1));

        verify(cryptoRepository).findAllSymbols();
        verify(cryptoValuesRepository, times(2)).findPriceLimitsBySymbolInInterval(anyString(), any(Instant.class), any(Instant.class));
    }

    @Test
    void getHighestNormalizedCryptoByDate_ReturnTheCryptoSymbol() {
        var cryptoEntityList = getCryptoEntityList();
        var cryptoValues1 = getCryptoValuesDynamically("LTE", 10d, 12d);
        var cryptoValues2 = getCryptoValuesDynamically("DOGE", 11d, 19d);

        when(cryptoRepository.findAllSymbols()).thenReturn(cryptoEntityList);
        when(cryptoValuesRepository.findPriceLimitsBySymbolInInterval(anyString(), any(Instant.class), any(Instant.class)))
                .thenReturn(cryptoValues1)
                .thenReturn(cryptoValues2);

        var highestNormalizedCryptoByDate = cryptoService.getHighestNormalizedCryptoByDate(LocalDate.now());

        assertNotNull(highestNormalizedCryptoByDate);
        assertEquals("DOGE", highestNormalizedCryptoByDate);

        verify(cryptoRepository).findAllSymbols();
        verify(cryptoValuesRepository, times(2)).findPriceLimitsBySymbolInInterval(anyString(), any(Instant.class), any(Instant.class));
    }

    @Test
    void getHighestNormalizedCryptoByDate_ThrowsRuntimeException_WhenCryptoNotFoundForTheDate() {
        var cryptoEntityList = getCryptoEntityList();
        var cryptoValues1 = getCryptoValuesDynamically(null, null, null);
        var date = LocalDate.now();

        when(cryptoRepository.findAllSymbols()).thenReturn(cryptoEntityList);
        when(cryptoValuesRepository.findPriceLimitsBySymbolInInterval(anyString(), any(Instant.class), any(Instant.class)))
                .thenReturn(cryptoValues1);

        var message = Assert.assertThrows(NotFoundException.class, () -> cryptoService.getHighestNormalizedCryptoByDate(date)).getMessage();
        assertEquals(String.format("Could not found any crypto for this date %s", date), message);

        verify(cryptoRepository).findAllSymbols();
        verify(cryptoValuesRepository, times(2)).findPriceLimitsBySymbolInInterval(anyString(), any(Instant.class), any(Instant.class));
    }

    @Test
    void getCryptoValuesBySymbol_ReturnTheCryptoValues() {
        var cryptoValues = getCryptoValues();
        when(cryptoValuesRepository.findCryptoValuesBySymbol(anyString())).thenReturn(cryptoValues);

        CryptoValues cryptoValuesBySymbol = cryptoService.getCryptoValuesBySymbol("DOGE");
        assertEquals(cryptoValues, cryptoValuesBySymbol);

        verify(cryptoValuesRepository).findCryptoValuesBySymbol(anyString());
    }

    @Test
    void getCryptoValuesBySymbol_ThrowsRuntimeException_WhenCryptoSymbolNotFound() {
        var cryptoValues = CryptoValues.builder().build();
        var symbol = "DOGE";
        when(cryptoValuesRepository.findCryptoValuesBySymbol(anyString())).thenReturn(cryptoValues);

        var message = Assert.assertThrows(NotFoundException.class, () -> cryptoService.getCryptoValuesBySymbol(symbol)).getMessage();
        assertEquals(String.format("Crypto currency with value %s could not be found.", symbol), message);

        verify(cryptoValuesRepository).findCryptoValuesBySymbol(anyString());
    }
}