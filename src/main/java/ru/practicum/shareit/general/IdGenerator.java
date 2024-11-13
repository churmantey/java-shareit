package ru.practicum.shareit.general;

import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class IdGenerator {

    private Long id;

    public IdGenerator() {
        id = 0L;
    }

    public Long getNextId() {
        return ++id;
    }

}