// Archivo: src/main/java/Tennis_ERP/TennisErp/service/ConvocatoriaServiceImpl.java
package Tennis_ERP.TennisErp.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import Tennis_ERP.TennisErp.domain.Convocatoria;
import Tennis_ERP.TennisErp.repository.ConvocatoriaRepository;
import jakarta.mail.MessagingException;

@Service
public class ConvocatoriaServiceImpl implements ConvocatoriaService {

    @Autowired
    private ConvocatoriaRepository convocatoriaRepository;

    @Autowired
    private EmailService emailService;

    @Override
    @Transactional
    public Convocatoria crearYEnviarConvocatoria(Convocatoria convocatoria) {
        // 1. Guardamos el registro en la BD
        Convocatoria guardada = convocatoriaRepository.save(convocatoria);

        // 2. Disparamos los correos usando el EmailService existente
        try {
            if (guardada.getCategoria() != null) {
                // Si tiene categoría, enviamos por categoría
                emailService.sendMailByCategory(
                    guardada.getCategoria().getId(), 
                    guardada.getAsunto(), 
                    guardada.getMensaje()
                );
            } else {
                // Si no tiene, es masivo a todos
                emailService.sendMassiveEmail(
                    guardada.getAsunto(), 
                    guardada.getMensaje()
                );
            }
        } catch (MessagingException e) {
            // Logueamos el error pero permitimos que la convocatoria se guarde
            System.err.println("Error al enviar correos de convocatoria: " + e.getMessage());
        }

        return guardada;
    }

    @Override
    public List<Convocatoria> listarTodas() {
        return convocatoriaRepository.findAll();
    }
}