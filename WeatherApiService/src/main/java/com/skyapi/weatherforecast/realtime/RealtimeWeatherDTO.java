package com.skyapi.weatherforecast.realtime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import java.util.Date;


public class RealtimeWeatherDTO {


  private String location;
  private int temperature;
  private int humidity;
  private int precipitation;
  @JsonProperty("wind_speed")
  private int windSpeed;
  @Column(length = 50)
  private String status;
  @JsonProperty("last_update")
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
  private Date latUpdate;



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


  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }
}
