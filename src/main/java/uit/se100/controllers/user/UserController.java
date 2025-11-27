package uit.se100.controllers.user;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uit.se100.controllers.GenericController;
import uit.se100.dtos.user.UserRequest;
import uit.se100.dtos.user.UserResponse;
import uit.se100.entities.authentication.User;
import uit.se100.services.CrudService;

@Tag(name = "User")
@RequestMapping("/users")
@RestController
public class UserController extends GenericController<User, Long, UserRequest, UserResponse> {

  public UserController(CrudService<User, Long, UserRequest, UserResponse> service) {
    super(service);
  }
}
