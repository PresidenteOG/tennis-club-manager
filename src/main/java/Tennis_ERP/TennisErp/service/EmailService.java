package Tennis_ERP.TennisErp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public interface EmailService {
    // Para enviar un solo correo (ej. bienvenida)
    void sendSingleEmail(String to, String subject, String body) throws MessagingException;
    
    // Para enviar a toda la base de datos
    void sendMassiveEmail(String subject, String body);

    void sendMailById(Long userid, String subject, String body) throws MessagingException;

    void sendMailByCategory(Long categoriaId, String subject, String body) throws MessagingException;
}