package com.example.starter.verticles;

import com.example.starter.handler.*;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.http.Cookie;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.CorsHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;
import io.vertx.ext.web.sstore.SessionStore;
import netscape.javascript.JSObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.CookieHandler;
import java.net.CookieManager;

public class MainVerticle extends AbstractVerticle {
  static final Logger log = LogManager.getLogger(MainVerticle.class);
  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    Router router=Router.router(vertx);

    router.route().handler(BodyHandler.create());
    router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));

    router.route().pathRegex("/api[/]?.*").handler(AuthHandler::checkAuthAPI);
    //router.route().handler(CorsHandler.create("*").allowedMethod(HttpMethod.POST).allowedMethod(HttpMethod.GET));

    //receive and save facture file in resources & DB (file_name)
    router.post("/api/files/:type/:id").handler(FileHandler::saveFile);
    router.get("/api/files/:type/:fileName").handler(FileHandler::getFile);

    //clients
    router.get("/api/clients").handler(ClientsHandler::clients);
    router.post("/api/clients").handler(ClientsHandler::addClient);
    router.get("/api/clients/:id").handler(ClientsHandler::getClient);
    router.put("/api/clients/:id").handler(ClientsHandler::updateClient);
    router.delete("/clients/:id").handler(ClientsHandler::deleteClient);
    //Project
    router.get("/api/projects").handler(ProjectHandler::projects);
    router.get("/api/projects/:id").handler(ProjectHandler::getProject);
    router.post("/api/projects").handler(ProjectHandler::addProject);
    router.put("/api/projects/:id").handler(ProjectHandler::updateProject);
    router.delete("/api/projects/:id").handler(ProjectHandler::deleteProject);
    router.delete("/api/projects/:id/file").handler(ProjectHandler::removeFile);
    //Facture
    router.get("/api/factures/project/:id").handler(FactureHandler::factures);
    router.get("/api/factures/one/:id").handler(FactureHandler::getFacture);
    router.put("/api/factures/:id").handler(FactureHandler::updateFacture);
    router.post("/api/factures").handler(FactureHandler::addFacture);
    router.delete("/api/factures/:id").handler(FactureHandler::deleteFacture);

    //Auth
    router.post("/auth/register").handler(AuthHandler::register);
    router.post("/auth/login").handler(AuthHandler::login);
    router.get("/auth/check").handler(AuthHandler::checkAuth);
    router.get("/auth/logout").handler(AuthHandler::logOut);

    //static
    router.route("/").handler(StaticHandler.create("src/main/app/dist").setIndexPage("index.html"));
    router.route("/auth").handler(StaticHandler.create("src/main/app/dist"));
    router.route("/clients").handler(StaticHandler.create("src/main/app/dist"));
    //router.route("/clients/:id").handler(StaticHandler.create("src/main/app/dist"));
    router.route("/ajouter/projet").handler(StaticHandler.create("src/main/app/dist"));
    router.route("/ajouter/client").handler(StaticHandler.create("src/main/app/dist"));
    router.route("/*").handler(ctx->ctx.redirect("/"));

    vertx.createHttpServer().requestHandler(router).listen(8888, http -> {
      if (http.succeeded()) {
        startPromise.complete();
        log.info("HTTP server started on port 8888");
      } else {
        log.warn(http.cause().getMessage());
        startPromise.fail(http.cause());
      }
    });
  }
}
