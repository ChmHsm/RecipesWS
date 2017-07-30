package recipes;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by Me on 30/07/2017.
 */
@Entity
public class LikeRelationship {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Recipe recipe;

    @ManyToOne
    private Cook cook;

    public LikeRelationship() {
        //Jpa
    }

    public LikeRelationship(Recipe recipe, Cook cook) {
        this.recipe = recipe;
        this.cook = cook;
    }

    public Recipe getRecipe() {

        return recipe;
    }

    public Long getId() {
        return id;
    }

    public void setRecipe(Recipe recipe) {

        this.recipe = recipe;
    }

    public Cook getCook() {
        return cook;
    }

    public void setCook(Cook cook) {
        this.cook = cook;
    }
}
