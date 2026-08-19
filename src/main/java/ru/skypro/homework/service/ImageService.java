package ru.skypro.homework.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skypro.homework.exception.ImageProcessingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Сервис для работы с изображениями.
 * Обеспечивает сохранение и обновление изображений объявлений и аватаров пользователей
 * в файловой системе. Принимает только изображения форматов JPEG, PNG или GIF.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {

    @Value("${images.dir.path:uploads/images}")
    private String imagesDir;

    @Value("${avatars.dir.path:uploads/avatars}")
    private String avatarsDir;

    private static final List<String> ALLOWED_TYPES = List.of(
            "image/jpeg", "image/png", "image/gif");

    public String saveImage(MultipartFile file) {
        validateImage(file);
        String fileName = generateFileName(file);
        writeToFile(Path.of(imagesDir, fileName), file);
        return fileName;
    }

    public String saveAvatar(MultipartFile file) {
        validateImage(file);
        String fileName = generateFileName(file);
        writeToFile(Path.of(avatarsDir, fileName), file);
        return fileName;
    }

    public byte[] getImage(String fileName) {
        return readFromDisk(imagesDir, fileName);
    }

    public byte[] getAvatar(String fileName) {
        return readFromDisk(avatarsDir, fileName);
    }

    public record ImageResult(String fileName, byte[] bytes) {}

    public ImageResult updateImage(String oldFileName, MultipartFile file) {
        deleteFile(imagesDir, oldFileName);
        String fileName = generateFileName(file);
        writeToFile(Path.of(imagesDir, fileName), file);
        return new ImageResult(fileName, readFromDisk(imagesDir, fileName));
    }

    public ImageResult updateAvatar(String oldFileName, MultipartFile file) {
        deleteFile(avatarsDir, oldFileName);
        String fileName = generateFileName(file);
        writeToFile(Path.of(avatarsDir, fileName), file);
        return new ImageResult(fileName, readFromDisk(avatarsDir, fileName));
    }

    private void validateImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new ValidationException("Поддерживаются только JPEG, PNG, GIF");
        }
        if (file.isEmpty()) {
            throw new ValidationException("Файл пустой");
        }
    }

    private String generateFileName(MultipartFile file) {
        String ext = getExtension(file.getOriginalFilename());
        return UUID.randomUUID() + (ext.isEmpty() ? "" : "." + ext);
    }

    private String getExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private void writeToFile(Path path, MultipartFile file) {
        try {
            Files.createDirectories(path.getParent());
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            throw new ImageProcessingException("Ошибка сохранения файла", e);
        }
    }

    private byte[] readFromDisk(String dir, String fileName) {
        try {
            return Files.readAllBytes(Path.of(dir, fileName));
        } catch (IOException e) {
            throw new ImageProcessingException("Изображение не найдено: " + fileName);
        }
    }

    private void deleteFile(String dir, String fileName) {
        if (fileName == null || fileName.isBlank()) return;
        try {
            Files.deleteIfExists(Path.of(dir, fileName));
        } catch (IOException ignored) {
        }
    }
}
