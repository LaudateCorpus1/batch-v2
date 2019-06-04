package com.uniandes.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "contests")
public class Contest extends AbstractDocument {

    @Field("name")
    private String name;

    @Field("url")
    private String url;
}
