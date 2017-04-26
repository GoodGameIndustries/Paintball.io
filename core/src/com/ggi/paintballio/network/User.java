package com.ggi.paintballio.network;

public class User {
	public int team = -1;
	public int playerID = -1;
	public int map = -1;
	public float x = -1;
	public float y = -1;
	public float lastX = -1;
	public float lastY= -1;
	public float servX = -1;
	public float servY= -1;
	public String name = "";
	public float angle = 0;
	public int kills = 0;
	public int hand = 0;
	public int rCount = 0;
	public int isSafe = 180;
	public long time = System.currentTimeMillis();
	public long lastTime = System.currentTimeMillis();
	public int fps = 0;
}
