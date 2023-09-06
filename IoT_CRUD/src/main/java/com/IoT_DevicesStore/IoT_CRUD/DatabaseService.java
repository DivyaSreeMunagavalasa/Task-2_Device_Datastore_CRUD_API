package com.IoT_DevicesStore.IoT_CRUD;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;

public class DatabaseService {
  private final SQLClient sqlClient;

  public DatabaseService(Vertx vertx) {
    JsonObject config = new JsonObject()
      .put("url", "jdbc:postgresql://localhost:5432/devices")
      .put("driver_class", "org.postgresql.Driver")
      .put("user", "postgres")
      .put("password", "Postgres@12");

    this.sqlClient = JDBCClient.createShared(vertx, config);
  }

  public SQLClient getClient() {
    return sqlClient;
  }
}
