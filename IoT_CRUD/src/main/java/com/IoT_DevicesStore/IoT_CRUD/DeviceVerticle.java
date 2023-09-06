package com.IoT_DevicesStore.IoT_CRUD;

import com.IoT_DevicesStore.IoT_CRUD.DatabaseService;
import com.IoT_DevicesStore.IoT_CRUD.Device;
import io.vertx.core.*;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.*;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.util.List;
import java.util.Map;


public class DeviceVerticle extends AbstractVerticle {

  private final DatabaseService databaseService;
  private final Logger logger = LoggerFactory.getLogger(DeviceVerticle.class);

  public DeviceVerticle(DatabaseService databaseService) {
    this.databaseService = databaseService;
  }

  @Override
  public void start(Promise<Void> startPromise) {
    Router router = Router.router(vertx);

    // Enable JSON body parsing
    router.route().handler(BodyHandler.create());

    // Define API routes for CRUD operations
    router.post("/devices").handler(this::addDevice);
    router.put("/devices/:deviceId").handler(this::updateDevice);
    router.get("/devices/:deviceId").handler(this::getDevice);
    router.delete("/devices/:deviceId").handler(this::deleteDevice);

    vertx.createHttpServer()
      .requestHandler(router)
      .listen(8080, result -> {
        if (result.succeeded()) {
          logger.info("HTTP server started successfully on port 8080.");
          startPromise.complete();
        } else {
          logger.error("Failed to start HTTP server: " + result.cause());
          startPromise.fail(result.cause());
        }
      });
  }

  private void addDevice(RoutingContext context) {
    HttpServerResponse response = context.response();
    Device device = context.getBodyAsJson().mapTo(Device.class);
    databaseService.getClient().getConnection(res -> {
      if (res.succeeded()) {
        SQLConnection connection = res.result();

        // Perform the database insert operation
        connection.updateWithParams(
          "INSERT INTO devices (\"deviceId\", domain, state, city, location, \"deviceType\") VALUES (?, ?, ?, ?, ?::json, ?)",
          new JsonArray().add(device.getDeviceId()).add(device.getDomain())
            .add(device.getState()).add(device.getCity())
          .add(JsonObject.mapFrom(device.getLocation()).toString())
            .add(device.getDeviceType()),
          insertResult -> {
            connection.close();
            if (insertResult.succeeded()) {
              response.setStatusCode(201) // Created
                .end();
            } else {
              logger.error("Error while inserting device: " + insertResult.cause());
              response.setStatusCode(500) // Internal Server Error
                .end("Error while inserting device");
            }
          }
        );
      } else {
        logger.error("Error while connecting to the database: " + res.cause());
        response.setStatusCode(500) // Internal Server Error
          .end("Error while connecting to the database");
      }
    });
  }



  private void updateDevice(RoutingContext context) {
    HttpServerResponse response = context.response();
   String deviceId = context.request().getParam("deviceId");
    logger.debug(context.body().asJsonObject());
    JsonObject requestJson = context.body().asJsonObject();
    System.out.println("Hi Hello: "+requestJson.getJsonObject("location").getString("Type"));
    //JsonObject requestJson = new JsonObject().put("deviceId","123-asdasd-123").put("domain","smart_transport");
   // requestJson.put("city","anylocation");
    Device updatedDevice = requestJson.mapTo(Device.class);
    Promise<Void> updatePromise = Promise.promise();

    databaseService.getClient().getConnection(res -> {
      if (res.succeeded()) {
        SQLConnection connection = res.result();
        System.out.println("This is device city:"+updatedDevice.getCity());
        // Perform the database update operation
        connection.updateWithParams(
          "UPDATE devices SET domain = ?, state = ?, city = ?, location = ?::json,\"deviceType\" = ? WHERE \"deviceId\" = ?",
          new JsonArray().add(updatedDevice.getDomain())
            .add(updatedDevice.getState())
            .add(updatedDevice.getCity())
            .add(JsonObject.mapFrom(updatedDevice.getLocation()).toString())
            .add(updatedDevice.getDeviceType())
            .add(deviceId),
          updateResult -> {
            connection.close();
            if (updateResult.succeeded()) {
              response.setStatusCode(204) // No Content
                .end();
              logger.info("Device updated successfully.");
              updatePromise.complete();
            } else {
              logger.error("Error while updating device: " + updateResult.cause());
              response.setStatusCode(500) // Internal Server Error
                .end("Error while updating device");
              updatePromise.fail(updateResult.cause());
            }
          }
        );
      } else {
        logger.error("Error while connecting to the database: " + res.cause());
        response.setStatusCode(500) // Internal Server Error
          .end("Error while connecting to the database");
        updatePromise.fail(res.cause());
      }
    });


  }
  private void getDevice(RoutingContext context) {
    HttpServerResponse response = context.response();
    String deviceId = context.request().getParam("deviceId");

    databaseService.getClient().getConnection(res -> {
      if (res.succeeded()) {
        SQLConnection connection = res.result();

        // Perform the database select operation
        connection.queryWithParams(
          "SELECT \"deviceId\", domain, state, city, location::json, \"deviceType\"\n" +
            "\tFROM devices WHERE \"deviceId\" = ?",
          new JsonArray().add(deviceId),
          queryResult -> {
            connection.close();
            if (queryResult.succeeded()) {
              List<JsonObject> rows = queryResult.result().getRows();
              if (!rows.isEmpty()) {
                JsonObject deviceJson = rows.get(0);
                logger.debug(deviceJson.encodePrettily());
                //Device device = deviceJson.mapTo(Device.class);
                //Device device=Device.fromJson(deviceJson);
                response.putHeader("content-type", "application/json")
                  .end(deviceJson.encodePrettily());
                logger.info("Device retrieved successfully.");
              } else {
                logger.error("Device not found.");
                response.setStatusCode(404) // Not Found
                  .end("Device not found");
              }
            } else {
              // An error occurred during the database query
              Throwable queryError = queryResult.cause(); // Get the exception
              logger.error("Error while retrieving device: " + queryError.getMessage(), queryError); // Log error with stack trace
              response.setStatusCode(500) // Internal Server Error status code
                .end("Error while retrieving device: " + queryError.getMessage());
            }

          }
        );
      } else {
        logger.error("Error while connecting to the database: " + res.cause());
        response.setStatusCode(500) // Internal Server Error
          .end("Error while connecting to the database");
      }
    });
  }


  private void deleteDevice(RoutingContext context) {
    HttpServerResponse response = context.response();
    String deviceId = context.request().getParam("deviceId");

    databaseService.getClient().getConnection(res -> {
      if (res.succeeded()) {
        SQLConnection connection = res.result();

        // Perform the database delete operation
        connection.updateWithParams(
          "DELETE FROM devices WHERE \"deviceId\" = ?",
          new JsonArray().add(deviceId),
          deleteResult -> {
            connection.close();
            if (deleteResult.succeeded()) {
              response.setStatusCode(204) // No Content
                .end();
              logger.info("Device deleted successfully.");
            } else {
              logger.error("Error while deleting device: " + deleteResult.cause());
              response.setStatusCode(500) // Internal Server Error
                .end("Error while deleting device");
            }
          }
        );
      } else {
        logger.error("Error while connecting to the database: " + res.cause());
        response.setStatusCode(500) // Internal Server Error
          .end("Error while connecting to the database");
      }
    });
  }

}
