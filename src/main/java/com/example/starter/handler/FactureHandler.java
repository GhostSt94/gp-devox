package com.example.starter.handler;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class FactureHandler {

  final static String TYPE="facture";

  public static void factures(RoutingContext ctx){
    String id=ctx.pathParam("id");
    CRUD.findAll(ctx,getJsonType().put("id_project",id));
  }

  public static void getFacture(RoutingContext ctx){
    String id=ctx.pathParam("id");
    CRUD.findOne(ctx,getJsonType().put("id",id));
  }

  public static void addFacture(RoutingContext ctx) {
    JsonObject data=ctx.getBodyAsJson();
    CRUD.addOne(ctx,data.put("type",TYPE));
  }

  public static void deleteFacture(RoutingContext ctx) {
    CRUD.deleteOne(ctx,getJsonType().put("id",ctx.pathParam("id")));
  }

  public static void updateFacture(RoutingContext ctx) {
    JsonObject data=ctx.getBodyAsJson();
    CRUD.updateOne(ctx,data.put("type",TYPE).put("id",ctx.pathParam("id")));
  }

  public static JsonObject getJsonType(){
    return new JsonObject().put("type",TYPE);
  }

}
