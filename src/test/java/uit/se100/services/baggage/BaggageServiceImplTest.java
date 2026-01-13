//package uit.se100.services.baggage;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import uit.se100.dtos.baggage.BaggageRequest;
//import uit.se100.entities.passenger.Passenger;
//import uit.se100.enums.baggage.BaggageType;
//import uit.se100.enums.passenger.TierEnum;
//import uit.se100.exceptions.errors.ApiException;
//import uit.se100.repositories.passenger.PassengerRepository;
//
//import java.math.BigDecimal;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.when;
//
/// **
// * Unit tests for BaggageServiceImpl.
// *
// * <p>Tests baggage fee calculation according to the rules:
// * <ul>
// *   <li>Carry-on: 7kg free
// *   <li>Checked (Economy): 20kg free
// *   <li>Checked (Business): 30kg free
// *   <li>Checked (First class): 40kg free
// *   <li>Excess fee: 100,000 VND per 5kg (rounded up)
// * </ul>
// */
//@DisplayName("Baggage Service Tests")
//class BaggageServiceImplTest {
//
//    @Mock
//    private PassengerRepository passengerRepository;
//
//    @InjectMocks
//    private BaggageServiceImpl baggageService;
//
//    private Passenger economyPassenger;
//    private Passenger businessPassenger;
//    private Passenger firstPassenger;
//
//    @BeforeEach
//    void setUp() {
//        @SuppressWarnings("resource")
//        var ignored = MockitoAnnotations.openMocks(this);
//
//        economyPassenger = new Passenger();
//        economyPassenger.setId(1L);
//        economyPassenger.setTier(TierEnum.ECONOMY);
//
//        businessPassenger = new Passenger();
//        businessPassenger.setId(2L);
//        businessPassenger.setTier(TierEnum.BUSINESS);
//
//        firstPassenger = new Passenger();
//        firstPassenger.setId(3L);
//        firstPassenger.setTier(TierEnum.FIRST);
//    }
//
//    @Test
//    @DisplayName("Should calculate zero fee for carry-on baggage within limit")
//    void testCarryOnBaggageWithinLimit() {
//        when(passengerRepository.findById(1L)).thenReturn(java.util.Optional.of(economyPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CARRY_ON);
//        request.setWeight(BigDecimal.valueOf(7));
//        request.setPassengerId(1L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.ZERO, fee, "Fee should be zero for baggage within limit");
//    }
//
//    @Test
//    @DisplayName("Should calculate 100,000 VND for carry-on baggage with 1kg excess")
//    void testCarryOnBaggageWith1KgExcess() {
//        when(passengerRepository.findById(1L)).thenReturn(java.util.Optional.of(economyPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CARRY_ON);
//        request.setWeight(BigDecimal.valueOf(8));
//        request.setPassengerId(1L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.valueOf(100000), fee, "Fee should be 100,000 for 1kg excess (5kg rounded)");
//    }
//
//    @Test
//    @DisplayName("Should calculate 200,000 VND for carry-on baggage with 5.5kg excess")
//    void testCarryOnBaggageWith5_5KgExcess() {
//        when(passengerRepository.findById(1L)).thenReturn(java.util.Optional.of(economyPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CARRY_ON);
//        request.setWeight(BigDecimal.valueOf(12.5));
//        request.setPassengerId(1L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.valueOf(200000), fee, "Fee should be 200,000 for 5.5kg excess (10kg rounded)");
//    }
//
//    @Test
//    @DisplayName("Should calculate zero fee for checked baggage (economy) within 20kg limit")
//    void testCheckedBaggageEconomyWithinLimit() {
//        when(passengerRepository.findById(1L)).thenReturn(java.util.Optional.of(economyPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CHECKED);
//        request.setWeight(BigDecimal.valueOf(20));
//        request.setPassengerId(1L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.ZERO, fee, "Fee should be zero for baggage within limit");
//    }
//
//    @Test
//    @DisplayName("Should calculate 100,000 VND for checked baggage (economy) with 5kg excess")
//    void testCheckedBaggageEconomyWith5KgExcess() {
//        when(passengerRepository.findById(1L)).thenReturn(java.util.Optional.of(economyPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CHECKED);
//        request.setWeight(BigDecimal.valueOf(25));
//        request.setPassengerId(1L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.valueOf(100000), fee, "Fee should be 100,000 for 5kg excess");
//    }
//
//    @Test
//    @DisplayName("Should calculate 200,000 VND for checked baggage (economy) with 6kg excess")
//    void testCheckedBaggageEconomyWith6KgExcess() {
//        when(passengerRepository.findById(1L)).thenReturn(java.util.Optional.of(economyPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CHECKED);
//        request.setWeight(BigDecimal.valueOf(26));
//        request.setPassengerId(1L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.valueOf(200000), fee, "Fee should be 200,000 for 6kg excess (10kg rounded)");
//    }
//
//    @Test
//    @DisplayName("Should calculate zero fee for checked baggage (business) within 30kg limit")
//    void testCheckedBaggageBusinessWithinLimit() {
//        when(passengerRepository.findById(2L)).thenReturn(java.util.Optional.of(businessPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CHECKED);
//        request.setWeight(BigDecimal.valueOf(30));
//        request.setPassengerId(2L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.ZERO, fee, "Fee should be zero for baggage within limit");
//    }
//
//    @Test
//    @DisplayName("Should calculate 100,000 VND for checked baggage (business) with 5kg excess")
//    void testCheckedBaggageBusinessWith5KgExcess() {
//        when(passengerRepository.findById(2L)).thenReturn(java.util.Optional.of(businessPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CHECKED);
//        request.setWeight(BigDecimal.valueOf(35));
//        request.setPassengerId(2L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.valueOf(100000), fee, "Fee should be 100,000 for 5kg excess");
//    }
//
//    @Test
//    @DisplayName("Should calculate zero fee for checked baggage (first class) within 40kg limit")
//    void testCheckedBaggageFirstClassWithinLimit() {
//        when(passengerRepository.findById(3L)).thenReturn(java.util.Optional.of(firstPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CHECKED);
//        request.setWeight(BigDecimal.valueOf(40));
//        request.setPassengerId(3L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.ZERO, fee, "Fee should be zero for baggage within limit");
//    }
//
//    @Test
//    @DisplayName("Should calculate 100,000 VND for checked baggage (first class) with 1kg excess")
//    void testCheckedBaggageFirstClassWith1KgExcess() {
//        when(passengerRepository.findById(3L)).thenReturn(java.util.Optional.of(firstPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CHECKED);
//        request.setWeight(BigDecimal.valueOf(41));
//        request.setPassengerId(3L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.valueOf(100000), fee, "Fee should be 100,000 for 1kg excess (5kg rounded)");
//    }
//
//    @Test
//    @DisplayName("Should throw exception for non-existent passenger")
//    void testCalculateFeeWithNonExistentPassenger() {
//        when(passengerRepository.findById(999L)).thenReturn(java.util.Optional.empty());
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CARRY_ON);
//        request.setWeight(BigDecimal.valueOf(8));
//        request.setPassengerId(999L);
//
//        assertThrows(ApiException.class, () -> baggageService.calculateExtraFee(request),
//                "Should throw ApiException for non-existent passenger");
//    }
//
//    @Test
//    @DisplayName("Should handle decimal weights correctly")
//    void testDecimalWeightCalculation() {
//        when(passengerRepository.findById(1L)).thenReturn(java.util.Optional.of(economyPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CARRY_ON);
//        request.setWeight(BigDecimal.valueOf(8.75));
//        request.setPassengerId(1L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        assertEquals(BigDecimal.valueOf(100000), fee, "Fee should be 100,000 for 1.75kg excess (5kg rounded)");
//    }
//
//    @Test
//    @DisplayName("Should handle large excess weight correctly")
//    void testLargeExcessWeight() {
//        when(passengerRepository.findById(1L)).thenReturn(java.util.Optional.of(economyPassenger));
//
//        BaggageRequest request = new BaggageRequest();
//        request.setType(BaggageType.CHECKED);
//        request.setWeight(BigDecimal.valueOf(45));
//        request.setPassengerId(1L);
//
//        BigDecimal fee = baggageService.calculateExtraFee(request);
//
//        // Excess: 45 - 20 = 25kg, rounded: 25kg, units: 25/5 = 5, fee = 5 * 100,000 = 500,000
//        assertEquals(BigDecimal.valueOf(500000), fee, "Fee should be 500,000 for 25kg excess");
//    }
//}
//
