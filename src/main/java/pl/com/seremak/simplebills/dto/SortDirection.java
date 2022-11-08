package pl.com.seremak.simplebills.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortDirection {

    ASC, DESC;

    public static SortDirection of(final String name) {
        return SortDirection.valueOf(name.toUpperCase());
    }
}
