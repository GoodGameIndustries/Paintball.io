package com.ggi.paintballio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ggi.paintballio.PBall;

public class DieScreen implements Screen {

	private PBall pb;

	private SpriteBatch pic = new SpriteBatch();

	int waitCount = 0;

	private boolean showCall = false;
	
	private GlyphLayout layout = new GlyphLayout();
	
	private String killBy = "";

	private float rot = 0;

	public DieScreen(PBall pb, String killBy) {
		this.pb = pb;
		this.killBy = killBy;
	}

	@Override
	public void show() {
		Thread t = new Thread(new Runnable(){

			@Override
			public void run() {
				pb.client.close();
				pb.createClient();
				
			}
			
		});
		t.start();
		

	}

	@Override
	public void render(float delta) {
		rot -= 5;
		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		pic.begin();
		for (int i = 0; i < pb.w / pb.gridSize; i++) {
			for (int j = 0; j < pb.h / pb.gridSize; j++) {
				// pic.draw(pb.assets.get("grid.png",
				// Texture.class),i*pb.gridSize,j*pb.gridSize,pb.gridSize,pb.gridSize);
			}
		}
		pic.draw(
				pb.user.team > 0 ? pb.assets.get("blueSplat.png", Texture.class)
						: pb.assets.get("redSplat.png", Texture.class),
				pb.w / 2 - pb.h / 4, pb.h / 3f, pb.h / 2, pb.h / 2);
		
		layout = new GlyphLayout(pb.mediumFnt,"You were shot by: "+killBy);
		pb.mediumFnt.draw(pic, "You were shot by: "+killBy,pb.w/2-layout.width/2,pb.h/4+layout.height/2);
		
		pic.draw(new TextureRegion(pb.assets.get("loading.png", Texture.class)), pb.w / 2 - (pb.gridSize),
				pb.h / 8 - (pb.gridSize), pb.gridSize, pb.gridSize, 2 * pb.gridSize, 2 * pb.gridSize, 1, 1, rot ,
				true);
		
		waitCount++;
		if (waitCount > 60) {
			if (!showCall) {
				pb.resolver.showAd();
				showCall = true;
			}

			if (pb.resolver.isAdShown()||waitCount>660) {
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
