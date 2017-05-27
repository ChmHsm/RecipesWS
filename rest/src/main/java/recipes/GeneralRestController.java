package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.websocket.server.PathParam;
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

    @RequestMapping(method = RequestMethod.GET, value="/recipes/20")
    Collection<Recipe> getLatest20Recipes(){
        return recipeRepository.findTop20ByOrderByDateCreatedDesc();
    }

    @RequestMapping(method = RequestMethod.GET, value="/recipes/50")
    Collection<Recipe> getLatest50Recipes(){
        return recipeRepository.findTop50ByOrderByDateCreatedDesc();
    }

    @RequestMapping(method = RequestMethod.GET, value="/recipes/100")
    Collection<Recipe> getLatest100Recipes(){
        return recipeRepository.findTop100ByOrderByDateCreatedDesc();
    }
}
