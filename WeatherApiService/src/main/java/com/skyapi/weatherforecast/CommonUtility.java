package com.skyapi.weatherforecast;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonUtility {
  public static final Logger LOGGER = LoggerFactory.getLogger(CommonUtility.class);
   public static String GetIpAddress(HttpServletRequest request){
     String ip = request.getHeader("X-FORWARD-FOR");
     if(ip==null || ip.isEmpty()){
       ip = request.getRemoteAddr();
     }
     LOGGER.info("User IP is "+ip);
     return ip;
   }

}
