package recipes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Me on 22/05/2017.
 */
public interface RecipeRepository extends JpaRepository<Recipe, Long>{

    Collection<Recipe> findByCookUsernameIgnoreCase(String username);
    Collection<Recipe> findByCookUsernameIgnoreCaseOrderByDateCreatedDesc(String username);
    Collection<Recipe> findByTitle(String title);

    Collection<Recipe> findTop20ByOrderByDateCreatedDesc();
    Collection<Recipe> findTop50ByOrderByDateCreatedDesc();
    Collection<Recipe> findTop100ByOrderByDateCreatedDesc();

}
