package com.IoT_DevicesStore.IoT_CRUD;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClient;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.core.json.JsonObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(VertxExtension.class)
public class DeviceVerticleTest {
  private WebClient client;

  @BeforeEach
  void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
    client = WebClient.create(vertx);
    vertx.deployVerticle(new DeviceVerticle(new DatabaseService(vertx)), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void testAddDevice(Vertx vertx, VertxTestContext testContext) {
    JsonObject deviceJson = new JsonObject()
      .put("deviceId", "789-tnssdc-789")
      .put("domain", "smart-irrigation")
      .put("state", "TN")
      .put("city", "Chennai")
      .put("location", new JsonObject().put("type", "point").put("coordinates", new JsonArray().add(44.32).add(90.15)))
      .put("deviceType", "smart-phone");

    client.post(8080, "localhost", "/devices")
      .sendJsonObject(deviceJson, response -> {
        if (response.succeeded()) {
          assertEquals(201, response.result().statusCode());
          testContext.completeNow();
        } else {
          testContext.failNow(response.cause());
        }
      });
  }
  @Test
  void testUpdateDevice(Vertx vertx, VertxTestContext testContext) {
    JsonObject deviceUpdateJson = new JsonObject()
      .put("deviceId", "789-tnssdc-789")
      .put("domain", "smart-irrigation")
      .put("state", "TN")
      .put("city", "Chennai")
      .put("location", new JsonObject().put("type", "point").put("coordinates", new JsonArray().add(44.32).add(90.15)))
      .put("deviceType", "smart-tv");

    client.put(8080, "localhost", "/devices/123-asdasd-123")
      .sendJsonObject(deviceUpdateJson, response -> {
        if (response.succeeded()) {
          assertEquals(204, response.result().statusCode());
          testContext.completeNow();
        } else {
          testContext.failNow(response.cause());
        }
      });
  }

  @Test
  void testGetDevice(Vertx vertx, VertxTestContext testContext) {
    client.get(8080, "localhost", "/devices/123-asdasd-123")
      .send(response -> {
        if (response.succeeded()) {
          assertEquals(200, response.result().statusCode());
          JsonObject deviceData = response.result().bodyAsJsonObject();
          assertEquals("123-asdasd-123", deviceData.getString("deviceId"));
          testContext.completeNow();
        } else {
          testContext.failNow(response.cause());
        }
      });
  }

  @Test
  void testDeleteDevice(Vertx vertx, VertxTestContext testContext) {
    client.delete(8080, "localhost", "/devices/789-tnssdc-789")
      .send(response -> {
        if (response.succeeded()) {
          assertEquals(204, response.result().statusCode());
          testContext.completeNow();
        } else {
          testContext.failNow(response.cause());
        }
      });
  }


  @AfterEach
  void cleanup(Vertx vertx, VertxTestContext testContext) {
    vertx.close(testContext.succeeding(v -> testContext.completeNow()));
  }

}
