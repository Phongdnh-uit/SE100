package uit.se100.controllers.aircraft;

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
import uit.se100.dtos.aircraft.AircraftRequest;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.enums.aircraft.AircraftStatus;
import uit.se100.repositories.aircraft.AircraftRepository;

@AutoConfigureMockMvc
class AircraftControllerIntegrationTest extends BaseIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private AircraftRepository aircraftRepository;

  @BeforeEach
  void setUp() {
    aircraftRepository.deleteAll();
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

  private AircraftRequest createAircraftRequest(
      String type,
      Integer seatCapacity,
      String registrationNumber,
      String manufacturer,
      String model,
      Integer manufactureYear,
      String serialNumber,
      AircraftStatus status) {
    AircraftRequest request = new AircraftRequest();
    request.setType(type);
    request.setSeatCapacity(seatCapacity);
    request.setRegistrationNumber(registrationNumber);
    request.setManufacturer(manufacturer);
    request.setModel(model);
    request.setManufactureYear(manufactureYear);
    request.setSerialNumber(serialNumber);
    request.setStatus(status);
    return request;
  }

  @Nested
  @DisplayName("GET /aircrafts/all")
  class FindAllTests {

    @Test
    @DisplayName("Should return empty page when no aircrafts exist")
    void shouldReturnEmptyPageWhenNoAircraftsExist() throws Exception {
      mockMvc
          .perform(get("/aircrafts/all").contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.content", hasSize(0)))
          .andExpect(jsonPath("$.data.totalElements", is(0)));
    }

    @Test
    @DisplayName("Should return all aircrafts with pagination")
    void shouldReturnAllAircraftsWithPagination() throws Exception {
      createAndSaveAircraft(
          "Narrow-body", 180, "VN-A001", "Boeing", "737-800", 2020, "SN001", AircraftStatus.ACTIVE);
      createAndSaveAircraft(
          "Wide-body", 300, "VN-A002", "Airbus", "A350-900", 2021, "SN002", AircraftStatus.ACTIVE);
      createAndSaveAircraft(
          "Narrow-body",
          150,
          "VN-A003",
          "Airbus",
          "A320neo",
          2022,
          "SN003",
          AircraftStatus.MAINTENANCE);

      mockMvc
          .perform(
              get("/aircrafts/all")
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
    @DisplayName("Should return all aircrafts when all=true")
    void shouldReturnAllAircraftsWhenAllIsTrue() throws Exception {
      createAndSaveAircraft(
          "Narrow-body", 180, "VN-A001", "Boeing", "737-800", 2020, "SN001", AircraftStatus.ACTIVE);
      createAndSaveAircraft(
          "Wide-body", 300, "VN-A002", "Airbus", "A350-900", 2021, "SN002", AircraftStatus.ACTIVE);
      createAndSaveAircraft(
          "Narrow-body",
          150,
          "VN-A003",
          "Airbus",
          "A320neo",
          2022,
          "SN003",
          AircraftStatus.MAINTENANCE);

      mockMvc
          .perform(
              get("/aircrafts/all")
                  .param("all", "true")
                  .param("size", "1")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(3)))
          .andExpect(jsonPath("$.data.totalElements", is(3)));
    }

    @Test
    @DisplayName("Should filter aircrafts with RSQL filter by manufacturer")
    void shouldFilterAircraftsWithRsqlByManufacturer() throws Exception {
      createAndSaveAircraft(
          "Narrow-body", 180, "VN-A001", "Boeing", "737-800", 2020, "SN001", AircraftStatus.ACTIVE);
      createAndSaveAircraft(
          "Wide-body", 300, "VN-A002", "Airbus", "A350-900", 2021, "SN002", AircraftStatus.ACTIVE);
      createAndSaveAircraft(
          "Narrow-body",
          150,
          "VN-A003",
          "Airbus",
          "A320neo",
          2022,
          "SN003",
          AircraftStatus.MAINTENANCE);

      mockMvc
          .perform(
              get("/aircrafts/all")
                  .param("filter", "manufacturer==Boeing")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(1)))
          .andExpect(jsonPath("$.data.content[0].manufacturer", is("Boeing")))
          .andExpect(jsonPath("$.data.content[0].model", is("737-800")));
    }

    @Test
    @DisplayName("Should filter aircrafts with RSQL filter by status")
    void shouldFilterAircraftsWithRsqlByStatus() throws Exception {
      createAndSaveAircraft(
          "Narrow-body", 180, "VN-A001", "Boeing", "737-800", 2020, "SN001", AircraftStatus.ACTIVE);
      createAndSaveAircraft(
          "Wide-body", 300, "VN-A002", "Airbus", "A350-900", 2021, "SN002", AircraftStatus.ACTIVE);
      createAndSaveAircraft(
          "Narrow-body",
          150,
          "VN-A003",
          "Airbus",
          "A320neo",
          2022,
          "SN003",
          AircraftStatus.MAINTENANCE);

      mockMvc
          .perform(
              get("/aircrafts/all")
                  .param("filter", "status==MAINTENANCE")
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.content", hasSize(1)))
          .andExpect(jsonPath("$.data.content[0].status", is("MAINTENANCE")))
          .andExpect(jsonPath("$.data.content[0].registrationNumber", is("VN-A003")));
    }
  }

  @Nested
  @DisplayName("GET /aircrafts/{id}")
  class FindByIdTests {

    @Test
    @DisplayName("Should return aircraft by id")
    void shouldReturnAircraftById() throws Exception {
      Aircraft savedAircraft =
          createAndSaveAircraft(
              "Narrow-body",
              180,
              "VN-A001",
              "Boeing",
              "737-800",
              2020,
              "SN001",
              AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              get("/aircrafts/{id}", savedAircraft.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", is(savedAircraft.getId().intValue())))
          .andExpect(jsonPath("$.data.type", is("Narrow-body")))
          .andExpect(jsonPath("$.data.seatCapacity", is(180)))
          .andExpect(jsonPath("$.data.registrationNumber", is("VN-A001")))
          .andExpect(jsonPath("$.data.manufacturer", is("Boeing")))
          .andExpect(jsonPath("$.data.model", is("737-800")))
          .andExpect(jsonPath("$.data.manufactureYear", is(2020)))
          .andExpect(jsonPath("$.data.serialNumber", is("SN001")))
          .andExpect(jsonPath("$.data.status", is("ACTIVE")));
    }

    @Test
    @DisplayName("Should return error when aircraft not found")
    void shouldReturnErrorWhenAircraftNotFound() throws Exception {
      mockMvc
          .perform(get("/aircrafts/{id}", 99999L).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("POST /aircrafts")
  class CreateTests {

    @Test
    @DisplayName("Should create new aircraft")
    void shouldCreateNewAircraft() throws Exception {
      AircraftRequest request =
          createAircraftRequest(
              "Wide-body", 350, "VN-A100", "Boeing", "787-9", 2023, "SN100", AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              post("/aircrafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", notNullValue()))
          .andExpect(jsonPath("$.data.type", is("Wide-body")))
          .andExpect(jsonPath("$.data.seatCapacity", is(350)))
          .andExpect(jsonPath("$.data.registrationNumber", is("VN-A100")))
          .andExpect(jsonPath("$.data.manufacturer", is("Boeing")))
          .andExpect(jsonPath("$.data.model", is("787-9")))
          .andExpect(jsonPath("$.data.manufactureYear", is(2023)))
          .andExpect(jsonPath("$.data.serialNumber", is("SN100")))
          .andExpect(jsonPath("$.data.status", is("ACTIVE")));
    }

    @Test
    @DisplayName("Should return validation error when type is blank")
    void shouldReturnValidationErrorWhenTypeIsBlank() throws Exception {
      AircraftRequest request =
          createAircraftRequest(
              "", 350, "VN-A100", "Boeing", "787-9", 2023, "SN100", AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              post("/aircrafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when seatCapacity is null")
    void shouldReturnValidationErrorWhenSeatCapacityIsNull() throws Exception {
      AircraftRequest request =
          createAircraftRequest(
              "Wide-body",
              null,
              "VN-A100",
              "Boeing",
              "787-9",
              2023,
              "SN100",
              AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              post("/aircrafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when registrationNumber is blank")
    void shouldReturnValidationErrorWhenRegistrationNumberIsBlank() throws Exception {
      AircraftRequest request =
          createAircraftRequest(
              "Wide-body", 350, "", "Boeing", "787-9", 2023, "SN100", AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              post("/aircrafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when manufacturer is blank")
    void shouldReturnValidationErrorWhenManufacturerIsBlank() throws Exception {
      AircraftRequest request =
          createAircraftRequest(
              "Wide-body", 350, "VN-A100", "", "787-9", 2023, "SN100", AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              post("/aircrafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when model is blank")
    void shouldReturnValidationErrorWhenModelIsBlank() throws Exception {
      AircraftRequest request =
          createAircraftRequest(
              "Wide-body", 350, "VN-A100", "Boeing", "", 2023, "SN100", AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              post("/aircrafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when manufactureYear is null")
    void shouldReturnValidationErrorWhenManufactureYearIsNull() throws Exception {
      AircraftRequest request =
          createAircraftRequest(
              "Wide-body", 350, "VN-A100", "Boeing", "787-9", null, "SN100", AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              post("/aircrafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when serialNumber is blank")
    void shouldReturnValidationErrorWhenSerialNumberIsBlank() throws Exception {
      AircraftRequest request =
          createAircraftRequest(
              "Wide-body", 350, "VN-A100", "Boeing", "787-9", 2023, "", AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              post("/aircrafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return validation error when status is null")
    void shouldReturnValidationErrorWhenStatusIsNull() throws Exception {
      AircraftRequest request =
          createAircraftRequest(
              "Wide-body", 350, "VN-A100", "Boeing", "787-9", 2023, "SN100", null);

      mockMvc
          .perform(
              post("/aircrafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(request)))
          .andExpect(status().isBadRequest());
    }
  }

  @Nested
  @DisplayName("PUT /aircrafts/{id}")
  class UpdateTests {

    @Test
    @DisplayName("Should update existing aircraft")
    void shouldUpdateExistingAircraft() throws Exception {
      Aircraft savedAircraft =
          createAndSaveAircraft(
              "Narrow-body",
              180,
              "VN-A001",
              "Boeing",
              "737-800",
              2020,
              "SN001",
              AircraftStatus.ACTIVE);
      AircraftRequest updateRequest =
          createAircraftRequest(
              "Wide-body",
              350,
              "VN-A001",
              "Airbus",
              "A380",
              2022,
              "SN001",
              AircraftStatus.MAINTENANCE);

      mockMvc
          .perform(
              put("/aircrafts/{id}", savedAircraft.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")))
          .andExpect(jsonPath("$.data.id", is(savedAircraft.getId().intValue())))
          .andExpect(jsonPath("$.data.type", is("Wide-body")))
          .andExpect(jsonPath("$.data.seatCapacity", is(350)))
          .andExpect(jsonPath("$.data.manufacturer", is("Airbus")))
          .andExpect(jsonPath("$.data.model", is("A380")))
          .andExpect(jsonPath("$.data.manufactureYear", is(2022)))
          .andExpect(jsonPath("$.data.status", is("MAINTENANCE")));
    }

    @Test
    @DisplayName("Should return error when updating non-existent aircraft")
    void shouldReturnErrorWhenUpdatingNonExistentAircraft() throws Exception {
      AircraftRequest updateRequest =
          createAircraftRequest(
              "Wide-body", 350, "VN-A100", "Boeing", "787-9", 2023, "SN100", AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              put("/aircrafts/{id}", 99999L)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return validation error when update type is blank")
    void shouldReturnValidationErrorWhenUpdateTypeIsBlank() throws Exception {
      Aircraft savedAircraft =
          createAndSaveAircraft(
              "Narrow-body",
              180,
              "VN-A001",
              "Boeing",
              "737-800",
              2020,
              "SN001",
              AircraftStatus.ACTIVE);
      AircraftRequest updateRequest =
          createAircraftRequest(
              "", 350, "VN-A001", "Airbus", "A380", 2022, "SN001", AircraftStatus.MAINTENANCE);

      mockMvc
          .perform(
              put("/aircrafts/{id}", savedAircraft.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update aircraft status from ACTIVE to INACTIVE")
    void shouldUpdateAircraftStatusFromActiveToInactive() throws Exception {
      Aircraft savedAircraft =
          createAndSaveAircraft(
              "Narrow-body",
              180,
              "VN-A001",
              "Boeing",
              "737-800",
              2020,
              "SN001",
              AircraftStatus.ACTIVE);
      AircraftRequest updateRequest =
          createAircraftRequest(
              "Narrow-body",
              180,
              "VN-A001",
              "Boeing",
              "737-800",
              2020,
              "SN001",
              AircraftStatus.INACTIVE);

      mockMvc
          .perform(
              put("/aircrafts/{id}", savedAircraft.getId())
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.data.status", is("INACTIVE")));
    }
  }

  @Nested
  @DisplayName("DELETE /aircrafts/{id}")
  class DeleteTests {

    @Test
    @DisplayName("Should delete existing aircraft")
    void shouldDeleteExistingAircraft() throws Exception {
      Aircraft savedAircraft =
          createAndSaveAircraft(
              "Narrow-body",
              180,
              "VN-A001",
              "Boeing",
              "737-800",
              2020,
              "SN001",
              AircraftStatus.ACTIVE);

      mockMvc
          .perform(
              delete("/aircrafts/{id}", savedAircraft.getId())
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")));

      // Verify the aircraft is deleted
      mockMvc
          .perform(
              get("/aircrafts/{id}", savedAircraft.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }

  @Nested
  @DisplayName("DELETE /aircrafts/bulk")
  class BulkDeleteTests {

    @Test
    @DisplayName("Should delete multiple aircrafts")
    void shouldDeleteMultipleAircrafts() throws Exception {
      Aircraft aircraft1 =
          createAndSaveAircraft(
              "Narrow-body",
              180,
              "VN-A001",
              "Boeing",
              "737-800",
              2020,
              "SN001",
              AircraftStatus.ACTIVE);
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
              AircraftStatus.MAINTENANCE);

      mockMvc
          .perform(
              delete("/aircrafts/bulk")
                  .param("ids", aircraft1.getId().toString(), aircraft2.getId().toString())
                  .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.code", is(1000)))
          .andExpect(jsonPath("$.message", is("success")));

      // Verify deleted aircrafts
      mockMvc
          .perform(
              get("/aircrafts/{id}", aircraft1.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());

      mockMvc
          .perform(
              get("/aircrafts/{id}", aircraft2.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());

      // Verify remaining aircraft still exists
      mockMvc
          .perform(
              get("/aircrafts/{id}", aircraft3.getId()).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.registrationNumber", is("VN-A003")));
    }
  }

  @Nested
  @DisplayName("Complete CRUD Workflow")
  class CrudWorkflowTests {

    @Test
    @DisplayName("Should perform complete CRUD workflow")
    void shouldPerformCompleteCrudWorkflow() throws Exception {
      // Create
      AircraftRequest createRequest =
          createAircraftRequest(
              "Wide-body",
              400,
              "VN-A999",
              "Airbus",
              "A380-800",
              2019,
              "SN999",
              AircraftStatus.ACTIVE);
      MvcResult createResult =
          mockMvc
              .perform(
                  post("/aircrafts")
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(objectMapper.writeValueAsString(createRequest)))
              .andExpect(status().isOk())
              .andExpect(jsonPath("$.data.type", is("Wide-body")))
              .andExpect(jsonPath("$.data.manufacturer", is("Airbus")))
              .andReturn();

      Integer createdId =
          objectMapper
              .readTree(createResult.getResponse().getContentAsString())
              .path("data")
              .path("id")
              .asInt();

      // Read
      mockMvc
          .perform(get("/aircrafts/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.type", is("Wide-body")))
          .andExpect(jsonPath("$.data.seatCapacity", is(400)))
          .andExpect(jsonPath("$.data.registrationNumber", is("VN-A999")))
          .andExpect(jsonPath("$.data.manufacturer", is("Airbus")))
          .andExpect(jsonPath("$.data.model", is("A380-800")))
          .andExpect(jsonPath("$.data.manufactureYear", is(2019)))
          .andExpect(jsonPath("$.data.serialNumber", is("SN999")))
          .andExpect(jsonPath("$.data.status", is("ACTIVE")));

      // Update
      AircraftRequest updateRequest =
          createAircraftRequest(
              "Wide-body",
              400,
              "VN-A999",
              "Airbus",
              "A380-800",
              2019,
              "SN999",
              AircraftStatus.MAINTENANCE);
      mockMvc
          .perform(
              put("/aircrafts/{id}", createdId)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(objectMapper.writeValueAsString(updateRequest)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.status", is("MAINTENANCE")));

      // Verify Update
      mockMvc
          .perform(get("/aircrafts/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.status", is("MAINTENANCE")));

      // Delete
      mockMvc
          .perform(delete("/aircrafts/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());

      // Verify Delete
      mockMvc
          .perform(get("/aircrafts/{id}", createdId).contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound());
    }
  }
}
