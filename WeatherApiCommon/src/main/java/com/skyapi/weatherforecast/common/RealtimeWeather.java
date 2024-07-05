package com.skyapi.weatherforecast.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import java.util.Objects;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

@Entity
@Table(name = "realtime_weather")
public class RealtimeWeather {

  @Id
  @Column(name = "location_code")
  @JsonIgnore
  private String locationCode;
  @Range(min = -50, max = 50, message = "Temperature must be between -50 and 50")
  private int temperature;
  @Range(min = 0, max = 100, message = "Humidity must be between 0 and 100")
  private int humidity;
  @Range(min = 0, max = 100, message = "Precipitation must be between 0 and 100")
  private int precipitation;
  @Range(min = 0, max = 100, message = "Wind speed must be between 0 and 100")
  @JsonProperty("wind_speed")
  private int windSpeed;
  @Column(length = 50)
  @NotBlank(message = "Status can't be blank")
  @Length(min = 3, max = 50, message = "Status must have 3-50 characters")
  private String status;
  @JsonProperty("last_update")
  @JsonIgnore
  private Date latUpdate;
  @OneToOne
  @JoinColumn(name = "location_code")
  @MapsId
  @JsonIgnore
  private Location location;

  public String getLocationCode() {
    return locationCode;
  }

  public void setLocationCode(String locationCode) {
    this.locationCode = locationCode;
  }

  public int getTemperature() {
    return temperature;
  }

  public void setTemperature(int temperature) {
    this.temperature = temperature;
  }

  public int getHumidity() {
    return humidity;
  }

  public void setHumidity(int humidity) {
    this.humidity = humidity;
  }

  public int getPrecipitation() {
    return precipitation;
  }

  public void setPrecipitation(int precipitation) {
    this.precipitation = precipitation;
  }

  public int getWindSpeed() {
    return windSpeed;
  }

  public void setWindSpeed(int windSpeed) {
    this.windSpeed = windSpeed;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Date getLatUpdate() {
    return latUpdate;
  }

  public void setLatUpdate(Date latUpdate) {
    this.latUpdate = latUpdate;
  }

  public Location getLocation() {
    return location;
  }

  public void setLocation(Location location) {
    this.locationCode = location.getCode();
    this.location = location;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RealtimeWeather that = (RealtimeWeather) o;
    return Objects.equals(locationCode, that.locationCode);
  }

  @Override
  public int hashCode() {
    return Objects.hash(locationCode);
  }
}
