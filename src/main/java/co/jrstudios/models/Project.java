package co.jrstudios.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "projects")
public class Project {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    private String title;

    @DatabaseField
    private String description;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String[] tags;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    @JsonIgnore
    private String[] imagePaths;  // This stores the paths to the uploaded image files

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    private String[] serverImagePaths;

    @DatabaseField
    private String location;

    public Project() {}

    public Project(int id, String title, String description, String[] tags, String location) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.location = location;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getTags() {
        return tags;
    }
    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    public String[] getImagePaths() {
        return imagePaths;
    }
    public void setImagePaths(String[] imagePaths) {
        this.imagePaths = imagePaths;
    }
    public String[] getServerImagePaths() {
        return serverImagePaths;
    }
    public void setServerImagePaths(String[] serverImagePaths) {
        this.serverImagePaths = serverImagePaths;
    }
}