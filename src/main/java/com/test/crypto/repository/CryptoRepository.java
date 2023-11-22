package com.test.crypto.repository;

import com.test.crypto.domain.entity.CryptoEntity;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CryptoRepository extends CassandraRepository<CryptoEntity, String> {

    @Query("SELECT DISTINCT symbol FROM CRYPTO")
    List<CryptoEntity> findAllSymbols();
}
