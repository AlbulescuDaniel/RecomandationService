package com.test.crypto.service.mapper;

import com.test.crypto.commons.model.CryptoValuesDto;
import com.test.crypto.commons.model.SortingOrderDto;
import com.test.crypto.domain.entity.CryptoEntity;
import com.test.crypto.domain.entity.CryptoValues;
import com.test.crypto.domain.enums.SortingOrder;
import com.test.crypto.utils.csv.record.CryptoCsvRecord;
import org.mapstruct.Mapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CryptoMapper {

    default List<CryptoEntity> fromRecordList(List<CryptoCsvRecord> cryptoCsvRecords) {
        return cryptoCsvRecords.stream().map(this::fromRecord).toList();
    }

    default LocalDate map(Instant value) {
        return value.atZone(ZoneOffset.UTC).toLocalDate();
    }

    private CryptoEntity fromRecord(CryptoCsvRecord cryptoCsvRecord) {
        return CryptoEntity.builder()
                .symbol(cryptoCsvRecord.getSymbol())
                .price(Double.parseDouble(cryptoCsvRecord.getPrice()))
                .timestamp(Instant.ofEpochMilli(Long.parseLong(cryptoCsvRecord.getTimestamp())))
                .build();
    }

    CryptoValuesDto toDto(CryptoValues cryptoValues);

    SortingOrder fromDto(SortingOrderDto sortingOrderDto);
}
