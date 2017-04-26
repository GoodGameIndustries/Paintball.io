package com.ggi.paintballio.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.ggi.paintballio.PBall;
import com.ggi.paintballio.Resolver;

public class DesktopLauncher implements Resolver {
	
	public DesktopLauncher(){
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		 System.setProperty("org.lwjgl.opengl.Display.allowSoftwareOpenGL", "true");
		config.height = 450;
		config.width = 800;
		new LwjglApplication(new PBall(this), config);
	}
	
	public static void main (String[] arg) {
		new DesktopLauncher();
	}

	@Override
	public void showAd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void loadAd() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAdShown() {
		// TODO Auto-generated method stub
		return true;
	}
}
