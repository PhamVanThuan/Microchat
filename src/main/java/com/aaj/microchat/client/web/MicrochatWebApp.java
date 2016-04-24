package com.aaj.microchat.client.web;





import com.aaj.microchat.client.web.actor.UsersActor;
import com.aaj.microchat.client.web.controller.Controller;
import com.aaj.microchat.client.web.controller.UsersController;


import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.server.HttpApp;
import akka.http.javadsl.server.Route;
import akka.routing.FromConfig;

public class MicrochatWebApp extends HttpApp{

	private final Controller logInController;
	
	public MicrochatWebApp(ActorSystem frontSystem) {
		ActorRef logInActor = frontSystem.actorOf(FromConfig.getInstance().props(UsersActor.getProps()), 
				 "usersActor");
		this.logInController = new UsersController(logInActor);
		bindRoute("localhost", 8080, frontSystem);
	}

	@Override
	public Route createRoute() {
		try {
			return this.logInController.defineRoutes().get();
		} catch (Exception e) {
			throw(new RuntimeException(e));
		}
	}

}
