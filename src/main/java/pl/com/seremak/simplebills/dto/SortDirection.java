package pl.com.seremak.simplebills.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SortDirection {

    ASC("asc"),
    DESC("desc");

    private final String name;

    public SortDirection of(final String name) {
        return SortDirection.valueOf(name.toUpperCase());
    }
}
