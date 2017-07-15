package recipes;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

/**
 * Created by Me on 23/05/2017.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer{

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner init(final CookRepository cookRepository, final RecipeRepository recipeRepository,
                           final ImageRepository imageRepository){
        //TODO Delete all newly added images to AWS S3 bucket
        return (evt) -> Arrays.asList("Houssam,Boualam,Safaa,Chaymae".split(","))
                .forEach(
                        c ->{
                            Cook cook = cookRepository.save(new Cook(c, "password"));
                            Recipe r1 = recipeRepository.save(new Recipe(c+"'s first Recipe", 5, 10, 50,
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                                    "Vivamus volutpat sem id sem iaculis convallis." +
                                    "Nullam malesuada urna nec lorem dignissim ornare." +
                                    "Donec sit amet turpis maximus, lacinia erat nec, vehicula magna." +
                                    "Mauris gravida elit sed tempor eleifend." +
                                    "Etiam sodales purus non erat egestas imperdiet.",
                                    "Pellentesque eu tincidunt nunc. Aliquam ut est porttitor, gravida velit nec, imperdiet nibh. Donec dictum, nunc non varius ultrices, nisi nunc tincidunt augue, a cursus dui lectus quis mauris. Aenean eleifend, neque vitae vestibulum posuere, leo massa venenatis felis, eget condimentum neque sem quis magna. Nulla auctor varius vehicula. Fusce tincidunt est vitae nisi malesuada, sit amet venenatis nunc volutpat. Maecenas sed dolor convallis leo consectetur pretium. Ut ut laoreet turpis, ut sagittis elit. Aliquam eu velit tortor. Phasellus venenatis euismod pharetra. Nullam ultricies ipsum scelerisque hendrerit commodo. Ut maximus libero a rutrum rutrum.",
                                    cook));

                            Image image1 = new Image(RecipesRestController.IMAGE_STORAGE_LOCATION+r1.getId(),
                                    RecipesRestController.IMAGE_STORAGE_LOCATION+r1.getId(), r1, true);
                            image1.setExtension(".jpg");
                            imageRepository.save(image1);

                            Recipe r2 = recipeRepository.save(new Recipe(c+"'s second Recipe", 4, 20, 40,
                                    c+"'s recipe's ingredients go here", c+"'s recipe's instructions go here",
                                    cook));

                            Image image2 = new Image(RecipesRestController.IMAGE_STORAGE_LOCATION+r2.getId(),
                                    RecipesRestController.IMAGE_STORAGE_LOCATION+r2.getId(), r2, true);
                            image2.setExtension(".jpg");
                            imageRepository.save(image2);
                        }
                );
    }
}
