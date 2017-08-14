package recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.w3c.dom.html.HTMLTableCaptionElement;
import recipes.Exceptions.ImageNotFoundException;
import recipes.Exceptions.RecipeNotFoundException;
import recipes.security.UserNotFoundException;
import recipes.security.resourceEntities.CookResource;
import recipes.security.resourceEntities.ImageResource;
import recipes.security.resourceEntities.*;

import java.net.URI;
import java.security.Principal;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
    public CookResource getCook(Principal principal, @PathVariable String username) {
        validateCook(principal);

        return new CookResource(
                this.cookRepository.findByUsernameIgnoreCase(username)
                        .orElseThrow(() -> new UserNotFoundException(username)));

    }

    private void validateCook(Principal principal) {
        String username = principal.getName();
        this.cookRepository
                .findByUsernameIgnoreCase(username)
                .orElseThrow(
                        () -> new UserNotFoundException(username));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/recipes/{recipeId}/recipeMainImage")
    ResponseEntity<?> addImage(Principal principal, @PathVariable Long recipeId, @RequestBody Image input) {
        validateCook(principal);
        this.validateRecipe(recipeId);
        Recipe recipe = recipeRepository.findOne(recipeId);
        Image image = new Image(null, input.getOriginalName(), recipe, true);
        image.setExtension(input.getExtension());
        image = this.imageRepository.save(image);

        Link forOneImage = new ImageResource(image).getLink(Link.REL_SELF);

        return ResponseEntity
                .created(URI
                        .create(forOneImage.getHref()))
                .build();
    }

    private void validateRecipe(Long recipeId) {
        Recipe recipe = this.recipeRepository.findOne(recipeId);
        if (recipe == null) throw new RecipeNotFoundException();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/recipes/{recipeId}/recipeMainImage")
    public ResponseEntity<Image> getImageEntity(Principal principal, @PathVariable Long recipeId) {
        validateCook(principal);
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
    ResponseEntity<?> addLikeToRecipe(Principal principal, @PathVariable Long recipeId, @PathVariable String cookUsername) {
        validateRecipe(recipeId);
        validateCook(principal);

        List<LikeRelationship> likesByRecipe = (List) likeRelationshipRepository.findByRecipe(recipeRepository.findOne(recipeId));
        for (LikeRelationship likes : likesByRecipe) {
            if (likes.getCook().getUsername().equalsIgnoreCase(cookUsername)) {

                return ResponseEntity.ok()
                        .body(likes);
            }
        }

        Cook cook = cookRepository.findByUsernameIgnoreCase(cookUsername)
                .orElseThrow(() -> new UserNotFoundException(cookUsername));
        LikeRelationship like = likeRelationshipRepository.save(new LikeRelationship(recipeRepository.findOne(recipeId),
                cook));
        return ResponseEntity.ok()
                .body(like);

    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/recipesLikes/{likeId}")
    ResponseEntity<?> deleteLikeToRecipe(Principal principal, @PathVariable Long likeId) {
        validateCook(principal);
        LikeRelationship like = likeRelationshipRepository.findOne(likeId);
        if (like != null) {
            likeRelationshipRepository.delete(likeId);

        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.POST, value = "/follows/{followerCook}/{followeeCook}")
    ResponseEntity<?> addFollowToCook(Principal principal, @PathVariable String followerCook, @PathVariable String followeeCook) {

        validateCook(principal);

        if(followerCook.equalsIgnoreCase(followeeCook)){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Cook cook2 = cookRepository.findByUsernameIgnoreCase(followeeCook)
                .orElseThrow(() -> new UserNotFoundException(followeeCook));

        List<FollowRelationship> cookFollowers =
                (List) followRelationshipRepository.findByFollower(cook2);

        for (FollowRelationship follow : cookFollowers) {
            if (follow.getFollower().getUsername().equalsIgnoreCase(followerCook)) {

                return ResponseEntity.ok()
                        .body(follow);
            }
        }

        Cook cook1 = cookRepository.findByUsernameIgnoreCase(followerCook)
                .orElseThrow(() -> new UserNotFoundException(followerCook));
        FollowRelationship followRelationship = followRelationshipRepository.save(new FollowRelationship(cook1, cook2));
        return ResponseEntity.ok()
                .body(followRelationship);

    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/follows/{followId}")
    ResponseEntity<?> unfollowCook(Principal principal, @PathVariable Long followId) {

        validateCook(principal);

        FollowRelationship follow = followRelationshipRepository.findOne(followId);

        if (follow != null) {
            followRelationshipRepository.delete(follow);
        }
        return ResponseEntity.ok().build();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{cookUsername}/followers")
    ResponseEntity<?> getCookFollowers(Principal principal, @PathVariable String cookUsername){
        validateCook(principal);

        Cook cook = cookRepository.findByUsernameIgnoreCase(cookUsername)
                .orElseThrow(() -> new UserNotFoundException(cookUsername));

        List<FollowRelationship> followers = (List) followRelationshipRepository.findByFollowee(cook);

        return ResponseEntity.ok()
                .body(followers);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{cookUsername}/followees")
    ResponseEntity<?> getCookFollowees(Principal principal, @PathVariable String cookUsername){
        validateCook(principal);

        Cook cook = cookRepository.findByUsernameIgnoreCase(cookUsername)
                .orElseThrow(() -> new UserNotFoundException(cookUsername));

        List<FollowRelationship> followers = (List) followRelationshipRepository.findByFollower(cook);

        return ResponseEntity.ok()
                .body(followers);
    }
}
