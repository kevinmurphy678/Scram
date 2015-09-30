package com.lum.scram.net;

import com.badlogic.gdx.math.MathUtils;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.lum.scram.Core;
import com.lum.scram.Player;
import com.lum.scram.net.packets.Packet;
import com.lum.scram.net.packets.PlayerJoinedPacket;
import com.lum.scram.net.packets.PlayerLeftPacket;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameServer {
	private final Server server;
	
	public GameServer() {
		server = new Server();
		server.getKryo().setRegistrationRequired(false);
	}
	
	public void Listen() {
		try {
			server.start();
			server.bind(9696, 9696);
		} catch (IOException ex) {
			Logger.getLogger(GameServer.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void ConnectListeners() {
		server.addListener(new Listener() {
			public void connected(Connection conn) {
				System.out.println("CONNECTION FROM " + conn.getID());
				server.sendToAllUDP(new PlayerJoinedPacket(conn.getID(), MathUtils.random(0, 300), MathUtils.random(0, 300)));
				
				// Give new client info on all previous clients
				for (Map.Entry<Integer, Player> playerEntry : Core.players.entrySet()) {
					Player p = (Player) playerEntry.getValue();
					float x = p.body.getPosition().x;
					float y = p.body.getPosition().y;
					server.sendToUDP(conn.getID(), new PlayerJoinedPacket(playerEntry.getKey(), x, y));
				}
				
			}
			
			public void disconnected(Connection conn) {
				System.out.println("DISCONNECT FROM " + conn.getID());
				server.sendToAllUDP(new PlayerLeftPacket(conn.getID()));
			}
			
			public void received(Connection conn, Object obj) {
				System.out.println("Packet");
				if (obj instanceof Packet) {
					((Packet)obj).HandlePacketServer(server);
				}
			}
		});
	}
	
	public int GetConnections() {
		return server.getConnections().length;
	}
}
