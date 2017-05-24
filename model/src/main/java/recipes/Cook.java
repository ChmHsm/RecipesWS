package recipes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

/**
 * Created by Me on 22/05/2017.
 */
@Entity
public class Cook {

    @Id
    @GeneratedValue
    private Long Id;

    private String username;

    @JsonIgnore
    private String password;

    @OneToMany(mappedBy = "cook")
    private Set<Recipe> recipes;

    private Date dateCreated;

    @PrePersist
    private void datePersisted(){
        this.dateCreated = new Date();
    }

    @JsonIgnore
    private Date lastUpdated;

    public String getDateCreated() {

        SimpleDateFormat dt1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dt1.format(this.dateCreated);
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    @PreUpdate
    private void dateUpdated(){
        this.lastUpdated = new Date();
    }

    public Cook(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRecipes(Set<Recipe> recipes) {
        this.recipes = recipes;
    }

    public Long getId() {

        return Id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    Cook() { //JPA
    }
}
