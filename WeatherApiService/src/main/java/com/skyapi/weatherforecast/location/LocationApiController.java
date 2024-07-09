package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/locations")
public class LocationApiController extends RuntimeException {

  private LocationService service;

  private ModelMapper modelMapper;

  public LocationApiController(LocationService service, ModelMapper modelMapper) {
    super();
    this.service = service;
    this.modelMapper = modelMapper;
  }

  @PostMapping
  public ResponseEntity<LocationDTO> addLocation(@RequestBody @Valid LocationDTO locationDTO) {
    Location addedLocation = service.add(convertToEntity(locationDTO));
    URI uri = URI.create("/v1/locations/" + addedLocation.getCode());
    return ResponseEntity.created(uri).body(convertToDTO(addedLocation));
  }

  @GetMapping
  public ResponseEntity<?> ListLocation() {
    List<Location> locations = service.list();
    if (locations.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(convertToListDTO(locations));
  }

  @GetMapping("/{code}")
  public ResponseEntity<?> getLocation(@PathVariable("code") String code) {
    Location location = service.get(code);
    return ResponseEntity.ok(convertToDTO(location));
  }

  @PutMapping
  public ResponseEntity<?> updateLocation(@RequestBody @Valid LocationDTO locationDTO) {
    Location updatedLocation = service.update(convertToEntity(locationDTO));
    return ResponseEntity.ok(convertToDTO(updatedLocation));
  }

  @DeleteMapping("/{code}")
  public ResponseEntity<?> deleteLocation(@PathVariable("code") String code) {
    service.delete(code);
    return ResponseEntity.noContent().build();
  }

  private LocationDTO convertToDTO(Location location) {
    return modelMapper.map(location, LocationDTO.class);
  }

  private Location convertToEntity(LocationDTO locationDTO) {
    return modelMapper.map(locationDTO, Location.class);
  }

  private List<LocationDTO> convertToListDTO(List<Location> locations) {
    return locations.stream().map(this::convertToDTO).collect(Collectors.toList());
  }
}
