package com.uniandes.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

@EqualsAndHashCode
public class AbstractDocument {

    @Id
    @Getter
    @Field("_id")
    private ObjectId id;
}