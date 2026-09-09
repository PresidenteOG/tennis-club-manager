package Tennis_ERP.TennisErp.springsecurity;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Obtenemos la ruta absoluta del directorio de trabajo actual (raíz del proyecto)
        String rootPath = System.getProperty("user.dir");

        // Construimos la ruta hacia la carpeta 'uploads'.
        // Usamos File.separator para que funcione tanto en Windows (\) como en Linux/Mac (/)
        String uploadDir = rootPath + File.separator + "uploads" + File.separator;

        // Mapeamos la URL /images/** para que busque los archivos en esa carpeta física
        // El prefijo "file:" es OBLIGATORIO para indicarle a Spring que es una ruta del sistema de archivos
        registry.addResourceHandler("/images/**", "/uploads/**")
                .addResourceLocations("file:" + uploadDir);
    }
}