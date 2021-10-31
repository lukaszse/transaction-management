package pl.com.seremak.simplebills.service.util;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import pl.com.seremak.simplebills.endpoint.dto.BillQueryParams;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.model.Metadata;

import java.time.Instant;

public class ServiceCommons {
    public static final String ID_FIELD = "_id";
    public static final String MODIFIED_AT_FIELD = "metadata.modifiedAt";
    public static final String VERSION_FIELD = "metadata.version";
    public static final String CATEGORY_FIELD = "category";
    public static final String DATE_FIELD = "date";
    public static final String USER_FIELD = "user";

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

}
