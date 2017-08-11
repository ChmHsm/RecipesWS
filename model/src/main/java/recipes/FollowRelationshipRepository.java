package recipes;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;

/**
 * Created by Me on 11/08/2017.
 */
public interface FollowRelationshipRepository extends JpaRepository<FollowRelationship, Long> {

    Collection<FollowRelationship> findByFollower(Cook follower);
    Collection<FollowRelationship> findByFollowee(Cook followee);
}
