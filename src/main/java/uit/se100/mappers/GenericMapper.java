package uit.se100.mappers;

import org.mapstruct.MapperConfig;
import org.mapstruct.MappingTarget;

@MapperConfig()
public interface GenericMapper<E, I, O> {
  E requestToEntity(I request);

  O entityToResponse(E entity);

  void partialUpdate(I request, @MappingTarget E entity);
}
