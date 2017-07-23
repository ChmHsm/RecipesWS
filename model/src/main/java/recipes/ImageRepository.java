package recipes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Me on 29/05/2017.
 */
public interface ImageRepository extends JpaRepository<Image, Long> {

    public Optional<Image> findByCookUsername(String username);
    public Collection<Image> findByRecipe(Recipe recipe);
    public Collection<Image> findByCook(Cook cook);

    Collection<Image> findTop20ByOrderByDateCreatedDesc();
    Collection<Image> findTop50ByOrderByDateCreatedDesc();
    Collection<Image> findTop100ByOrderByDateCreatedDesc();
}
