package pl.com.seremak.simplebills.util;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import pl.com.seremak.simplebills.dto.TransactionQueryParams;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import static pl.com.seremak.simplebills.util.DateUtils.toInstantUTC;

public class TransactionQueryUtils {

    public static final String CATEGORY_FIELD = "category";
    public static final String DATE_FIELD = "date";
    public static final String USER_FIELD = "user";
    public static final int DEFAULT_PAGE_SIZE = 1000;
    public static final String DEFAULT_SORTING_COLUMN = "transactionNumber";


    @SuppressWarnings("all")
    public static Query prepareFindByCategoryQueryPageable(final String userName, final TransactionQueryParams params) {
        Query query = new Query().addCriteria(Criteria.where(USER_FIELD).is(userName));
        if (params.getCategory() != null) query.addCriteria(Criteria.where(CATEGORY_FIELD).is(params.getCategory()));
        query.skip(calculateSkip(params));
        query.limit((int) extractPageSize(params));
        query.with(Sort.by(extractDirection(params), extractSortingColumn(params)));
        return addBetweenDatesCriteria(params, query);
    }

    public static Query prepareFindByCategoryQuery(final String userName, final TransactionQueryParams params) {
        final Query query = new Query().addCriteria(Criteria.where(USER_FIELD).is(userName));
        if (params.getCategory() != null) query.addCriteria(Criteria.where(CATEGORY_FIELD).is(params.getCategory()));
        return addBetweenDatesCriteria(params, query);
    }

    private static Query addBetweenDatesCriteria(final TransactionQueryParams params, final Query query) {
        final Optional<Instant> dateFrom = toInstantUTC(params.getDateFrom());
        final Optional<Instant> dateTo = toInstantUTC(params.getDateTo()).map(presentDateTo -> presentDateTo.plus(1, ChronoUnit.DAYS));
        final Criteria criteria = Criteria.where(DATE_FIELD);
        if (dateFrom.isPresent() && dateTo.isPresent()) {
            criteria.gte(dateFrom.get()).lte(dateTo.get());
            query.addCriteria(criteria);
        } else if (dateFrom.isPresent()) {
            criteria.gte(dateFrom.get());
            query.addCriteria(criteria);
        } else if (dateTo.isPresent()) {
            criteria.lte(dateTo.get());
            query.addCriteria(criteria);
        }
        return query;
    }

    public static long calculateSkip(final TransactionQueryParams params) {
        return (long) Optional.ofNullable(params.getPageSize()).orElse(0) * (Optional.ofNullable(params.getPageNumber()).orElse(0) - 1);
    }

    public static long extractPageSize(final TransactionQueryParams params) {
        return (long) Optional.ofNullable(params.getPageSize()).orElse(DEFAULT_PAGE_SIZE);
    }

    private static Sort.Direction extractDirection(final TransactionQueryParams params) {
        return Optional.ofNullable(params.getSortDirection())
                .map(Enum::toString)
                .map(String::toUpperCase)
                .map(Sort.Direction::valueOf)
                .orElse(Sort.Direction.DESC);
    }

    private static String extractSortingColumn(final TransactionQueryParams params) {
        return Optional.ofNullable(params.getSortColumn())
                .orElse(DEFAULT_SORTING_COLUMN);
    }
}
