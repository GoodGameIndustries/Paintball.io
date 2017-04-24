package com.ggi.paintballio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ggi.paintballio.PBall;

public class DieScreen implements Screen{

	private PBall pb;
	
	private SpriteBatch pic = new SpriteBatch();
	
	int waitCount = 0;

	private boolean showCall = false;
	
	public DieScreen(PBall pb){
		this.pb=pb;
	}

	@Override
	public void show() {
		pb.client.close();
		pb.createClient();
		
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		pic.begin();
		for(int i = 0; i < pb.w/pb.gridSize; i++){
			for(int j = 0; j< pb.h/pb.gridSize; j++){
				//pic.draw(pb.assets.get("grid.png", Texture.class),i*pb.gridSize,j*pb.gridSize,pb.gridSize,pb.gridSize);
			}
		}
		pic.draw(pb.user.team>0?pb.assets.get("blueSplat.png", Texture.class):pb.assets.get("redSplat.png", Texture.class),pb.w/2-pb.h/4, pb.h/4,pb.h/2,pb.h/2);
		waitCount++;
		if(waitCount > 60){
			if(!showCall){pb.resolver.showAd();showCall  = true;}
			
			if(pb.resolver.isAdShown()){
				pb.setScreen(new MainScreen(pb));
			}
			
		}
		
		
		pic.end();
		
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
}
