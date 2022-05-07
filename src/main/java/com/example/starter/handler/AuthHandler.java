package com.example.starter.handler;

import io.vertx.ext.web.RoutingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AuthHandler {
  static final Logger log = LogManager.getLogger(AuthHandler.class);

  public static void register(RoutingContext ctx){
    try {
      ctx.vertx().eventBus().request("register.user", ctx.getBodyAsJson(), rep -> {
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
      ctx.vertx().eventBus().request("login.user", ctx.getBodyAsJson(), rep -> {
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
}
