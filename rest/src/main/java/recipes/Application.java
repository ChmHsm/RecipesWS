package recipes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import org.springframework.boot.web.support.SpringBootServletInitializer;


// curl -X POST -vu android-bookmarks:123456 http://localhost:8080/oauth/token -H
// "Accept: application/json" -d "password=password&username=jlong&grant_type=password&scope=write
// &client_secret=123456&client_id=android-bookmarks"
// curl -v POST http://127.0.0.1:8080/bookmarks -H "Authorization: Bearer <oauth_token>""


/**
 * Created by Me on 23/05/2017.
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer{

    public static void main(String[] args){
        SpringApplication.run(Application.class, args);
    }

    // Handling CORS, redirect to 9000
    @Bean
    FilterRegistrationBean corsFilter(
            @Value("${tagit.origin:http://localhost:9000}") String origin) {
        return new FilterRegistrationBean(new Filter() {
            public void doFilter(ServletRequest req, ServletResponse res,
                                 FilterChain chain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) req;
                HttpServletResponse response = (HttpServletResponse) res;
                String method = request.getMethod();
                // this origin value could just as easily have come from a database
                response.setHeader("Access-Control-Allow-Origin", origin);
                response.setHeader("Access-Control-Allow-Methods",
                        "POST,GET,OPTIONS,DELETE");
                response.setHeader("Access-Control-Max-Age", Long.toString(60 * 60));
                response.setHeader("Access-Control-Allow-Credentials", "true");
                response.setHeader(
                        "Access-Control-Allow-Headers",
                        "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
                if ("OPTIONS".equals(method)) {
                    response.setStatus(HttpStatus.OK.value());
                }
                else {
                    chain.doFilter(req, res);
                }
            }

            public void init(FilterConfig filterConfig) {
            }

            public void destroy() {
            }
        });
    }

    @Bean
    CommandLineRunner init(final CookRepository cookRepository, final RecipeRepository recipeRepository,
                           final ImageRepository imageRepository, final LikeRelationshipRepository likeRelationshipRepository){
        //TODO Delete all newly added images to AWS S3 bucket

        //cleanApplicationDatabase(cookRepository, recipeRepository, imageRepository, likeRelationshipRepository);

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
                            likeRelationshipRepository.save(new LikeRelationship(r1, cook));

                            Image image1 = new Image(RecipesRestController.IMAGE_STORAGE_LOCATION+r1.getId(),
                                    RecipesRestController.IMAGE_STORAGE_LOCATION+r1.getId(), r1, true);
                            image1.setExtension(".jpg");
                            imageRepository.save(image1);

                            Recipe r2 = recipeRepository.save(new Recipe(c+"'s second Recipe", 4, 20, 40,
                                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                                            "Vivamus volutpat sem id sem iaculis convallis." +
                                            "Nullam malesuada urna nec lorem dignissim ornare." +
                                            "Donec sit amet turpis maximus, lacinia erat nec, vehicula magna." +
                                            "Mauris gravida elit sed tempor eleifend." +
                                            "Etiam sodales purus non erat egestas imperdiet.",
                                    "Pellentesque eu tincidunt nunc. Aliquam ut est porttitor, gravida velit nec, imperdiet nibh. Donec dictum, nunc non varius ultrices, nisi nunc tincidunt augue, a cursus dui lectus quis mauris. Aenean eleifend, neque vitae vestibulum posuere, leo massa venenatis felis, eget condimentum neque sem quis magna. Nulla auctor varius vehicula. Fusce tincidunt est vitae nisi malesuada, sit amet venenatis nunc volutpat. Maecenas sed dolor convallis leo consectetur pretium. Ut ut laoreet turpis, ut sagittis elit. Aliquam eu velit tortor. Phasellus venenatis euismod pharetra. Nullam ultricies ipsum scelerisque hendrerit commodo. Ut maximus libero a rutrum rutrum.",
                                    cook));

                            Image image2 = new Image(RecipesRestController.IMAGE_STORAGE_LOCATION+r2.getId(),
                                    RecipesRestController.IMAGE_STORAGE_LOCATION+r2.getId(), r2, true);
                            image2.setExtension(".jpg");
                            imageRepository.save(image2);

                            likeRelationshipRepository.save(new LikeRelationship(r2, cook));
                        }
                );
    }

    private void cleanApplicationDatabase(final CookRepository cookRepository, final RecipeRepository recipeRepository,
                               final ImageRepository imageRepository, final LikeRelationshipRepository likeRelationshipRepository){
        imageRepository.deleteAll();
        likeRelationshipRepository.deleteAll();
        recipeRepository.deleteAll();
        cookRepository.deleteAll();
    }
}
