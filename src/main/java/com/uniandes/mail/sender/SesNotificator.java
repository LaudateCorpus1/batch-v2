package com.uniandes.mail.sender;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(value = "mail.notificator", havingValue = "AWS-SES")
public class SesNotificator implements Notificator {

    /**
     * Email desde el que se envía el mensaje.
     */
    @Value("${mail.user}")
    private String from;

    @Async
    @Override
    public void sendNotification(String to, String subject, String message) {
        try {
            AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
                .withRegion(Regions.US_EAST_1).build();
            SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(to))
                .withMessage(
                    new Message().withBody(new Body()
                        .withHtml(new Content().withCharset(StandardCharsets.UTF_8.displayName()).withData(message)))
                        .withSubject(new Content().withCharset(StandardCharsets.UTF_8.displayName()).withData(subject)))
                .withSource(from);
            client.sendEmail(request);
            log.info("Email sent!");
        } catch (Exception ex) {
            log.error("Error sending mail: ", ex.getMessage());
        }
    }
}
