package uit.se100.hooks.aircraft;

import org.springframework.stereotype.Component;
import uit.se100.dtos.aircraft.AircraftRequest;
import uit.se100.dtos.aircraft.AircraftResponse;
import uit.se100.entities.aircraft.Aircraft;
import uit.se100.hooks.GenericHook;

@Component
public class AircraftHook
    implements GenericHook<Aircraft, Long, AircraftRequest, AircraftResponse> {}
