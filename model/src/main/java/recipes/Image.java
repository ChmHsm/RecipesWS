package recipes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Me on 28/05/2017.
 */
@Entity
public class Image {

    Image(){
        //JPA
    }

    @Id
    @GeneratedValue
    private Long id;

    private String originalName;

    private String originalPath;

    private Date dateCreated;

    private Date lastUpdated;

    private boolean isMainPicture;

    public Image(String originalPath, String originalName, Recipe recipe, boolean isMainPicture) {
        this.originalPath = originalPath;
        this.originalName = originalName;
        this.isMainPicture = isMainPicture;
        this.recipe = recipe;
    }

    public String getExtension() {

        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    private String extension;

    public boolean isMainPicture() {
        return isMainPicture;
    }

    public void setMainPicture(boolean isMainPicture) {
        this.isMainPicture = isMainPicture;
    }

    public Cook getCook() {
        return cook;
    }

    public void setCook(Cook cook) {
        this.cook = cook;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Image(String originalName, String originalPath, Recipe recipe) {
        this.originalName = originalName;
        this.originalPath = originalPath;
        this.recipe = recipe;
        this.isMainPicture = true;
    }

    public Image(String originalName, boolean isMainPicture) {
        this.originalName = originalName;
        this.isMainPicture = isMainPicture;
    }

    public Image(String originalName, String originalPath, Cook cook) {

        this.originalName = originalName;
        this.originalPath = originalPath;
        this.cook = cook;
        this.isMainPicture = true;

    }

    @JsonIgnore
    @OneToOne
    private Cook cook;

    @JsonIgnore
    @ManyToOne
    private Recipe recipe;

    public Image(String originalName, String originalPath) {
        this.originalName = originalName;
        this.originalPath = originalPath;
    }

    public void setOriginalName(String originalName) {
        this.originalName = originalName;
    }

    public void setOriginalPath(String originalPath) {
        this.originalPath = originalPath;
    }

    public Long getId() {

        return id;
    }

    public String getOriginalName() {
        return originalName;
    }

    public String getOriginalPath() {
        return originalPath;
    }

    @PrePersist
    private void datePersisted(){
        this.dateCreated = new Date();
    }

    @PreUpdate
    private void dateUpdated(){
        this.lastUpdated = new Date();
    }

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }
}
