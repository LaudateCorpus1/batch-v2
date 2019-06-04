package com.uniandes.store;

import com.uniandes.entity.Audio;
import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "file.store", havingValue = "file-system")
public class StoreFileSystem implements StoreFiles {

    @Override
    public File readFromService(String name) throws Exception {
        return null;
    }

    @Override
    public String writeToService(Audio audio) throws Exception {
        return "";
    }
}
