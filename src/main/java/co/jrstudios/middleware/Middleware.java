package co.jrstudios.middleware;

import co.jrstudios.Main;
import co.jrstudios.auth.JwtUtil;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.HttpStatus;
import org.eclipse.jetty.http.HttpMethod;

public class Middleware {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private final Javalin app;
    public Middleware(Javalin app) {
        this.app = app;
    }
    public void setupLogging() {
        app.before(ctx -> {
            logger.info("Incoming request: {} {}", ctx.method(), ctx.path());
            ctx.header("X-Processed-By", "JRStudios");
        });
    }
    public void validatePanelLogin(Context ctx) {
        String token = ctx.cookie("token");
        if (token == null || !JwtUtil.validateAuthorizationRequest(token)) {
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.redirect("/login?error=request_unauthorized");
        } else {
            ctx.status(200);
        }
    }
    /**
     * Validate API requests with JWT authentication
     */
    public void validateApiRequest(Context ctx) {
        // Skip authentication for GET /api/projects
        if (ctx.path().equalsIgnoreCase("/api/projects") &&
                ctx.req().getMethod().startsWith(HttpMethod.GET.name())) {
            ctx.status(200);
            return;
        }

        String authHeader = ctx.header("Authorization");
        if (authHeader == null) {
            ctx.contentType("application/json");
            throw new io.javalin.http.UnauthorizedResponse("Missing Authorization header");
        }

        String token = authHeader.substring("Bearer ".length());
        if (!JwtUtil.validateAuthorizationRequest(token)) {
            throw new io.javalin.http.UnauthorizedResponse("Unauthorized");
        }

        ctx.status(HttpStatus.OK);
    }
}
