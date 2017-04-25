package com.ggi.paintballio.screens;

import java.util.Collections;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.ggi.paintballio.PBall;
import com.ggi.paintballio.network.Die;
import com.ggi.paintballio.network.NewBullet;
import com.ggi.paintballio.network.PlayerUpdate;
import com.ggi.paintballio.network.User;
import com.ggi.paintballio.util.Joystick;
import com.ggi.paintballio.util.KillComparator;
import com.ggi.paintballio.util.Point2D;

public class GameScreen implements Screen, InputProcessor {

	private PBall pb;

	private float focusX = 0;
	private float focusY = 0;
	private float speed = .1f;
	private float pbSpeed = .36f;
	private float lastX = 0;
	private float lastY = 0;
	private float shootX = -1;
	private float shootY = -1;

	private SpriteBatch pic = new SpriteBatch();

	private Point2D blueSpawn;
	private Point2D redSpawn;

	private boolean hasSpawned = false;

	private int keys = 0000;
	private int fc = 0;
	private int hz = 45;
	private int shooting = -1;
	private int bps = 10;
	private int bpsc = 0;

	private GlyphLayout layout = new GlyphLayout();

	private Button switchHands;

	private Rectangle switchHandsB;

	private Joystick joystick;

	public GameScreen(PBall pb) {
		this.pb = pb;
		pb.user.isSafe = 180;

		switchHandsB = new Rectangle(.85f * pb.w, .1f * pb.h, .1f * pb.w, .1f * pb.w);

		switchHands = new Button(pb.switchStyle);
		switchHands.setBounds(switchHandsB.x, switchHandsB.y, switchHandsB.width, switchHandsB.height);

		joystick = new Joystick(pb.assets.get("jsTop.png", Texture.class), pb.assets.get("jsBot.png", Texture.class),
				new Rectangle(.05f * pb.w, .1f * pb.h, .15f * pb.w, .15f * pb.w));

	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(this);

	}

	@Override
	public void render(float delta) {
		if (pb.user.isSafe > 0) {
			pb.user.isSafe--;
		}
		if (!hasSpawned && blueSpawn != null && redSpawn != null) {
			spawn();
		}
		fc++;
		bpsc++;
		if (fc > Gdx.graphics.getFramesPerSecond() / hz) {
			fc = 0;
			sendUpdate();
		}

		Gdx.gl.glClearColor(1f, 1f, 1f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		aimAndMove();

		if (bpsc > Gdx.graphics.getFramesPerSecond() / bps) {
			bpsc = 0;
			if (shooting != -1 && pb.user.isSafe <= 0) {
				NewBullet b = new NewBullet();
				b.x = pb.user.x;
				b.y = pb.user.y;
				b.xA = shootX / pb.gridSize;
				b.yA = shootY / pb.gridSize;
				b.angle = getAngle(new Point2D(shootX / pb.gridSize, shootY / pb.gridSize),
						new Point2D(pb.user.x, pb.user.y));
				b.team = pb.user.team;
				b.shootID = pb.user.playerID;
				pb.user.angle = b.angle;
				switch (pb.user.hand) {
				case 0:
					b.x += 1 / 3f * Math.sin(Math.toRadians(pb.user.angle));
					b.y -= 1 / 3f * Math.cos(Math.toRadians(pb.user.angle));
					break;
				case 1:
					b.x -= 1 / 3f * Math.sin(Math.toRadians(pb.user.angle));
					b.y += 1 / 3f * Math.cos(Math.toRadians(pb.user.angle));
					break;
				}
				pb.send(b);
				pb.bullets.add(b);
			}
		}
		// System.out.println(Math.toRadians(pb.user.angle));

		pb.user.x *= pb.gridSize;
		pb.user.y *= pb.gridSize;

		focusX = pb.user.x - pb.w / 2;
		focusY = pb.user.y - pb.h / 2;

		pic.begin();
		for (int i = 0; i < pb.map.length; i++) {
			for (int j = 0; j < pb.map[i].length; j++) {
				if (pb.map[i][j] == 0) {
					pic.draw(pb.assets.get("grid.png", Texture.class), j * pb.gridSize - focusX,
							(pb.map.length - i) * pb.gridSize - focusY, pb.gridSize, pb.gridSize);
				} else if (pb.map[i][j] == 1) {
					pic.draw(pb.assets.get("bunker.png", Texture.class), j * pb.gridSize - focusX,
							(pb.map.length - i) * pb.gridSize - focusY, pb.gridSize, pb.gridSize);
				} else if (pb.map[i][j] == 2) {
					pic.draw(pb.assets.get("blueSpawn.png", Texture.class), j * pb.gridSize - focusX,
							(pb.map.length - i) * pb.gridSize - focusY, pb.gridSize, pb.gridSize);
					if (blueSpawn == null) {
						blueSpawn = new Point2D(j, pb.map.length - i);
					}
				} else if (pb.map[i][j] == 3) {
					pic.draw(pb.assets.get("redSpawn.png", Texture.class), j * pb.gridSize - focusX,
							(pb.map.length - i) * pb.gridSize - focusY, pb.gridSize, pb.gridSize);
					if (redSpawn == null) {
						redSpawn = new Point2D(j, pb.map.length - i);
					}
				}
			}
		}

		// user
		if (hasSpawned) {
			if (pb.user.team == 0) {
				switch (pb.user.hand) {
				case 0:
					pic.draw(new TextureRegion(pb.assets.get("bluePlayerRight.png", Texture.class)),
							pb.user.x - focusX - pb.gridSize / 2, pb.user.y - focusY - pb.gridSize / 2, pb.gridSize / 2,
							pb.gridSize / 2, pb.gridSize, pb.gridSize, 1, 1, pb.user.angle);
					break;
				case 1:
					pic.draw(new TextureRegion(pb.assets.get("bluePlayerLeft.png", Texture.class)),
							pb.user.x - focusX - pb.gridSize / 2, pb.user.y - focusY - pb.gridSize / 2, pb.gridSize / 2,
							pb.gridSize / 2, pb.gridSize, pb.gridSize, 1, 1, pb.user.angle);
					break;
				}
				layout.setText(pb.smallFnt, pb.user.name);
				pb.smallFnt.setColor(Color.BLUE);
				pb.smallFnt.draw(pic, pb.user.name,
						pb.user.x + pb.gridSize / 2 - layout.width / 2 - focusX - pb.gridSize / 2,
						pb.user.y - focusY - pb.gridSize / 2);

				pic.setColor(1, 1, 1, Math.abs((float) (pb.user.isSafe) / 180f));
				pic.draw(pb.assets.get("blueShield.png", Texture.class), pb.user.x - focusX - .55f * pb.gridSize,
						pb.user.y - focusY - .55f * pb.gridSize, 1.1f * pb.gridSize, 1.1f * pb.gridSize);
				pic.setColor(1, 1, 1, 1);
			} else if (pb.user.team == 1) {
				switch (pb.user.hand) {
				case 0:
					pic.draw(new TextureRegion(pb.assets.get("redPlayerRight.png", Texture.class)),
							pb.user.x - focusX - pb.gridSize / 2, pb.user.y - focusY - pb.gridSize / 2, pb.gridSize / 2,
							pb.gridSize / 2, pb.gridSize, pb.gridSize, 1, 1, pb.user.angle);
					break;
				case 1:
					pic.draw(new TextureRegion(pb.assets.get("redPlayerLeft.png", Texture.class)),
							pb.user.x - focusX - pb.gridSize / 2, pb.user.y - focusY - pb.gridSize / 2, pb.gridSize / 2,
							pb.gridSize / 2, pb.gridSize, pb.gridSize, 1, 1, pb.user.angle);
					break;
				}
				layout.setText(pb.smallFnt, pb.user.name);
				pb.smallFnt.setColor(Color.RED);
				pb.smallFnt.draw(pic, pb.user.name,
						pb.user.x + pb.gridSize / 2 - layout.width / 2 - focusX - pb.gridSize / 2,
						pb.user.y - focusY - pb.gridSize / 2);

				pic.setColor(1, 1, 1, Math.abs((float) (pb.user.isSafe) / 180f));
				pic.draw(pb.assets.get("redShield.png", Texture.class), pb.user.x - focusX - .55f * pb.gridSize,
						pb.user.y - focusY - .55f * pb.gridSize, 1.1f * pb.gridSize, 1.1f * pb.gridSize);
				pic.setColor(1, 1, 1, 1);
			}
		}

		for (int i = 0; i < pb.players.size(); i++) {
			User user = pb.players.get(i);

			smooth(user);
			user.x *= pb.gridSize;
			user.y *= pb.gridSize;

			if (user.team == 0) {
				switch (user.hand) {
				case 0:
					pic.draw(new TextureRegion(pb.assets.get("bluePlayerRight.png", Texture.class)),
							user.x - focusX - pb.gridSize / 2, user.y - focusY - pb.gridSize / 2, pb.gridSize / 2,
							pb.gridSize / 2, pb.gridSize, pb.gridSize, 1, 1, user.angle);
					break;
				case 1:
					pic.draw(new TextureRegion(pb.assets.get("bluePlayerLeft.png", Texture.class)),
							user.x - focusX - pb.gridSize / 2, user.y - focusY - pb.gridSize / 2, pb.gridSize / 2,
							pb.gridSize / 2, pb.gridSize, pb.gridSize, 1, 1, user.angle);
					break;
				}
				layout.setText(pb.smallFnt, user.name);
				pb.smallFnt.setColor(Color.BLUE);
				pb.smallFnt.draw(pic, user.name, user.x + pb.gridSize / 2 - layout.width / 2 - focusX - pb.gridSize / 2,
						user.y - focusY - pb.gridSize / 2);

				pic.setColor(1, 1, 1, Math.abs((float) (user.isSafe) / 180f));
				pic.draw(pb.assets.get("blueShield.png", Texture.class), user.x - focusX - .55f * pb.gridSize,
						user.y - focusY - .55f * pb.gridSize, 1.1f * pb.gridSize, 1.1f * pb.gridSize);
				pic.setColor(1, 1, 1, 1);
			} else if (user.team == 1) {
				switch (user.hand) {
				case 0:
					pic.draw(new TextureRegion(pb.assets.get("redPlayerRight.png", Texture.class)),
							user.x - focusX - pb.gridSize / 2, user.y - focusY - pb.gridSize / 2, pb.gridSize / 2,
							pb.gridSize / 2, pb.gridSize, pb.gridSize, 1, 1, user.angle);
					break;
				case 1:
					pic.draw(new TextureRegion(pb.assets.get("redPlayerLeft.png", Texture.class)),
							user.x - focusX - pb.gridSize / 2, user.y - focusY - pb.gridSize / 2, pb.gridSize / 2,
							pb.gridSize / 2, pb.gridSize, pb.gridSize, 1, 1, user.angle);
					break;
				}
				layout.setText(pb.smallFnt, user.name);
				pb.smallFnt.setColor(Color.RED);
				pb.smallFnt.draw(pic, user.name, user.x + pb.gridSize / 2 - layout.width / 2 - focusX - pb.gridSize / 2,
						user.y - focusY - pb.gridSize / 2);

				pic.setColor(1, 1, 1, Math.abs((float) (user.isSafe) / 180f));
				pic.draw(pb.assets.get("redShield.png", Texture.class), user.x - focusX - .55f * pb.gridSize,
						user.y - focusY - .55f * pb.gridSize, 1.1f * pb.gridSize, 1.1f * pb.gridSize);
				pic.setColor(1, 1, 1, 1);
			}
			user.x /= pb.gridSize;
			user.y /= pb.gridSize;
		}

		for (int i = 0; i < pb.bullets.size(); i++) {
			NewBullet b = pb.bullets.get(i);
			b.x += pbSpeed * Math.cos(Math.toRadians(b.angle));
			b.y += pbSpeed * Math.sin(Math.toRadians(b.angle));

			switch (b.team) {
			case 0:
				pic.draw(pb.assets.get("blueBall.png", Texture.class), b.x * pb.gridSize - pb.gridSize / 10 - focusX,
						b.y * pb.gridSize - pb.gridSize / 10 - focusY, pb.gridSize / 5, pb.gridSize / 5);
				break;
			case 1:
				pic.draw(pb.assets.get("redBall.png", Texture.class), b.x * pb.gridSize - pb.gridSize / 10 - focusX,
						b.y * pb.gridSize - pb.gridSize / 10 - focusY, pb.gridSize / 5, pb.gridSize / 5);
				break;
			}

			if ((Intersector.overlaps(new Circle(b.x * pb.gridSize, b.y * pb.gridSize, pb.gridSize / 10),
					new Circle(pb.user.x, pb.user.y, pb.gridSize / 4)) && b.team != pb.user.team)
					|| System.currentTimeMillis() - pb.lastUpdate > 20000) {
				pb.bullets.remove(b);
				if (pb.user.isSafe <= 0) {
					Die d = new Die();
					d.playerID = pb.user.playerID;
					d.killerID = b.shootID;
					d.playerTeam = pb.user.team;
					pb.send(d);
					pb.setScreen(new DieScreen(pb));
				}
			}

			for (int j = 0; j < pb.players.size(); j++) {
				User user = pb.players.get(j);
				if (Intersector.overlaps(new Circle(b.x, b.y, 1 / 10f), new Circle(user.x, user.y, 1 / 4f))
						&& b.team != user.team) {
					pb.bullets.remove(b);
				}
			}

			for (int k = 0; k < pb.map.length; k++) {
				for (int j = 0; j < pb.map[k].length; j++) {
					if (pb.map[k][j] == 1
							&& Intersector.overlaps(new Circle(b.x * pb.gridSize, b.y * pb.gridSize, pb.gridSize / 10),
									new Rectangle(j * pb.gridSize, (pb.map.length - k) * pb.gridSize, pb.gridSize,
											pb.gridSize))) {
						pb.bullets.remove(b);
					}
				}
			}

		}

		pic.draw(pb.assets.get("leaderboard.png", Texture.class), .75f * pb.w, .75f * pb.h, .25f * pb.w, .25f * pb.h);

		pb.players.add(pb.user);
		Collections.sort(pb.players, new KillComparator());
		for (int i = 0; i < pb.players.size(); i++) {
			if (i > 10) {
				break;
			}
			if (pb.players.get(i) != null) {
				pb.smallFnt.setColor(pb.players.get(i).team == 0 ? Color.BLUE : Color.RED);
				layout = new GlyphLayout(pb.smallFnt,
						i + ": " + pb.players.get(i).name + " " + pb.players.get(i).kills);
				pb.smallFnt.draw(pic, pb.players.get(i).kills + ": " + pb.players.get(i).name, .76f * pb.w,
						.99f * pb.h - i * 1.2f * layout.height);
			}
		}
		pb.players.remove(pb.user);

		switchHands.draw(pic, 1);
		joystick.draw(pic);

		if (pb.user != null) {
			pb.user.x /= pb.gridSize;
			pb.user.y /= pb.gridSize;
		}
		pic.end();

		if ((pb.map.length - pb.user.y < 0 || pb.map.length - pb.user.y > pb.map.length || pb.user.x < 0
				|| pb.user.x > pb.map[0].length) && pb.user != null) {
			spawn();
		}
	}

	private void smooth(User user) {
		user.rCount++;
		if (user.rCount > 200) {
			pb.players.remove(user);
		}
		/*
		 * float difX = user.servX-user.lastX; float difY =
		 * user.servY-user.lastY; user.x+=difX/hz; user.y+=difY/hz;
		 */
		user.x += (user.servX - user.x) / (hz / 5);
		user.y += (user.servY - user.y) / (hz / 5);
	}

	private void aimAndMove() {
		lastX = pb.user.x;
		lastY = pb.user.y;
		switch (keys) {
		case 1000:
			pb.user.y += speed;
			if (shooting < 0) {
				pb.user.angle = 90;
			}
			break;
		case 0100:
			pb.user.x -= speed;
			if (shooting < 0) {
				pb.user.angle = 180;
			}
			break;
		case 0010:
			pb.user.y -= speed;
			if (shooting < 0) {
				pb.user.angle = 270;
			}
			break;
		case 0001:
			pb.user.x += speed;
			if (shooting < 0) {
				pb.user.angle = 0;
			}
			break;
		}

		float magnitude = joystick.dif.len() / (.05f * Gdx.graphics.getWidth());
		// System.out.println(joystick.dif.len());

		pb.user.x += magnitude * speed * Math.cos(joystick.dif.angleRad());
		pb.user.y += magnitude * speed * Math.sin(joystick.dif.angleRad());
		if (shooting < 0 && joystick.dif.angle() != 0) {
			pb.user.angle = joystick.dif.angle();
		}

		boundsCheck();
		boundsCheck();

	}

	private void boundsCheck() {
		for (int i = 0; i < pb.map.length; i++) {
			for (int j = 0; j < pb.map[i].length; j++) {
				if (pb.map[i][j] == 1 && Intersector.overlaps(new Circle(pb.user.x, pb.user.y, .25f),
						new Rectangle(j, (pb.map.length - i), 1, 1))) {
					float temp = pb.user.x;
					pb.user.x = lastX;
					if (pb.map[i][j] == 1 && Intersector.overlaps(new Circle(pb.user.x, pb.user.y, .25f),
							new Rectangle(j, (pb.map.length - i), 1, 1))) {
						pb.user.x = temp;
					} else {
						return;
					}
					temp = pb.user.y;
					pb.user.y = lastY;
					if (pb.map[i][j] == 1 && Intersector.overlaps(new Circle(pb.user.x, pb.user.y, .25f),
							new Rectangle(j, (pb.map.length - i), 1, 1))) {
						pb.user.y = temp;
					} else {
						return;
					}

					if (pb.map[i][j] == 1 && Intersector.overlaps(new Circle(pb.user.x, pb.user.y, .25f),
							new Rectangle(j, (pb.map.length - i), 1, 1))) {
						pb.user.y = lastY;
						pb.user.x = lastX;
					}
				}
			}
		}

	}

	private void spawn() {
		if (pb.user.team == 0) {
			pb.user.x = blueSpawn.x + .5f;
			pb.user.y = blueSpawn.y + .5f;
			pb.user.servX = blueSpawn.x + .5f;
			pb.user.servY = blueSpawn.y + .5f;
			hasSpawned = true;
		} else if (pb.user.team == 1) {
			pb.user.x = redSpawn.x + .5f;
			pb.user.y = redSpawn.y + .5f;
			pb.user.servX = redSpawn.x + .5f;
			pb.user.servY = redSpawn.y + .5f;
			hasSpawned = true;
		}
		sendUpdate();

	}

	private void sendUpdate() {
		PlayerUpdate u = new PlayerUpdate();
		u.angle = pb.user.angle;
		u.x = pb.user.x;
		u.y = pb.user.y;
		u.hand = pb.user.hand;
		u.kills = pb.user.kills;
		u.name = " "+pb.user.name+" ";
		u.playerID = pb.user.playerID;
		u.team = pb.user.team;
		u.isSafe = pb.user.isSafe;

		pb.client.sendUDP(u);

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
		if (keycode == Keys.W) {
			keys += 1000;
		}
		if (keycode == Keys.A) {
			keys += 0100;
		}
		if (keycode == Keys.S) {
			keys += 0010;
		}
		if (keycode == Keys.D) {
			keys += 0001;
		}
		return true;
	}

	@Override
	public boolean keyUp(int keycode) {
		if (keycode == Keys.W) {
			keys -= 1000;
		}
		if (keycode == Keys.A) {
			keys -= 0100;
		}
		if (keycode == Keys.S) {
			keys -= 0010;
		}
		if (keycode == Keys.D) {
			keys -= 0001;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		screenY = (int) (pb.h - screenY);
		Rectangle touch = new Rectangle(screenX, screenY, 1, 1);
		if (Intersector.overlaps(touch, switchHandsB)) {
			switchHands.toggle();
		} else if (Intersector.overlaps(touch, joystick.bounds)) {
			joystick.touchDown(screenX, screenY, pointer);
		} else {
			shooting = pointer;
			shootX = screenX + focusX;
			shootY = screenY + focusY;
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		toggleOff();
		screenY = (int) (pb.h - screenY);
		Rectangle touch = new Rectangle(screenX, screenY, 1, 1);

		if (pointer == shooting) {
			shooting = -1;
		} else if (joystick.touchPointer == pointer) {
			joystick.touchUp();
		} else if (Intersector.overlaps(touch, switchHandsB)) {
			pb.user.hand = (pb.user.hand + 1) % 2;
		}
		return true;
	}

	private void toggleOff() {
		if (switchHands.isChecked()) {
			switchHands.toggle();
		}

	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		screenY = (int) (pb.h - screenY);
		if (pointer == shooting) {
			shootX = screenX + focusX;
			shootY = screenY + focusY;
		} else if (joystick.touchPointer == pointer) {
			joystick.touchDragged(screenX, screenY);
		}
		return true;
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

	public float getAngle(Point2D target, Point2D me) {
		float angle = (float) Math.toDegrees(Math.atan2(target.y - me.y, target.x - me.x));

		if (angle < 0) {
			angle += 360;
		}

		return angle;
	}
}
