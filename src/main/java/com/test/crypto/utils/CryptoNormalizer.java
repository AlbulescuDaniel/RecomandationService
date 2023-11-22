package com.test.crypto.utils;

import org.springframework.stereotype.Component;

@Component
public class CryptoNormalizer {

    /**
     * Normalization formula for crypto currencies
     *
     * @param min The minimum value a cryptocurrency had
     * @param max The maximum value a cryptocurrency had
     * @return the normalised value
     */
    public Double getNormalisedValue(Double min, Double max) {
        return (max - min) / min;
    }

}
