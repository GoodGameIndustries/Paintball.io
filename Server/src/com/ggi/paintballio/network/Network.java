package com.ggi.paintballio.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class Network {

	static public void register (EndPoint endPoint) {
		Kryo kryo = endPoint.getKryo();
		kryo.register(Login.class);
		kryo.register(NewBullet.class);
		kryo.register(PlayerUpdate.class);
		kryo.register(User.class);
		kryo.register(int[][].class);
		kryo.register(int[].class);
		kryo.register(Die.class);
		kryo.register(ServerConn.class);
		kryo.register(ClientConn.class);

	}
	
}
