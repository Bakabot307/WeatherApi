package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.hibernate.validator.constraints.Length;

@Entity
@Table(name = "locations")
public class Location {

  @Column(length = 12, nullable = false, unique = true)
  @Id
  @NotNull(message = "Location code can't be null")
  @Length(min = 3, max = 12, message = "Location code must have 3-12 characters")
  private String code;

  @Column(length = 128, nullable = false)
  @JsonProperty("city_name")
  @NotNull(message = "City name can't be null")
  @Length(min = 3, max = 128, message = "City name must have 3-128 characters")
  private String cityName;

  @Column(length = 128, nullable = false)
  @JsonProperty("region_name")
  @Length(min = 3, max = 128, message = "Region name must have 3-128 characters")
  private String regionName;

  @Column(length = 64, nullable = false)
  @JsonProperty("country_name")
  @NotNull(message = "Country name can't be null")
  @Length(min = 3, max = 64, message = "Country name must have 3-128 characters")
  private String countryName;

  @Column(length = 2, nullable = false)
  @JsonProperty("country_code")
  @NotNull(message = "Country code can't be null")
  @Length(min = 2, max = 2, message = "City name must have 2 characters")
  private String countryCode;

  private boolean enabled;
  @JsonIgnore
  private boolean trashed;
  @OneToOne(mappedBy = "location", cascade = CascadeType.ALL)
  @PrimaryKeyJoinColumn
  @JsonIgnore
  private RealtimeWeather realTimeWeather;

  @OneToMany(mappedBy = "id.location", cascade = CascadeType.ALL,orphanRemoval = true)
  private List<HourlyWeather> listHourlyWeather = new ArrayList<>();

  public Location() {
  }

  public Location(String cityName, String regionName, String countryName, String countryCode) {
    super();
    this.cityName = cityName;
    this.regionName = regionName;
    this.countryName = countryName;
    this.countryCode = countryCode;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getCityName() {
    return cityName;
  }

  public void setCityName(String cityName) {
    this.cityName = cityName;
  }

  public String getRegionName() {
    return regionName;
  }

  public void setRegionName(String regionName) {
    this.regionName = regionName;
  }

  public String getCountryName() {
    return countryName;
  }

  public void setCountryName(String countryName) {
    this.countryName = countryName;
  }

  public String getCountryCode() {
    return countryCode;
  }

  public void setCountryCode(String countryCode) {
    this.countryCode = countryCode;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public boolean isTrashed() {
    return trashed;
  }

  public void setTrashed(boolean trashed) {
    this.trashed = trashed;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Location location = (Location) o;
    return Objects.equals(code, location.code);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code);
  }

  @Override
  public String toString() {
    return cityName + (regionName != null ? ", " + regionName : "") + ", " + countryName;
  }

  public RealtimeWeather getRealTimeWeather() {
    return realTimeWeather;
  }

  public void setRealTimeWeather(RealtimeWeather realTimeWeather) {
    this.realTimeWeather = realTimeWeather;
  }

  public List<HourlyWeather> getListHourlyWeather() {
    return listHourlyWeather;
  }

  public void setListHourlyWeather(List<HourlyWeather> listHourlyWeather) {
    this.listHourlyWeather = listHourlyWeather;
  }

  public Location code(String code) {
    setCode(code);
    return this;
  }
}

