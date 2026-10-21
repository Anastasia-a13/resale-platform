package ru.skypro.homework.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import ru.skypro.homework.exception.ImageProcessingException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImageServiceTest {

    private ImageService imageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        imageService = new ImageService();

        var imagesField = ImageService.class.getDeclaredField("imagesDir");
        imagesField.setAccessible(true);
        imagesField.set(imageService, tempDir.resolve("images").toString());

        var avatarsField = ImageService.class.getDeclaredField("avatarsDir");
        avatarsField.setAccessible(true);
        avatarsField.set(imageService, tempDir.resolve("avatars").toString());
    }

    private MockMultipartFile createImage(String name, String contentType, byte[] content) {
        return new MockMultipartFile("image", name, contentType, content);
    }

    @Test
    void validateImageValidType() {
        MockMultipartFile jpeg = createImage("photo.jpg", "image/jpeg", new byte[]{1});
        MockMultipartFile png = createImage("photo.png", "image/png", new byte[]{1});
        MockMultipartFile gif = createImage("photo.gif", "image/gif", new byte[]{1});

        assertDoesNotThrow(() -> {
            imageService.saveImage(jpeg);
            imageService.saveImage(png);
            imageService.saveImage(gif);
        });
    }

    @Test
    void validateImageInvalidType() {
        MockMultipartFile pdf = createImage("doc.pdf", "application/pdf", new byte[]{1});

        assertThrows(jakarta.validation.ValidationException.class, () -> imageService.saveImage(pdf));
    }

    @Test
    void validateImageEmpty() {
        MockMultipartFile empty = createImage("empty.jpg", "image/jpeg", new byte[0]);

        assertThrows(jakarta.validation.ValidationException.class, () -> imageService.saveImage(empty));
    }

    @Test
    void validateImageNullContentType() {
        MockMultipartFile noType = new MockMultipartFile("image", "file.jpg", null, new byte[]{1});

        assertThrows(jakarta.validation.ValidationException.class, () -> imageService.saveImage(noType));
    }

    @Test
    void saveImageCreatesFile() throws IOException {
        MockMultipartFile image = createImage("test.jpg", "image/jpeg", new byte[]{1, 2, 3});

        String fileName = imageService.saveImage(image);

        assertNotNull(fileName);
        assertTrue(fileName.endsWith(".jpg"));
        Path saved = tempDir.resolve("images").resolve(fileName);
        assertTrue(Files.exists(saved));
        assertArrayEquals(new byte[]{1, 2, 3}, Files.readAllBytes(saved));
    }

    @Test
    void saveAvatarCreatesFile() throws IOException {
        MockMultipartFile avatar = createImage("avatar.png", "image/png", new byte[]{4, 5});

        String fileName = imageService.saveAvatar(avatar);

        assertNotNull(fileName);
        assertTrue(fileName.endsWith(".png"));
        assertTrue(Files.exists(tempDir.resolve("avatars").resolve(fileName)));
    }

    @Test
    void getImageSuccess() throws IOException {
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        Files.write(imagesDir.resolve("test.jpg"), new byte[]{10, 20});

        byte[] result = imageService.getImage("test.jpg");

        assertArrayEquals(new byte[]{10, 20}, result);
    }

    @Test
    void getImageNotFound() {
        assertThrows(ImageProcessingException.class, () -> imageService.getImage("nonexistent.jpg"));
    }

    @Test
    void getAvatarSuccess() throws IOException {
        Path avatarsDir = tempDir.resolve("avatars");
        Files.createDirectories(avatarsDir);
        Files.write(avatarsDir.resolve("avatar.jpg"), new byte[]{30, 40});

        byte[] result = imageService.getAvatar("avatar.jpg");

        assertArrayEquals(new byte[]{30, 40}, result);
    }

    @Test
    void updateImageReplacesOldFile() throws IOException {
        Path imagesDir = tempDir.resolve("images");
        Files.createDirectories(imagesDir);
        Files.write(imagesDir.resolve("old.jpg"), new byte[]{1});

        MockMultipartFile newImage = createImage("new.jpg", "image/jpeg", new byte[]{7, 8, 9});

        ImageService.ImageResult result = imageService.updateImage("old.jpg", newImage);

        assertFalse(Files.exists(imagesDir.resolve("old.jpg")));
        assertTrue(Files.exists(imagesDir.resolve(result.fileName())));
        assertArrayEquals(new byte[]{7, 8, 9}, result.bytes());
    }

    @Test
    void updateAvatarReplacesOldFile() throws IOException {
        Path avatarsDir = tempDir.resolve("avatars");
        Files.createDirectories(avatarsDir);
        Files.write(avatarsDir.resolve("old_avatar.jpg"), new byte[]{1});

        MockMultipartFile newAvatar = createImage("new.jpg", "image/jpeg", new byte[]{5, 6});

        ImageService.ImageResult result = imageService.updateAvatar("old_avatar.jpg", newAvatar);

        assertFalse(Files.exists(avatarsDir.resolve("old_avatar.jpg")));
        assertTrue(Files.exists(avatarsDir.resolve(result.fileName())));
    }
}
