package recipes;

import ij.IJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import recipes.Exceptions.CookNotFoundException;
import recipes.Exceptions.NullImageUploadException;
import recipes.Exceptions.RecipeNotFoundException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    public static String THUMBNAIL_STORAGE_LOCATION = "thumbnail storage directory/";
    private int THUMBNAIL_WIDTH = 100;
    private final Logger logger;

    @Autowired
    public RecipesRestController(CookRepository cookRepository, RecipeRepository recipeRepository, ImageRepository imageRepository) {
        this.cookRepository = cookRepository;
        this.recipeRepository = recipeRepository;
        this.imageRepository = imageRepository;
        logger = LoggerFactory.getLogger(RecipesRestController.class);

    }

    @RequestMapping(method = RequestMethod.GET)
    Collection<Recipe> getCookRecipes(@PathVariable String cookUsername) {
        validateCook(cookUsername);
        return recipeRepository.findByCookUsernameIgnoreCaseOrderByDateCreatedDesc(cookUsername);
    }

    @RequestMapping(method = RequestMethod.POST)
    ResponseEntity<?> addRecipe(@PathVariable String cookUsername, @RequestBody Recipe input) {
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
                .orElse(ResponseEntity.notFound().build());
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{recipeId}")
    Recipe readRecipe(@PathVariable String cookUsername, @PathVariable Long recipeId) {
        this.validateCook(cookUsername);
        return this.recipeRepository.findByCookUsernameIgnoreCase(cookUsername).stream()
                .filter(recipe -> recipe.getId() == recipeId)
                .findFirst()
                .orElseThrow(() -> new RecipeNotFoundException());
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{recipeId}")
    ResponseEntity<?> updateRecipe(@PathVariable String cookUsername, @PathVariable Long recipeId, @RequestBody Recipe input) {
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
                    this.recipeRepository.save(recipe);
                    URI location = ServletUriComponentsBuilder
                            .fromCurrentRequest()
                            .buildAndExpand().toUri();
                    return ResponseEntity.created(location).build();
                })
                .orElse(ResponseEntity.noContent().build());
    }

    private void validateCook(String cookUsername) {
        this.cookRepository.findByUsernameIgnoreCase(cookUsername).orElseThrow(() -> new CookNotFoundException(cookUsername));
    }

    private void validateRecipe(Long recipeId) {
        Recipe recipe = this.recipeRepository.findOne(recipeId);
        if (recipe == null) throw new RecipeNotFoundException();
    }

    @RequestMapping(value = "/{recipeId}/recipeMainImage/{isMainPicture}", method = RequestMethod.POST)
    ResponseEntity<?> handleRecipeImageUpload(@RequestParam("file") MultipartFile file, @PathVariable Long recipeId, @PathVariable Boolean isMainPicture) {
        validateRecipe(recipeId);
        validateImageFile(file);

        String originalName = file.getOriginalFilename();

        String extension = originalName.substring(originalName.lastIndexOf("."), originalName.length());

        Image recipeImage = this.imageRepository.save(new Image(originalName, isMainPicture));
        recipeImage.setOriginalPath(IMAGE_STORAGE_LOCATION + recipeImage.getId());
        recipeImage.setRecipe(this.recipeRepository.findOne(recipeId));
        recipeImage.setExtension(extension);
        this.imageRepository.save(recipeImage);

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream =
                        new BufferedOutputStream(new FileOutputStream(new File(IMAGE_STORAGE_LOCATION + recipeImage.getId()+extension)));

                stream.write(bytes);
                stream.close();

                resizePicture(IMAGE_STORAGE_LOCATION + recipeImage.getId() + extension, THUMBNAIL_WIDTH, recipeImage.getId(), extension);

                return ResponseEntity.ok().body(recipeImage);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).build();
        }
    }

    @RequestMapping(value = "/{recipeId}/recipeMainImage/{thumbnail}", method = RequestMethod.GET)
    public ResponseEntity<Resource> getImageByRecipe(@PathVariable Long recipeId, @PathVariable boolean thumbnail) {
        validateRecipe(recipeId);

        Recipe recipe = this.recipeRepository.findOne(recipeId);
        Collection<Image> images = this.imageRepository.findByRecipe(recipe);
        String filePath = thumbnail ? THUMBNAIL_STORAGE_LOCATION : IMAGE_STORAGE_LOCATION;

        if (images != null) {
            if (images.size() > 0) {
                for (Image image : images) {
                    if (image.isMainPicture()) {
                        filePath += "/" + image.getId()+image.getExtension();
                        break;
                    }
                }

                File file = new File(filePath);
                if (file.exists()) {
                    Path path = Paths.get(file.getAbsolutePath());
                    try {
                        ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));

                        final HttpHeaders headers = new HttpHeaders();
                        headers.setContentType(MediaType.IMAGE_JPEG);

                        return ResponseEntity.ok()
                                .headers(headers)
                                .contentLength(file.length())
                                .contentType(MediaType.parseMediaType("application/octet-stream"))
                                .body(resource);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    private void validateImageFile(MultipartFile file) {
        if (!file.isEmpty()) {
            Tika tika = new Tika();
            String detectedType = null;
            try {
                detectedType = tika.detect(file.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (detectedType == null) {
                throw new NullImageUploadException();
            } else {
                if (detectedType.isEmpty() || !detectedType.contains("image/")) {
                    throw new InvalidParameterException("The updated file isn't an image");
                }
            }
        }
    }

    @RequestMapping(value = "/{recipeId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteRecipe(@PathVariable Long recipeId) {
        validateRecipe(recipeId);
        Recipe recipe = this.recipeRepository.findOne(recipeId);
        Collection<Image> recipeImages = this.imageRepository.findByRecipe(recipe);
        if (recipeImages != null) {
            if (recipeImages.size() > 0) {
                recipeImages.forEach(image -> {
                    File recipeImageFile = new File(image.getOriginalPath());
                    if (recipeImageFile.exists())
                        if (!recipeImageFile.delete())
                            logger.warn("Could not delete recipe image in " + recipeImageFile.getAbsolutePath());
                    this.imageRepository.delete(image);
                });
            }
        }
        if (recipe != null) {
            this.recipeRepository.delete(recipe);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    private boolean resizePicture(String imageUrl, int targetWidth, Long id, String extension) {

        ImagePlus imp = IJ.openImage(imageUrl);

        ImageProcessor ip = imp.getProcessor();
        ip.setInterpolationMethod(ImageProcessor.BILINEAR);

        ip = ip.resize(targetWidth);
        BufferedImage resizedImage = ip.getBufferedImage();


        try {
            ImageIO.write(resizedImage, extension.substring(1),
                    new File(THUMBNAIL_STORAGE_LOCATION + String.valueOf(id)+extension));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }



}
