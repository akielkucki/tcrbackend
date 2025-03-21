package co.jrstudios.controllers;

import co.jrstudios.auth.JwtUtil;
import co.jrstudios.models.LoginRequest;
import co.jrstudios.models.LoginResponse;
import io.javalin.http.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public static void renderLoginPage(Context ctx) {
        // Render the login page; you can pass a model if needed
        ctx.render("login.jte", new java.util.HashMap<>());
    }

    public static void handleLoginPost(Context ctx) {
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

        // Generate JWT Token and set cookie
        String token = JwtUtil.generateToken(username);
        ctx.cookie("token", token, 864000);
        ctx.status(200).json(new LoginResponse(token, username, "success"));
    }
    public static void handleLogoutGet(Context ctx) {
        ctx.cookie("token", "", 864000);
        ctx.status(200).json(new LoginResponse("token", "null", "success"));
        ctx.redirect("/login");
    }
}
