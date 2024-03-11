package ji.vegan.backend.service;

import ji.vegan.backend.config.CustomConfig;
import ji.vegan.backend.util.FileUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {
    private final CustomConfig customConfig;

    public String fileUpload(String directoryName, MultipartFile multipartFile) {
        String filename = "";

        try {
            String current = customConfig.getFileRootPath();
            Path uploadPath = FileUtils.makePath(current, directoryName);
            FileUtils.createDirectories(uploadPath);

            Path path = uploadFile(uploadPath, multipartFile);
            filename = path.getFileName().toString();
        } catch (Exception e) {
            log.error("file upload error", e);

            deleteFiles(directoryName, List.of(filename));
            throw new RuntimeException("file upload error");
        }

        return filename;
    }

    public String fileUpload(String directoryName, ClassPathResource classPathResource) {
        String filename = "";

        try {
            String current = customConfig.getFileRootPath();
            Path uploadPath = FileUtils.makePath(current, directoryName);
            FileUtils.createDirectories(uploadPath);

            Path path = uploadFile(uploadPath, classPathResource);
            filename = path.getFileName().toString();
        } catch (Exception e) {
            log.error("file upload error", e);

            deleteFiles(directoryName, List.of(filename));
            throw new RuntimeException("file upload error");
        }

        return filename;
    }

    private Path uploadFile(Path uploadPath, ClassPathResource classPathResource) {
        Path destinationFile = randomName(uploadPath, classPathResource.getFilename());

        try {
            try (InputStream inputStream = classPathResource.getInputStream()) {
                Files.copy(inputStream, destinationFile);
            }

            log.info("File store success. {}", destinationFile);
            return destinationFile;

        } catch (IOException e) {
            log.error("File store error.", e);
            throw new RuntimeException("File store error.");
        }
    }

    private Path uploadFile(Path uploadPath, MultipartFile multipartFile) {
        Path destinationFile = randomName(uploadPath, multipartFile.getOriginalFilename());

        try {
            if (multipartFile.isEmpty() || multipartFile.getOriginalFilename() == null) {
                log.error("Failed to store empty file.");
                throw new RuntimeException("Failed to store empty file.");
            }

            try (InputStream inputStream = multipartFile.getInputStream()) {
                Files.copy(inputStream, destinationFile);
            }

            log.info("File store success. {}", destinationFile);
            return destinationFile;

        } catch (IOException e) {
            log.error("File store error.", e);
            throw new RuntimeException("File store error.");
        }
    }

    public void deleteFiles(String directoryName, Collection<String> filenames) {
        for (String filename : filenames) {
            Path path = Paths.get(filename);

            Path uploadPath = FileUtils.makePath(customConfig.getFileRootPath(), directoryName);
            Path targetPath = uploadPath.resolve(path).normalize().toAbsolutePath();
            Path thumbnailPath = FileUtils.makePath(customConfig.getFileRootPath(), directoryName, "thumbnail");
            Path targetThumbnailPath = thumbnailPath.resolve(path).normalize().toAbsolutePath();

            try {
                Files.deleteIfExists(targetPath);
                Files.deleteIfExists(targetThumbnailPath);

                log.info("File delete success. {}", targetPath);
            } catch (IOException e) {
                log.error("File delete error.", e);
            }
        }
    }

    private Path randomName(Path uploadPath, String filename) {
        String baseName = FilenameUtils.getBaseName(filename);
        String extension = FilenameUtils.getExtension(filename);

        String newFilename = baseName + "_" + RandomStringUtils.randomAlphanumeric(4) + "." + extension;
        return uploadPath.resolve(Paths.get(newFilename)).normalize().toAbsolutePath();
    }
}
