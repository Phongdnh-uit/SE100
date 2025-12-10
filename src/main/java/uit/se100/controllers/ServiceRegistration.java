package uit.se100.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uit.se100.dtos.aircraft.AircraftRequest;
import uit.se100.dtos.aircraft.AircraftResponse;
import uit.se100.dtos.passenger.PassengerRequest;
import uit.se100.dtos.passenger.PassengerResponse;
import uit.se100.dtos.user.UserRequest;
import uit.se100.dtos.user.UserResponse;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.entities.authentication.User;
import uit.se100.entities.passenger.Passenger;
import uit.se100.hooks.aircraft.AircraftHook;
import uit.se100.hooks.passenger.PassengerHook;
import uit.se100.hooks.user.UserHook;
import uit.se100.mappers.aircraft.AircraftMapper;
import uit.se100.mappers.passenger.user.PassengerMapper;
import uit.se100.mappers.user.UserMapper;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.authentication.UserRepository;
import uit.se100.repositories.passenger.PassengerRepository;
import uit.se100.services.CrudService;
import uit.se100.services.GenericService;

@RequiredArgsConstructor
@Configuration
public class ServiceRegistration {
    private final ApplicationContext context;

    @Bean
    CrudService<User, Long, UserRequest, UserResponse> userService() {
        return new GenericService<User, Long, UserRequest, UserResponse>(
                context.getBean(UserRepository.class),
                context.getBean(UserMapper.class),
                context.getBean(UserHook.class));
    }

    @Bean
    CrudService<Aircraft, Long, AircraftRequest, AircraftResponse> aircraftService() {
        return new GenericService<Aircraft, Long, AircraftRequest, AircraftResponse>(
                context.getBean(AircraftRepository.class),
                context.getBean(AircraftMapper.class),
                context.getBean(AircraftHook.class));
    }

    @Bean
    CrudService<Passenger, Long, PassengerRequest, PassengerResponse> passengerService() {
        return new GenericService<Passenger, Long, PassengerRequest, PassengerResponse>(
                context.getBean(PassengerRepository.class),
                context.getBean(PassengerMapper.class),
                context.getBean(PassengerHook.class));
    }
}
