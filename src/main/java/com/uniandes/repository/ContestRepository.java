package com.uniandes.repository;

import com.uniandes.entity.Contest;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestRepository extends MongoRepository<Contest, ObjectId> {

    Contest findById(ObjectId id);
}
