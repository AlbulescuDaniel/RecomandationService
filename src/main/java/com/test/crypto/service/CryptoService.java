package com.test.crypto.service;

import com.test.crypto.common.exceptions.NotFoundException;
import com.test.crypto.domain.entity.CryptoEntity;
import com.test.crypto.domain.entity.CryptoValues;
import com.test.crypto.domain.enums.SortingOrder;
import com.test.crypto.repository.CryptoRepository;
import com.test.crypto.repository.CryptoValuesRepository;
import com.test.crypto.utils.CryptoNormalizer;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@AllArgsConstructor
@Service
public class CryptoService {

    private final CryptoRepository cryptoRepository;
    private final CryptoValuesRepository cryptoValuesRepository;
    private final CryptoNormalizer cryptoNormalizer;

    public void saveAll(List<CryptoEntity> cryptoList) {
        cryptoRepository.saveAll(cryptoList);
    }

    /**
     * Returns a sorted list with all the crypto symbols withing a specified period
     *
     * @param start The start date used for filtering
     * @param end The end date used for filtering
     * @param sortingOrder The sorting order. It can be ASD or DESC.
     * @return The list with sorted currency symbols.
     */
    public List<String> getCryptoListOrderedByNormalization(LocalDate start, LocalDate end, SortingOrder sortingOrder) {
        Comparator<Pair<CryptoValues, Double>> normalizationComparator = sortingOrder == SortingOrder.ASC ? Comparator.comparing(Pair::getRight) : (c1, c2) -> c2.getRight().compareTo(c1.getRight());

        var allCryptoSymbols = getCryptoSymbols();

        return getCryptoValuesWithNormalisation(allCryptoSymbols, start, end)
                .sorted(normalizationComparator)
                .map(Pair::getLeft)
                .map(CryptoValues::getSymbol)
                .toList();
    }

    /**
     * Returns the crypto symbol with the highers normalization for a specific date
     *
     * @param date Date used for searching. It will search from the beginning to the end of the day.
     * @return Returns the crypto symbol
     */
    public String getHighestNormalizedCryptoByDate(LocalDate date) {
        var allCryptoSymbols = getCryptoSymbols();

        return getCryptoValuesWithNormalisation(allCryptoSymbols, date, date)
                .max(Comparator.comparing(Pair::getRight))
                .map(Pair::getLeft)
                .map(CryptoValues::getSymbol)
                .orElseThrow(() -> new NotFoundException(String.format("Could not found any crypto for this date %s", date)));
    }

    /**
     * Finds the oldest/newest/min/max values for a requested crypto symbol
     *
     * @param symbol The symbol of the cryptocurrency
     * @return An object containing oldest/newest/min/max values for the specific crypto
     */
    public CryptoValues getCryptoValuesBySymbol(String symbol) {
        var cryptoValuesBySymbol = cryptoValuesRepository.findCryptoValuesBySymbol(symbol);

        if (!cryptoExistsPredicate().test(cryptoValuesBySymbol)) {
            throw new NotFoundException(String.format("Crypto currency with value %s could not be found.", symbol));
        }

        return cryptoValuesBySymbol;
    }

    /**
     * Calculate the normalisation for each crypto
     *
     * @param allCryptoSymbols All crypto distinct symbols
     * @param start The start date used for filtering
     * @param end The end date used for filtering
     * @return Pairs of crypto information and normalisation value
     */
    private Stream<Pair<CryptoValues, Double>> getCryptoValuesWithNormalisation(List<String> allCryptoSymbols, LocalDate start, LocalDate end) {
        return allCryptoSymbols.stream()
                .map(symbol -> getCryptoValuesBySymbolInInterval(start, end, symbol))
                .filter(cryptoExistsPredicate())
                .map(cryptoValues -> Pair.of(cryptoValues, cryptoNormalizer.getNormalisedValue(cryptoValues.getMinPrice(), cryptoValues.getMaxPrice())));
    }

    /**
     * Check if the crypto exists.
     * Cassandra is returning an instance of the CryptoValues object with all the fields null.
     */
    private Predicate<CryptoValues> cryptoExistsPredicate() {
        return (cryptoValue) -> Objects.nonNull(cryptoValue.getMinPrice()) || Objects.nonNull(cryptoValue.getMaxPrice());
    }

    /**
     * Get the crypto information (oldest/newest/min/max) for a specific crypto symbol
     *
     * @param start The start date used for filtering
     * @param end The end date used for filtering
     * @param symbol The symbol of the cryptocurrency
     * @return The crypto information
     */
    private CryptoValues getCryptoValuesBySymbolInInterval(LocalDate start, LocalDate end, String symbol) {
        return cryptoValuesRepository.findPriceLimitsBySymbolInInterval(symbol, start.atTime(LocalTime.MIN).toInstant(ZoneOffset.UTC),
                end.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC));
    }

    /**
     * @return All the distinct symbols of the crypto entries in database. The symbols are the actual table partitions.
     */
    private List<String> getCryptoSymbols() {
        return cryptoRepository.findAllSymbols().stream().map(CryptoEntity::getSymbol).collect(Collectors.toList());
    }
}
