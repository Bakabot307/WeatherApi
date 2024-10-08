package com.skyapi.weatherforecast.common;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "weather_daily")
public class DailyWeather {

  @EmbeddedId
  private DailyWeatherId id = new DailyWeatherId();

  private int minTemp;
  private int maxTemp;
  private int precipitation;
  @Column(length = 50)
  private String status;

  public DailyWeather() {
  }

  public DailyWeatherId getId() {
    return id;
  }

  public void setId(DailyWeatherId id) {
    this.id = id;
  }

  public int getMinTemp() {
    return minTemp;
  }

  public void setMinTemp(int minTemp) {
    this.minTemp = minTemp;
  }

  public int getMaxTemp() {
    return maxTemp;
  }

  public void setMaxTemp(int maxTemp) {
    this.maxTemp = maxTemp;
  }

  public int getPrecipitation() {
    return precipitation;
  }

  public void setPrecipitation(int precipitation) {
    this.precipitation = precipitation;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }


  public DailyWeather minTemp(int minTemp) {
    setMinTemp(minTemp);
    return this;
  }

  public DailyWeather maxTemp(int maxTemp) {
    setMaxTemp(maxTemp);
    return this;
  }

  public DailyWeather precipitation(int precipitation) {
    setPrecipitation(precipitation);
    return this;
  }

  public DailyWeather status(String status) {
    setStatus(status);
    return this;
  }
  public DailyWeather id(int dayOfMonth, int month, Location location) {
    this.id.setLocation(location);
    this.id.setDayOfMonth(dayOfMonth);
    this.id.setMonth(month);
    return this;
  }
  public DailyWeather location(Location location) {
    this.id.setLocation(location);
    return this;
  }

  public DailyWeather dayOfMonth(int day) {
    this.id.setDayOfMonth(day);
    return this;
  }

  public DailyWeather month(int month) {
    this.id.setMonth(month);
    return this;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DailyWeather that = (DailyWeather) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return String.format(
        "DailyWeather (        id=%s        ,         minTemp=%s        ,         maxTemp=%s        ,         precipitation=%s        ,         status=%s)",
        this.id, this.minTemp, this.maxTemp, this.precipitation, this.status);
  }
}
