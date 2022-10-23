package pl.com.seremak.simplebills.util;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import pl.com.seremak.simplebills.dto.BillQueryParams;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

public class BillQueryUtils {

    public static final String MODIFIED_AT_FIELD = "metadata.modifiedAt";
    public static final String VERSION_FIELD = "metadata.version";
    public static final String CATEGORY_FIELD = "category";
    public static final String DATE_FIELD = "date";
    public static final String USER_FIELD = "user";
    public static final int DEFAULT_PAGE_SIZE = 1000;
    public static final String DEFAULT_SORTING_COLUMN = "billNumber";


    public static Update updateMetadata(final Update update) {
        return update
                .set(MODIFIED_AT_FIELD, Instant.now())
                .inc(VERSION_FIELD, 1);
    }

    @SuppressWarnings("all")
    public static Query prepareFindByCategoryQueryPageable(final String userName, final BillQueryParams params) {
        Query query = new Query().addCriteria(Criteria.where(USER_FIELD).is(userName));
        if (params.getCategory() != null) query.addCriteria(Criteria.where(CATEGORY_FIELD).is(params.getCategory()));
        query.skip(calculateSkip(params));
        query.limit((int) extractPageSize(params));
        query.with(Sort.by(extractDirection(params), extractSortingColumn(params)));
        return addBetweenDatesCriteria(params, query);
    }

    public static Query prepareFindByCategoryQuery(final String userName, final BillQueryParams params) {
        Query query = new Query().addCriteria(Criteria.where(USER_FIELD).is(userName));
        if (params.getCategory() != null) query.addCriteria(Criteria.where(CATEGORY_FIELD).is(params.getCategory()));
        return addBetweenDatesCriteria(params, query);
    }

    private static Query addBetweenDatesCriteria(final BillQueryParams params, final Query query) {
        final Optional<Instant> dateFrom = getToInstantUTC(params.getDateFrom());
        final Optional<Instant> dateTo = getToInstantUTC(params.getDateTo()).map(presentDateTo -> presentDateTo.plus(1, ChronoUnit.DAYS));
        Criteria criteria = Criteria.where(DATE_FIELD);
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

    private static Optional<Instant> getToInstantUTC(LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(presentLocalDate -> presentLocalDate.atStartOfDay(ZoneId.of("UTC")).toInstant());
    }

    public static long calculateSkip(final BillQueryParams params) {
        return (long) Optional.ofNullable(params.getPageSize()).orElse(0) * (Optional.ofNullable(params.getPageNumber()).orElse(0) - 1);
    }

    public static long extractPageSize(BillQueryParams params) {
        return (long) Optional.ofNullable(params.getPageSize()).orElse(DEFAULT_PAGE_SIZE);
    }

    private static Sort.Direction extractDirection(final BillQueryParams params) {
        return Optional.ofNullable(params.getSortDirection())
                .map(Enum::toString)
                .map(String::toUpperCase)
                .map(Sort.Direction::valueOf)
                .orElse(Sort.Direction.DESC);
    }

    private static String extractSortingColumn(final BillQueryParams params) {
        return Optional.ofNullable(params.getSortColumn())
                .orElse(DEFAULT_SORTING_COLUMN);
    }
}
