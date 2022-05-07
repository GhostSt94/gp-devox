package com.example.starter.handler;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CRUD {
  static final Logger log = LogManager.getLogger(CRUD.class);

  public static void findAll(RoutingContext ctx, JsonObject jsonType) {
    try {
      ctx.vertx().eventBus().request("find.all.db", jsonType, resp -> {
        if (resp.succeeded()) {
          JsonArray arr = (JsonArray) resp.result().body();
          ctx.response().send(arr.toBuffer());
        } else {
          ctx.response().setStatusCode(404).end(resp.cause().getMessage());
        }
      });
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("Unexpected Error");
      log.error(e.getMessage());
    }
  }

  public static void findOne(RoutingContext ctx, JsonObject jsonType) {
    try {
      ctx.vertx().eventBus().request("find.one.db", jsonType, resp -> {
        if (resp.succeeded()) {
          JsonObject obj = (JsonObject) resp.result().body();
          ctx.response().send(obj.toBuffer());
        } else {
          ctx.response().setStatusCode(404).end(resp.cause().getMessage());
        }
      });
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("Unexpected Error");
      log.error(e.getMessage());
    }
  }

  public static void updateOne(RoutingContext ctx, JsonObject jsonType) {
    try {
      ctx.vertx().eventBus().request("update.one.db", jsonType, resp -> {
        if (resp.succeeded()) {
          ctx.response().setStatusCode(200).send(resp.result().body().toString());
        } else {
          ctx.response().setStatusCode(404).end(resp.cause().getMessage());
        }
      });
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("Unexpected Error");
      log.error(e.getMessage());
    }
  }

  public static void addOne(RoutingContext ctx, JsonObject jsonType) {
    try {
      ctx.vertx().eventBus().request("add.db", jsonType, resp -> {
        if (resp.succeeded()) {
          ctx.response().setStatusCode(200).send(resp.result().body().toString());
        } else {
          ctx.response().setStatusCode(404).end(resp.cause().getMessage());
        }
      });
    } catch (Exception e) {
      ctx.response().setStatusCode(404).end("Unexpected Error");
      log.error(e.getMessage());
    }
  }

  public static void deleteOne(RoutingContext ctx, JsonObject jsonType) {
    try {
      String address="";
      String type=jsonType.getString("type");
      if(type.equals("facture")){
        address="delete.facture.db";
      }else if(type.equals("project")){
        address="delete.project.db";
      }else if(type.equals("client")){
        address="delete.client.db";
      }

      String id=jsonType.getString("id");
      ctx.vertx().eventBus().request(address, id, resp -> {
        if (resp.succeeded()&&resp.result().body()!=null) {
          ctx.response().setStatusCode(200).send(resp.result().body().toString());
        } else {
          ctx.response().setStatusCode(404).end(resp.cause().getMessage());
        }
      });
    } catch (Exception e) {
      ctx.response().setStatusCode(404).end("Unexpected Error");
      log.error(e.getMessage());
    }
  }
}
