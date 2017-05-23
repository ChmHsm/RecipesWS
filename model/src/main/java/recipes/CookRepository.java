package recipes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.Optional;

/**
 * Created by Me on 22/05/2017.
 */
public interface CookRepository extends JpaRepository<Cook, Long>{
    Optional<Cook> findByUsernameIgnoreCase(String username);

}
