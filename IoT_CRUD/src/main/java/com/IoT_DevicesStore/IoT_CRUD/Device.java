package com.IoT_DevicesStore.IoT_CRUD;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.vertx.core.json.JsonObject;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class Device {
  @JsonProperty("deviceId")
  private String deviceId;
  @JsonProperty("domain")
  private String domain;
  @JsonProperty("state")
  private String state;
  @JsonProperty("city")
  private String city;
  @JsonProperty("location")
  private Location location;
 // private String Type;
 // private JsonArray Coordinates;
  @JsonProperty("deviceType")
  private String deviceType;

  public Device() {
    // Default constructor
  }

  public Device(String deviceId, String domain, String state, String city, Location location, String deviceType) {
    this.deviceId = deviceId;
    System.out.println("This is DeviceId:"+deviceId);
    this.domain = domain;
    this.state = state;
    this.city = city;
    //this.location = location;
    //this.Type= location.getString("Type");
   // this.Coordinates=location.getJsonArray("Coordinates");
    setLocation(location);
    this.deviceType = deviceType;
    System.out.println(getLocation());
  }
/*public Location getLocation(){
    return location;
}*/
  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }

  public void setState(String state) {
    this.state = state;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public void setLocation(Location location) {
    this.location = location;
  }

  public void setDeviceType(String deviceType) {
    this.deviceType = deviceType;
  }


}




