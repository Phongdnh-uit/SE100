package uit.se100.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uit.se100.dtos.user.UserRequest;
import uit.se100.dtos.user.UserResponse;
import uit.se100.entities.authentication.User;
import uit.se100.hooks.user.UserHook;
import uit.se100.mappers.user.UserMapper;
import uit.se100.repositories.authentication.UserRepository;
import uit.se100.services.CrudService;
import uit.se100.services.GenericService;

@RequiredArgsConstructor
@Configuration
public class ServiceRegistration {
  private final ApplicationContext context;

  @Bean
  CrudService<User, Long, UserRequest, UserResponse> permissionService() {
    return new GenericService<User, Long, UserRequest, UserResponse>(
        context.getBean(UserRepository.class),
        context.getBean(UserMapper.class),
        context.getBean(UserHook.class));
  }
}
