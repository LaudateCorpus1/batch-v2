package com.uniandes.store;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.uniandes.entity.Audio;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "file.store", havingValue = "AWS-S3")
public class StoreS3 implements StoreFiles {

    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${path.original.files}")
    private String pathAudioOriginalFiles;

    @Value("${path.converted.files}")
    private String pathAudioConvertedFiles;

    @Value("${path.temporal.files}")
    private String pathTemporalConvertedFiles;

    private final AmazonS3 s3client;

    public StoreS3(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    @Override
    public File readFromService(String name) throws Exception {
        File targetFile = new File(pathTemporalConvertedFiles + "/" + name);
        log.info("Path target {}", targetFile.getPath());
        try {
            S3Object s3Object = s3client
                .getObject(new GetObjectRequest(bucketName, pathAudioOriginalFiles + "/" + name));
            InputStream is = s3Object.getObjectContent().getDelegateStream();
            Files.copy(is, Paths.get(targetFile.getPath()), StandardCopyOption.REPLACE_EXISTING);
            log.info("Copy finish");
            return targetFile;
        } catch (IOException io) {
            log.error("Error in file {}", io.getMessage());
            throw new Exception(io.getMessage());
        } catch (Exception e) {
            log.error("Error in file {}", e.getMessage());
            throw new Exception(e.getMessage());
        }
    }

    @Override
    public String writeToService(Audio audio) {
        log.info("Writing file {}", audio.getConvertedName());

        try (InputStream io = new FileInputStream(pathTemporalConvertedFiles + "/" + audio.getConvertedName())) {

            ObjectMetadata metaData = new ObjectMetadata();
            byte[] bytes = IOUtils.toByteArray(io);
            metaData.setContentLength(bytes.length);
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);

            PutObjectRequest request = new PutObjectRequest(bucketName,
                pathAudioConvertedFiles + "/" + audio.getConvertedName(), byteArrayInputStream, metaData);
            request.setCannedAcl(CannedAccessControlList.PublicRead);
            s3client.putObject(request);

            return "https://s3.amazonaws.com/" + bucketName + "/" + pathAudioConvertedFiles + "/" + audio
                .getConvertedName();
        } catch (Exception e) {
            log.error("Error writing in S3 {}: ", e.getMessage());
        }
        return "";
    }
}
