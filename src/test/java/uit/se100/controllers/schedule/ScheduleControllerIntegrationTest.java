package uit.se100.controllers.schedule;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
import uit.se100.dtos.schedule.ScheduleRequest;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.entities.flight.Flight;
import uit.se100.entities.route.Route;
import uit.se100.entities.schedule.Schedule;
import uit.se100.enums.aircraft.AircraftStatus;
import uit.se100.enums.flight.FlightStatus;
import uit.se100.repositories.aircraft.AircraftRepository;
import uit.se100.repositories.flight.FlightRepository;
import uit.se100.repositories.route.RouteRepository;
import uit.se100.repositories.schedule.ScheduleRepository;

@AutoConfigureMockMvc
class ScheduleControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ScheduleRepository scheduleRepository;

  @Autowired private FlightRepository flightRepository;

  @Autowired private RouteRepository routeRepository;

  @Autowired private AircraftRepository aircraftRepository;

  private Flight savedFlight;
  private Route savedRoute;
  private Aircraft savedAircraft;
  private Instant baseTime;

  @BeforeEach
  void setUp() {
    // Clear in correct order due to foreign key constraints
    scheduleRepository.deleteAll();
    flightRepository.deleteAll();
    routeRepository.deleteAll();
    aircraftRepository.deleteAll();

    baseTime = Instant.now().truncatedTo(ChronoUnit.SECONDS);

    // Create default route and aircraft
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
    savedFlight = createAndSaveFlight(savedRoute, savedAircraft, FlightStatus.OPEN);
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
    return flightRepository.save(flight);
  }

  private Schedule createAndSaveSchedule(
      Flight flight, Instant departureTime, Instant arrivalTime) {
    Schedule schedule = new Schedule();
    schedule.setFlight(flight);
    schedule.setDepartureTime(departureTime);
    schedule.setArrivalTime(arrivalTime);
    return scheduleRepository.save(schedule);
  }

  private ScheduleRequest createScheduleRequest(
      Long flightId, Instant departureTime, Instant arrivalTime) {
    ScheduleRequest request = new ScheduleRequest();
    request.setFlightId(flightId);
    request.setDepartureTime(departureTime);
    request.setArrivalTime(arrivalTime);
    return request;
  }

  @Nested
  @DisplayName("GET /schedules/all")
  class FindAllTests {

    @Test
    @DisplayName("Should return empty page when no schedules exist")
    void shouldReturnEmptyPageWhenNoSchedulesExist() throws Exception {
      mockMvc
          .perform(get("/schedules/all").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.content", hasSize(0)))
          .andExpect(jsonPath("$.data.totalElements", is(0)));
    }

    @Test
    @DisplayName("Should return all schedules with pagination")
    void shouldReturnAllSchedulesWithPagination() throws Exception {
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

      Flight flight2 = createAndSaveFlight(route2, aircraft2, FlightStatus.FULL);
      Flight flight3 = createAndSaveFlight(route3, aircraft3, FlightStatus.DEPARTED);

      createAndSaveSchedule(
          savedFlight, baseTime.plus(1, ChronoUnit.HOURS), baseTime.plus(3, ChronoUnit.HOURS));
      createAndSaveSchedule(
          flight2, baseTime.plus(4, ChronoUnit.HOURS), baseTime.plus(6, ChronoUnit.HOURS));
      createAndSaveSchedule(
          flight3, baseTime.plus(7, ChronoUnit.HOURS), baseTime.plus(9, ChronoUnit.HOURS));

      mockMvc
          .perform(
              get("/schedules/all")
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
    @DisplayName("Should return all schedules when all=true")
    void shouldReturnAllSchedulesWhenAllIsTrue() throws Exception {
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

      Flight flight2 = createAndSaveFlight(route2, aircraft2, FlightStatus.FULL);
      Flight flight3 = createAndSaveFlight(route3, aircraft3, FlightStatus.DEPARTED);

      createAndSaveSchedule(
          savedFlight, baseTime.plus(1, ChronoUnit.HOURS), baseTime.plus(3, ChronoUnit.HOURS));
      createAndSaveSchedule(
          flight2, baseTime.plus(4, ChronoUnit.HOURS), baseTime.plus(6, ChronoUnit.HOURS));
      createAndSaveSchedule(
          flight3, baseTime.plus(7, ChronoUnit.HOURS), baseTime.plus(9, ChronoUnit.HOURS));

      mockMvc
          .perform(
              get("/schedules/all")
                  .param("all", "true")
                  .param("size", "1")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(3)))
          .andExpect(jsonPath("$.data.totalElements", is(3)));
    }

    @Test
    @DisplayName("Should filter schedules with RSQL filter by flight status")
    void shouldFilterSchedulesWithRsqlByFlightStatus() throws Exception {
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

      Flight flight2 = createAndSaveFlight(route2, aircraft2, FlightStatus.CANCELED);

      createAndSaveSchedule(
          savedFlight, baseTime.plus(1, ChronoUnit.HOURS), baseTime.plus(3, ChronoUnit.HOURS));
      createAndSaveSchedule(
          flight2, baseTime.plus(4, ChronoUnit.HOURS), baseTime.plus(6, ChronoUnit.HOURS));

      mockMvc
          .perform(
              get("/schedules/all")
                  .param("filter", "flight.status==CANCELED")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(1)))
          .andExpect(jsonPath("$.data.content[0].flight.status", is("CANCELED")));
    }
  }

  @Nested
  @DisplayName("GET /schedules/{id}")
  class FindByIdTests {

    @Test
    @DisplayName("Should return schedule by id")
    void shouldReturnScheduleById() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);
      Schedule savedSchedule = createAndSaveSchedule(savedFlight, departureTime, arrivalTime);

      mockMvc
          .perform(
              get("/schedules/{id}", savedSchedule.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", is(savedSchedule.getId().intValue())))
          .andExpect(jsonPath("$.data.departureTime", notNullValue()))
          .andExpect(jsonPath("$.data.arrivalTime", notNullValue()))
          .andExpect(jsonPath("$.data.flight.status", is("OPEN")))
          .andExpect(jsonPath("$.data.flight.route.origin", is("Ho Chi Minh City")))
          .andExpect(jsonPath("$.data.flight.route.destination", is("Hanoi")));
    }

    @Test
    @DisplayName("Should return error when schedule not found")
    void shouldReturnErrorWhenScheduleNotFound() throws Exception {
      mockMvc
          .perform(get("/schedules/{id}", 99999L).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST /schedules")
  class CreateTests {

    @Test
    @DisplayName("Should create new schedule")
    void shouldCreateNewSchedule() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);
      ScheduleRequest request =
          createScheduleRequest(savedFlight.getId(), departureTime, arrivalTime);

      mockMvc
          .perform(
              post("/schedules")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", notNullValue()))
          .andExpect(jsonPath("$.data.departureTime", notNullValue()))
          .andExpect(jsonPath("$.data.arrivalTime", notNullValue()))
          .andExpect(jsonPath("$.data.flight.status", is("OPEN")))
          .andExpect(jsonPath("$.data.flight.route.origin", is("Ho Chi Minh City")));
    }

    @Test
    @DisplayName("Should create schedule with different times")
    void shouldCreateScheduleWithDifferentTimes() throws Exception {
      Instant departureTime = baseTime.plus(5, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(8, ChronoUnit.HOURS);
      ScheduleRequest request =
          createScheduleRequest(savedFlight.getId(), departureTime, arrivalTime);

      mockMvc
          .perform(
              post("/schedules")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.departureTime", notNullValue()))
          .andExpect(jsonPath("$.data.arrivalTime", notNullValue()));
    }

    @Test
    @DisplayName("Should return validation error when flightId is null")
    void shouldReturnValidationErrorWhenFlightIdIsNull() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);
      ScheduleRequest request = createScheduleRequest(null, departureTime, arrivalTime);

      mockMvc
          .perform(
              post("/schedules")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when departureTime is null")
    void shouldReturnValidationErrorWhenDepartureTimeIsNull() throws Exception {
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);
      ScheduleRequest request = createScheduleRequest(savedFlight.getId(), null, arrivalTime);

      mockMvc
          .perform(
              post("/schedules")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when arrivalTime is null")
    void shouldReturnValidationErrorWhenArrivalTimeIsNull() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      ScheduleRequest request = createScheduleRequest(savedFlight.getId(), departureTime, null);

      mockMvc
          .perform(
              post("/schedules")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return error when flight not found")
    void shouldReturnErrorWhenFlightNotFound() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);
      ScheduleRequest request = createScheduleRequest(99999L, departureTime, arrivalTime);

      mockMvc
          .perform(
              post("/schedules")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("PUT /schedules/{id}")
  class UpdateTests {

    @Test
    @DisplayName("Should update existing schedule times")
    void shouldUpdateExistingScheduleTimes() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);
      Schedule savedSchedule = createAndSaveSchedule(savedFlight, departureTime, arrivalTime);

      Instant newDepartureTime = baseTime.plus(5, ChronoUnit.HOURS);
      Instant newArrivalTime = baseTime.plus(8, ChronoUnit.HOURS);
      ScheduleRequest updateRequest =
          createScheduleRequest(savedFlight.getId(), newDepartureTime, newArrivalTime);

      mockMvc
          .perform(
              put("/schedules/{id}", savedSchedule.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", is(savedSchedule.getId().intValue())))
          .andExpect(jsonPath("$.data.departureTime", notNullValue()))
          .andExpect(jsonPath("$.data.arrivalTime", notNullValue()));
    }

    @Test
    @DisplayName("Should update schedule with different flight")
    void shouldUpdateScheduleWithDifferentFlight() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);
      Schedule savedSchedule = createAndSaveSchedule(savedFlight, departureTime, arrivalTime);

      Route newRoute = createAndSaveRoute("Hanoi", "Da Nang");
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
      Flight newFlight = createAndSaveFlight(newRoute, newAircraft, FlightStatus.FULL);

      ScheduleRequest updateRequest =
          createScheduleRequest(newFlight.getId(), departureTime, arrivalTime);

      mockMvc
          .perform(
              put("/schedules/{id}", savedSchedule.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.flight.status", is("FULL")))
          .andExpect(jsonPath("$.data.flight.route.origin", is("Hanoi")))
          .andExpect(jsonPath("$.data.flight.route.destination", is("Da Nang")));
    }

    @Test
    @DisplayName("Should return error when updating non-existent schedule")
    void shouldReturnErrorWhenUpdatingNonExistentSchedule() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);
      ScheduleRequest updateRequest =
          createScheduleRequest(savedFlight.getId(), departureTime, arrivalTime);

      mockMvc
          .perform(
              put("/schedules/{id}", 99999L)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("DELETE /schedules/{id}")
  class DeleteTests {

    @Test
    @DisplayName("Should delete existing schedule")
    void shouldDeleteExistingSchedule() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);
      Schedule savedSchedule = createAndSaveSchedule(savedFlight, departureTime, arrivalTime);

      mockMvc
          .perform(
              delete("/schedules/{id}", savedSchedule.getId())
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")));
    }
  }

  @Nested
  @DisplayName("DELETE /schedules/bulk")
  class BulkDeleteTests {

    @Test
    @DisplayName("Should delete multiple schedules")
    void shouldDeleteMultipleSchedules() throws Exception {
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

      Flight flight2 = createAndSaveFlight(route2, aircraft2, FlightStatus.FULL);
      Flight flight3 = createAndSaveFlight(route3, aircraft3, FlightStatus.DEPARTED);

      Schedule schedule1 =
          createAndSaveSchedule(
              savedFlight, baseTime.plus(1, ChronoUnit.HOURS), baseTime.plus(3, ChronoUnit.HOURS));
      Schedule schedule2 =
          createAndSaveSchedule(
              flight2, baseTime.plus(4, ChronoUnit.HOURS), baseTime.plus(6, ChronoUnit.HOURS));
      Schedule schedule3 =
          createAndSaveSchedule(
              flight3, baseTime.plus(7, ChronoUnit.HOURS), baseTime.plus(9, ChronoUnit.HOURS));

      mockMvc
          .perform(
              delete("/schedules/bulk")
                  .param("ids", schedule1.getId().toString(), schedule2.getId().toString())
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")));

      // Verify deleted schedules
      mockMvc
          .perform(
              get("/schedules/{id}", schedule1.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());

      mockMvc
          .perform(
              get("/schedules/{id}", schedule2.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());

      // Verify remaining schedule still exists
      mockMvc
          .perform(
              get("/schedules/{id}", schedule3.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.flight.status", is("DEPARTED")));
    }
  }

  @Nested
  @DisplayName("Complete CRUD Workflow")
  class CrudWorkflowTests {

    @Test
    @DisplayName("Should perform complete CRUD workflow")
    void shouldPerformCompleteCrudWorkflow() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);

      // Create
      ScheduleRequest createRequest =
          createScheduleRequest(savedFlight.getId(), departureTime, arrivalTime);

      MvcResult createResult =
          mockMvc
              .perform(
                  post("/schedules")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data.departureTime", notNullValue()))
              .andExpect(jsonPath("$.data.arrivalTime", notNullValue()))
              .andExpect(jsonPath("$.data.flight.route.origin", is("Ho Chi Minh City")))
              .andReturn();

      Integer createdId =
          objectMapper
              .readTree(createResult.getResponse().getContentAsString())
              .path("data")
              .path("id")
              .asInt();

      // Read
      mockMvc
          .perform(get("/schedules/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.departureTime", notNullValue()))
          .andExpect(jsonPath("$.data.arrivalTime", notNullValue()))
          .andExpect(jsonPath("$.data.flight.route.origin", is("Ho Chi Minh City")))
          .andExpect(jsonPath("$.data.flight.route.destination", is("Hanoi")))
          .andExpect(jsonPath("$.data.flight.aircraft.registrationNumber", is("VN-A001")));

      // Update
      Instant newDepartureTime = baseTime.plus(10, ChronoUnit.HOURS);
      Instant newArrivalTime = baseTime.plus(12, ChronoUnit.HOURS);
      ScheduleRequest updateRequest =
          createScheduleRequest(savedFlight.getId(), newDepartureTime, newArrivalTime);

      mockMvc
          .perform(
              put("/schedules/{id}", createdId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.departureTime", notNullValue()))
          .andExpect(jsonPath("$.data.arrivalTime", notNullValue()));

      // Verify Update
      mockMvc
          .perform(get("/schedules/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.departureTime", notNullValue()))
          .andExpect(jsonPath("$.data.arrivalTime", notNullValue()));

      // Delete
      mockMvc
          .perform(delete("/schedules/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should create and update schedule with different flight")
    void shouldCreateAndUpdateScheduleWithDifferentFlight() throws Exception {
      Instant departureTime = baseTime.plus(1, ChronoUnit.HOURS);
      Instant arrivalTime = baseTime.plus(3, ChronoUnit.HOURS);

      // Create initial schedule
      ScheduleRequest createRequest =
          createScheduleRequest(savedFlight.getId(), departureTime, arrivalTime);

      MvcResult createResult =
          mockMvc
              .perform(
                  post("/schedules")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data.flight.route.origin", is("Ho Chi Minh City")))
              .andReturn();

      Integer createdId =
          objectMapper
              .readTree(createResult.getResponse().getContentAsString())
              .path("data")
              .path("id")
              .asInt();

      // Create new flight
      Route newRoute = createAndSaveRoute("Da Nang", "Phu Quoc");
      Aircraft newAircraft =
          createAndSaveAircraft(
              "Wide-body",
              350,
              "VN-A888",
              "Airbus",
              "A350-1000",
              2023,
              "SN888",
              AircraftStatus.ACTIVE);
      Flight newFlight = createAndSaveFlight(newRoute, newAircraft, FlightStatus.DELAYED);

      // Update with new flight
      ScheduleRequest updateRequest =
          createScheduleRequest(newFlight.getId(), departureTime, arrivalTime);

      mockMvc
          .perform(
              put("/schedules/{id}", createdId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.flight.route.origin", is("Da Nang")))
          .andExpect(jsonPath("$.data.flight.route.destination", is("Phu Quoc")))
          .andExpect(jsonPath("$.data.flight.status", is("DELAYED")));
    }
  }
}
