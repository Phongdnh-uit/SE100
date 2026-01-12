package uit.se100.controllers.flight;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uit.se100.BaseIntegrationTest;
import uit.se100.dtos.flight.FlightRequest;
import uit.se100.dtos.flight.PriceSeatClassDto;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.route.Route;
import uit.se100.enums.aircraft.AircraftStatus;
import uit.se100.enums.flight.FlightStatus;
import uit.se100.enums.seat.SeatClass;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.repositories.route.RouteRepository;

@AutoConfigureMockMvc
@WithMockUser
class FlightControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private FlightRepository flightRepository;

  @Autowired private RouteRepository routeRepository;

  @Autowired private AircraftRepository aircraftRepository;

  private Route savedRoute;
  private Aircraft savedAircraft;

  @BeforeEach
  void setUp() {
    flightRepository.deleteAll();
    routeRepository.deleteAll();
    aircraftRepository.deleteAll();

    // Create default route and aircraft for tests
    savedRoute = createAndSaveRoute("Ho Chi Minh City", "Hanoi");
    savedAircraft =
        createAndSaveAircraft(
            "Narrow-body",
            180,
            "VN-A001",
            "Boeing",
            "737-800",
            2020,
            "SN001",
            AircraftStatus.ACTIVE);
  }

  private Route createAndSaveRoute(String origin, String destination) {
    Route route = new Route();
    route.setOrigin(origin);
    route.setDestination(destination);
    return routeRepository.save(route);
  }

  private Aircraft createAndSaveAircraft(
      String type,
      Integer seatCapacity,
      String registrationNumber,
      String manufacturer,
      String model,
      Integer manufactureYear,
      String serialNumber,
      AircraftStatus status) {
    Aircraft aircraft = new Aircraft();
    aircraft.setType(type);
    aircraft.setSeatCapacity(seatCapacity);
    aircraft.setRegistrationNumber(registrationNumber);
    aircraft.setManufacturer(manufacturer);
    aircraft.setModel(model);
    aircraft.setManufactureYear(manufactureYear);
    aircraft.setSerialNumber(serialNumber);
    aircraft.setStatus(status);
    return aircraftRepository.save(aircraft);
  }

  private Flight createAndSaveFlight(Route route, Aircraft aircraft, FlightStatus status) {
    Flight flight = new Flight();
    flight.setRoute(route);
    flight.setAircraft(aircraft);
    flight.setStatus(status);
    flight.setDepartureTime(Instant.now().plus(1, ChronoUnit.DAYS));
    flight.setArrivalTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
    return flightRepository.save(flight);
  }

  private List<PriceSeatClassDto> createDefaultPriceSeatClass() {
    PriceSeatClassDto economy = new PriceSeatClassDto();
    economy.setSeatClass(SeatClass.ECONOMY);
    economy.setPrice(new BigDecimal("1000000"));

    PriceSeatClassDto business = new PriceSeatClassDto();
    business.setSeatClass(SeatClass.BUSINESS);
    business.setPrice(new BigDecimal("3000000"));

    PriceSeatClassDto firstClass = new PriceSeatClassDto();
    firstClass.setSeatClass(SeatClass.FIRST_CLASS);
    firstClass.setPrice(new BigDecimal("5000000"));

    return Arrays.asList(economy, business, firstClass);
  }

  private FlightRequest createFlightRequest(
      Long routeId, Long aircraftId, FlightStatus status, List<PriceSeatClassDto> priceSeatClass) {
    FlightRequest request = new FlightRequest();
    request.setRouteId(routeId);
    request.setAircraftId(aircraftId);
    request.setStatus(status);
    request.setPriceSeatClass(priceSeatClass);
    request.setDepartureTime(Instant.now().plus(1, ChronoUnit.DAYS));
    request.setArrivalTime(Instant.now().plus(1, ChronoUnit.DAYS).plus(2, ChronoUnit.HOURS));
    return request;
  }

  private FlightRequest createFlightRequest(Long routeId, Long aircraftId, FlightStatus status) {
    return createFlightRequest(routeId, aircraftId, status, createDefaultPriceSeatClass());
  }

  @Nested
  @DisplayName("GET /flights/all")
  class FindAllTests {

    @Test
    @DisplayName("Should return empty page when no flights exist")
    void shouldReturnEmptyPageWhenNoFlightsExist() throws Exception {
      mockMvc
          .perform(get("/flights/all").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.content", hasSize(0)))
          .andExpect(jsonPath("$.data.totalElements", is(0)));
    }

    @Test
    @DisplayName("Should return all flights with pagination")
    void shouldReturnAllFlightsWithPagination() throws Exception {
      Route route2 = createAndSaveRoute("Hanoi", "Da Nang");
      Route route3 = createAndSaveRoute("Da Nang", "Ho Chi Minh City");

      Aircraft aircraft2 =
          createAndSaveAircraft(
              "Wide-body",
              300,
              "VN-A002",
              "Airbus",
              "A350-900",
              2021,
              "SN002",
              AircraftStatus.ACTIVE);
      Aircraft aircraft3 =
          createAndSaveAircraft(
              "Narrow-body",
              150,
              "VN-A003",
              "Airbus",
              "A320neo",
              2022,
              "SN003",
              AircraftStatus.ACTIVE);

      createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);
      createAndSaveFlight(route2, aircraft2, FlightStatus.FULL);
      createAndSaveFlight(route3, aircraft3, FlightStatus.DEPARTED);

      mockMvc
          .perform(
              get("/flights/all")
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
    @DisplayName("Should return all flights when all=true")
    void shouldReturnAllFlightsWhenAllIsTrue() throws Exception {
      Route route2 = createAndSaveRoute("Hanoi", "Da Nang");
      Route route3 = createAndSaveRoute("Da Nang", "Ho Chi Minh City");

      Aircraft aircraft2 =
          createAndSaveAircraft(
              "Wide-body",
              300,
              "VN-A002",
              "Airbus",
              "A350-900",
              2021,
              "SN002",
              AircraftStatus.ACTIVE);
      Aircraft aircraft3 =
          createAndSaveAircraft(
              "Narrow-body",
              150,
              "VN-A003",
              "Airbus",
              "A320neo",
              2022,
              "SN003",
              AircraftStatus.ACTIVE);

      createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);
      createAndSaveFlight(route2, aircraft2, FlightStatus.FULL);
      createAndSaveFlight(route3, aircraft3, FlightStatus.DEPARTED);

      mockMvc
          .perform(
              get("/flights/all")
                  .param("all", "true")
                  .param("size", "1")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(3)))
          .andExpect(jsonPath("$.data.totalElements", is(3)));
    }

    @Test
    @DisplayName("Should filter flights with RSQL filter by status")
    void shouldFilterFlightsWithRsqlByStatus() throws Exception {
      Route route2 = createAndSaveRoute("Hanoi", "Da Nang");

      Aircraft aircraft2 =
          createAndSaveAircraft(
              "Wide-body",
              300,
              "VN-A002",
              "Airbus",
              "A350-900",
              2021,
              "SN002",
              AircraftStatus.ACTIVE);

      createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);
      createAndSaveFlight(route2, aircraft2, FlightStatus.CANCELED);

      mockMvc
          .perform(
              get("/flights/all")
                  .param("filter", "status==CANCELED")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(1)))
          .andExpect(jsonPath("$.data.content[0].status", is("CANCELED")));
    }

    @Test
    @DisplayName("Should filter flights with RSQL filter by route origin")
    void shouldFilterFlightsWithRsqlByRouteOrigin() throws Exception {
      Route route2 = createAndSaveRoute("Hanoi", "Da Nang");

      Aircraft aircraft2 =
          createAndSaveAircraft(
              "Wide-body",
              300,
              "VN-A002",
              "Airbus",
              "A350-900",
              2021,
              "SN002",
              AircraftStatus.ACTIVE);

      createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);
      createAndSaveFlight(route2, aircraft2, FlightStatus.FULL);

      mockMvc
          .perform(
              get("/flights/all")
                  .param("filter", "route.origin=='Ho Chi Minh City'")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(1)))
          .andExpect(jsonPath("$.data.content[0].route.origin", is("Ho Chi Minh City")));
    }
  }

  @Nested
  @DisplayName("GET /flights/{id}")
  class FindByIdTests {

    @Test
    @DisplayName("Should return flight by id")
    void shouldReturnFlightById() throws Exception {
      Flight savedFlight = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);

      mockMvc
          .perform(
              get("/flights/{id}", savedFlight.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", is(savedFlight.getId().intValue())))
          .andExpect(jsonPath("$.data.status", is("OPEN")))
          .andExpect(jsonPath("$.data.route.origin", is("Ho Chi Minh City")))
          .andExpect(jsonPath("$.data.route.destination", is("Hanoi")))
          .andExpect(jsonPath("$.data.aircraft.registrationNumber", is("VN-A001")));
    }

    @Test
    @DisplayName("Should return error when flight not found")
    void shouldReturnErrorWhenFlightNotFound() throws Exception {
      mockMvc
          .perform(get("/flights/{id}", 99999L).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST /flights")
  class CreateTests {

    @Test
    @DisplayName("Should create new flight")
    void shouldCreateNewFlight() throws Exception {
      FlightRequest request =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.OPEN);

      mockMvc
          .perform(
              post("/flights")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", notNullValue()))
          .andExpect(jsonPath("$.data.status", is("OPEN")))
          .andExpect(jsonPath("$.data.route.origin", is("Ho Chi Minh City")))
          .andExpect(jsonPath("$.data.route.destination", is("Hanoi")))
          .andExpect(jsonPath("$.data.aircraft.registrationNumber", is("VN-A001")));
    }

    @Test
    @DisplayName("Should create flight with different statuses")
    void shouldCreateFlightWithDifferentStatuses() throws Exception {
      FlightRequest request =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.DELAYED);

      mockMvc
          .perform(
              post("/flights")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.status", is("DELAYED")));
    }

    @Test
    @DisplayName("Should return validation error when routeId is null")
    void shouldReturnValidationErrorWhenRouteIdIsNull() throws Exception {
      FlightRequest request =
          createFlightRequest(null, savedAircraft.getId(), FlightStatus.OPEN);

      mockMvc
          .perform(
              post("/flights")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when aircraftId is null")
    void shouldReturnValidationErrorWhenAircraftIdIsNull() throws Exception {
      FlightRequest request =
          createFlightRequest(savedRoute.getId(), null, FlightStatus.OPEN);

      mockMvc
          .perform(
              post("/flights")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when status is null")
    void shouldReturnValidationErrorWhenStatusIsNull() throws Exception {
      FlightRequest request =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), null);

      mockMvc
          .perform(
              post("/flights")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when priceSeatClass is null")
    void shouldReturnValidationErrorWhenPriceSeatClassIsNull() throws Exception {
      FlightRequest request =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.OPEN, null);

      mockMvc
          .perform(
              post("/flights")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when departureTime is null")
    void shouldReturnValidationErrorWhenDepartureTimeIsNull() throws Exception {
      FlightRequest request =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.OPEN);
      request.setDepartureTime(null);

      mockMvc
          .perform(
              post("/flights")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when arrivalTime is null")
    void shouldReturnValidationErrorWhenArrivalTimeIsNull() throws Exception {
      FlightRequest request =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.OPEN);
      request.setArrivalTime(null);

      mockMvc
          .perform(
              post("/flights")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return error when route not found")
    void shouldReturnErrorWhenRouteNotFound() throws Exception {
      FlightRequest request =
          createFlightRequest(99999L, savedAircraft.getId(), FlightStatus.OPEN);

      mockMvc
          .perform(
              post("/flights")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return error when aircraft not found")
    void shouldReturnErrorWhenAircraftNotFound() throws Exception {
      FlightRequest request =
          createFlightRequest(savedRoute.getId(), 99999L, FlightStatus.OPEN);

      mockMvc
          .perform(
              post("/flights")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("PUT /flights/{id}")
  class UpdateTests {

    @Test
    @DisplayName("Should update existing flight status")
    void shouldUpdateExistingFlightStatus() throws Exception {
      Flight savedFlight = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);

      FlightRequest updateRequest =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.DEPARTED);

      mockMvc
          .perform(
              put("/flights/{id}", savedFlight.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", is(savedFlight.getId().intValue())))
          .andExpect(jsonPath("$.data.status", is("DEPARTED")));
    }

    @Test
    @DisplayName("Should update flight with different route")
    void shouldUpdateFlightWithDifferentRoute() throws Exception {
      Flight savedFlight = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);
      Route newRoute = createAndSaveRoute("Hanoi", "Da Nang");

      FlightRequest updateRequest =
          createFlightRequest(newRoute.getId(), savedAircraft.getId(), FlightStatus.OPEN);

      mockMvc
          .perform(
              put("/flights/{id}", savedFlight.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.route.origin", is("Hanoi")))
          .andExpect(jsonPath("$.data.route.destination", is("Da Nang")));
    }

    @Test
    @DisplayName("Should update flight with different aircraft")
    void shouldUpdateFlightWithDifferentAircraft() throws Exception {
      Flight savedFlight = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);
      Aircraft newAircraft =
          createAndSaveAircraft(
              "Wide-body",
              350,
              "VN-A999",
              "Airbus",
              "A350-900",
              2022,
              "SN999",
              AircraftStatus.ACTIVE);

      FlightRequest updateRequest =
          createFlightRequest(savedRoute.getId(), newAircraft.getId(), FlightStatus.OPEN);

      mockMvc
          .perform(
              put("/flights/{id}", savedFlight.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.aircraft.registrationNumber", is("VN-A999")));
    }

    @Test
    @DisplayName("Should return error when updating non-existent flight")
    void shouldReturnErrorWhenUpdatingNonExistentFlight() throws Exception {
      FlightRequest updateRequest =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.OPEN);

      mockMvc
          .perform(
              put("/flights/{id}", 99999L)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should update flight status from OPEN to COMPLETED")
    void shouldUpdateFlightStatusFromOpenToCompleted() throws Exception {
      Flight savedFlight = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);

      FlightRequest updateRequest =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.COMPLETED);

      mockMvc
          .perform(
              put("/flights/{id}", savedFlight.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.status", is("COMPLETED")));
    }

    @Test
    @DisplayName("Should update flight status from OPEN to CANCELED")
    void shouldUpdateFlightStatusFromOpenToCanceled() throws Exception {
      Flight savedFlight = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);

      FlightRequest updateRequest =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.CANCELED);

      mockMvc
          .perform(
              put("/flights/{id}", savedFlight.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.status", is("CANCELED")));
    }

    @Test
    @DisplayName("Should update flight with different price seat class")
    void shouldUpdateFlightWithDifferentPriceSeatClass() throws Exception {
      Flight savedFlight = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);

      PriceSeatClassDto economy = new PriceSeatClassDto();
      economy.setSeatClass(SeatClass.ECONOMY);
      economy.setPrice(new BigDecimal("2000000"));

      FlightRequest updateRequest = createFlightRequest(
          savedRoute.getId(),
          savedAircraft.getId(),
          FlightStatus.OPEN,
          List.of(economy));

      mockMvc
          .perform(
              put("/flights/{id}", savedFlight.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)));
    }
  }

  @Nested
  @DisplayName("DELETE /flights/{id}")
  class DeleteTests {

    @Test
    @DisplayName("Should delete existing flight")
    void shouldDeleteExistingFlight() throws Exception {
      Flight savedFlight = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);

      mockMvc
          .perform(
              delete("/flights/{id}", savedFlight.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")));

      // Verify the flight is deleted
      mockMvc
          .perform(
              get("/flights/{id}", savedFlight.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should succeed when deleting non-existent flight")
    void shouldSucceedWhenDeletingNonExistentFlight() throws Exception {
      // JPA's deleteById is silently successful even if the entity doesn't exist
      mockMvc
          .perform(delete("/flights/{id}", 99999L).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)));
    }
  }

  @Nested
  @DisplayName("DELETE /flights/bulk")
  class BulkDeleteTests {

    @Test
    @DisplayName("Should delete multiple flights")
    void shouldDeleteMultipleFlights() throws Exception {
      Route route2 = createAndSaveRoute("Hanoi", "Da Nang");
      Route route3 = createAndSaveRoute("Da Nang", "Ho Chi Minh City");

      Aircraft aircraft2 =
          createAndSaveAircraft(
              "Wide-body",
              300,
              "VN-A002",
              "Airbus",
              "A350-900",
              2021,
              "SN002",
              AircraftStatus.ACTIVE);
      Aircraft aircraft3 =
          createAndSaveAircraft(
              "Narrow-body",
              150,
              "VN-A003",
              "Airbus",
              "A320neo",
              2022,
              "SN003",
              AircraftStatus.ACTIVE);

      Flight flight1 = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);
      Flight flight2 = createAndSaveFlight(route2, aircraft2, FlightStatus.FULL);
      Flight flight3 = createAndSaveFlight(route3, aircraft3, FlightStatus.DEPARTED);

      mockMvc
          .perform(
              delete("/flights/bulk")
                  .param("ids", flight1.getId().toString(), flight2.getId().toString())
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")));

      // Verify deleted flights
      mockMvc
          .perform(get("/flights/{id}", flight1.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());

      mockMvc
          .perform(get("/flights/{id}", flight2.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());

      // Verify remaining flight still exists
      mockMvc
          .perform(get("/flights/{id}", flight3.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.status", is("DEPARTED")));
    }

    @Test
    @DisplayName("Should handle bulk delete with single id")
    void shouldHandleBulkDeleteWithSingleId() throws Exception {
      Flight flight = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);

      mockMvc
          .perform(
              delete("/flights/bulk")
                  .param("ids", flight.getId().toString())
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")));

      // Verify the flight is deleted
      mockMvc
          .perform(get("/flights/{id}", flight.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("Complete CRUD Workflow")
  class CrudWorkflowTests {

    @Test
    @DisplayName("Should perform complete CRUD workflow")
    void shouldPerformCompleteCrudWorkflow() throws Exception {
      // Create
      FlightRequest createRequest =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.OPEN);

      MvcResult createResult =
          mockMvc
              .perform(
                  post("/flights")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data.status", is("OPEN")))
              .andExpect(jsonPath("$.data.route.origin", is("Ho Chi Minh City")))
              .andReturn();

      Integer createdId =
          objectMapper
              .readTree(createResult.getResponse().getContentAsString())
              .path("data")
              .path("id")
              .asInt();

      // Read
      mockMvc
          .perform(get("/flights/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.status", is("OPEN")))
          .andExpect(jsonPath("$.data.route.origin", is("Ho Chi Minh City")))
          .andExpect(jsonPath("$.data.route.destination", is("Hanoi")))
          .andExpect(jsonPath("$.data.aircraft.registrationNumber", is("VN-A001")));

      // Update
      FlightRequest updateRequest =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.DEPARTED);

      mockMvc
          .perform(
              put("/flights/{id}", createdId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.status", is("DEPARTED")));

      // Verify Update
      mockMvc
          .perform(get("/flights/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.status", is("DEPARTED")));

      // Delete
      mockMvc
          .perform(delete("/flights/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());

      // Verify Delete
      mockMvc
          .perform(get("/flights/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should perform workflow with status transitions")
    void shouldPerformWorkflowWithStatusTransitions() throws Exception {
      // Create flight with OPEN status
      FlightRequest createRequest =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.OPEN);

      MvcResult createResult =
          mockMvc
              .perform(
                  post("/flights")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data.status", is("OPEN")))
              .andReturn();

      Integer createdId =
          objectMapper
              .readTree(createResult.getResponse().getContentAsString())
              .path("data")
              .path("id")
              .asInt();

      // Update to FULL
      FlightRequest fullRequest =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.FULL);
      mockMvc
          .perform(
              put("/flights/{id}", createdId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(fullRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.status", is("FULL")));

      // Update to DEPARTED
      FlightRequest departedRequest =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.DEPARTED);
      mockMvc
          .perform(
              put("/flights/{id}", createdId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(departedRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.status", is("DEPARTED")));

      // Update to COMPLETED
      FlightRequest completedRequest =
          createFlightRequest(savedRoute.getId(), savedAircraft.getId(), FlightStatus.COMPLETED);
      mockMvc
          .perform(
              put("/flights/{id}", createdId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(completedRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.status", is("COMPLETED")));
    }
  }
}
