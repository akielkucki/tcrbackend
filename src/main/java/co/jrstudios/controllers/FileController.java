package co.jrstudios.controllers;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class FileController {
    private static final Map<String, byte[]> imageCache = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private static final FileController instance = new FileController();

    // Update this path to match Docker container structure
    private static final String UPLOADS_DIR = "uploads";

    public static FileController getInstance() {
        return instance;
    }

    // Load all images from the uploads directory into the cache
    public void loadAllImages() {
        imageCache.clear();

        // First try to load from external directory
        File directory = new File(UPLOADS_DIR);
        if (!directory.exists()) {
            log.info("Creating uploads directory at: {}", directory.getAbsolutePath());
            if (!directory.mkdirs()) {
                log.warn("Failed to create uploads directory: {}", directory.getAbsolutePath());
            }
        }

        if (directory.exists() && directory.isDirectory()) {
            log.info("Loading images from external directory: {}", directory.getAbsolutePath());
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    updateCache(f.getName(), f);
                    log.info("Loaded file from external dir: {}", f.getName());
                }
            }
        } else {
            log.warn("External uploads directory does not exist or is not a directory: {}", directory.getAbsolutePath());
        }

        // Then try to load from classpath resources
        try {
            log.info("Attempting to load images from classpath resources");
            InputStream resourceListing = getClass().getResourceAsStream("/uploads");
            if (resourceListing != null) {
                log.info("Found uploads directory in classpath");
                // List resources in the directory - this is just a placeholder
                // In reality, you may need a different approach to list resources in a JAR
                // Consider using ClassPathScanner or similar utility
            } else {
                log.warn("No uploads directory found in classpath resources");
            }
        } catch (Exception e) {
            log.warn("Error loading from classpath resources", e);
        }
    }

    // Update cache for a single image file
    public void updateCache(String fileName, File imageFile) {
        try {
            byte[] data = Files.readAllBytes(imageFile.toPath());
            imageCache.put(fileName, data);
        } catch (IOException e) {
            log.warn("Failed to read image file: {}", fileName, e);
        }
    }

    // Retrieve the image data from cache
    public byte[] getImage(String fileName) {
        return imageCache.get(fileName);
    }

    // Remove image data from cache
    public void removeImage(String fileName) {
        imageCache.remove(fileName);
    }

    // Handle the file route to serve cached images
    public void handleFileRoute(Context ctx) {
        ctx.header("Access-Control-Allow-Origin", "*");
        String fileName = ctx.pathParam("fileName");
        byte[] imageData = getImage(fileName);

        // If not in cache, try to load it on demand
        if (imageData == null) {
            // First try external file
            File file = new File(UPLOADS_DIR, fileName);
            if (file.exists() && file.isFile()) {
                try {
                    imageData = Files.readAllBytes(file.toPath());
                    imageCache.put(fileName, imageData);
                    log.info("Loaded file on demand from external dir: {}", fileName);
                } catch (IOException e) {
                    log.warn("Failed to read image file: {}", fileName, e);
                }
            }

            // If still null, try classpath resources
            if (imageData == null) {
                try {
                    // Try loading from classpath
                    InputStream resourceStream = getClass().getResourceAsStream("/uploads/" + fileName);
                    if (resourceStream != null) {
                        imageData = resourceStream.readAllBytes();
                        imageCache.put(fileName, imageData);
                        log.info("Loaded file on demand from classpath: {}", fileName);
                    }
                } catch (IOException e) {
                    log.warn("Failed to read image from classpath: {}", fileName, e);
                }
            }
        }

        if (imageData == null) {
            throw new NotFoundResponse("File not found: " + fileName);
        }

        // Determine content type
        String contentType = determineContentType(fileName);
        ctx.contentType(contentType);
        ctx.result(imageData);
    }

    // Helper method to determine content type based on file extension
    private String determineContentType(String fileName) {
        if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (fileName.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else if (fileName.toLowerCase().endsWith(".gif")) {
            return "image/gif";
        } else if (fileName.toLowerCase().endsWith(".svg")) {
            return "image/svg+xml";
        } else if (fileName.toLowerCase().endsWith(".webp")) {
            return "image/webp";
        } else if (fileName.toLowerCase().endsWith(".bmp")) {
            return "image/bmp";
        } else if (fileName.toLowerCase().endsWith(".ico")) {
            return "image/x-icon";
        } else {
            return "application/octet-stream";
        }
    }
}