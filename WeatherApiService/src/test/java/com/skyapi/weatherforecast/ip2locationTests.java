package com.skyapi.weatherforecast;

import static org.assertj.core.api.Assertions.assertThat;

import com.ip2location.IP2Location;
import com.ip2location.IPResult;
import java.io.IOException;
import org.junit.jupiter.api.Test;

public class ip2locationTests {

  private String DBPath = "ip2locdb/IP2LOCATION-LITE-DB3.BIN";

  @Test
  public void testInvalidIP() throws IOException {
    IP2Location location = new IP2Location();
    location.Open(DBPath);

    String ipAddress = "abc";

    IPResult ipResult = location.IPQuery(ipAddress);

    assertThat(ipResult.getStatus()).isEqualTo("INVALID_IP_ADDRESS");
  }

  @Test
  public void testValidIP1() throws IOException {
    IP2Location location = new IP2Location();
    location.Open(DBPath);

    String ipAddress = "108.30.178.78"; //new work

    IPResult ipResult = location.IPQuery(ipAddress);

    assertThat(ipResult.getStatus()).isEqualTo("OK");
    assertThat(ipResult.getCity()).isEqualTo("New York City");
    System.out.println(ipResult);
  }

  @Test
  public void testValidIP2() throws IOException {
    IP2Location location = new IP2Location();
    location.Open(DBPath);

    String ipAddress = "103.48.198.141"; //Delhi india

    IPResult ipResult = location.IPQuery(ipAddress);

    assertThat(ipResult.getStatus()).isEqualTo("OK");
    assertThat(ipResult.getCity()).isEqualTo("Delhi");
    System.out.println(ipResult);
  }
}
