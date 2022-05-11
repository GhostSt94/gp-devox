package com.example.starter.handler;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientsHandler {
  final static String TYPE="client";
  /*public static void clients(RoutingContext ctx){
    try {
      ctx.vertx().eventBus().request("get.client.all.db", "", res -> {
        if (res.succeeded()) {
          JsonArray arr = (JsonArray) res.result().body();
          ctx.response().setStatusCode(200).send(arr.toBuffer());
        } else {
          ctx.response().setStatusCode(404).end(res.cause().getMessage());
        }
      });
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("Error");
      System.out.println(e.toString());
    }
  }

  public static void addClient(RoutingContext ctx){
    try {
      JsonObject data = ctx.getBodyAsJson();
      ctx.vertx().eventBus().request("add.client.db", data, rep -> {
        if (rep.succeeded()) {
          ctx.response().setStatusCode(200).send(rep.result().body().toString());
        } else {
          ctx.response().setStatusCode(403).end(rep.cause().getMessage());
        }
      });
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("Error");
      System.out.println(e.toString());
    }
  }

  public static void getClient(RoutingContext ctx){
    try {
      String id = ctx.pathParam("id");
      ctx.vertx().eventBus().request("get.client.db", id, res -> {
        if (res.succeeded()) {
          JsonObject obj = (JsonObject) res.result().body();
          ctx.response().setStatusCode(200).send(obj.toBuffer());
        } else {
          ctx.response().setStatusCode(404).end(res.cause().getMessage());
        }
      });
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("Error");
      System.out.println(e.toString());
    }
  }

  public static void updateClient(RoutingContext ctx){
    try {
      String id = ctx.pathParam("id");
      JsonObject data = ctx.getBodyAsJson().put("_id", id);
      ctx.vertx().eventBus().request("update.client.db", data, res -> {
        if (res.succeeded()) {
          JsonObject obj = (JsonObject) res.result().body();
          ctx.response().setStatusCode(200).send(obj.toBuffer());
        } else {
          ctx.response().setStatusCode(404).end(res.cause().getMessage());
        }
      });
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("Error");
      System.out.println(e.toString());
    }
  }

  public static void deleteClient(RoutingContext ctx){
    try {
      String id = ctx.pathParam("id");
      ctx.vertx().eventBus().request("delete.client.db", id, res -> {
        if (res.succeeded()) {
          JsonObject obj = (JsonObject) res.result().body();
          ctx.response().setStatusCode(200).send(obj.toBuffer());
        } else {
          ctx.response().setStatusCode(404).end(res.cause().getMessage());
        }
      });
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("Error");
      System.out.println(e.toString());
    }
  }*/

  public static void clients(RoutingContext ctx){ CRUD.findAll(ctx, getJsonType());
  }

  public static void getClient(RoutingContext ctx){
    CRUD.findOne(ctx, getJsonType().put("id",ctx.pathParam("id")));
  }

  public static void updateClient(RoutingContext ctx) {
    JsonObject data=ctx.getBodyAsJson();
    CRUD.updateOne(ctx,data.put("id",ctx.pathParam("id")).put("type",TYPE));
  }

  public static void addClient(RoutingContext ctx) {
    JsonObject data=ctx.getBodyAsJson();
    CRUD.addOne(ctx,data.put("type",TYPE));
  }

  public static void deleteClient(RoutingContext ctx) {
    CRUD.deleteOne(ctx,getJsonType().put("id",ctx.pathParam("id")));
  }

  public static JsonObject getJsonType(){
    return new JsonObject().put("type",TYPE);
  }
}
