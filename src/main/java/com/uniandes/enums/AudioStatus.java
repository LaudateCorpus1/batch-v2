package com.uniandes.enums;

public enum AudioStatus {

    /** Audio have been uploaded and is ready to be processed */
    IN_PROGRESS,

    /** The worker have taken the audio and is in converting process */
    CONVERTING,

    /** Once the audio have been converted successfully */
    CONVERTED,

    /** After the conversion process and 3+ wrong attempts, the audio is marked with ERROR */
    ERROR
}