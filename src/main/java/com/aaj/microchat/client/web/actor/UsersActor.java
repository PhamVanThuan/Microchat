package com.aaj.microchat.client.web.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;

public class UsersActor extends UntypedActor {

	
	public static Props getProps(){
		return Props.create(UsersActor.class);
	}
	
	public UsersActor() {
		System.out.println("Login actor created");
	}

	@Override
	public void onReceive(Object name) throws Exception {
		sender().tell("Hello " + name + " you are now logged in.", self());
	}
	
	


}
