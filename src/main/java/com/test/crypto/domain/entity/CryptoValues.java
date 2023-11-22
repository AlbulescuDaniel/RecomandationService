package com.test.crypto.domain.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CryptoValues {
    private String symbol;
    private Double minPrice;
    private Double maxPrice;
    private Instant oldest;
    private Instant newest;
}
