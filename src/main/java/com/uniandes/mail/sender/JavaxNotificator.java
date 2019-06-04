package com.uniandes.mail.sender;

import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Funcionalidad para el envío de correos para la notificación a los usuarios de plataforma.
 *
 * @author <a href="mailto:ja.misnaza@uniandes.edu.co">Julán Misnaza</a>
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "mail.notificator", havingValue = "javax")
public class JavaxNotificator implements Notificator {

    private Properties properties = new Properties();

    /**
     * Email desde el que se envía el mensaje.
     */
    @Value("${mail.user}")
    private String from;

    @Value("${mail.pass}")
    private String pass;

    /**
     * Host desde el cual se envía el mensaje.
     */
    @Value("${mail.host}")
    private String host;

    @PostConstruct
    public void init() {
        this.properties.setProperty("mail.smtp.host", host);
        this.properties.put("mail.smtp.auth", "true");
        this.properties.setProperty("mail.transport.protocol", "smtp");
        this.properties.put("mail.debug", "true");
        this.properties.put("mail.smtp.port", 465);
        this.properties.put("mail.smtp.socketFactory.port", 465);
        this.properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        this.properties.put("mail.smtp.socketFactory.fallback", "false");
    }

    @Override
    @Async
    public void sendNotification(String to, String subject, String htmlMessage) {
        // Obtenemos una sesion para enviar email.
        Session session = Session.getDefaultInstance(properties);
        try {
            MimeMessage message = new MimeMessage(session);
            // De:
            log.info("Origen del correo:" + from);
            log.info("Destino del correo:" + to);
            message.setFrom(new InternetAddress(from));
            // Para:
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            // Asunto:
            message.setSubject(subject);
            // Mensaje:
            message.setText(htmlMessage);
            Transport transport = session.getTransport();
            transport.connect(from, pass);
            // Enviamos el mensaje.
            transport.sendMessage(message,
                    message.getRecipients(Message.RecipientType.TO));
        } catch (Exception ex) {
            log.info("Se produjo un eror en el envío de la notificación: " + ex.getMessage());
        }
    }
}
