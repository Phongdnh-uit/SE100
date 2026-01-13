//package uit.se100.controllers.baggage;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//import uit.se100.dtos.baggage.BaggageRequest;
//import uit.se100.entities.aircraft.Aircraft;
//import uit.se100.entities.flight.Flight;
//import uit.se100.entities.passenger.Passenger;
//import uit.se100.entities.route.Route;
//import uit.se100.enums.aircraft.AircraftStatus;
//import uit.se100.enums.baggage.BaggageType;
//import uit.se100.enums.flight.FlightStatus;
//import uit.se100.enums.passenger.TierEnum;
//import uit.se100.repositories.aircraft.AircraftRepository;
//import uit.se100.repositories.flight.FlightRepository;
//import uit.se100.repositories.passenger.PassengerRepository;
//import uit.se100.repositories.route.RouteRepository;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
/// **
// * Integration tests for BaggageController.
// *
// * <p>Tests API endpoints with real database interactions.
// */
//@SpringBootTest
//@AutoConfigureMockMvc
//@ActiveProfiles("test")
//@Transactional
//@DisplayName("Baggage Controller Integration Tests")
//class BaggageControllerIntegrationTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Autowired
//    private PassengerRepository passengerRepository;
//
//    @Autowired
//    private FlightRepository flightRepository;
//
//    @Autowired
//    private RouteRepository routeRepository;
//
//    @Autowired
//    private AircraftRepository aircraftRepository;
//
//    @Test
//    @DisplayName("Should create baggage with calculated fee")
//    void testCreateBaggage() throws Exception {
//        // Setup: Create test data
//        Passenger passenger = new Passenger();
//        passenger.setFullName("John Doe");
//        passenger.setTier(TierEnum.ECONOMY);
//        passenger = passengerRepository.save(passenger);
//
//        Route route = new Route();
//        route.setOrigin("SGN");
//        route.setDestination("HAN");
//        route = routeRepository.save(route);
//
//        Aircraft aircraft = new Aircraft();
//        aircraft.setType("Boeing");
//        aircraft.setRegistrationNumber("VN-A001");
//        aircraft.setManufacturer("Boeing");
//        aircraft.setModel("B737");
//        aircraft.setManufactureYear(2020);
//        aircraft.setSerialNumber("SN123");
//        aircraft.setStatus(AircraftStatus.ACTIVE);
//        aircraft = aircraftRepository.save(aircraft);
//
//        Flight flight = new Flight();
//        flight.setRoute(route);
//        flight.setAircraft(aircraft);
//        flight.setStatus(FlightStatus.OPEN);
//        flight.setDepartureTime(Instant.now().plusSeconds(86400));
//        flight.setArrivalTime(Instant.now().plusSeconds(86400 + 3600));
//        flight = flightRepository.save(flight);
//
//        // Create baggage request
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CARRY_ON);
//        request.setWeight(BigDecimal.valueOf(8.5));
//        request.setPassengerId(passenger.getId());
//        request.setFlightId(flight.getId());
//
//        // Test: POST /baggages
//        mockMvc
//                .perform(
//                        post("/baggages")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.data.type").value("CARRY_ON"))
//                .andExpect(jsonPath("$.data.weight").value(8.5))
//                .andExpect(jsonPath("$.data.extraFee").value(100000));
//    }
//
//    @Test
//    @DisplayName("Should retrieve baggage by passenger ID")
//    void testGetBaggageByPassengerId() throws Exception {
//        // Setup: Create test data
//        Passenger passenger = new Passenger();
//        passenger.setFullName("Jane Doe");
//        passenger.setTier(TierEnum.ECONOMY);
//        passenger = passengerRepository.save(passenger);
//
//        // Test: GET /baggages/passenger/{passengerId}
//        mockMvc
//                .perform(get("/baggages/passenger/" + passenger.getId()).param("page", "0").param("size", "10"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.data.content").isArray());
//    }
//
//    @Test
//    @DisplayName("Should calculate baggage fee for carry-on")
//    void testCalculateBaggageFeeCarryOn() throws Exception {
//        // Setup: Create test data
//        Passenger passenger = new Passenger();
//        passenger.setFullName("John Smith");
//        passenger.setTier(TierEnum.ECONOMY);
//        passenger = passengerRepository.save(passenger);
//
//        Route route = new Route();
//        route.setOrigin("SGN");
//        route.setDestination("HAN");
//        route = routeRepository.save(route);
//
//        Aircraft aircraft = new Aircraft();
//        aircraft.setType("Boeing");
//        aircraft.setRegistrationNumber("VN-A002");
//        aircraft.setManufacturer("Boeing");
//        aircraft.setModel("B737");
//        aircraft.setManufactureYear(2020);
//        aircraft.setSerialNumber("SN124");
//        aircraft.setStatus(AircraftStatus.ACTIVE);
//        aircraft = aircraftRepository.save(aircraft);
//
//        Flight flight = new Flight();
//        flight.setRoute(route);
//        flight.setAircraft(aircraft);
//        flight.setStatus(FlightStatus.OPEN);
//        flight.setDepartureTime(Instant.now().plusSeconds(86400));
//        flight.setArrivalTime(Instant.now().plusSeconds(86400 + 3600));
//        flight = flightRepository.save(flight);
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CARRY_ON);
//        request.setWeight(BigDecimal.valueOf(12));
//        request.setPassengerId(passenger.getId());
//        request.setFlightId(flight.getId());
//
//        // Test: POST /baggages/calculate-fee
//        mockMvc
//                .perform(
//                        post("/baggages/calculate-fee")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.data.fee").value(200000));
//    }
//
//    @Test
//    @DisplayName("Should calculate baggage fee for checked (economy)")
//    void testCalculateBaggageFeeCheckedEconomy() throws Exception {
//        // Setup: Create test data
//        Passenger passenger = new Passenger();
//        passenger.setFullName("Alice Johnson");
//        passenger.setTier(TierEnum.ECONOMY);
//        passenger = passengerRepository.save(passenger);
//
//        Route route = new Route();
//        route.setOrigin("SGN");
//        route.setDestination("BKK");
//        route = routeRepository.save(route);
//
//        Aircraft aircraft = new Aircraft();
//        aircraft.setType("Airbus");
//        aircraft.setRegistrationNumber("VN-A003");
//        aircraft.setManufacturer("Airbus");
//        aircraft.setModel("A320");
//        aircraft.setManufactureYear(2021);
//        aircraft.setSerialNumber("SN125");
//        aircraft.setStatus(AircraftStatus.ACTIVE);
//        aircraft = aircraftRepository.save(aircraft);
//
//        Flight flight = new Flight();
//        flight.setRoute(route);
//        flight.setAircraft(aircraft);
//        flight.setStatus(FlightStatus.OPEN);
//        flight.setDepartureTime(Instant.now().plusSeconds(172800));
//        flight.setArrivalTime(Instant.now().plusSeconds(172800 + 3600));
//        flight = flightRepository.save(flight);
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CHECKED);
//        request.setWeight(BigDecimal.valueOf(26));
//        request.setPassengerId(passenger.getId());
//        request.setFlightId(flight.getId());
//
//        // Test: POST /baggages/calculate-fee
//        mockMvc
//                .perform(
//                        post("/baggages/calculate-fee")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value(200))
//                .andExpect(jsonPath("$.data.fee").value(200000));
//    }
//
//    @Test
//    @DisplayName("Should return error for non-existent passenger")
//    void testCreateBaggageWithNonExistentPassenger() throws Exception {
//        // Setup: Create test flight
//        Route route = new Route();
//        route.setOrigin("SGN");
//        route.setDestination("HAN");
//        route = routeRepository.save(route);
//
//        Aircraft aircraft = new Aircraft();
//        aircraft.setType("Boeing");
//        aircraft.setRegistrationNumber("VN-A004");
//        aircraft.setManufacturer("Boeing");
//        aircraft.setModel("B737");
//        aircraft.setManufactureYear(2020);
//        aircraft.setSerialNumber("SN126");
//        aircraft.setStatus(AircraftStatus.ACTIVE);
//        aircraft = aircraftRepository.save(aircraft);
//
//        Flight flight = new Flight();
//        flight.setRoute(route);
//        flight.setAircraft(aircraft);
//        flight.setStatus(FlightStatus.OPEN);
//        flight.setDepartureTime(Instant.now().plusSeconds(86400));
//        flight.setArrivalTime(Instant.now().plusSeconds(86400 + 3600));
//        flight = flightRepository.save(flight);
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CARRY_ON);
//        request.setWeight(BigDecimal.valueOf(8));
//        request.setPassengerId(999L); // Non-existent passenger
//        request.setFlightId(flight.getId());
//
//        // Test: POST /baggages
//        mockMvc
//                .perform(
//                        post("/baggages")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().is4xxClientError());
//    }
//
//    @Test
//    @DisplayName("Should return error for invalid weight")
//    void testCreateBaggageWithInvalidWeight() throws Exception {
//        // Setup: Create test data
//        Passenger passenger = new Passenger();
//        passenger.setFullName("Bob Wilson");
//        passenger.setTier(TierEnum.ECONOMY);
//        passenger = passengerRepository.save(passenger);
//
//        Route route = new Route();
//        route.setOrigin("SGN");
//        route.setDestination("HAN");
//        route = routeRepository.save(route);
//
//        Aircraft aircraft = new Aircraft();
//        aircraft.setType("Boeing");
//        aircraft.setRegistrationNumber("VN-A005");
//        aircraft.setManufacturer("Boeing");
//        aircraft.setModel("B737");
//        aircraft.setManufactureYear(2020);
//        aircraft.setSerialNumber("SN127");
//        aircraft.setStatus(AircraftStatus.ACTIVE);
//        aircraft = aircraftRepository.save(aircraft);
//
//        Flight flight = new Flight();
//        flight.setRoute(route);
//        flight.setAircraft(aircraft);
//        flight.setStatus(FlightStatus.OPEN);
//        flight.setDepartureTime(Instant.now().plusSeconds(86400));
//        flight.setArrivalTime(Instant.now().plusSeconds(86400 + 3600));
//        flight = flightRepository.save(flight);
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CARRY_ON);
//        request.setWeight(BigDecimal.valueOf(0)); // Invalid weight
//        request.setPassengerId(passenger.getId());
//        request.setFlightId(flight.getId());
//
//        // Test: POST /baggages
//        mockMvc
//                .perform(
//                        post("/baggages")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().is4xxClientError());
//    }
//}
//
