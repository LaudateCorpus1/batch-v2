package com.uniandes.entity;

import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "options")
public class Option extends AbstractDocument {

    private String name;

    private String value;

    @Transient
    public boolean isActive() {
        return Boolean.parseBoolean(value);
    }
}
