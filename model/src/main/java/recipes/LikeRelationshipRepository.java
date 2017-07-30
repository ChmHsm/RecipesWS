package recipes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

/**
 * Created by Me on 30/07/2017.
 */
public interface LikeRelationshipRepository extends JpaRepository<LikeRelationship, Long> {

    Collection<LikeRelationship> findByCookUsername(String username);
    Collection<LikeRelationship> findByRecipe(Recipe recipe);
}
