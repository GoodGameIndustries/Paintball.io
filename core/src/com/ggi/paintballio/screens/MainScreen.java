package com.ggi.paintballio.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.Align;
import com.ggi.paintballio.PBall;
import com.ggi.paintballio.network.Login;

public class MainScreen implements Screen, InputProcessor {

	private PBall pb;

	private SpriteBatch pic = new SpriteBatch();

	private Texture grid;

	private GlyphLayout layout = new GlyphLayout();

	private TextField userField;

	private TextButton play;

	private Rectangle userFieldB;
	private Rectangle playB;

	private Stage stage = new Stage();

	private String u = "";

	private float rot = 0;

	public MainScreen(PBall pb) {

		this.pb = pb;
		
		pb.connect();
		
		pb.isLoading = false;
		pb.error = false;
		grid = pb.assets.get("grid.png");

		// Bounds
		userFieldB = new Rectangle(pb.w / 4, pb.h / 2, pb.w / 2, pb.h / 15);
		playB = new Rectangle(pb.w / 3, pb.h / 4, pb.w / 3, pb.w / 15);

		// Field
		userField = new TextField(u, pb.fieldStyle);
		userField.setAlignment(Align.center);
		userField.setBounds(userFieldB.x, userFieldB.y, userFieldB.width, userFieldB.height);
		userField.setMessageText("Username");
		if (pb.lastName.length() > 0) {
			u = pb.lastName;
			userField.setText(u);
		}

		// Button
		play = new TextButton("Go Go Go!", pb.playStyle);
		play.setBounds(playB.x, playB.y, playB.width, playB.height);

		stage.addActor(userField);
		
		
	}

	@Override
	public void show() {
		// pb.resolver.loadAd();
		Gdx.input.setInputProcessor(this);
		pb.user = null;
		pb.bullets.clear();
		pb.players.clear();

	}

	@Override
	public void render(float delta) {
		rot -= 5;
		Gdx.gl.glClearColor(.1f, .1f, .1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (pb.user != null) {
			pb.setScreen(new GameScreen(pb));
		}

		// RenderBG
		pic.begin();
		for (int i = 0; i < pb.w / pb.gridSize; i++) {
			for (int j = 0; j < pb.h / pb.gridSize; j++) {
				pic.draw(grid, i * pb.gridSize, j * pb.gridSize, pb.gridSize, pb.gridSize);
			}
		}

		// Render Title
		layout = new GlyphLayout(pb.largeFnt, "Paintball.io");
		pb.largeFnt.setColor(.3f, .3f, .3f, 1);
		pb.largeFnt.draw(pic, "Paintball.io", pb.w / 2 - layout.width / 2, .75f * pb.h + layout.height / 2);

		// Field
		userField.draw(pic, 1);

		// Render loading
		if (pb.isLoading) {
			pic.draw(new TextureRegion(pb.assets.get("loading.png", Texture.class)), pb.w / 2 - (pb.gridSize),
					pb.h / 8 - (pb.gridSize), pb.gridSize, pb.gridSize, 2 * pb.gridSize, 2 * pb.gridSize, 1, 1, rot,
					true);
		}

		if (pb.error) {
			pb.isLoading = false;
			layout = new GlyphLayout(pb.mediumFnt,
					"Error connecting to server. Please make sure your app is up to date.");
			pb.mediumFnt.setColor(1f, .3f, .3f, 1);
			pb.mediumFnt.draw(pic, "Error connecting to server. Please make sure your app is up to date.",
					pb.w / 2 - layout.width / 2, .15f * pb.h + layout.height / 2);
		}
		// Button
		play.draw(pic, 1);
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

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		String punctuation = ".,:;'!@#$%^&*()-_+=/?";
		if (!Character.isAlphabetic(character) && !Character.isDigit(character) && !Character.isSpaceChar(character)
				&& !punctuation.contains("" + character) && character != '') {
			return true;
		}

		if (stage.getKeyboardFocus() == null) {
		} else if (stage.getKeyboardFocus().equals(userField)) {
			if (character == '' && u.length() > 0) {
				u = u.substring(0, u.length() - 1);
			} else if ((character == '\r' || character == '\n')) {
			} else if (u.length() < 25) {
				u += character;
			}
		}
		userField.setText(u);
		userField.setCursorPosition(u.length());
		return true;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		stage.setKeyboardFocus(null);
		screenY = (int) (pb.h - screenY);
		Rectangle touch = new Rectangle(screenX, screenY, 1, 1);
		if (Intersector.overlaps(touch, playB)) {
			play.toggle();
		} else if (Intersector.overlaps(touch, userFieldB)) {
			stage.setKeyboardFocus(userField);

		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		toggleOff();
		screenY = (int) (pb.h - screenY);
		Rectangle touch = new Rectangle(screenX, screenY, 1, 1);
		if (Intersector.overlaps(touch, playB)) {
			Login l = new Login();
			l.user = " "+u+" ";
			l.version = pb.version;
			pb.lastName = u;
			pb.send(l);
			pb.isLoading = true;
		} else if (Intersector.overlaps(touch, userFieldB)) {
			stage.setKeyboardFocus(userField);
			Gdx.input.setOnscreenKeyboardVisible(true);
		}
		return true;
	}

	private void toggleOff() {
		Gdx.input.setOnscreenKeyboardVisible(false);
		stage.setKeyboardFocus(null);
		if (play.isChecked()) {
			play.toggle();
		}
		// stage.setKeyboardFocus(null);

	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}
