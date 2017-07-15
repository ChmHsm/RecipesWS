package recipes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;

/**
 * Created by Me on 22/05/2017.
 */

@Entity
public class Recipe {

    @Id
    @GeneratedValue
    private Long Id;

    private String title;

    private int difficultyRating;

    private int prepTime;

    private double prepCost;

    @Lob
    @Column(length = 10000)
    private String ingredients;

    @Lob
    @Column(length = 10000)
    private String instructions;

    public Date getDateCreated() {
        return this.dateCreated;
    }

    public Date getLastUpdated() {
        return this.lastUpdated;
    }

    public Cook getCook() {
        return cook;
    }

    public void setCook(Cook cook) {
        this.cook = cook;
    }

    @ManyToOne
    private Cook cook;


    private Date dateCreated;

    @PrePersist
    private void datePersisted(){
        this.dateCreated = new Date();
    }

    private Date lastUpdated;

    @PreUpdate
    private void dateUpdated(){
        this.lastUpdated = new Date();
    }

    Recipe(){
        //JPA specific
    }


    public Recipe(String title, int difficultyRating, int prepTime, double prepCost, String ingredients, String instructions) {
        this.title = title;
        this.difficultyRating = difficultyRating;
        this.prepTime = prepTime;
        this.prepCost = prepCost;
        this.ingredients = ingredients;
        this.instructions = instructions;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDifficultyRating(int difficultyRating) {
        this.difficultyRating = difficultyRating;
    }

    public void setPrepTime(int prepTime) {
        this.prepTime = prepTime;
    }

    public void setPrepCost(double prepCost) {
        this.prepCost = prepCost;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Long getId() {

        return Id;
    }

    public String getTitle() {
        return title;
    }

    public int getDifficultyRating() {
        return difficultyRating;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public double getPrepCost() {
        return prepCost;
    }

    public String getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public Recipe(String title, int difficultyRating, int prepTime, double prepCost, String ingredients, String instructions, Cook cook) {
        this.title = title;
        this.difficultyRating = difficultyRating;
        this.prepTime = prepTime;
        this.prepCost = prepCost;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.cook = cook;
    }
}
