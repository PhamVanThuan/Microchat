package com.aaj.microchat.client.web.actor;

import com.aaj.microchat.client.web.controller.UsersWSController.RegisterSourceMessage;

import akka.actor.ActorRef;
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

	private ActorRef requestor;
	
	public WebSocketCommandSubscriber() {
	}

	public static Props getProps(){
		return Props.create(WebSocketCommandSubscriber.class);
	}
	
	@Override
	public void onReceive(Object arg) throws Exception {
		System.out.println("subs: " + this.getSender()+ ", arg: " + arg + " class" + arg.getClass()); 
		if(RegisterSourceMessage.class.equals(arg.getClass())){
			this.requestor = ((RegisterSourceMessage)arg).getRequestor();
		}else{
			if(this.requestor!=null){
				this.requestor.tell("ack!", getSelf());
			}
		}
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
