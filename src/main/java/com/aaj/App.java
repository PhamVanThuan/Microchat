package com.aaj;

import java.io.File;
import java.net.URL;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;

import com.aaj.microchat.client.web.MicrochatWebApp;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;




public class App {
	public static void main(String[] args) throws Exception {
		URL filePath = MicrochatWebApp.class.getClassLoader().getResource("front.conf");
		Config config = ConfigFactory.parseFile(new File(filePath.toURI()));
		final ActorSystem frontSystem = ActorSystem.create("front", config);
		final ActorMaterializer materializer = ActorMaterializer.create(frontSystem);
		new MicrochatWebApp(frontSystem, materializer);
		frontSystem.terminate();
	}
}