package com.test.crypto.domain.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;

@Data
@Builder
@Table("crypto")
public class CryptoEntity {
    @PrimaryKeyColumn(
            name = "symbol",
            ordinal = 0,
            type = PrimaryKeyType.PARTITIONED)
    private String symbol;

    @PrimaryKeyColumn(
            name = "timestamp",
            ordinal = 1,
            type = PrimaryKeyType.CLUSTERED)
    private Instant timestamp;

    @Column
    private Double price;
}
