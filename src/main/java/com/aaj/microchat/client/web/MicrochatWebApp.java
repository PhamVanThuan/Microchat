package com.aaj.microchat.client.web;





import java.io.IOException;

import com.aaj.microchat.client.web.actor.UsersActor;
import com.aaj.microchat.client.web.controller.Controller;
import com.aaj.microchat.client.web.controller.UsersController;


import com.aaj.microchat.client.web.controller.UsersWSController;
import com.aaj.microchat.server.actor.ChatManagerActor;

import akka.NotUsed;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.*;
import akka.routing.FromConfig;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

public class MicrochatWebApp extends AllDirectives {

	private final Controller logInController;
	private final Controller wsController;
	
	public MicrochatWebApp(ActorSystem system, ActorMaterializer materializer) throws IOException {
		ActorRef logInActor = system.actorOf(FromConfig.getInstance().props(UsersActor.getProps()), 
				 "usersActor");
		
		ActorRef chatManagerActor = system.actorOf(FromConfig.getInstance().props(ChatManagerActor.getProps()), 
				 "chatManagerActor");
		
		this.logInController = new UsersController(logInActor, chatManagerActor);
		this.wsController = new UsersWSController(logInActor, chatManagerActor, system);
		final Flow<HttpRequest, HttpResponse, NotUsed> flow = createRoute().flow(system, materializer);
		Http.get(system).bindAndHandle(flow, ConnectHttp.toHost("localhost", 8080), materializer);
		System.out.println("Snapchat running on port 8080. Type RETURN to exit.");
		System.in.read();
	}

	
	public Route createRoute() {
		try {
			return this.wsController.defineRoutes().get();
		} catch (Exception e) {
			throw(new RuntimeException(e));
		}
	}

}
