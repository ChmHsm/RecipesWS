package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.w3c.dom.html.HTMLTableCaptionElement;
import recipes.Exceptions.CookNotFoundException;
import recipes.Exceptions.RecipeNotFoundException;

import java.net.URI;
import java.util.Collection;

/**
 * Created by Me on 24/05/2017.
 */

@RestController
@RequestMapping("/general")
public class GeneralRestController {

    private final CookRepository cookRepository;
    private final RecipeRepository recipeRepository;
    private final ImageRepository imageRepository;

    @Autowired
    public GeneralRestController(CookRepository cookRepository,
                                 RecipeRepository recipeRepository, ImageRepository imageRepository) {
        this.cookRepository = cookRepository;
        this.recipeRepository = recipeRepository;
        this.imageRepository = imageRepository;
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recipes")
    Collection<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recipes/20")
    Collection<Recipe> getLatest20Recipes() {
        return recipeRepository.findTop20ByOrderByDateCreatedDesc();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recipes/50")
    Collection<Recipe> getLatest50Recipes() {
        return recipeRepository.findTop50ByOrderByDateCreatedDesc();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recipes/100")
    Collection<Recipe> getLatest100Recipes() {
        return recipeRepository.findTop100ByOrderByDateCreatedDesc();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/newCook")
    ResponseEntity<?> addCook(@RequestBody Cook input) {
        if (cookRepository.findByUsernameIgnoreCase(input.getUsername()) == null) {

            Cook cook = cookRepository.save(new Cook(input.getUsername(), input.getPassword()));
            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/cook/{username}")
                    .buildAndExpand(cook.getUsername()).toUri();
            return ResponseEntity.created(location).build();

        } else {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "cook/{username}")
    Cook getCook(@PathVariable String username) {
        return this.cookRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new CookNotFoundException(username));
    }

    private void validateCook(String cookUsername) {
        this.cookRepository.findByUsernameIgnoreCase(cookUsername).orElseThrow(() -> new CookNotFoundException(cookUsername));
    }

    @RequestMapping(method = RequestMethod.POST, value="/recipes/{recipeId}/recipeMainImage")
    ResponseEntity<?> addImage(@PathVariable Long recipeId, @RequestBody Image input) {
        this.validateRecipe(recipeId);
        Recipe recipe = recipeRepository.findOne(recipeId);
        Image image = new Image(null, input.getOriginalName(), recipe, true)
        image.setExtension(input.getExtension());
        image = this.imageRepository.save(image);
        
        return image != null ? ResponseEntity.ok().body(image) : ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }

    private void validateRecipe(Long recipeId) {
        Recipe recipe = this.recipeRepository.findOne(recipeId);
        if (recipe == null) throw new RecipeNotFoundException();
    }
}
