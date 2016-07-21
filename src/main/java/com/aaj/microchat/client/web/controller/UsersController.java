package com.aaj.microchat.client.web.controller;

import akka.NotUsed;
import static akka.http.javadsl.server.PathMatchers.segment;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.ConnectionContext;
import akka.http.javadsl.Http;
import akka.http.javadsl.HttpsConnectionContext;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.*;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;

import static akka.http.javadsl.server.PathMatchers.integerSegment;
import static akka.http.javadsl.server.Unmarshaller.entityToString;
import akka.pattern.PatternsCS;

public class UsersController extends AllDirectives implements Controller {

	final ActorRef logInActor;
	final ActorRef chatsActor;
	final CompletableFuture<Route> routes;

	public UsersController(ActorRef logInActor, ActorRef chatsActor) {
		this.logInActor = Objects.requireNonNull(logInActor);
		this.chatsActor = Objects.requireNonNull(chatsActor);
		routes = initRoutes();
	}

	@Override
	public CompletableFuture<Route> defineRoutes() {
		return this.routes;
	}

	/*
	 * private CompletionStage<RouteResult> handleLogin(final RequestContext
	 * ctx) { return PatternsCS.ask(logInActor, userNamePathParam.get(ctx),
	 * 1000).thenApplyAsync((t) -> { return ctx.complete(t.toString()); },
	 * ctx.getExecutionContext()); }
	 * 
	 * private CompletionStage<RouteResult> getAvailableChats(final
	 * RequestContext ctx) { return PatternsCS.ask(chatsActor,
	 * userNamePathParam.get(ctx), 1000).thenApplyAsync((t) -> { return
	 * ctx.complete(t.toString()); }, ctx.getExecutionContext()); }
	 */

	public Route multiply(int x, int y) {
		int result = x * y;
		return complete(String.format("%d * %d = %d", x, y, result));
	}

	public CompletionStage<Route> multiplyAsync(Executor ctx, int x, int y) {
		return CompletableFuture.supplyAsync(() -> multiply(x, y), ctx);
	}

	private CompletableFuture<Route> initRoutes() {

		return CompletableFuture
				.supplyAsync(() -> {
					Route addHandler = parameter(StringUnmarshallers.INTEGER, "x",
							x -> parameter(StringUnmarshallers.INTEGER, "y", y -> extractExecutionContext(ctx -> onSuccess(() -> {
								int result = x + y;
								return PatternsCS.ask(logInActor, result,
										  1000).thenApplyAsync((t) -> { return complete(t.toString()); },
												  ctx);
							},Function.identity()))));

					BiFunction<Integer, Integer, Route> subtractHandler = (x, y) -> {
						int result = x - y;
						return complete(String.format("%d - %d = %d", x, y, result));
					};

					return route(
							// matches the empty path
							pathSingleSlash(() -> getFromResource("web/calculator.html")),
							// matches paths like this: /add?x=42&y=23
							path("add", () -> addHandler),
							path("subtract",
									() -> parameter(
											StringUnmarshallers.INTEGER,
											"x",
											x -> parameter(StringUnmarshallers.INTEGER, "y",
													y -> subtractHandler.apply(x, y)))),
							// matches paths like this: /multiply/{x}/{y}
							path(PathMatchers.segment("multiply").slash(integerSegment()).slash(integerSegment()),
									this::multiply),
							path(PathMatchers.segment("multiplyAsync").slash(integerSegment()).slash(integerSegment()),
									(x, y) -> extractExecutionContext(ctx -> onSuccess(() -> multiplyAsync(ctx, x, y),
											Function.identity()))),
							post(() -> path("hello",
									() -> entity(entityToString(), body -> complete("Hello " + body + "!")))));

				});

	}
}
