package uit.se100.hooks.passenger;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uit.se100.dtos.passenger.PassengerRequest;
import uit.se100.dtos.passenger.PassengerResponse;
import uit.se100.dtos.user.UserRequest;
import uit.se100.dtos.user.UserResponse;
import uit.se100.entities.authentication.User;
import uit.se100.entities.passenger.Passenger;
import uit.se100.enums.RoleEnum;
import uit.se100.exceptions.errors.ApiException;
import uit.se100.exceptions.errors.ErrorCode;
import uit.se100.hooks.GenericHook;
import uit.se100.repositories.authentication.UserRepository;
import uit.se100.services.CrudService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class PassengerHook implements GenericHook<Passenger, Long, PassengerRequest, PassengerResponse> {

    private final CrudService<User, Long, UserRequest, UserResponse> userService;
    private final UserRepository userRepository;

    @Override
    public void enrichCreate(PassengerRequest input, Passenger entity, Map<String, Object> context) {
        UserRequest userRequest = input.accountRequest();
        userRequest.setRole(RoleEnum.PASSENGER);

//        Save user request
        UserResponse userResponse = userService.create(userRequest);

//        update user in entity
        User userInDb = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new ApiException(ErrorCode.RESOURCE_NOT_FOUND));

        entity.setUser(userInDb);
    }


}
