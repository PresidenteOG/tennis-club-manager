package Tennis_ERP.TennisErp.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import Tennis_ERP.TennisErp.service.EmailService;
import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/api/mail") // Esto define la primera parte de la ruta
public class MailRestController {

    @Autowired
    private EmailService emailService;

    // Esto define la segunda parte: /send-massive
    @PostMapping("/send-massive")
    public ResponseEntity<String> sendMassive(@RequestBody Map<String, String> request) {
        String subject = request.get("subject");
        String content = request.get("content");

        // Llama al servicio asíncrono que ya creamos
        emailService.sendMassiveEmail(subject, content);

        return ResponseEntity.ok("Proceso de envío iniciado");
    }

    @PostMapping("/send-to-category")
    public ResponseEntity<?> sendToCategory(@RequestBody Map<String, Object> request) {
        try {
            String subject = (String) request.get("subject");
            String content = (String) request.get("content");
            Long categoriaId = Long.parseLong(request.get("categoriaId").toString());

            emailService.sendMailByCategory(categoriaId, subject, content);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Correo enviado correctamente a la categoría",
                    "estado", "exito"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "mensaje", "ID de categoría inválido",
                    "estado", "error"));
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(Map.of(
                    "mensaje", "Error al enviar el correo: " + e.getMessage(),
                    "estado", "error"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "mensaje", "Error interno del servidor",
                    "estado", "error"));
        }
    }


    @PostMapping("/send-to-user")
    public ResponseEntity<?> sendtoaUser(@RequestBody Map<String, Object> request) {
        try {
            String subject = (String) request.get("subject");
            String content = (String) request.get("content");
            Long usuarioId = Long.parseLong(request.get("usuarioId").toString());

            emailService.sendMailById(usuarioId, subject, content);
            return ResponseEntity.ok(Map.of(
                    "mensaje", "Correo enviado correctamente al usuario",
                    "estado", "exito"));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "mensaje", "ID de usuario inválido",
                    "estado", "error"));
        } catch (MessagingException e) {
            return ResponseEntity.status(500).body(Map.of(
                    "mensaje", "Error al enviar el correo: " + e.getMessage(),
                    "estado", "error"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "mensaje", "Error interno del servidor",
                    "estado", "error"));
        }
    }

}