package recipes.security.resourceEntities;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceSupport;
import recipes.Cook;
import recipes.GeneralRestController;
import recipes.Image;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

/**
 * Created by Me on 14/08/2017.
 */
public class ImageResource extends ResourceSupport {
    private final Image image;

    public ImageResource(Image image) {
        this.image = image;
        String username = image.getCook().getUsername();
        this.add(new Link(String.valueOf(image.getId()), "image-id"));
        this.add(linkTo(GeneralRestController.class, username).withRel("general"));
        this.add(linkTo(
                methodOn(GeneralRestController.class, username).getImageEntity(null, image.getId())).withSelfRel());
    }

    public Image getImage() {
        return image;
    }
}
