package pl.com.seremak.simplebills.service.util;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import pl.com.seremak.simplebills.dto.BillQueryParams;

import java.time.Instant;
import java.util.Optional;

public class ServiceCommons {
    public static final String ID_FIELD = "_id";
    public static final String MODIFIED_AT_FIELD = "metadata.modifiedAt";
    public static final String VERSION_FIELD = "metadata.version";
    public static final String CATEGORY_FIELD = "category";
    public static final String DATE_FIELD = "date";
    public static final String USER_FIELD = "user";
    public static final int DEFAULT_PAGE_SIZE = 1000;
    public static final int DEFAULT_PAGE_NUMBER = 1;

    public static Update updateMetadata(final Update update) {
        return update
                .set(MODIFIED_AT_FIELD, Instant.now())
                .inc(VERSION_FIELD, 1);
    }

    public static Query prepareFindBillByUserAndCategoryQuery(final String userName, final BillQueryParams params) {
        Query query = new Query().addCriteria(Criteria.where(USER_FIELD).is(userName));
        if (params.getCategory() != null) query.addCriteria(Criteria.where(CATEGORY_FIELD).is(params.getCategory()));
        return addBetweenDatesCriteria(params, query);
    }

    private static Query addBetweenDatesCriteria(final BillQueryParams params, final Query query) {
        Criteria criteria = Criteria.where(DATE_FIELD);
        if (params.getDateFrom() != null && params.getDateTo() != null) {
            criteria.gte(params.getDateFrom()).lte(params.getDateTo());
            query.addCriteria(criteria);
        } else if (params.getDateFrom() != null) {
            criteria.gte(params.getDateFrom());
            query.addCriteria(criteria);
        } else if (params.getDateTo() != null) {
            criteria.lte(params.getDateTo());
            query.addCriteria(criteria);
        }
        return query;
    }

    public static long calculateSkip(final BillQueryParams params) {
        return (long) Optional.ofNullable(params.getPageSize()).orElse(0) * (Optional.ofNullable(params.getPageNumber()).orElse(0) - 1);
    }

    public static long extractPageSize(BillQueryParams params) {
        return (long) Optional.ofNullable(params.getPageSize()).orElse(DEFAULT_PAGE_SIZE);
    }


}
