package com.aaj.microchat.client.web.actor;

import akka.actor.Props;
import akka.http.javadsl.model.ws.TextMessage;
import akka.stream.actor.ActorPublisherMessage;
import akka.stream.actor.UntypedActorPublisher;
import akka.stream.javadsl.Source;

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
				System.out.println("arrrg 1" + arg);
				TextMessage.create(Source.single("ack!"));
				//Send an element to the stream subscriber.
				
			//}
		}else{
			System.out.println("arrrg 2" + arg);
			onNext(TextMessage.create("Ack!"));
		}

	}

}
