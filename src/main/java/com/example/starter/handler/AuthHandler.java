package com.example.starter.handler;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthHandler {
  static final Logger log = LogManager.getLogger(AuthHandler.class);
  static Session session;

  public static void register(RoutingContext ctx){
    try {
      ctx.vertx().eventBus().request("auth.user", setMessage("register",ctx.getBodyAsJson()), rep -> {
        if (rep.succeeded()) {
          ctx.response().setStatusCode(200).send(rep.result().body().toString());
        } else {
          ctx.response().setStatusCode(403).end(rep.cause().getMessage());
        }
      });
    }catch (Exception e){
      log.info(e.getMessage());
    }
  }

  public static void login(RoutingContext ctx){
    try {
      ctx.vertx().eventBus().request("auth.user", setMessage("login",ctx.getBodyAsJson()), rep -> {
        if (rep.succeeded()) {
          JsonObject obj= (JsonObject) rep.result().body();
          session= ctx.session();
          session.put("user", obj);
          ctx.response().setStatusCode(200).send(rep.result().body().toString());
        } else {
          ctx.response().setStatusCode(403).end(rep.cause().getMessage());
        }
      });
    }catch (Exception e){
      log.info(e.getMessage());
    }
  }

  public static void checkAuthAPI(RoutingContext ctx) {
    if(session==null){
      log.warn("session null");
      //ctx.response().setStatusCode(400).end("not connected");
      ctx.redirect("/auth");
    }else if (session.get("user") == null) {
      log.warn("user not found in session");
      ctx.redirect("/auth");
    }else{
      JsonObject user=session.get("user");
      ctx.next();
    }
  }

  public static void checkAuth(RoutingContext ctx){
    if(session==null){
      log.warn("session null");
      ctx.response().setStatusCode(400).end("not connected");
    }else if (session.get("user") == null) {
        log.warn("user not found in session");
        ctx.response().setStatusCode(400).end("not connected");
    }else{
      JsonObject user=session.get("user");
      ctx.response().setStatusCode(200).end(user.toBuffer());
    }
  }

  public static void logOut(RoutingContext ctx) {
    if(session==null){
      log.warn("session null");
      ctx.response().setStatusCode(400).end("Already loggedOut");
    }else{
      log.info("session destroyed");
      session.destroy();
      ctx.response().setStatusCode(200).end("logout successfully");
    }
  }

  public static JsonObject setMessage(String type,JsonObject credentials){
    return new JsonObject()
      .put("type",type)
      .put("credentials",credentials);
  }
}

