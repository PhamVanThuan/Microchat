package com.aaj.microchat.client.web.actor;

import akka.actor.Props;
import akka.stream.actor.OneByOneRequestStrategy;
import akka.stream.actor.RequestStrategy;
import akka.stream.actor.UntypedActorSubscriber;

/**
 * Handles commands, by sending them to a corresponding actor in the actor system.
 * 
 *
 */
public class WebSocketCommandSubscriber extends UntypedActorSubscriber {

	
	
	
	public WebSocketCommandSubscriber() {
		System.out.print(requestStrategy());
	}

	public static Props getProps(){
		return Props.create(WebSocketCommandSubscriber.class);
	}
	
	@Override
	public void onReceive(Object obj) throws Exception {
		System.out.println(obj);
	}

	@Override
	public RequestStrategy requestStrategy() {
		return OneByOneRequestStrategy.getInstance();
	}
	
	

}
