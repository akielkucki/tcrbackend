package co.jrstudios;

import co.jrstudios.auth.JwtUtil;
import co.jrstudios.controllers.ProjectService;
import co.jrstudios.db.DatabaseManager;
import co.jrstudios.middleware.Middleware;
import co.jrstudios.models.LoginRequest;
import co.jrstudios.models.LoginResponse;
import co.jrstudios.models.Project;
import co.jrstudios.projects.ProjectsCRUD;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.DirectoryCodeResolver;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.rendering.FileRenderer;
import io.javalin.rendering.template.JavalinJte;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.*;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static Javalin app;

    public static void main(String[] args) {
        // Initialize application
        initializeApplication();

        // Set up middleware
        setupMiddleware();

        // Define routes
        setupRoutes();

        // Set up database cleanup
        setupDatabaseCleanup();
    }

    /**
     * Initialize the Javalin application with JTE template engine
     */
    private static void initializeApplication() {
        TemplateEngine engine = TemplateEngine.create(
                new DirectoryCodeResolver(Path.of("src/main/resources/jte")),
                ContentType.Html
        );

        FileRenderer jteRenderer = new JavalinJte(engine);

        app = Javalin.create(config -> {
            config.fileRenderer(jteRenderer);
        }).start(5050);

        try {
            DatabaseManager.getInstance().setupDatabase();
            DatabaseManager.getInstance().fetchAllProjects();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set up middleware for logging and authentication
     */
    private static void setupMiddleware() {
        // Request logging middleware
        Middleware middleware = new Middleware(app);
        app.before("/",middleware::validatePanelLogin);

        // API authentication middleware
        app.before("/api/*", middleware::validateApiRequest);
    }

    /**
     * Set up application routes
     */
    private static void setupRoutes() {
        app.get("/", Main::handleHomePage);
        app.get("/login", Main::handleLoginPage);

        app.post("/login", Main::handleLoginPost);

        // API routes
        ProjectService projectService = new ProjectService();
        app.get("/api/projects", projectService::getProjects);
        app.post("/api/projects", projectService::createProject);
        app.put("/api/projects", projectService::updateProject);
        app.delete("/api/projects/{id}", projectService::deleteProject);
    }

    /**
     * Set up cleanup actions for after request handling
     */
    private static void setupDatabaseCleanup() {
        app.after(ctx -> {
            DatabaseManager.getInstance().close();
        });
    }

    /**
     * Handle the home page request
     */
    private static void handleHomePage(Context ctx) {
        ctx.contentType("application/json");
        var model = new HashMap<String, Object>();

        List<Project> projects = ProjectsCRUD.getInstance().getProjects();
        model.put("projects", projects);
        ctx.render("index.jte", model);
    }

    /**
     * Handle login requests
     */
    private static void handleLoginPost(Context ctx) {
        if (!ctx.isJson()) {
            ctx.status(400).json(new LoginResponse("null", "null", "Invalid Request Format"));
            return;
        }

        LoginRequest loginRequest = ctx.bodyAsClass(LoginRequest.class);
        String username = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        if (username == null || password == null ||
                (!username.equals("test@example.com") || !password.equals("test123"))) {
            logger.error("Invalid Login Request {}", loginRequest);
            ctx.status(401).json(new LoginResponse("null", "null", "Unauthorized"));
            return;
        }

        // Generate JWT Token
        var token = JwtUtil.generateToken(username);
        ctx.cookie("token", token, 864000);
        ctx.status(200).json(new LoginResponse(token, username, "success"));
    }
    private static void handleLoginPage(Context ctx) {
        Map<String, Object> model = new HashMap<>();
        model.put("error", Optional.of("request_unauthorized"));
        model.put("error", Optional.of("invalid_credentials"));
        model.put("error", Optional.empty());

        ctx.render("login.jte", model);
    }




    /**
     * Generate a secure key
     * Utility method for creating secure keys
     */
    public static String generateSecureKey() {
        UUID rand = UUID.randomUUID();
        Random random = new Random();
        Base64.Encoder encoder = Base64.getEncoder();
        String b64 = encoder.encodeToString(rand.toString().getBytes(StandardCharsets.UTF_8));
        char[] chars = b64.toCharArray();
        char[] key = new char[b64.length()];

        for (int i = 0; i < chars.length; i++) {
            key[i] = chars[random.nextInt(chars.length)];
        }

        StringBuilder secureKey = new StringBuilder();
        for (char c : key) {
            secureKey.append(c);
        }

        return secureKey.toString();
    }
}