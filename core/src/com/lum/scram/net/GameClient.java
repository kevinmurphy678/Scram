package com.lum.scram.net;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.lum.scram.Core;
import com.lum.scram.net.packets.Packet;
import com.lum.scram.net.packets.PlayerInfoPacket;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameClient {
	private final Client client;
	
	public GameClient() {
		client = new Client();
		client.getKryo().setRegistrationRequired(false);
	}
	
	public void Connect() {
		try {
			client.start();
			client.connect(5000, Core.netip, Core.netport, Core.netport);
			
			//client.sendUDP(new PlayerJoinedPacket(client.getID()));
		} catch (IOException ex) {
			Logger.getLogger(GameClient.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void ConnectListeners() {
		client.addListener(new Listener() {
			public void received(Connection conn, final Object obj) {
				if (obj instanceof Packet) {
					Gdx.app.postRunnable(new Runnable() {
						public void run () {
							((Packet)obj).HandlePacket();
						}
					});
				}
			}
		});
	}
	
	public boolean IsConnected() {
		return client.isConnected();
	}
	
	public int GetID() {
		return client.getID();
	}
	
	public void SendPosition(float x, float y, float rot, float normal, float health) {
		client.sendUDP(new PlayerInfoPacket(client.getID(), x, y, rot, normal, health));
	}
	
	public void Send(Object object) {
		client.sendUDP(object);
	}
	
	public void Disconnect() {
		client.stop();
	}
		
}
