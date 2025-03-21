package co.jrstudios.db;

import co.jrstudios.models.Project;
import co.jrstudios.repositories.ProjectRepository;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:/database.db";
    private static final Logger log = LoggerFactory.getLogger(DatabaseManager.class);
    private ConnectionSource connectionSource;
    private Dao<Project, Integer> projectDao;
    private static final DatabaseManager instance = new DatabaseManager();

    public void setupDatabase() throws Exception {
        connectionSource = new JdbcConnectionSource(DATABASE_URL);
        // Create the projects table if it doesn't exist
        TableUtils.createTableIfNotExists(connectionSource, Project.class);
        projectDao = DaoManager.createDao(connectionSource, Project.class);
    }

    public Dao<Project, Integer> getProjectDao() throws Exception {
        return DaoManager.createDao(connectionSource, Project.class);
    }

    public void updateProject(Project project) throws Exception {
        try {
            projectDao.update(project);
            log.info("Successfully updated project {}", project.getTitle());
        } catch (Exception e) {
            log.error("Failed to update project {}", project.getTitle(), e);
        }
    }

    public void deleteProject(int id) throws Exception {
        projectDao.deleteById(id);
        log.info("Successfully deleted project {}", id);
    }

    public void createProject(Project project) throws Exception {
        try {
            projectDao.createIfNotExists(project);
            log.info("Successfully created project {}", project.getTitle());
        } catch (Exception e) {
            log.error("Failed to create project {}", project.getTitle(), e);
        }
    }

    public void fetchAllProjects() throws Exception {
        try {
            List<Project> data = projectDao.queryForAll();
            ProjectRepository.getInstance().loadProjects(data);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    public void close() throws Exception {
        if (connectionSource != null) {
            connectionSource.close();
        }
    }

    public static DatabaseManager getInstance() {
        return instance;
    }
}
