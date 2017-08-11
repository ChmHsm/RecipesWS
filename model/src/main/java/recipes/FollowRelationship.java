package recipes;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * Created by Me on 11/08/2017.
 */
@Entity
public class FollowRelationship {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Cook follower;

    @ManyToOne
    private Cook followee;

    public FollowRelationship() {
        //JPA
    }

    public Cook getFollower() {
        return follower;
    }

    public void setFollower(Cook follower) {
        this.follower = follower;
    }

    public Long getId() {
        return id;
    }

    public Cook getFollowee() {
        return followee;
    }

    public void setFollowee(Cook followee) {
        this.followee = followee;
    }

    public FollowRelationship(Cook cook1, Cook cook2) {
        this.follower = cook1;
        this.followee = cook2;
    }
}
