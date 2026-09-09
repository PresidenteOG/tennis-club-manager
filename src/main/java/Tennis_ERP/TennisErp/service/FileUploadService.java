package Tennis_ERP.TennisErp.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileUploadService {

    private final Path uploadPath;

    public FileUploadService() {
        // Ruta absoluta a la carpeta "uploads" EXTERNA (al lado del archivo .jar)
        String rootPath = System.getProperty("user.dir");
        this.uploadPath = Paths.get(rootPath + File.separator + "uploads").toAbsolutePath().normalize();
        
        try {
            // Crear el directorio si no existe
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads en: " + this.uploadPath, e);
        }
    }

    public String storeFile(MultipartFile file, Long userId) throws IOException {
        if (file.isEmpty()) {
            return null;
        }

        // Validar extensión
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);

        if (!isValidImageExtension(extension)) {
            throw new IllegalArgumentException("Formato de imagen no válido. Use JPG, PNG, GIF o WEBP.");
        }

        // Generar nombre único
        String uniqueFilename = userId + "_" + UUID.randomUUID() + extension;

        // Guardar solo en la carpeta externa
        Path targetLocation = this.uploadPath.resolve(uniqueFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetLocation, StandardCopyOption.REPLACE_EXISTING);
        }

        return uniqueFilename;
    }

    public void deleteFile(String filename) throws IOException {
        if (filename == null || filename.isEmpty()) {
            return;
        }

        Path targetFile = this.uploadPath.resolve(filename);
        Files.deleteIfExists(targetFile);
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }

    private boolean isValidImageExtension(String extension) {
        String ext = extension.toLowerCase();
        return ext.equals(".jpg") || ext.equals(".jpeg") ||
               ext.equals(".png") || ext.equals(".gif") || ext.equals(".webp");
    }

    public Path getUploadPath() {
        return uploadPath;
    }
}