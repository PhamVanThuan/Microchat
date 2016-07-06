package com.aaj.microchat.client.web;





import com.aaj.microchat.client.web.actor.UsersActor;
import com.aaj.microchat.client.web.controller.Controller;
import com.aaj.microchat.client.web.controller.UsersController;


import com.aaj.microchat.client.web.controller.UsersWSController;
import com.aaj.microchat.server.actor.ChatManagerActor;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import akka.routing.FromConfig;

public class MicrochatWebApp extends HttpApp{

	private final Controller logInController;
	private final Controller wsController;
	
	public MicrochatWebApp(ActorSystem frontSystem) {
		ActorRef logInActor = frontSystem.actorOf(FromConfig.getInstance().props(UsersActor.getProps()), 
				 "usersActor");
		
		ActorRef chatManagerActor = frontSystem.actorOf(FromConfig.getInstance().props(ChatManagerActor.getProps()), 
				 "chatManagerActor");
		
		this.logInController = new UsersController(logInActor, chatManagerActor);
		this.wsController = new UsersWSController(logInActor, chatManagerActor, frontSystem);
		bindRoute("localhost", 8080, frontSystem);
	}

	@Override
	public Route createRoute() {
		try {
			return this.wsController.defineRoutes().get();
		} catch (Exception e) {
			throw(new RuntimeException(e));
		}
	}

}
