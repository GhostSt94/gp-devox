package com.example.starter.handler;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;

public class ProjectHandler {

  final static String TYPE="project";

  public static void projects(RoutingContext ctx){
    CRUD.findAll(ctx, getJsonType());
  }

  public static void getProject(RoutingContext ctx){
    CRUD.findOne(ctx, getJsonType().put("id",ctx.pathParam("id")));
  }

  public static void updateProject(RoutingContext ctx) {
    JsonObject data=ctx.getBodyAsJson();
    CRUD.updateOne(ctx,data.put("id",ctx.pathParam("id")).put("type",TYPE));
  }

  public static void addProject(RoutingContext ctx) {
    JsonObject data=ctx.getBodyAsJson();
    CRUD.addOne(ctx,data.put("type",TYPE));
  }

  public static void deleteProject(RoutingContext ctx) {
    CRUD.deleteOne(ctx,getJsonType().put("id",ctx.pathParam("id")));
  }

  public static JsonObject getJsonType(){
    return new JsonObject().put("type",TYPE);
  }

  public static void removeFile(RoutingContext ctx) {
    try {
      Vertx vertx = ctx.vertx();
      vertx.eventBus().request("remove.file.db", ctx.pathParam("id"), rep -> {
        if (rep.succeeded()) {
          FileHandler.deleteFile(vertx.fileSystem(), TYPE, rep.result().body().toString());
          ctx.response().setStatusCode(200).end("File deleted");
        } else {
          ctx.response().setStatusCode(404).end(rep.cause().getMessage());
        }
      });
    }catch (Exception e){
      ctx.response().setStatusCode(404).end("Unexpected Error");
      System.out.println(e);
    }
  }
}
