package com.aaj.microchat.client.web.controller;

import java.util.concurrent.CompletableFuture;

import akka.http.javadsl.server.Route;

public interface Controller {

	CompletableFuture<Route> defineRoutes();
	
}
