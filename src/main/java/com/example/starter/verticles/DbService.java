package com.example.starter.verticles;

import com.example.starter.handler.FileHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.mongo.*;
import io.vertx.ext.mongo.FindOptions;
import io.vertx.ext.mongo.MongoClient;
import org.bson.types.ObjectId;

import java.nio.file.FileSystem;

public class DbService extends AbstractVerticle {
  final String COLLECTION="projects";
  final String COLLECTION_CLIENTS="clients";
  final String COLLECTION_FACTURES="factures";



  @Override
  public void start(Promise<Void> startPromise) throws Exception {

    final JsonObject config=new JsonObject()
      .put("db_name","testDB")
      .put("connection_string","mongodb://localhost");
    MongoClient client = MongoClient.create(vertx, config);

    EventBus eb=vertx.eventBus();

    //
    eb.consumer("add.file.db",msg->{
      try {
        JsonObject message = (JsonObject) msg.body();
        String type=message.getString("type");
        JsonObject query = new JsonObject()
          .put("_id", message.getString("id"));
        JsonObject data = new JsonObject()
          .put("$set", new JsonObject()
            .put("file", message.getString("file_name")));

        client.findOneAndUpdate(setCollection(type), query, data, res -> {
          if (res.succeeded()) {
            msg.reply(res.result());
          } else {
            msg.fail(404, "Error");
          }
        });
      }catch (Exception e){
        msg.fail(404, "Unexpected Error");
        System.out.println(e.toString());
      }
    });
    eb.consumer("remove.file.db",msg->{
      try {
        JsonObject query = new JsonObject().put("_id", msg.body().toString());
        JsonObject setData = new JsonObject()
          .put("$unset", new JsonObject().put("file", ""));
        client.findOneAndUpdate(COLLECTION, query, setData, res -> {
          if (res.succeeded()&&res.result().getString("file")!=null) {
            msg.reply(res.result().getString("file"));
          }else{
            msg.fail(404,"Error File not found");
          }
        });
      }catch (Exception e){
        msg.fail(404,"Error DB");
        System.out.println(e);
      }
    });
    //
    eb.consumer("find.all.db",msg->{
      try {
        JsonObject msg_data= (JsonObject) msg.body();
        String type=msg_data.getString("type");
        JsonObject query=new JsonObject();
        if(type.equals("project")||type.equals("facture")) {
          if(msg_data.containsKey("id_project")){
            query.put("id_project",msg_data.getString("id_project"));
          }
          msg_data.remove("type");
          client.find(setCollection(type), query, res -> {
            if (res.succeeded()) {
              msg.reply(new JsonArray(res.result()));
            } else {
              msg.fail(404, type.toUpperCase()+" Not found");
            }
          });
        }else if(type.equals("client")){
          JsonObject fields = new JsonObject().put("nom", true);
          client.findWithOptions(COLLECTION_CLIENTS, query, new FindOptions().setFields(fields), res -> {
            if (res.succeeded()) {
              msg.reply(new JsonArray(res.result()));
            } else {
              msg.fail(404, type.toUpperCase()+" Not found");
            }
          });
        }else {
          msg.fail(404, "Invalid URl");
        }
      }catch (Exception e){
        msg.fail(404,"Not found");
        System.out.println(e.toString());
      }
    });
    eb.consumer("find.one.db",msg->{
      try {
        JsonObject msg_data= (JsonObject) msg.body();
        String type=msg_data.getString("type");
        String id=msg_data.getString("id");
        JsonObject query=new JsonObject().put("_id", id);
        client.findOne(setCollection(type), query, null, res -> {
          if (res.succeeded()&&res.result()!=null) {
            msg.reply(res.result());
          } else {
            msg.fail(404, type+" Not Found");
          }
        });
      }catch (Exception e){
        msg.fail(404, "Unexpected Error");
        System.out.println(e.toString());
      }
    });
    eb.consumer("update.one.db",msg->{
      try {
        JsonObject data = (JsonObject) msg.body();
        String id = data.getString("id");
        String type= data.getString("type");
        data.remove("type");data.remove("id");
        JsonObject setData = new JsonObject()
          .put("$set", data);
        JsonObject query = new JsonObject().put("_id", id);
        client.updateCollection(setCollection(type), query, setData, res -> {
          if (res.succeeded()) {
            System.out.println(type+" "+id+" updated.");
            msg.reply("updated");
          } else {
            msg.fail(400, "Error");
          }
        });
      }catch (Exception e){
        System.out.println(e.toString());
      }
    });
    eb.consumer("add.db",msg->{
      try {
        JsonObject data = (JsonObject) msg.body();
        String type= data.getString("type");
        data.remove("type");
        client.insert(setCollection(type), data, res -> {
          if (res.succeeded()) {
            System.out.println("New "+type+" created ("+res.result()+")");
            msg.reply(res.result());
          } else {
            msg.fail(404, "Error");
          }
        });
      }catch (Exception e){
        msg.fail(404, "Unexpected Error");
        System.out.println(e.toString());
      }
    });
    eb.consumer("delete.facture.db",msg->{
      try {
        String id = msg.body().toString();
        JsonObject query = new JsonObject()
          .put("_id",id);
        client.findOneAndDelete(COLLECTION_FACTURES, query, res -> {
          if (res.succeeded()&&res.result()!=null) {

            if (res.result().getString("file") != null) {
              String file = res.result().getString("file");
              FileHandler.deleteFile(vertx.fileSystem(), "facture", file);
            }
            System.out.println("Facture "+id+" deleted");
            msg.reply("Facture deleted");
          } else {
            msg.fail(404, "Facture not found");
          }
        });
      }catch (Exception e){
        msg.fail(404, "Unexpected Error");
        System.out.println(e.toString());
      }
    });
    eb.consumer("delete.project.db",msg->{
      try {
        String id= (String) msg.body();
        JsonObject query = new JsonObject().put("_id", id);
        client.findOneAndDelete(COLLECTION, query, res -> {
          if (res.succeeded()&&res.result()!=null) {
            if (res.result().getString("file") != null) {
              String file = res.result().getString("file");
              FileHandler.deleteFile(vertx.fileSystem(), "project", file);
            }
            JsonObject query2 = new JsonObject()
              .put("id_project", id);
            client.find(COLLECTION_FACTURES, query2, resData -> {
              if (resData.succeeded()) {
                resData.result().forEach(obj -> {
                  String id_facture=obj.getString("_id");
                  eb.request("delete.facture.db", id_facture);
                });

              }
            });
            System.out.println("Project " + id + " Deleted");
            msg.reply("Project deleted");
          } else {
            msg.fail(404, "Project not found");
          }
        });
      }catch (Exception e){
        msg.fail(404, "Unexpected Error");
        System.out.println(e.toString());
      }
    });
    eb.consumer("delete.client.db",msg->{
      try {
        JsonObject query = new JsonObject().put("_id", msg.body().toString());
        client.removeDocument(COLLECTION_CLIENTS, query, res -> {
          if (res.succeeded()&&res.result().getRemovedCount()>0) {
            System.out.println("client "+msg.body().toString()+" deleted");
            msg.reply("deleted");
          } else {
            msg.fail(404, "Client not Found");
          }
        });
      }catch (Exception e){
        msg.fail(404, "Unexpected Error");
        System.out.println(e.toString());
      }
    });
    //



    //auth
    MongoAuthenticationOptions options = new MongoAuthenticationOptions();
    eb.consumer("register.user",msg->{
      try {
        JsonObject credentials = (JsonObject) msg.body();
        MongoUserUtil us = MongoUserUtil.create(client, options, null);
        us.createUser(credentials.getString("username"), credentials.getString("password"), res -> {
          if (res.succeeded()) {
            System.out.println(res.result());
            msg.reply(res.result());
          } else {
            msg.fail(403, "Error registering");
          }
        });
      }catch (Exception e){
        System.out.println(e.toString());
      }
    });
    eb.consumer("login.user",msg-> {
      try {
        JsonObject credentials = (JsonObject) msg.body();
        MongoAuthentication authenticationProvider = MongoAuthentication.create(client, options);
        authenticationProvider.authenticate(credentials)
          .onSuccess(user -> {
            msg.reply(user.principal());
          })
          .onFailure(err -> {
            msg.fail(403, err.getMessage());
          });
      }catch (Exception e){
        System.out.println(e.toString());
      }
    });
  }

  public JsonObject setType(String type,JsonObject obj){
    if(type.equals("project")){
      obj.put("type","project");
    }else if(type.equals("facture")){
      obj.put("type","facture");
    }
    return obj;
  }
  public String setCollection(String type){
    String coll="";
    if(type.equals("project")){
      coll=COLLECTION;
    }else if(type.equals("client")){
      coll=COLLECTION_CLIENTS;
    }else if(type.equals("facture")){
      coll=COLLECTION_FACTURES;
    }
    return coll;
  }

}
