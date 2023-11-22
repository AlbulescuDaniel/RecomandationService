package com.test.crypto.helper;

import com.test.crypto.domain.entity.CryptoEntity;
import com.test.crypto.domain.entity.CryptoValues;

import java.time.Instant;
import java.util.List;

public class TestHelper {

    public static CryptoValues getCryptoValues() {
        return CryptoValues.builder()
                .symbol("LTE")
                .maxPrice(123d)
                .minPrice(100d)
                .newest(Instant.now().minusSeconds(60))
                .oldest(Instant.now().minusSeconds(3600))
                .build();
    }

    public static CryptoValues getCryptoValuesDynamically(String symbol, Double minPrice, Double maxPrice) {
        return CryptoValues.builder()
                .symbol(symbol)
                .maxPrice(maxPrice)
                .minPrice(minPrice)
                .newest(Instant.now().minusSeconds(60))
                .oldest(Instant.now().minusSeconds(3600))
                .build();
    }

    public static CryptoEntity getCryptoEntity(String symbol, Double price) {
        return CryptoEntity.builder()
                .symbol(symbol)
                .timestamp(Instant.now().minusSeconds(60))
                .price(price)
                .build();
    }

    public static List<CryptoEntity> getCryptoEntityList() {
        return List.of(getCryptoEntity("LTE", 100d), getCryptoEntity("DOGE", 200d));
    }
}
