package com.ggi.paintballio;

import java.util.Arrays;

import org.robovm.apple.foundation.NSAutoreleasePool;
import org.robovm.apple.uikit.UIApplication;
import org.robovm.pods.google.mobileads.GADInterstitial;
import org.robovm.pods.google.mobileads.GADInterstitialDelegateAdapter;
import org.robovm.pods.google.mobileads.GADRequest;

import com.badlogic.gdx.backends.iosrobovm.IOSApplication;
import com.badlogic.gdx.backends.iosrobovm.IOSApplicationConfiguration;

public class IOSLauncher extends IOSApplication.Delegate implements Resolver {
	private GADInterstitial interstitial;
	private boolean adShown=false;
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

    public void showOrLoadInterstital() {
		loadAd();
		
		
		
	}

	
	
	private GADInterstitial createAndLoadInterstitial() {
	     //Ad Unit ID of your interstital, from your adMob account. Use the TEST one for now
	     GADInterstitial interstitial = new GADInterstitial("ca-app-pub-3725510963686041/6099729412");
	     interstitial.setDelegate(new GADInterstitialDelegateAdapter() {
	       @Override
	         public void didDismissScreen(GADInterstitial ad) {
	             //IOSLauncher.this.interstitial = createAndLoadInterstitial();
	         }
	       
	       	 public void didReceiveAd(GADInterstitial ad) {
	       		 IOSLauncher.this.showAd();
	         }
	       		
	       	
	     });
	     interstitial.loadRequest(createRequest());
	     return interstitial;
	 }
	 
	 private GADRequest createRequest() {
	     GADRequest request = new GADRequest();
	     // To test on your devices, add their UDIDs here:
	     request.setTestDevices(Arrays.asList(GADRequest.getSimulatorID()));
	     return request;
	 }
	 
	 public void loadAd() {
	     interstitial = createAndLoadInterstitial();
	 }
	 
	 
	 public void showAd() {
		 adShown = false;
	     if (interstitial!=null&&interstitial.isReady()) {
	         interstitial.present(UIApplication.getSharedApplication().getKeyWindow().getRootViewController());
	         adShown=true;
	     } else {
	    	 loadAd();
	         System.out.println("Interstitial not ready!");
	     }
	 }

	@Override
	public boolean isAdShown() {
		// TODO Auto-generated method stub
		return adShown;
	}
}