package recipes.security.resourceEntities;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import recipes.Recipe;
import recipes.RecipesRestController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

/**
 * Created by Me on 14/08/2017.
 */
public class RecipeResource extends ResourceSupport {

    private final Recipe recipe;

    public RecipeResource(Recipe recipe) {
        this.recipe = recipe;
        String username = recipe.getCook().getUsername();
        this.add(new Link(String.valueOf(recipe.getId()), "recipe-id"));
        this.add(linkTo(RecipesRestController.class, username).withRel("recipes"));
        this.add(linkTo(
                methodOn(RecipesRestController.class, username).readRecipe(null, recipe.getId())).withSelfRel());
    }

    public Recipe getRecipe() {
        return recipe;
    }

}
