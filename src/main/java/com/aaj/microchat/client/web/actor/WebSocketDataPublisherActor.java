package com.aaj.microchat.client.web.actor;

import akka.actor.Props;
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
	public void onReceive(Object arg0) throws Exception {
		
		System.out.println(arg0);

	}

}
