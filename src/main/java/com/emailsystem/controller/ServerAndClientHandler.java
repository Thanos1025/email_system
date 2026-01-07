package main.java.com.emailsystem.controller;

import java.util.HashMap;
import java.util.Map;

import main.java.com.emailsystem.client.MailClient;
import main.java.com.emailsystem.server.MailServer;

public class ServerAndClientHandler {
	public static Map<Integer, MailServer> servers = new HashMap<>();
	public static Map<Integer, MailClient> clients = new HashMap<>();

	public static int createServer(String name, String domain) {
		int serverId = servers.size();
		servers.put(serverId, new MailServer(name, domain));
		return serverId;
	}

	public static int createClients(String name, int serverId) {
		int clientId = clients.size();
		clients.put(clientId, new MailClient(name, serverId));
		return clientId;
	}

	public static MailServer getServer(int id) {
		return servers.get(id);
	}

	public static MailClient getClient(int id) {
		return clients.get(id);
	}
}
