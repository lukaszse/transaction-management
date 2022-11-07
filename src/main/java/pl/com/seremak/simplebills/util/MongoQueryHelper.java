package pl.com.seremak.simplebills.util;

import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;

import static java.util.Objects.nonNull;
import static pl.com.seremak.simplebills.util.ReflectionsUtils.getFieldValue;
import static pl.com.seremak.simplebills.util.VersionedEntityUtils.updateMetadata;

public class MongoQueryHelper {

    public static <T> Update preparePartialUpdateQuery(final Object object, final Class<T> clazz) {
        final Update update = new Update();
        Arrays.stream(clazz.getDeclaredFields())
                .filter(field -> nonNull(getFieldValue(field, object)))
                .forEach(field -> update.set(field.getName(), getFieldValue(field, object)));
        return updateMetadata(update);
    }
}
