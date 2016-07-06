package com.aaj.microchat.client.web.actor;

import akka.actor.Props;
import akka.stream.actor.ActorPublisherMessage;
import akka.stream.actor.UntypedActorPublisher;

/**
 * Publisher listens on the event stream and publishes update information back to the stream (and so finally to the client)
 *
 */
public class WebSocketDataPublisherActor extends UntypedActorPublisher<Object> {

	public WebSocketDataPublisherActor() {
		
	}
	
	public static Props getProps(){
		return Props.create(WebSocketDataPublisherActor.class);
	}
	
	@Override
	public void onReceive(Object arg) throws Exception {
		System.out.println("pub: " + this.getSender());
		if(ActorPublisherMessage.Request.class.equals(arg.getClass())){
			ActorPublisherMessage.Request m = (ActorPublisherMessage.Request)arg;
			//while (isActive() && totalDemand() > 0) {
				System.out.println("mmm" + m);
				System.out.println("arrrg " + arg);
				//Send an element to the stream subscriber.
				//onNext("hola");
			//}
		}else{
			System.out.println("arrrg " + arg);
			//onNext(arg);
		}

	}

}
