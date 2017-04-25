package com.ggi.paintballio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ggi.paintballio.PBall;

public class LoadScreen implements Screen {

	private PBall pb;

	private SpriteBatch pic = new SpriteBatch();

	private Texture loading;
	private Texture grid;

	private float rot = 0;

	private GlyphLayout layout;

	public LoadScreen(PBall pb) {
		this.pb = pb;

	}

	@Override
	public void show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void render(float delta) {
		rot -= 5;
		if (pb.assets.update() && rot <= -720) {
			pb.loadStyles();
			pb.setScreen(new MainScreen(pb));
		}
		Gdx.gl.glClearColor(.1f, .1f, .1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (pb.assets.isLoaded("loading.png")) {
			if (loading == null) {
				loading = pb.assets.get("loading.png");
			}
			if (grid == null) {
				grid = pb.assets.get("grid.png");
			}
			// RenderBG
			pic.begin();
			for (int i = 0; i < pb.w / pb.gridSize; i++) {
				for (int j = 0; j < pb.h / pb.gridSize; j++) {
					pic.draw(grid, i * pb.gridSize, j * pb.gridSize, pb.gridSize, pb.gridSize);
				}
			}

			// Render loading
			pic.draw(new TextureRegion(loading), pb.w / 2 - (pb.gridSize), pb.h / 4 - (pb.gridSize), pb.gridSize,
					pb.gridSize, 2 * pb.gridSize, 2 * pb.gridSize, 1, 1, rot, true);

			// Render Title
			layout = new GlyphLayout(pb.largeFnt, "Paintball.io");
			pb.largeFnt.setColor(.3f, .3f, .3f, 1);
			pb.largeFnt.draw(pic, "Paintball.io", pb.w / 2 - layout.width / 2, .75f * pb.h + layout.height / 2);

			pic.end();
		}
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
