package com.example.starter.verticles;

import com.example.starter.handler.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.file.FileSystem;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.bridge.BridgeEventType;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.FileUpload;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.sockjs.SockJSBridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

public class MainVerticle extends AbstractVerticle {

  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router=Router.router(vertx);

    try {
      SockJSBridgeOptions options = new SockJSBridgeOptions();
      PermittedOptions address = new PermittedOptions().setAddressRegex("[^\n]+");
      options.addInboundPermitted(address);
      options.addOutboundPermitted(address);

      SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
      router.mountSubRouter("/eventbus",sockJSHandler.bridge(options, be -> {
        if (be.type() == BridgeEventType.REGISTER) {
          System.out.println("sockJs: connected");
        }
        be.complete(true);
      }));
    }catch (Exception e){
      System.out.println(e.getMessage());
    }


    router.route().handler(BodyHandler.create());
    router.route().handler(CorsHandler.create("http://localhost:8080")
      .allowedMethod(HttpMethod.POST)
      .allowedMethod(HttpMethod.GET)
      .allowedMethod(HttpMethod.PUT)
      .allowedMethod(HttpMethod.DELETE));

    //receive and save facture file in resources & DB (file_name)
    router.post("/files/:type/:id").handler(FileHandler::saveFile);
    router.get("/files/:type/:fileName").handler(FileHandler::getFile);

    //Auth
    router.post("/auth/register").handler(AuthHandler::register);
    router.post("/auth/login").handler(AuthHandler::login);

    //clients
    router.get("/clients").handler(ClientsHandler::clients);
    router.post("/clients").handler(ClientsHandler::addClient);
    router.get("/clients/:id").handler(ClientsHandler::getClient);
    router.put("/clients/:id").handler(ClientsHandler::updateClient);
    router.delete("/clients/:id").handler(ClientsHandler::deleteClient);
    //Project
    router.get("/projects").handler(ProjectHandler::projects);
    router.get("/projects/:id").handler(ProjectHandler::getProject);
    router.post("/projects").handler(ProjectHandler::addProject);
    router.put("/projects/:id").handler(ProjectHandler::updateProject);
    router.delete("/projects/:id").handler(ProjectHandler::deleteProject);
    router.delete("/projects/:id/file").handler(ProjectHandler::removeFile);
    //Facture
    router.get("/factures/:id").handler(FactureHandler::factures);
    router.get("/factures/one/:id").handler(FactureHandler::getFacture);
    router.post("/factures").handler(FactureHandler::addFacture);
    router.delete("/factures/:id").handler(FactureHandler::deleteFacture);


    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        System.out.println("HTTP server started on port 8888");
      } else {
        startPromise.fail(http.cause());
      }
    });
  }
}
