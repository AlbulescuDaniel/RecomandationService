package com.test.crypto.controller;

import com.test.crypto.commons.model.CryptoValuesDto;
import com.test.crypto.commons.model.SortingOrderDto;
import com.test.crypto.commons.ports.application.CryptoApi;
import com.test.crypto.service.CryptoService;
import com.test.crypto.service.mapper.CryptoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class CryptoController implements CryptoApi {

    private final CryptoService cryptoService;
    private final CryptoMapper cryptoMapper;

    @Override
    public ResponseEntity<List<String>> getCryptoListOrderedByNormalization(Optional<LocalDate> start, Optional<LocalDate> end, Optional<SortingOrderDto> sortingOrder) {
        if (start.isEmpty()) {
            start = Optional.of(LocalDate.now().minusMonths(1));
        }

        if (end.isEmpty()) {
            end = Optional.of(LocalDate.now());
        }

        if (sortingOrder.isEmpty()) {
            sortingOrder = Optional.of(SortingOrderDto.DESC);
        }

        var cryptoListOrderedByNormalization = cryptoService.getCryptoListOrderedByNormalization(start.get(), end.get(), cryptoMapper.fromDto(sortingOrder.get()));
        return new ResponseEntity<>(cryptoListOrderedByNormalization, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> getHighestNormalizedCryptoByDate(LocalDate date) {
        var highestNormalizedCryptoByDate = cryptoService.getHighestNormalizedCryptoByDate(date);
        return new ResponseEntity<>(highestNormalizedCryptoByDate, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<CryptoValuesDto> getCryptoValuesBySymbol(String symbol) {
        var cryptoValuesBySymbol = cryptoService.getCryptoValuesBySymbol(symbol);
        var cryptoValuesDto = cryptoMapper.toDto(cryptoValuesBySymbol);
        return new ResponseEntity<>(cryptoValuesDto, HttpStatus.OK);
    }
}
