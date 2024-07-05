package com.skyapi.weatherforecast.location;

import com.skyapi.weatherforecast.common.Location;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface LocationRepository extends CrudRepository<Location, String> {

  @Query("SELECT l from Location l where l.trashed = false")
  public List<Location> findUntrashed();

  @Query("SELECT l from Location l where l.trashed = false and l.code = ?1")
  public Location findByCode(String code);

  @Modifying
  @Query("UPDATE Location set trashed = true where code = ?1")
  public void trashByCode(String code);
}
