package com.example.starter;

import com.example.starter.verticles.DbService;
import com.example.starter.verticles.MainVerticle;
import io.vertx.core.Vertx;

public class Main {

  public static void main(String[] args){

    try {
      Vertx vertx = Vertx.vertx();
      vertx.deployVerticle(new MainVerticle(), res -> {
        if (res.succeeded()) {
          vertx.deployVerticle(new DbService());
        }
      });
    }catch (Exception e){
      System.out.println(e.getMessage());
    }
  }
}
