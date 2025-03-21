package co.jrstudios.middleware;

import co.jrstudios.auth.JwtUtil;
import io.javalin.Javalin;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Middleware {
    private static final Logger logger = LoggerFactory.getLogger(Middleware.class);

    public static void apply(Javalin app) {
        // Global logging middleware
        app.before(ctx -> {
            ctx.header("Access-Control-Allow-Origin", "*");
            ctx.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            ctx.header("Access-Control-Allow-Headers", "Content-Type, Authorization");
            logger.info("Incoming request: {} {}", ctx.method(), ctx.path());
            ctx.header("X-Processed-By", "JRStudios");
        });

        // Validate panel login for the home page
        app.before("/", Middleware::validatePanelLogin);

        // Validate API requests (except GET /api/projects)
        app.before("/api/*", Middleware::validateApiRequest);
    }

    public static void validatePanelLogin(Context ctx) {
        String token = ctx.cookie("token");
        if (token == null || !JwtUtil.validateAuthorizationRequest(token)) {
            ctx.contentType("application/json");
            ctx.redirect("/login?error=request_unauthorized");
        }
    }

    public static void validateApiRequest(Context ctx) {
        if (ctx.path().equalsIgnoreCase("/api/projects") &&
                ctx.req().getMethod().startsWith(HttpMethod.GET.name())) {
            return;
        }

        String authHeader = ctx.header("Authorization");
        if (authHeader == null) {
            ctx.contentType("application/json");
            throw new UnauthorizedResponse("Missing Authorization header");
        }

        String token = authHeader.substring("Bearer ".length());
        if (!JwtUtil.validateAuthorizationRequest(token)) {
            throw new UnauthorizedResponse("Unauthorized");
        }
    }
}
