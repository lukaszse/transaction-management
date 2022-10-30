package pl.com.seremak.simplebills.util;

import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.server.ServerErrorException;
import pl.com.seremak.simplebills.model.Metadata;
import pl.com.seremak.simplebills.model.VersionedEntity;

import java.time.Instant;

public class VersionedEntityUtils {

    public static final String INTERNAL_DATA_ERROR_MESSAGE = "Internal data error occurred.";
    public static final String MODIFIED_AT_FIELD = "metadata.modifiedAt";
    public static final String VERSION_FIELD = "metadata.version";

    public static <T> T setMetadata(final T versionedEntity) {
        if (versionedEntity instanceof VersionedEntity) {
            ((VersionedEntity) versionedEntity).setMetadata(
                    Metadata.builder()
                            .createdAt(Instant.now())
                            .modifiedAt(Instant.now())
                            .version(1L)
                            .build());
            return versionedEntity;
        } else throw new ServerErrorException(INTERNAL_DATA_ERROR_MESSAGE);
    }

    public static Update updateMetadata(final Update update) {
        return update
                .set(MODIFIED_AT_FIELD, Instant.now())
                .inc(VERSION_FIELD, 1);
    }
}
