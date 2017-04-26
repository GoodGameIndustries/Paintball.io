package com.ggi.paintballio.network;

public class PlayerUpdate {
	public int playerID = -1;
	public float x = -1;
	public float y = -1;
	public float lastX = -1;
	public float lastY = -1;
	public long time = System.currentTimeMillis();
	public long lastTime = System.currentTimeMillis();
	public String name = "";
	public float angle = 0;
	public int kills = 0;
	public int hand = 0;
	public int team = -1;
	public int isSafe = 180;
	public int fps = 0;
}
