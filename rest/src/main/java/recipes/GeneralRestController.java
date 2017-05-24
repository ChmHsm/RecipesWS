package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Created by Me on 24/05/2017.
 */

@RestController
@RequestMapping("/general")
public class GeneralRestController {

    private final CookRepository cookRepository;
    private final RecipeRepository recipeRepository;

    @Autowired
    public GeneralRestController(CookRepository cookRepository, RecipeRepository recipeRepository){
        this.cookRepository = cookRepository;
        this.recipeRepository = recipeRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recipes")
    Collection<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }
}
