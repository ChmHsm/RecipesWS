package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.InvalidParameterException;
import java.util.Collection;

/**
 * Created by Me on 23/05/2017.
 */
@RestController
@RequestMapping("/{cookUsername}/recipes")
public class RecipesRestController {

    private final CookRepository cookRepository;
    private final RecipeRepository recipeRepository;

    @Autowired
    public RecipesRestController(CookRepository cookRepository, RecipeRepository recipeRepository){
        this.cookRepository = cookRepository;
        this.recipeRepository = recipeRepository;
    }

     @RequestMapping(method = RequestMethod.GET)
    Collection<Recipe> getCookRecipes(@PathVariable String cookUsername){
         validateCook(cookUsername);
         return recipeRepository.findByCookUsernameIgnoreCase(cookUsername);
     }


    private void validateCook(String cookUsername){
        this.cookRepository.findByUsernameIgnoreCase(cookUsername).orElseThrow(() -> new CookNotFoundException(cookUsername));
    }

}
