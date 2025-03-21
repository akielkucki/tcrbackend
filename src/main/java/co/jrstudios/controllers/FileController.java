package co.jrstudios.controllers;

import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class FileController {
    private static final Map<String, byte[]> imageCache = new ConcurrentHashMap<>();
    private static final Logger log = LoggerFactory.getLogger(FileController.class);
    private static final FileController instance = new FileController();

    public static FileController getInstance() {
        return instance;
    }

    // Load all images from the uploads directory into the cache
    public void loadAllImages() {
        imageCache.clear();
        File directory = new File("src/main/resources/uploads/");
        // Ensure the directory exists and is indeed a directory
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    updateCache(f.getName(), f);
                    log.info("Uploading file: " + f.getAbsolutePath());
                }
            }
        } else {
            log.warn("Uploads directory does not exist or is not a directory: {}", directory.getAbsolutePath());
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
        if (imageData == null) {
            throw new NotFoundResponse("File not found: " + fileName);
        }
        // Determine the content type based on the file extension
        File file = new File("src/main/resources/uploads/", fileName);
        String contentType;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            log.warn("Could not determine content type for file: {}", fileName, e);
            contentType = "application/octet-stream";
        }
        ctx.contentType(contentType);
        ctx.result(imageData);
    }
}
