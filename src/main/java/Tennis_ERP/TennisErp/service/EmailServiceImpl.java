package Tennis_ERP.TennisErp.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Tennis_ERP.TennisErp.dao.UsuarioDAO;
import Tennis_ERP.TennisErp.domain.Usuario;
import jakarta.mail.MessagingException;

/**
 * Local outbox implementation. The original project sent club announcements over
 * an external SMTP account (GMAIL_API); this build writes every message as an .html file into {@code app.outbox.dir}
 * and logs one line per recipient. The mail screens keep working exactly as before,
 * nothing is sent over the network, and no mail account is needed.
 */
@Service
public class EmailServiceImpl implements EmailService {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    @Autowired
    private UsuarioDAO userRepository;

    @Value("${app.outbox.dir:./outbox}")
    private String outboxDir;

    private final AtomicInteger counter = new AtomicInteger();

    @Override
    public void sendMailById(Long userid, String subject, String body) throws MessagingException {
        Usuario user = userRepository.getReferenceById(userid);
        writeToOutbox(user.getEmail(), subject, body);
    }

    @Override
    public void sendSingleEmail(String to, String subject, String body) throws MessagingException {
        writeToOutbox(to, subject, body);
    }

    @Override
    @Transactional
    public void sendMailByCategory(Long categoriaId, String subject, String body) throws MessagingException {
        List<Usuario> users = userRepository.findByUsuarioCategorias_Categoria_Id(categoriaId);
        for (Usuario user : users) {
            boolean isActive = user.getUsuarioCategorias().stream()
                .anyMatch(uc -> uc.getCategoria().getId().equals(categoriaId) && uc.isActivo());
            if (isActive) {
                writeToOutbox(user.getEmail(), subject, body);
            }
        }
    }

    @Override
    @Async
    public void sendMassiveEmail(String subject, String body) {
        List<Usuario> users = userRepository.findAll();
        for (Usuario user : users) {
            try {
                writeToOutbox(user.getEmail(), subject, body);
            } catch (RuntimeException e) {
                System.err.println("Outbox write failed for " + user.getEmail() + ": " + e.getMessage());
            }
        }
    }

    private void writeToOutbox(String to, String subject, String body) {
        String html = wrapHtmlContent(body);
        try {
            Path dir = Paths.get(outboxDir).toAbsolutePath().normalize();
            Files.createDirectories(dir);
            String safeTo = to == null ? "unknown" : to.replaceAll("[^A-Za-z0-9._-]", "_");
            String name = STAMP.format(LocalDateTime.now()) + "-" + counter.incrementAndGet() + "-" + safeTo + ".html";
            Path file = dir.resolve(name);
            String doc = "<!-- To: " + to + " | Subject: " + subject + " -->\n" + html;
            Files.writeString(file, doc, StandardCharsets.UTF_8);
            System.out.println("[outbox] " + subject + "  ->  " + to + "   (" + file + ")");
        } catch (IOException e) {
            throw new RuntimeException("Could not write email to outbox: " + e.getMessage(), e);
        }
    }

    private String wrapHtmlContent(String content) {
        return """
        <html>
        <body style="margin: 0; padding: 0; background-color: #0f172a; font-family: 'Segoe UI', Arial, sans-serif;">
            <table width="100%" border="0" cellspacing="0" cellpadding="0" style="background-color: #0f172a; padding: 20px;">
                <tr>
                    <td align="center">
                        <table width="600" border="0" cellspacing="0" cellpadding="0" style="background-color: #ffffff; border-radius: 24px; overflow: hidden; box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1);">
                            <tr>
                                <td style="background-color: #1e293b; padding: 40px; text-align: center; border-bottom: 4px solid #10b981;">
                                    <h1 style="color: #ffffff; margin: 0; font-size: 24px; text-transform: uppercase; letter-spacing: 3px;">Almendral Tennis Club</h1>
                                    <p style="color: #10b981; margin: 5px 0 0 0; font-weight: bold; font-size: 12px; letter-spacing: 1px;">SOCIOS 2026</p>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding: 40px; background-color: #ffffff; color: #334155; font-size: 16px; line-height: 1.8;">
                                    <div style="margin-bottom: 20px;">
                                        """ + content.replace("\n", "<br>") + """
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td style="padding: 30px; background-color: #f8fafc; text-align: center; border-top: 1px solid #e2e8f0;">
                                    <p style="color: #64748b; font-size: 11px; margin: 0; text-transform: uppercase; letter-spacing: 1px;">
                                        Club de Tenis Almendral &copy; 2026
                                    </p>
                                    <p style="color: #94a3b8; font-size: 10px; margin: 10px 0 0 0;">
                                        Comunicado interno para los socios del club.
                                    </p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            </table>
        </body>
        </html>
        """;
    }
}
