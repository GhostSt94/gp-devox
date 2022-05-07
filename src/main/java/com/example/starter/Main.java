package com.example.starter;

import com.example.starter.verticles.DbService;
import com.example.starter.verticles.MainVerticle;
import io.vertx.core.Vertx;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {
  static final Logger log = LogManager.getLogger(Main.class);

  public static void main(String[] args){
    try {
      Vertx vertx = Vertx.vertx();
      vertx.deployVerticle(new MainVerticle(), res -> {
        if (res.succeeded()) {
          vertx.deployVerticle(new DbService());
        }else{
          log.fatal(res.cause().getMessage());
        }
      });
    }catch (Exception e){
      log.fatal(e.getMessage());
    }
  }
}
