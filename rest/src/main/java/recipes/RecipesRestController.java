package recipes;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
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
    private final ImageRepository imageRepository;
    public static String IMAGE_STORAGE_LOCATION = "Image storage directory/";

    @Autowired
    public RecipesRestController(CookRepository cookRepository, RecipeRepository recipeRepository, ImageRepository imageRepository){
        this.cookRepository = cookRepository;
        this.recipeRepository = recipeRepository;
        this.imageRepository = imageRepository;
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
        validateRecipe(recipeId);
        validateImageFile(file);

        String originalName = file.getOriginalFilename();

        Image recipeImage = this.imageRepository.save(new Image(originalName, true));
        recipeImage.setOriginalPath(IMAGE_STORAGE_LOCATION+recipeImage.getId());
        recipeImage.setRecipe(this.recipeRepository.findOne(recipeId));
        this.imageRepository.save(recipeImage);

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(IMAGE_STORAGE_LOCATION + recipeImage.getId() )));
                stream.write(bytes);
                stream.close();
                return "You successfully uploaded " + originalName + " into " + IMAGE_STORAGE_LOCATION + originalName ;
            } catch (Exception e) {
                return "You failed to upload " + originalName + " => " + e.getMessage();
            }
        } else {
            return "You failed to upload " + originalName + " because the file was empty.";
        }
    }

    private void validateImageFile(MultipartFile file){
        if(!file.isEmpty())
        {
            Tika tika = new Tika();
            String detectedType = null;
            try {
                detectedType = tika.detect(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(detectedType == null){
                throw new NullImageUploadException2();
            }
            else{
                if(detectedType.isEmpty() || !detectedType.contains("image/")){
                    throw new InvalidParameterException("The updated file isn't an image");
                }
            }
        }
    }

}
