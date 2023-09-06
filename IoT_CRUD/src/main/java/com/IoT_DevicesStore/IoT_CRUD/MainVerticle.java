package com.IoT_DevicesStore.IoT_CRUD;
import io.vertx.core.Verticle;
import io.vertx.core.Vertx;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class MainVerticle {
  private static final Logger logger = LoggerFactory.getLogger(MainVerticle.class);

  public MainVerticle(DatabaseService databaseService) {
  }

  public static void main(String[] args) {
    Vertx vertx = Vertx.vertx();
    DatabaseService databaseService = new DatabaseService(vertx);

    vertx.deployVerticle((Verticle) new DeviceVerticle(databaseService), new Handler<AsyncResult<String>>() {
      @Override
      public void handle(AsyncResult<String> result) {
        if (result.succeeded()) {
          logger.info("DeviceVerticle deployed successfully.");
        } else {
          logger.error("Failed to deploy DeviceVerticle: " + result.cause());
        }
      }
    });
  }
}
