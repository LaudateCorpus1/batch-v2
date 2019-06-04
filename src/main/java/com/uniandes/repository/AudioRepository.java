package com.uniandes.repository;

import com.uniandes.entity.Audio;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudioRepository extends MongoRepository<Audio, ObjectId> {

    Audio findById(ObjectId id);
}
