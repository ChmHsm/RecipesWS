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
import recipes.Exceptions.ImageNotFoundException;
import recipes.Exceptions.RecipeNotFoundException;

import java.net.URI;
import java.util.Collection;
import java.util.List;

/**
 * Created by Me on 24/05/2017.
 */

@RestController
@RequestMapping("/general")
public class GeneralRestController {

    private final CookRepository cookRepository;
    private final RecipeRepository recipeRepository;
    private final ImageRepository imageRepository;
    private final LikeRelationshipRepository likeRelationshipRepository;
    private final FollowRelationshipRepository followRelationshipRepository;

    @Autowired
    public GeneralRestController(CookRepository cookRepository,
                                 RecipeRepository recipeRepository, ImageRepository imageRepository,
                                 LikeRelationshipRepository likeRelationshipRepository,
                                 FollowRelationshipRepository followRelationshipRepository) {
        this.cookRepository = cookRepository;
        this.recipeRepository = recipeRepository;
        this.imageRepository = imageRepository;
        this.likeRelationshipRepository = likeRelationshipRepository;
        this.followRelationshipRepository = followRelationshipRepository;
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

    @RequestMapping(method = RequestMethod.POST, value = "/recipes/{recipeId}/recipeMainImage")
    ResponseEntity<?> addImage(@PathVariable Long recipeId, @RequestBody Image input) {
        this.validateRecipe(recipeId);
        Recipe recipe = recipeRepository.findOne(recipeId);
        Image image = new Image(null, input.getOriginalName(), recipe, true);
        image.setExtension(input.getExtension());
        image = this.imageRepository.save(image);

        return image != null ? ResponseEntity.ok().body(image) : ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
    }

    private void validateRecipe(Long recipeId) {
        Recipe recipe = this.recipeRepository.findOne(recipeId);
        if (recipe == null) throw new RecipeNotFoundException();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recipes/{recipeId}/recipeMainImage")
    ResponseEntity<Image> getImageEntity(@PathVariable Long recipeId) {
        validateRecipe(recipeId);

        List<Image> images = (List) imageRepository.findByRecipe(recipeRepository.findOne(recipeId));
        if (images != null) {
            if (images.size() > 0) {
                for (Image image : images) {
                    if (image.isMainPicture()) return ResponseEntity
                            .ok()
                            .body(image);
                }
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity
                .ok()
                .body(images.get(0));
    }

    @RequestMapping(method = RequestMethod.GET, value = "/images")
    Collection<Image> getAllImages() {
        return imageRepository.findAll();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/images/20")
    Collection<Image> getLatest20Images() {
        return imageRepository.findTop20ByOrderByDateCreatedDesc();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/images/50")
    Collection<Image> getLatest50Images() {
        return imageRepository.findTop50ByOrderByDateCreatedDesc();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/images/100")
    Collection<Image> getLatest100Images() {
        return imageRepository.findTop100ByOrderByDateCreatedDesc();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recipesLikes/{recipeId}")
    ResponseEntity<?> getLikesByRecipeId(@PathVariable Long recipeId) {
        validateRecipe(recipeId);
        List<LikeRelationship> likes = (List) likeRelationshipRepository.findByRecipe(recipeRepository.findOne(recipeId));

        return ResponseEntity.ok()
                .body(likes);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/recipesLikes/{recipeId}/{cookUsername}")
    ResponseEntity<?> addLikeToRecipe(@PathVariable Long recipeId, @PathVariable String cookUsername) {
        validateRecipe(recipeId);
        validateCook(cookUsername);

        List<LikeRelationship> likesByRecipe = (List) likeRelationshipRepository.findByRecipe(recipeRepository.findOne(recipeId));
        for (LikeRelationship likes : likesByRecipe) {
            if (likes.getCook().getUsername().equalsIgnoreCase(cookUsername)) {

                return ResponseEntity.ok()
                        .body(likes);
            }
        }

        Cook cook = cookRepository.findByUsernameIgnoreCase(cookUsername)
                .orElseThrow(() -> new CookNotFoundException(cookUsername));
        LikeRelationship like = likeRelationshipRepository.save(new LikeRelationship(recipeRepository.findOne(recipeId),
                cook));
        return ResponseEntity.ok()
                .body(like);

    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/recipesLikes/{likeId}")
    ResponseEntity<?> deleteLikeToRecipe(@PathVariable Long likeId) {

        LikeRelationship like = likeRelationshipRepository.findOne(likeId);
        if (like != null) {
            likeRelationshipRepository.delete(likeId);

        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/follows/{followerCook}/{followeeCook}")
    ResponseEntity<?> addFollowToCook(@PathVariable String followerCook, @PathVariable String followeeCook) {
        if(followerCook.equalsIgnoreCase(followeeCook)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        validateCook(followerCook);
        validateCook(followeeCook);

        Cook cook2 = cookRepository.findByUsernameIgnoreCase(followeeCook)
                .orElseThrow(() -> new CookNotFoundException(followeeCook));

        List<FollowRelationship> cookFollowers =
                (List) followRelationshipRepository.findByFollower(cook2);

        for (FollowRelationship follow : cookFollowers) {
            if (follow.getFollower().getUsername().equalsIgnoreCase(followerCook)) {

                return ResponseEntity.ok()
                        .body(follow);
            }
        }

        Cook cook1 = cookRepository.findByUsernameIgnoreCase(followerCook)
                .orElseThrow(() -> new CookNotFoundException(followerCook));
        FollowRelationship followRelationship = followRelationshipRepository.save(new FollowRelationship(cook1, cook2));
        return ResponseEntity.ok()
                .body(followRelationship);

    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/follows/{followId}")
    ResponseEntity<?> unfollowCook(@PathVariable Long followId) {

        FollowRelationship follow = followRelationshipRepository.findOne(followId);

        if (follow != null) {
            followRelationshipRepository.delete(follow);
        }
        return ResponseEntity.ok().build();
    }
}
