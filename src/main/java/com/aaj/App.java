package com.aaj;

import java.io.File;
import java.net.URL;

import akka.actor.ActorSystem;

import com.aaj.microchat.client.web.MicrochatWebApp;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;




public class App {
	public static void main(String[] args) throws Exception {
		URL filePath = MicrochatWebApp.class.getClassLoader().getResource("front.conf");
		Config config = ConfigFactory.parseFile(new File(filePath.toURI()));
		ActorSystem frontSystem = ActorSystem.create("front", config);
		new MicrochatWebApp(frontSystem);
		System.out.println("Snapchat running on port 8080. Type RETURN to exit.");
		System.in.read();
		frontSystem.terminate();
	}
}