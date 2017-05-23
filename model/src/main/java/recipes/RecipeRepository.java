package recipes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

/**
 * Created by Me on 22/05/2017.
 */
public interface RecipeRepository extends JpaRepository<Recipe, Long>{

    Collection<Recipe> findByCookUsernameIgnoreCase(String username);
    Collection<Recipe> findByTitle(String title);
}
