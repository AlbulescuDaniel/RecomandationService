package com.test.crypto.repository;

import com.test.crypto.domain.entity.CryptoValues;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface CryptoValuesRepository extends CassandraRepository<CryptoValues, String> {

    @Query("SELECT symbol, MAX(price) as maxPrice, MIN(price) as minPrice, MAX(timestamp) as newest, MIN(timestamp) as oldest FROM crypto WHERE symbol = ?0")
    CryptoValues findCryptoValuesBySymbol(String symbol);

    @Query("SELECT symbol, MAX(price) as maxPrice, MIN(price) as minPrice FROM crypto WHERE symbol = ?0 AND timestamp >= ?1 AND timestamp <= ?2")
    CryptoValues findPriceLimitsBySymbolInInterval(String symbol, Instant start, Instant end);
}
