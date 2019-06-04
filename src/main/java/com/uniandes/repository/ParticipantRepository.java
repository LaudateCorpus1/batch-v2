package com.uniandes.repository;

import com.uniandes.entity.Participant;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends MongoRepository<Participant, ObjectId> {

    Participant findById(ObjectId id);
}
