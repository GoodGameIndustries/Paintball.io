package com.ggi.paintballio;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;
import com.ggi.paintballio.PBall;

public class IOSLauncher extends IOSApplication.Delegate implements Resolver {
    @Override
    protected IOSApplication createApplication() {
        IOSApplicationConfiguration config = new IOSApplicationConfiguration();
        return new IOSApplication(new PBall(this), config);
    }

    public static void main(String[] argv) {
        NSAutoreleasePool pool = new NSAutoreleasePool();
        UIApplication.main(argv, null, IOSLauncher.class);
        pool.close();
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
		return false;
	}
}