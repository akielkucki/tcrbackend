package co.jrstudios.projects;

import co.jrstudios.models.Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProjectsCRUD {
    private Map<Integer, Project> projectIdMap = new HashMap<>();
    private int lastId = 0;
    private static final ProjectsCRUD instance =  new ProjectsCRUD();
    public static ProjectsCRUD getInstance() {
        return instance;
    }
    public void loadProjects(List<Project> projects) {
        lastId = projects.get(projects.size() - 1).getId();
        for (Project project : projects) {
            projectIdMap.put(project.getId(), project);
        }
    }
    public void addProject(Project project) {
        project.setId(++lastId);
        projectIdMap.put(project.getId(), project);
        lastId = project.getId();
    }
    public void removeProject(int id) {
        projectIdMap.remove(id);
    }

    public List<Project> getProjects() {
        return new ArrayList<>(projectIdMap.values());
    }
    public Project getProjectById(int id) {
        return projectIdMap.get(id);
    }
    public void updateProjectById(int id, Project project) {
        projectIdMap.put(id, project);
    }

}
