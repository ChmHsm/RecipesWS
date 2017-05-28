package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
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
         return recipeRepository.findByCookUsernameIgnoreCaseOrderByDateCreatedDesc(cookUsername);
     }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> addRecipe(@PathVariable String cookUsername,@RequestBody Recipe input){
        this.validateCook(cookUsername);

        return cookRepository.findByUsernameIgnoreCase(cookUsername)
                .map(cook -> {
                    Recipe recipe = this.recipeRepository.save(new Recipe(input.getTitle(), input.getDifficultyRating(),
                            input.getPrepTime(), input.getPrepCost(), input.getIngredients(), input.getInstructions(), cook));
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest().path("/{id}")
                            .buildAndExpand(recipe.getId()).toUri();
                    return ResponseEntity.created(location).build();
                })
                .orElse(ResponseEntity.noContent().build());
    }

    @RequestMapping(method = RequestMethod.GET, value="/{recipeId}")
    Recipe readRecipe(@PathVariable String cookUsername, @PathVariable Long recipeId){
        this.validateCook(cookUsername);
        return this.recipeRepository.findByCookUsernameIgnoreCase(cookUsername).stream()
                .filter(recipe -> recipe.getId() == recipeId)
                .findFirst()
                .orElseThrow(() -> new RecipeNotFoundException());
    }

    @RequestMapping(method = RequestMethod.POST, value="/{recipeId}")
    ResponseEntity<?> updateRecipe(@PathVariable String cookUsername, @PathVariable Long recipeId,@RequestBody Recipe input){
        this.validateRecipe(recipeId);
        Recipe recipe = this.recipeRepository.findOne(recipeId);
        recipe.setTitle(input.getTitle());
        recipe.setDifficultyRating(input.getDifficultyRating());
        recipe.setPrepTime(input.getPrepTime());
        recipe.setPrepCost(input.getPrepCost());
        recipe.setIngredients(input.getIngredients());
        recipe.setInstructions(input.getInstructions());

        return cookRepository.findByUsernameIgnoreCase(cookUsername)
                .map(cook -> {
                    Recipe updatedRecipe = this.recipeRepository.save(recipe);
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .buildAndExpand().toUri();
                    return ResponseEntity.created(location).build();
                })
                .orElse(ResponseEntity.noContent().build());
    }

    private void validateCook(String cookUsername){
        this.cookRepository.findByUsernameIgnoreCase(cookUsername).orElseThrow(() -> new CookNotFoundException(cookUsername));
    }

    private void validateRecipe(Long recipeId){
        Recipe recipe = this.recipeRepository.findOne(recipeId);
        if(recipe == null) throw new RecipeNotFoundException();
    }

    @RequestMapping(value="/{recipeId}/recipeMainImage", method=RequestMethod.POST)
    public @ResponseBody String handleFileUpload(@RequestParam("file") MultipartFile file, @PathVariable Long recipeId){
        String name = "test11";
        System.out.println("File " + file.getName() +" was received.");
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + name + " into " + name + "-uploaded !";
            } catch (Exception e) {
                return "You failed to upload " + name + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + name + " because the file was empty.";
        }
    }

}
