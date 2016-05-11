package com.aaj.microchat.client.web.actor;

import akka.actor.Props;
import akka.http.javadsl.model.ws.TextMessage;
import akka.stream.actor.ActorSubscriberMessage;
import akka.stream.actor.OneByOneRequestStrategy;
import akka.stream.actor.RequestStrategy;
import akka.stream.actor.UntypedActorSubscriber;

/**
 * Sink, handles commands, by sending them to a corresponding actor in the actor system.
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
	public void onReceive(Object arg) throws Exception {
		System.out.println("subs: " + this.getSender()); 
		if(ActorSubscriberMessage.OnNext.class.equals(arg.getClass())){
			ActorSubscriberMessage.OnNext m = (ActorSubscriberMessage.OnNext)arg;
			
			System.out.println(m.element());
		}else{
			System.out.println(arg);
		}
	}

	@Override
	public RequestStrategy requestStrategy() {
		return OneByOneRequestStrategy.getInstance();
	}
	
	

}
