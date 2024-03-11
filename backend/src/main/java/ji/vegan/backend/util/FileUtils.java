package ji.vegan.backend.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Slf4j
public class FileUtils {
    public static String getFileName(String path) {
        return Paths.get(path).getFileName().toString();
    }

    public static String getOriginalFilename(MultipartFile multipartFile) {
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        return getFileName(fileName);
    }

    public static Path makePath(String first, String... more) {
        return Paths.get(first, more);
    }

    public static String getFilePath(Path filePath) {
        return filePath.toAbsolutePath().toString();
    }

    public static void createDirectories(Path uploadPath) {
        if (!Files.isDirectory(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (Exception e) {
                log.error("fail to createDirectory", e);
            }
        }
    }
}
