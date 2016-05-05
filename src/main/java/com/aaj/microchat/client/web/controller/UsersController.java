package com.aaj.microchat.client.web.controller;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import akka.actor.ActorRef;
import akka.http.javadsl.model.ContentTypes;
import akka.http.javadsl.server.RequestContext;
import akka.http.javadsl.server.RequestVal;
import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.RouteResult;
import akka.http.javadsl.server.directives.PathDirectives;
import akka.http.javadsl.server.values.Parameters;
import akka.http.javadsl.server.values.PathMatchers;
import akka.pattern.PatternsCS;

public class UsersController extends PathDirectives implements Controller {

	final ActorRef logInActor;
	final ActorRef chatsActor;
	final CompletableFuture<Route> routes;

	private RequestVal<String> name = Parameters.stringValue("name");
	final RequestVal<String> userNamePathParam = PathMatchers.segment();

	public UsersController(ActorRef logInActor, ActorRef chatsActor) {
		this.logInActor = Objects.requireNonNull(logInActor);
		this.chatsActor = Objects.requireNonNull(chatsActor);
		routes = initRoutes();
	}

	@Override
	public CompletableFuture<Route> defineRoutes() {
		return this.routes;
	}

	private CompletionStage<RouteResult> handleLogin(final RequestContext ctx) {
		return PatternsCS.ask(logInActor, userNamePathParam.get(ctx), 1000).thenApplyAsync((t) -> {
			return ctx.complete(t.toString());
		}, ctx.executionContext());
	}

	private CompletionStage<RouteResult> getAvailableChats(final RequestContext ctx) {
		return PatternsCS.ask(chatsActor, userNamePathParam.get(ctx), 1000).thenApplyAsync((t) -> {
			return ctx.complete(t.toString());
		}, ctx.executionContext());
	}

	private CompletableFuture<Route> initRoutes() {

		Route echoRoute = handleWith1(userNamePathParam, (ctx, name) -> ctx.complete("Echo " + name + "!"));

		return CompletableFuture.supplyAsync(() -> {
			return route(
			// handle GET requests
					get(
					// matches the empty path
					pathSingleSlash().route(
					// return a constant string with a certain content type
							complete(ContentTypes.TEXT_HTML_UTF8, "<html><body>Welcome to Microchat</body></html>")),
							path("chats", userNamePathParam).route(
									handleWith((ctx) -> ctx.completeWith(getAvailableChats(ctx)), userNamePathParam)),
							path("echo", userNamePathParam).route(echoRoute)),
					// handle POST
					post(path("user", userNamePathParam).route(
							handleWith((ctx) -> ctx.completeWith(handleLogin(ctx)), userNamePathParam))));
		});

	}
}
