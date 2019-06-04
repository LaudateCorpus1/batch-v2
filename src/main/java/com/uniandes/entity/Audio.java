package com.uniandes.entity;

import java.util.Date;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@Setter
@Document(collection = "audios")
public class Audio extends AbstractDocument {

    @Field("original_name")
    private String originalName;

    private String status;

    private String observations;

    @Field("participant_id")
    private ObjectId participantId;

    @Field("contest_id")
    private ObjectId contestId;

    @Field("location_original_audio")
    private String locationOriginalAudio;

    @Field("location_converted_audio")
    private String locationConvertedAudio;

    @Field("converted_name")
    private String convertedName;

    @Field("created_at")
    private Date createdAt;

    @Field("updated_at")
    private Date updatedAt;

    @Field("process_attempts")
    private int processAttempts;

    @Transient
    private boolean processingSuccess;
}
