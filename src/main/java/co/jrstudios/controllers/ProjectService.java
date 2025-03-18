package co.jrstudios.controllers;

import co.jrstudios.Main;
import co.jrstudios.db.DatabaseManager;
import co.jrstudios.models.Project;
import co.jrstudios.models.SuccessResponse;
import co.jrstudios.projects.ProjectsCRUD;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.util.List;

public class ProjectService {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    /**
     * Get all projects
     */
    public void getProjects(Context ctx) {
        List<Project> projects = ProjectsCRUD.getInstance().getProjects();
        ctx.status(200);
        ctx.json(projects);
    }
    /**
     * Create a new project
     */
    public void createProject(Context ctx) {
        logger.info("Request body: {}", ctx.body());

        if (!ctx.isJson()) {
            ctx.status(401);
            ctx.json(new SuccessResponse("Unauthorized, invalid json", 401));
            return;
        }

        try {
            Project project = ctx.bodyAsClass(Project.class);

            // Save project
            ProjectsCRUD.getInstance().addProject(project);
            DatabaseManager.getInstance().createProject(project);

            logger.info("Successfully processed project {} {} {}",
                    project.getId(), project.getTitle(), project.getDescription());

            ctx.status(200);
            ctx.json(project);
        } catch (Exception e) {
            logger.error("Error processing project", e);
            ctx.status(401);
            ctx.json(new SuccessResponse("Unauthorized, invalid json", 401));
        }
    }
    public void updateProject(Context ctx) {
        logger.info("Request body: {}", ctx.body());

        if (!ctx.isJson()) {
            ctx.status(401);
            ctx.json(new SuccessResponse("Unauthorized, invalid json", 401));
            return;
        }

        try {
            Project project = ctx.bodyAsClass(Project.class);
            if (ProjectsCRUD.getInstance().getProjectById(project.getId()) != null) {
                DatabaseManager.getInstance().updateProject(project);
                ProjectsCRUD.getInstance().updateProjectById(project.getId(), project);
                logger.info("Successfully processed project {} {} {}",
                        project.getId(), project.getTitle(), project.getDescription());

                ctx.status(200);
                ctx.json(project);
            } else {
                throw new NotFoundResponse("Project does not exist in the database");
            }
        } catch (Exception e) {
            logger.error("Error processing project", e);
            ctx.status(401);
            ctx.json(new SuccessResponse("Unauthorized, invalid request "+e.getMessage(), 401));
        }
    }
    public void deleteProject(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            logger.info("Deleting project with ID: {}", id);

            // Check if project exists before deleting
            if (ProjectsCRUD.getInstance().getProjectById(id) == null) {
                ctx.status(404);
                ctx.json(new SuccessResponse("Project not found", 404));
                return;
            }

            ProjectsCRUD.getInstance().removeProject(id);
            DatabaseManager.getInstance().deleteProject(id);

            // Return success response
            ctx.status(200);
            ctx.json(new SuccessResponse("Project deleted successfully", 200));
        } catch (NumberFormatException e) {
            ctx.status(400);
            ctx.json(new SuccessResponse("Invalid project ID format", 400));
        } catch (Exception e) {
            logger.error("Error deleting project", e);
            ctx.status(500);
            ctx.json(new SuccessResponse("Error deleting project: " + e.getMessage(), 500));
        }
    }
}
