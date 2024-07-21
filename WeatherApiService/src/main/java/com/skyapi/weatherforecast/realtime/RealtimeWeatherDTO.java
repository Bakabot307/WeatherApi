package com.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;


public class RealtimeWeatherDTO {
  private String location;
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
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date lastUpdate;



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

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }


  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

}
