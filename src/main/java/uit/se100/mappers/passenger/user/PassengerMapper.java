package uit.se100.mappers.passenger.user;

import org.mapstruct.*;
import uit.se100.dtos.passenger.PassengerRequest;
import uit.se100.dtos.passenger.PassengerResponse;
import uit.se100.entities.passenger.Passenger;
import uit.se100.mappers.GenericMapper;
import uit.se100.mappers.user.UserMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {UserMapper.class, GenericMapper.class})
public interface PassengerMapper extends GenericMapper<Passenger, PassengerRequest, PassengerResponse> {

    @Override
    Passenger requestToEntity(PassengerRequest request);

    @Override
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(PassengerRequest request, @MappingTarget Passenger entity);
}
