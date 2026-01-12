package uit.se100.mappers.baggage;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import uit.se100.dtos.baggage.BaggageRequest;
import uit.se100.dtos.baggage.BaggageResponse;
import uit.se100.entities.baggage.Baggage;
import uit.se100.mappers.GenericMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BaggageMapper
        extends GenericMapper<Baggage, BaggageRequest, BaggageResponse> {
}

