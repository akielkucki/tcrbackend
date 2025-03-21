package co.jrstudios;

import co.jrstudios.config.AppConfig;
import co.jrstudios.controllers.AuthController;
import co.jrstudios.controllers.FileController;
import co.jrstudios.controllers.ProjectController;
import co.jrstudios.db.DatabaseManager;
import co.jrstudios.middleware.Middleware;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.FileRenderer;
import io.javalin.rendering.template.JavalinJte;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static Javalin app;

    public static void main(String[] args) {
        initializeApplication();
        Middleware.apply(app);
        setupRoutes();
        setupDatabaseCleanup();
    }

    private static void initializeApplication() {
        TemplateEngine engine = TemplateEngine.create(
                new DirectoryCodeResolver(Path.of("src/main/resources/jte")),
                ContentType.Html
        );
        FileRenderer jteRenderer = new JavalinJte(engine);
        File uploadDir = new File("/uploads");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            File metadata = new File(uploadDir, "metadata.json");
            try {
                OutputStream fos = Files.newOutputStream(metadata.toPath());
                StringBuilder builder = new StringBuilder();
                builder.append("{ \"workdir\":\"").append(uploadDir.getAbsolutePath()).append("\" }");
                fos.write(builder.toString().getBytes());
                fos.close();
            } catch (IOException e) {
                log.warn("Could not create metadata.json because of {}", e.getMessage());
            }
        }

        app = Javalin.create(config -> {
            config.fileRenderer(jteRenderer);
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.directory = "/uploads";  // Use a relative path unless you intend an absolute one
                staticFileConfig.hostedPath = "/uploads";   // URL prefix
                staticFileConfig.location = Location.CLASSPATH;
                // Disable caching by setting appropriate headers
                staticFileConfig.headers.put("Cache-Control", "no-cache, no-store, must-revalidate");
                staticFileConfig.headers.put("Pragma", "no-cache");
                staticFileConfig.headers.put("Expires", "0");
            });


        }).start("0.0.0.0",AppConfig.SERVER_PORT);

        try {
            DatabaseManager.getInstance().setupDatabase();
            // Load projects from the database into the repository
            DatabaseManager.getInstance().fetchAllProjects();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void setupRoutes() {
        // Render home page showing projects
        FileController.getInstance().loadAllImages();
        app.get("/", ProjectController.getInstance()::renderHomePage);
        app.get("/images/{fileName}", FileController.getInstance()::handleFileRoute);
        // Authentication routes
        app.get("/login", AuthController::renderLoginPage);
        app.get("/logout", AuthController::handleLogoutGet);
        app.post("/login", AuthController::handleLoginPost);

        // API routes for projects
        app.get("/api/projects", ProjectController.getInstance()::getProjects);
        app.post("/api/projects", ProjectController.getInstance()::createProject);
        app.put("/api/projects", ProjectController.getInstance()::updateProject);
        app.delete("/api/projects/{id}", ProjectController.getInstance()::deleteProject);
    }

    private static void setupDatabaseCleanup() {
        app.after(ctx -> {
            try {
                DatabaseManager.getInstance().close();
            } catch (Exception e) {
                log.warn("Error closing DatabaseManager", e);
            }
        });
    }
}
