package com.uniandes.store;

import com.uniandes.entity.Audio;
import java.io.File;

public interface StoreFiles {

    /**
     * Search the file with name
     *
     * @return File
     */
    File readFromService(String name) throws Exception;

    /**
     * Save file to bucket in S3
     */
    String writeToService(Audio audio) throws Exception;
}
