package pl.com.seremak.simplebills.service.util;

import org.springframework.data.mongodb.core.query.Update;
import pl.com.seremak.simplebills.model.Bill;
import pl.com.seremak.simplebills.model.Metadata;

import java.time.Instant;

public class ServiceCommons {
    public static final String ID_FIELD = "_id";
    public static final String MODIFIED_AT_FIELD = "metadata.modifiedAt";
    public static final String VERSION_FIELD = "metadata.version";

    public static Update updateMetadata(final Update update) {
        return update
                .set(MODIFIED_AT_FIELD, Instant.now())
                .inc(VERSION_FIELD, 1);
    }

}
