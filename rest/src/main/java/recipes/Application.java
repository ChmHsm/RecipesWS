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
    CommandLineRunner init(final CookRepository cookRepository, final RecipeRepository recipeRepository){
        return (evt) -> Arrays.asList("Houssam,Boualam".split(","))
                .forEach(
                        c ->{
                            Cook cook = cookRepository.save(new Cook(c, "password"));
                            recipeRepository.save(new Recipe(c+"'s first Recipe", 5, 10, 50, c+"'s recipe's ingredients go here", c+"'s recipe's instructions go here", cook));
                            recipeRepository.save(new Recipe(c+"'s second Recipe", 5, 10, 50, c+"'s recipe's ingredients go here", c+"'s recipe's instructions go here", cook));
                        }
                );
    }
}
