package com.aaj.microchat.client.web.controller;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.RequestContext;
import akka.http.javadsl.server.RequestVal;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.RouteResult;
import akka.http.javadsl.server.values.Parameters;
import akka.http.javadsl.server.values.PathMatchers;
import akka.pattern.PatternsCS;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Source;
import akka.actor.ActorRef;
import akka.http.javadsl.server.directives.PathDirectives;
import akka.http.javadsl.server.directives.WebSocketDirectives;
import static akka.http.javadsl.model.ws.WebSocket.handleWebSocketRequestWith;
import akka.japi.JavaPartialFunction;

public class UsersWSController extends WebSocketDirectives implements Controller {

	final ActorRef logInActor;
	final ActorRef chatsActor;
	final CompletableFuture<Route> routes;

	private RequestVal<String> name = Parameters.stringValue("name");
	final RequestVal<String> userNamePathParam = PathMatchers.segment();

	public UsersWSController(ActorRef logInActor, ActorRef chatsActor) {
		this.logInActor = Objects.requireNonNull(logInActor);
		this.chatsActor = Objects.requireNonNull(chatsActor);
		routes = initRoutes();
	}

	@Override
	public CompletableFuture<Route> defineRoutes() {
		return this.routes;
	}

	private CompletableFuture<Route> initRoutes() {
		return CompletableFuture.supplyAsync(()->{	
			return path("wshello").route(handleWebSocketMessages(greeter()));
				});
    }
	
	/** 
     * A handler that treats incoming messages as a name, 
      * and responds with a greeting to that name 
      */ 
     public Flow<Message, Message, ?> greeter() { 
         return 
             Flow.<Message>create() 
                 .collect(new JavaPartialFunction<Message, Message>() { 
                     @Override 
                     public Message apply(Message msg, boolean isCheck) throws Exception { 
                         if (isCheck) 
                             if (msg.isText()) return null; 
                             else throw noMatch(); 
                         else 
                             return handleTextMessage(msg.asTextMessage()); 
                     } 
                 }); 
     } 
     public TextMessage handleTextMessage(TextMessage msg) { 
         if (msg.isStrict()) // optimization that directly creates a simple response... 
             return TextMessage.create("Hello "+msg.getStrictText()); 
         else // ... this would suffice to handle all text messages in a streaming fashion 
             return TextMessage.create(Source.single("Hello ").concat(msg.getStreamedText())); 
     } 
}
