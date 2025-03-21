package co.jrstudios.controllers;

import co.jrstudios.db.DatabaseManager;
import co.jrstudios.models.Project;
import co.jrstudios.models.SuccessResponse;
import co.jrstudios.repositories.ProjectRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import io.javalin.http.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class ProjectController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);
    private static ProjectController INSTANCE = new ProjectController();
    public static ProjectController getInstance() {
        return INSTANCE;
    }
    public void renderHomePage(Context ctx) {
        ctx.contentType("application/json");
        var model = new java.util.HashMap<String, Object>();
        List<Project> projects = ProjectRepository.getInstance().getProjects();
        model.put("projects", projects);
        ctx.render("index.jte", model);
    }

    public void getProjects(Context ctx) {
        List<Project> projects = ProjectRepository.getInstance().getProjects();
        ctx.status(200).json(projects);
    }

    public void createProject(Context ctx) throws Exception {
        // Process file uploads using the helper method
        List<UploadedFile> uploadedFiles = ctx.uploadedFiles("image");
        List<String> imagePaths = saveUploadedImages(uploadedFiles);

        // Set project fields and other processing...
        Project project = new Project();
        String idStr = ctx.formParam("id");
        if (idStr != null && !idStr.isEmpty()) {
            project.setId(Integer.parseInt(idStr));
        }
        project.setTitle(ctx.formParam("title"));
        project.setDescription(ctx.formParam("description"));
        project.setLocation(ctx.formParam("location"));

        String tagsJson = ctx.formParam("tags");
        if (tagsJson != null && !tagsJson.isEmpty()) {
            try {
                String[] tagsArray = new com.fasterxml.jackson.databind.ObjectMapper().readValue(tagsJson, String[].class);
                project.setTags(tagsArray);
            } catch (Exception e) {
                logger.error("Error parsing tags JSON", e);
            }
        }

        project.setImagePaths(imagePaths.toArray(new String[0]));

        // Set server image paths from the file names
        String[] files = new String[uploadedFiles.size()];
        for (int i = 0; i < uploadedFiles.size(); i++) {
            files[i] = uploadedFiles.get(i).filename();
        }
        project.setServerImagePaths(files);
        logger.info("Set server file list to {}", Arrays.toString(files));

        ProjectRepository.getInstance().addProject(project);
        DatabaseManager.getInstance().createProject(project);

        ctx.status(200).json(project);
    }



    public void updateProject(Context ctx) {
        logger.info("Updating project");

        try {
            // Process new file uploads using the helper method
            List<UploadedFile> uploadedFiles = ctx.uploadedFiles("image");
            List<String> newImagePaths = saveUploadedImages(uploadedFiles);

            // Get existing image paths from form data
            String existingImagesJson = ctx.formParam("existingImages");
            List<String> imagePaths = new ArrayList<>();
            if (existingImagesJson != null && !existingImagesJson.isEmpty()) {
                try {
                    String[] existingPaths = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(existingImagesJson, String[].class);
                    imagePaths.addAll(Arrays.asList(existingPaths));
                    logger.info("Keeping {} existing images", existingPaths.length);
                } catch (Exception e) {
                    logger.error("Error parsing existing images JSON", e);
                }
            }

            // Combine both existing and new image paths
            imagePaths.addAll(newImagePaths);

            // Create and update project with combined image paths
            Project project = new Project();
            String idStr = ctx.formParam("id");
            if (idStr != null && !idStr.isEmpty()) {
                project.setId(Integer.parseInt(idStr));
            } else {
                throw new IllegalArgumentException("Project ID is required for updates");
            }

            project.setTitle(ctx.formParam("title"));
            project.setDescription(ctx.formParam("description"));
            project.setLocation(ctx.formParam("location"));

            String tagsJson = ctx.formParam("tags");
            if (tagsJson != null && !tagsJson.isEmpty()) {
                try {
                    String[] tagsArray = new com.fasterxml.jackson.databind.ObjectMapper()
                            .readValue(tagsJson, String[].class);
                    project.setTags(tagsArray);
                } catch (Exception e) {
                    logger.error("Error parsing tags JSON", e);
                }
            }
            if (!imagePaths.isEmpty()) {
                project.setImagePaths(imagePaths.toArray(new String[0]));
            }
            String[] prevImagePaths = ProjectRepository.getInstance().getProjectById(project.getId()).getImagePaths();

            if (!imagePaths.isEmpty()) {
                project.setImagePaths(imagePaths.toArray(new String[0]));
            }

            Set<String> currentSet = new HashSet<>(imagePaths);
            List<String> deletedImages = new ArrayList<>();

            for (String prevImage : prevImagePaths) {
                if (!currentSet.contains(prevImage)) {
                    deletedImages.add(prevImage);
                }
            }

            for (String deletedImagePath : deletedImages) {
                File file = new File(deletedImagePath);
                if (file.exists()) {
                    if (file.delete()) {
                        logger.info("Deleted old image: {}", deletedImagePath);
                    } else {
                        logger.warn("Failed to delete image: {}", deletedImagePath);
                    }
                }

                String filename = new File(deletedImagePath).getName();
                FileController.getInstance().removeImage(filename);
            }
            String[] files = new String[uploadedFiles.size()];
            for (int i = 0; i < uploadedFiles.size(); i++) {
                files[i] = uploadedFiles.get(i).filename();
            }
            project.setServerImagePaths(files);
            logger.info("Set server file list to {}", Arrays.toString(files));


            if (ProjectRepository.getInstance().getProjectById(project.getId()) != null) {
                DatabaseManager.getInstance().updateProject(project);
                ProjectRepository.getInstance().updateProjectById(project.getId(), project);
                logger.info("Successfully processed project {} with {} images",
                        project.getId(), (project.getImagePaths() != null ? project.getImagePaths().length : 0));
                ctx.status(200).json(project);
            } else {
                throw new NotFoundResponse("Project does not exist in the database");
            }
        } catch (Exception e) {
            logger.error("Error processing project", e);
            ctx.status(401).json(new SuccessResponse("Unauthorized, invalid request " + e.getMessage(), 401));
        }
    }


    public void deleteProject(Context ctx) {
        try {
            int id = Integer.parseInt(ctx.pathParam("id"));
            logger.info("Deleting project with ID: {}", id);

            if (ProjectRepository.getInstance().getProjectById(id) == null) {
                ctx.status(404).json(new SuccessResponse("Project not found", 404));
                return;
            }
            Project project = ProjectRepository.getInstance().getProjectById(id);
            for (String imagePath : project.getImagePaths()) {
                File file = new File(imagePath);
                if (file.exists()) {
                    file.delete();
                }
            }
            ProjectRepository.getInstance().removeProject(id);
            DatabaseManager.getInstance().deleteProject(id);

            ctx.status(200).json(new SuccessResponse("Project deleted successfully", 200));
        } catch (NumberFormatException e) {
            ctx.status(400).json(new SuccessResponse("Invalid project ID format", 400));
        } catch (Exception e) {
            logger.error("Error deleting project", e);
            ctx.status(500).json(new SuccessResponse("Error deleting project: " + e.getMessage(), 500));
        }
    }
    private List<String> saveUploadedImages(List<UploadedFile> uploadedFiles) {
        String uploadDir = "src/main/resources/uploads/";
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }

        List<String> imagePaths = new ArrayList<>();
        for (UploadedFile file : uploadedFiles) {
            String outputFilePath = uploadDir + file.filename();
            File outputFile = new File(outputFilePath);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                file.content().transferTo(fos);
                FileController.getInstance().updateCache(outputFile.getName(), outputFile);

            } catch (IOException e) {
                logger.error("Error saving file: " + file.filename(), e);
            }
            imagePaths.add(outputFile.getAbsolutePath());

            logger.info("Image saved to: " + outputFilePath);
        }
        return imagePaths;
    }

}
