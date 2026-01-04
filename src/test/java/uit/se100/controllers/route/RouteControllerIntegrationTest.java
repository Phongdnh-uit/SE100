package uit.se100.controllers.route;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uit.se100.BaseIntegrationTest;
import uit.se100.dtos.route.RouteRequest;
import uit.se100.entities.route.Route;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.repositories.route.RouteRepository;
import uit.se100.repositories.schedule.ScheduleRepository;

@AutoConfigureMockMvc
class RouteControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private RouteRepository routeRepository;

  @Autowired private AircraftRepository aircraftRepository;

  @Autowired private FlightRepository flightRepository;

  @Autowired private ScheduleRepository scheduleRepository;

  @BeforeEach
  void setUp() {

    scheduleRepository.deleteAll();
    flightRepository.deleteAll();
    routeRepository.deleteAll();
    aircraftRepository.deleteAll();
  }

  private Route createAndSaveRoute(String origin, String destination) {
    Route route = new Route();
    route.setOrigin(origin);
    route.setDestination(destination);
    return routeRepository.save(route);
  }

  private RouteRequest createRouteRequest(String origin, String destination) {
    RouteRequest request = new RouteRequest();
    request.setOrigin(origin);
    request.setDestination(destination);
    return request;
  }

  @Nested
  @DisplayName("GET /routes/all")
  class FindAllTests {

    @Test
    @DisplayName("Should return empty page when no routes exist")
    void shouldReturnEmptyPageWhenNoRoutesExist() throws Exception {
      mockMvc
          .perform(get("/routes/all").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.content", hasSize(0)))
          .andExpect(jsonPath("$.data.totalElements", is(0)));
    }

    @Test
    @DisplayName("Should return all routes with pagination")
    void shouldReturnAllRoutesWithPagination() throws Exception {
      createAndSaveRoute("Hanoi", "Ho Chi Minh City");
      createAndSaveRoute("Da Nang", "Hanoi");
      createAndSaveRoute("Ho Chi Minh City", "Da Nang");

      mockMvc
          .perform(
              get("/routes/all")
                  .param("page", "0")
                  .param("size", "2")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(2)))
          .andExpect(jsonPath("$.data.totalElements", is(3)))
          .andExpect(jsonPath("$.data.totalPages", is(2)))
          .andExpect(jsonPath("$.data.page", is(0)))
          .andExpect(jsonPath("$.data.size", is(2)));
    }

    @Test
    @DisplayName("Should return all routes when all=true")
    void shouldReturnAllRoutesWhenAllIsTrue() throws Exception {
      createAndSaveRoute("Hanoi", "Ho Chi Minh City");
      createAndSaveRoute("Da Nang", "Hanoi");
      createAndSaveRoute("Ho Chi Minh City", "Da Nang");

      mockMvc
          .perform(
              get("/routes/all")
                  .param("all", "true")
                  .param("size", "1")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(3)))
          .andExpect(jsonPath("$.data.totalElements", is(3)));
    }

    @Test
    @DisplayName("Should filter routes with RSQL filter")
    void shouldFilterRoutesWithRsql() throws Exception {
      createAndSaveRoute("Hanoi", "Ho Chi Minh City");
      createAndSaveRoute("Da Nang", "Hanoi");
      createAndSaveRoute("Ho Chi Minh City", "Da Nang");

      mockMvc
          .perform(
              get("/routes/all")
                  .param("filter", "origin==Hanoi")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(1)))
          .andExpect(jsonPath("$.data.content[0].origin", is("Hanoi")))
          .andExpect(jsonPath("$.data.content[0].destination", is("Ho Chi Minh City")));
    }
  }

  @Nested
  @DisplayName("GET /routes/{id}")
  class FindByIdTests {

    @Test
    @DisplayName("Should return route by id")
    void shouldReturnRouteById() throws Exception {
      Route savedRoute = createAndSaveRoute("Hanoi", "Ho Chi Minh City");

      mockMvc
          .perform(get("/routes/{id}", savedRoute.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", is(savedRoute.getId().intValue())))
          .andExpect(jsonPath("$.data.origin", is("Hanoi")))
          .andExpect(jsonPath("$.data.destination", is("Ho Chi Minh City")));
    }

    @Test
    @DisplayName("Should return error when route not found")
    void shouldReturnErrorWhenRouteNotFound() throws Exception {
      mockMvc
          .perform(get("/routes/{id}", 99999L).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST /routes")
  class CreateTests {

    @Test
    @DisplayName("Should create new route")
    void shouldCreateNewRoute() throws Exception {
      RouteRequest request = createRouteRequest("Tokyo", "Osaka");

      mockMvc
          .perform(
              post("/routes")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", notNullValue()))
          .andExpect(jsonPath("$.data.origin", is("Tokyo")))
          .andExpect(jsonPath("$.data.destination", is("Osaka")));
    }

    @Test
    @DisplayName("Should return validation error when origin is blank")
    void shouldReturnValidationErrorWhenOriginIsBlank() throws Exception {
      RouteRequest request = createRouteRequest("", "Osaka");

      mockMvc
          .perform(
              post("/routes")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when destination is blank")
    void shouldReturnValidationErrorWhenDestinationIsBlank() throws Exception {
      RouteRequest request = createRouteRequest("Tokyo", "");

      mockMvc
          .perform(
              post("/routes")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when origin is null")
    void shouldReturnValidationErrorWhenOriginIsNull() throws Exception {
      RouteRequest request = new RouteRequest();
      request.setDestination("Osaka");

      mockMvc
          .perform(
              post("/routes")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PUT /routes/{id}")
  class UpdateTests {

    @Test
    @DisplayName("Should update existing route")
    void shouldUpdateExistingRoute() throws Exception {
      Route savedRoute = createAndSaveRoute("Hanoi", "Ho Chi Minh City");
      RouteRequest updateRequest = createRouteRequest("Da Nang", "Hue");

      mockMvc
          .perform(
              put("/routes/{id}", savedRoute.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", is(savedRoute.getId().intValue())))
          .andExpect(jsonPath("$.data.origin", is("Da Nang")))
          .andExpect(jsonPath("$.data.destination", is("Hue")));
    }

    @Test
    @DisplayName("Should return error when updating non-existent route")
    void shouldReturnErrorWhenUpdatingNonExistentRoute() throws Exception {
      RouteRequest updateRequest = createRouteRequest("Da Nang", "Hue");

      mockMvc
          .perform(
              put("/routes/{id}", 99999L)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return validation error when update origin is blank")
    void shouldReturnValidationErrorWhenUpdateOriginIsBlank() throws Exception {
      Route savedRoute = createAndSaveRoute("Hanoi", "Ho Chi Minh City");
      RouteRequest updateRequest = createRouteRequest("", "Hue");

      mockMvc
          .perform(
              put("/routes/{id}", savedRoute.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("DELETE /routes/{id}")
  class DeleteTests {

    @Test
    @DisplayName("Should delete existing route")
    void shouldDeleteExistingRoute() throws Exception {
      Route savedRoute = createAndSaveRoute("Hanoi", "Ho Chi Minh City");

      mockMvc
          .perform(
              delete("/routes/{id}", savedRoute.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")));

      // Verify the route is deleted
      mockMvc
          .perform(get("/routes/{id}", savedRoute.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("DELETE /routes/bulk")
  class BulkDeleteTests {

    @Test
    @DisplayName("Should delete multiple routes")
    void shouldDeleteMultipleRoutes() throws Exception {
      Route route1 = createAndSaveRoute("Hanoi", "Ho Chi Minh City");
      Route route2 = createAndSaveRoute("Da Nang", "Hue");
      Route route3 = createAndSaveRoute("Tokyo", "Osaka");

      mockMvc
          .perform(
              delete("/routes/bulk")
                  .param("ids", route1.getId().toString(), route2.getId().toString())
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")));

      // Verify deleted routes
      mockMvc
          .perform(get("/routes/{id}", route1.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());

      mockMvc
          .perform(get("/routes/{id}", route2.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());

      // Verify remaining route still exists
      mockMvc
          .perform(get("/routes/{id}", route3.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.origin", is("Tokyo")));
    }
  }

  @Nested
  @DisplayName("Complete CRUD Workflow")
  class CrudWorkflowTests {

    @Test
    @DisplayName("Should perform complete CRUD workflow")
    void shouldPerformCompleteCrudWorkflow() throws Exception {
      // Create
      RouteRequest createRequest = createRouteRequest("Singapore", "Kuala Lumpur");
      MvcResult createResult =
          mockMvc
              .perform(
                  post("/routes")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data.origin", is("Singapore")))
              .andReturn();

      Integer createdId =
          objectMapper
              .readTree(createResult.getResponse().getContentAsString())
              .path("data")
              .path("id")
              .asInt();

      // Read
      mockMvc
          .perform(get("/routes/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.origin", is("Singapore")))
          .andExpect(jsonPath("$.data.destination", is("Kuala Lumpur")));

      // Update
      RouteRequest updateRequest = createRouteRequest("Bangkok", "Phuket");
      mockMvc
          .perform(
              put("/routes/{id}", createdId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.origin", is("Bangkok")))
          .andExpect(jsonPath("$.data.destination", is("Phuket")));

      // Verify Update
      mockMvc
          .perform(get("/routes/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.origin", is("Bangkok")));

      // Delete
      mockMvc
          .perform(delete("/routes/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());

      // Verify Delete
      mockMvc
          .perform(get("/routes/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }
}
