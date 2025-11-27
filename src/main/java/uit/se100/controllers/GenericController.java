package uit.se100.controllers;

import io.github.perplexhub.rsql.RSQLJPASupport;
import jakarta.validation.groups.Default;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import uit.se100.dtos.Action.Create;
import uit.se100.dtos.Action.Update;
import uit.se100.dtos.ApiResponse;
import uit.se100.dtos.PageResponse;
import uit.se100.services.CrudService;

@RequiredArgsConstructor
public abstract class GenericController<E, ID, I, O> {

  protected final CrudService<E, ID, I, O> service;

  @GetMapping("/all")
  public ResponseEntity<ApiResponse<PageResponse<O>>> findAll(
      @ParameterObject Pageable pageable,
      @RequestParam(value = "filter", required = false) @Nullable String filter,
      @RequestParam(value = "all", defaultValue = "false") boolean all) {
    Specification<E> specification = RSQLJPASupport.toSpecification(filter);
    if (all) {
      pageable = Pageable.unpaged(pageable.getSort());
    }
    return ResponseEntity.ok(ApiResponse.ok(service.findAll(pageable, specification)));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<O>> findById(@PathVariable("id") ID id) {
    return ResponseEntity.ok(ApiResponse.ok(service.findById(id)));
  }

  @PostMapping()
  public ResponseEntity<ApiResponse<O>> create(
      @Validated({Default.class, Create.class}) @RequestBody I input) {
    return ResponseEntity.ok(ApiResponse.ok(service.create(input)));
  }

  @PutMapping("/{id}")
  public ResponseEntity<ApiResponse<O>> update(
      @PathVariable("id") ID id, @Validated({Default.class, Update.class}) @RequestBody I input) {
    return ResponseEntity.ok(ApiResponse.ok(service.update(id, input)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> delete(@PathVariable("id") ID id) {
    service.delete(id);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }

  @DeleteMapping("/bulk")
  public ResponseEntity<ApiResponse<Void>> deleteAll(@RequestParam("ids") List<ID> ids) {
    service.deleteAll(ids);
    return ResponseEntity.ok(ApiResponse.ok(null));
  }
}
