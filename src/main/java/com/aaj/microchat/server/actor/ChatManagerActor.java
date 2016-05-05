package com.aaj.microchat.server.actor;

import akka.actor.Props;
import akka.actor.UntypedActor;

public class ChatManagerActor extends UntypedActor {

	
	public static Props getProps(){
		return Props.create(ChatManagerActor.class);
	}
	
	public ChatManagerActor() {
		System.out.println("Chat manager actor created");
	}

	@Override
	public void onReceive(Object name) throws Exception {
		sender().tell("Hello " + name + " you are now logged in.", self());
	}
	
	


}
