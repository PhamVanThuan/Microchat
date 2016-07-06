package com.aaj.microchat.client.web.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class EchoActor extends UntypedActor {

	public EchoActor() {
	}

	public static Props getProps(){
		return Props.create(EchoActor.class);
	}
	
	@Override
	public void onReceive(Object msg) throws Exception {
		sender().tell(msg, self());
	}

}
