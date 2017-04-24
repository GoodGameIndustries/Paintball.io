package com.ggi.paintballio.util;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


public class Joystick {
	
	private Texture top;
	private Texture bottom;
	
	public Rectangle bounds;
	
	private Point2D botCenter;
	private Point2D topCenter;
	
	public Vector2 dif = new Vector2();
	
	private int touchDownX = 0;
	private int touchDownY = 0;
	public int touchPointer = -1;

	public Joystick(Texture top, Texture bottom, Rectangle bounds){
		this.top=top;
		this.bottom = bottom;
		this.bounds = bounds;
		botCenter = new Point2D(bounds.x+bounds.width/2,bounds.y+bounds.height/2);
		topCenter = new Point2D(bounds.x+bounds.width/2,bounds.y+bounds.height/2);
	}
	
	public void draw(SpriteBatch pic){
		pic.draw(bottom,bounds.x,bounds.y,bounds.width,bounds.height);
		pic.draw(top,(float)(topCenter.x-bounds.width/2),(float) (topCenter.y-bounds.height/2),bounds.width,bounds.height);
	}
	
	public void touchDown(int x, int y, int pointer){
		touchDownX = x;
		touchDownY = y;
		touchPointer = pointer;
	}
	
	public void touchUp(){
		touchDownX=0;
		touchDownY=0;
		topCenter=new Point2D(botCenter.x,botCenter.y);
		touchPointer = -1;
		dif.x=0;
		dif.y=0;
	}
	
	public void touchDragged(int x, int y){
		//topCenter = new Point2D(botCenter.getX()+x-touchDownX,botCenter.getY()+y-touchDownY);
		
		dif.x = (float) (x-botCenter.x);
		dif.y = (float) (y-botCenter.y);
		dif.limit(.05f*Gdx.graphics.getWidth());
		topCenter = new Point2D(botCenter.x+dif.x,botCenter.y+dif.y);
		
		
	}
}
