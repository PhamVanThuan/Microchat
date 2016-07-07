package com.aaj.microchat.client.web.controller;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.server.RequestVal;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.directives.WebSocketDirectives;
import akka.http.javadsl.server.values.Parameters;
import akka.http.javadsl.server.values.PathMatchers;
import akka.japi.JavaPartialFunction;
import akka.stream.impl.ActorPublisher;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;

import com.aaj.microchat.client.web.actor.EchoActor;
import com.aaj.microchat.client.web.actor.WebSocketCommandSubscriber;
import com.aaj.microchat.client.web.actor.WebSocketDataPublisherActor;
import com.google.gson.Gson;

public class UsersWSController extends WebSocketDirectives implements Controller {

	private static final Gson gson = new Gson();
	final ActorRef logInActor;
	final ActorRef chatsActor;
	final CompletableFuture<Route> routes;

	private RequestVal<String> name = Parameters.stringValue("name");
	final RequestVal<String> userNamePathParam = PathMatchers.segment();
	private final ActorRef echoActor;

	public UsersWSController(ActorRef logInActor, ActorRef chatsActor, ActorSystem system) {
		this.logInActor = Objects.requireNonNull(logInActor);
		this.chatsActor = Objects.requireNonNull(chatsActor);
		this.echoActor = system.actorOf(EchoActor.getProps(), "echoActor");
		this.routes = initRoutes();

	}

	@Override
	public CompletableFuture<Route> defineRoutes() {
		return this.routes;
	}

	private CompletableFuture<Route> initRoutes() {
		return CompletableFuture.supplyAsync(() -> {
			return route(path("metrics").route(handleWebSocketMessages(metrics())),
					path("wshello").route(handleWebSocketMessages(greeter())));
		});
	}

	/**
	 * A handler that treats incoming messages as a name, and responds with a
	 * greeting to that name
	 */
	public Flow<Message, Message, ?> greeter() {
		return Flow.<Message> create().collect(new JavaPartialFunction<Message, Message>() {
			@Override
			public Message apply(Message msg, boolean isCheck) throws Exception {
				if (isCheck)
					if (msg.isText())
						return null;
					else
						throw noMatch();
				else
					return handleTextMessage(msg.asTextMessage());
			}
		});
	}

	public TextMessage handleTextMessage(TextMessage msg) {
		if (msg.isStrict()) // optimization that directly creates a simple
							// response...
			return TextMessage.create("Hello " + msg.getStrictText());
		else
			// ... this would suffice to handle all text messages in a streaming
			// fashion
			return TextMessage.create(Source.single("Hello ").concat(msg.getStreamedText()));
	}

	// @Override
	// public Route createRoute() {
	// return get(
	// path("metrics").route(handleWebSocketMessages(metrics()))
	// );
	// }

	private Flow<Message, Message, ?> metrics() {
		Source<Message, ActorRef> metricsPublisher = Source.actorPublisher(WebSocketDataPublisherActor.getProps());

		Sink<Message, ActorRef> metricsSink = Sink.actorSubscriber(WebSocketCommandSubscriber.getProps());
		Source<Message, ActorRef> metricsSource = metricsPublisher.map((measurementData) -> { 
				return measurementData;//return TextMessage.create(gson.toJson(measurementData));
			}
		);
		// Flow<Message, Message, ?> flow = Flow.fromSinkAndSource(metricsSink,
		// metricsSource);
		Flow<Message, Message, ?> flow = Flow.fromSinkAndSourceMat(metricsSink, metricsSource,
				(sinkActor, sourceActor) -> {
					sinkActor.tell(new RegisterSourceMessage(sourceActor), sourceActor);return null;
				});
		return flow;
	}

	public static class RegisterSourceMessage implements Serializable{
		private static final long serialVersionUID = -8339839644697359338L;
		private final ActorRef requestor;

		private RegisterSourceMessage(ActorRef requestor) {
			this.requestor = requestor;
		}

		public ActorRef getRequestor() {
			return requestor;
		}

	}

}
