package com.uniandes.mail.sender;

/**
 * Interfaz general para el envío de de notificaciones.
 *
 * @author <a href="mailto:ja.misnaza@uniandes.edu.co">Juli&acute;n Misnaza</a>
 */
public interface Notificator {

    /**
     * Envío de un email.
     *
     * @param to      destinatario del correo.
     * @param subject asunto del mensaje.
     * @param message mensaje, puede ser en formato HTML.
     */
    void sendNotification(String to, String subject, String message);
}

