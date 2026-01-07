package main.java.com.emailsystem.enumpackage;

import main.java.com.emailsystem.controller.ServerAndClientHandler;
import main.java.com.emailsystem.server.MailServer;

public class Server {
	public static final int gmailServerId = ServerAndClientHandler.createServer("smtp.gmail.com", "@gmail.com");
	public static final int zohocorpServerId = ServerAndClientHandler.createServer("smtp.zohocorp.com", "@zohocorp.com");
	
	public static final MailServer gmailServer = ServerAndClientHandler.getServer(gmailServerId);
}
