package com.uniandes.repository;

import com.uniandes.entity.Option;
import com.uniandes.enums.OptionNames;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends MongoRepository<Option, ObjectId> {

    Option findByName(OptionNames name);
}
