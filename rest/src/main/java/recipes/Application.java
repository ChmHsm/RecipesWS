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
        return (evt) -> Arrays.asList("Houssam,Boualam,Safaa,Chaymae".split(","))
                .forEach(
                        c ->{
                            Cook cook = cookRepository.save(new Cook(c, "password"));
                            Recipe r1 = recipeRepository.save(new Recipe(c+"'s first Recipe", 5, 10, 50,
                                    c+"'s recipe's ingredients go here", c+"'s recipe's instructions go here",
                                    cook));

                            imageRepository.save(new Image(RecipesRestController.IMAGE_STORAGE_LOCATION+r1.getId(),
                                    RecipesRestController.IMAGE_STORAGE_LOCATION+r1.getId(), r1, true));

                            Recipe r2 = recipeRepository.save(new Recipe(c+"'s second Recipe", 4, 20, 40,
                                    c+"'s recipe's ingredients go here", c+"'s recipe's instructions go here",
                                    cook));

                            imageRepository.save(new Image(RecipesRestController.IMAGE_STORAGE_LOCATION+r2.getId(),
                                    RecipesRestController.IMAGE_STORAGE_LOCATION+r2.getId(), r2, true));
                        }
                );
    }
}
