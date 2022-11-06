package pl.com.seremak.simplebills.messageQueue.queueDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDeletionMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String username;
    private String deletedCategory;
    private String replacementCategory;
}