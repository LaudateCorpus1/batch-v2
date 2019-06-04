package com.uniandes.encoding.audio;

import com.uniandes.encoding.audio.task.ProcessingFile;
import com.uniandes.entity.Audio;
import com.uniandes.entity.Contest;
import com.uniandes.entity.Option;
import com.uniandes.entity.Participant;
import com.uniandes.enums.AudioStatus;
import com.uniandes.enums.OptionNames;
import com.uniandes.mail.sender.Notificator;
import com.uniandes.repository.AudioRepository;
import com.uniandes.repository.ContestRepository;
import com.uniandes.repository.OptionRepository;
import com.uniandes.repository.ParticipantRepository;
import com.uniandes.store.StoreFiles;
import java.io.File;
import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class AudioQueueConsumer implements Consumer {

    private final AudioRepository audioRepository;

    private final ParticipantRepository participantRepository;

    private final ContestRepository contestRepository;

    private final OptionRepository optionRepository;

    private final Notificator notificator;

    private final int MAXIMUM_ATTEMPS_ALLOWED = 3;

    private final StoreFiles store;

    /**
     * Path where the converted files will be stored
     */
    @Value("${aws.bucket.name}")
    private String bucketName;

    @Value("${path.temporal.files}")
    private String pathTemporalConvertedFiles;

    @Value("${path.converted.files}")
    private String pathAudioConvertedFiles;

    private final ExecutorService executor;

    public AudioQueueConsumer(AudioRepository audioRepository,
        ParticipantRepository participantRepository,
        ContestRepository contestRepository,
        OptionRepository optionRepository,
        Notificator notificator,
        StoreFiles store) {
        this.audioRepository = audioRepository;
        this.participantRepository = participantRepository;
        this.contestRepository = contestRepository;
        this.optionRepository = optionRepository;
        this.notificator = notificator;
        this.store = store;

        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public void audioConvert(String id) {
        log.info("Begin logic {} id {}", this.getClass().getName(), id);
        LocalDateTime now = LocalDateTime.now();
        Audio audioInQueue = audioRepository.findById(new ObjectId(id));
        log.info("Begin audio task " + now.toString());
        updateVoicesToConverting(audioInQueue);
        ProcessingFile process = new ProcessingFile(audioInQueue, pathTemporalConvertedFiles);
        try {
            long start = System.nanoTime();
            store.readFromService(audioInQueue.getOriginalName());
            CompletableFuture<Audio> future = CompletableFuture.supplyAsync(process::coding, executor);
            future.whenCompleteAsync((processedAudio, exception) -> {
                if (processedAudio.isProcessingSuccess()) {
                    String locationConvertedAudio = uploadS3File(processedAudio);
                    updateAudioToConverted(processedAudio, locationConvertedAudio);
                    sendMail(processedAudio, AudioStatus.CONVERTED);
                    deleteLocalFiles(processedAudio);
                } else if (processedAudio.getProcessAttempts() >= MAXIMUM_ATTEMPS_ALLOWED) {
                    updateAudioToError(processedAudio);
                    sendMail(processedAudio, AudioStatus.ERROR);
                } else {
                    updateOneMoreAttempt(processedAudio);
                }
            });
            long duration = (System.nanoTime() - start) / 1_000_000;
            log.info("Processed {} task(s) in {} millis", audioInQueue, duration);
        } catch (Exception e) {
            log.error("Error: {}", e.getMessage());
        }
    }

    private void deleteLocalFiles(Audio audio) {
        File originalFile = new File(pathTemporalConvertedFiles + "/" + audio.getOriginalName());
        originalFile.delete();

        File convertedFile = new File(pathTemporalConvertedFiles + "/" + audio.getConvertedName());
        convertedFile.delete();
    }

    /**
     * Store in S3 bucket
     *
     * @param audio Processed object by the codec
     */
    private String uploadS3File(Audio audio) {
        try {
            return store.writeToService(audio);
        } catch (Exception e) {
            log.error("Error to connect S3 service {}", e.getMessage());
        }
        return "";
    }

    private void updateVoicesToConverting(Audio audio) {
        log.info("updateVoicesToConverting --> pending audios: {}", audio);
        audio.setStatus(AudioStatus.CONVERTING.name());
        audioRepository.save(audio);
        log.info("Finish updateVoicesToConverting");
    }

    private void updateAudioToConverted(Audio audio, String locationConvertedAudio) {
        log.info("updateAudioToConverted: {}", audio.getId());
        audio.setLocationConvertedAudio(locationConvertedAudio);
        audio.setStatus(AudioStatus.CONVERTED.name());
        audioRepository.save(audio);
        log.info("Finish updateAudioToConverted: {}", audio.getId());
    }

    private void updateAudioToError(Audio audio) {
        log.info("updateAudioToError: {}", audio.getId());
        audio.setStatus(AudioStatus.ERROR.name());
        audioRepository.save(audio);
        log.info("Finish updateAudioToError: {}", audio.getId());
    }

    private void updateOneMoreAttempt(Audio audio) {
        log.info("updateOneMoreAttempt: {}", audio.getId());
        audio.setStatus(AudioStatus.IN_PROGRESS.name());
        audio.setProcessAttempts(audio.getProcessAttempts() + 1);
        audioRepository.save(audio);
        log.info("Finish updateOneMoreAttempt: {}", audio.getId());
    }

    private void sendMail(Audio audio, AudioStatus status) {
        Option emailService = optionRepository.findByName(OptionNames.EMAIL_SERVICE);
        if (emailService.isActive()) {

            Participant participant = participantRepository.findById(audio.getParticipantId());
            Contest contest = contestRepository.findById(audio.getContestId());

            log.info("Sending mail ... {}", participant.getEmail());

            String subject = "Tu audio en \"" + contest.getName() + "\"";
            String body =
                "<h1>Hola " + participant.getFirstName() + " " + participant.getLastName() + "</h1>";

            switch (status) {
                case CONVERTED:
                    subject += " ya fue procesado :)";
                    body += "<p>Tu audio ya fue procesado con éxito en el concurso \"" + contest.getName()
                        + "\" y está disponible en la página web <a href=\"" + contest.getUrl() + "\">"
                        + contest.getUrl() + "</a></p>";
                    break;

                case ERROR:
                    subject += " no pudo ser procesado :(";
                    body += "<p>Lo sentimos, pero no hemos podido procesar tu audio en el concurso \""
                        + contest.getName() + "\", por favor intentalo de nuevo en la página web <a href=\""
                        + contest.getUrl() + "\">" + contest.getUrl() + "</a></p>";
                    break;
            }

            body += "<h3>Saludos, el equipo de SUPER VOICES</h3>";
            notificator.sendNotification(participant.getEmail(), subject, body);

        } else {
            log.warn("Email service is inactive");
        }
    }
}
