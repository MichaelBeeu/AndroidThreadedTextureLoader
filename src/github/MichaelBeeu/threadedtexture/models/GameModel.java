package github.MichaelBeeu.threadedtexture.models;

import android.util.Log;
import java.util.Observable;

public class GameModel extends Observable implements Runnable {
	private final String TAG = "GameModel";
	private boolean isRunning = true;
	
	@Override
	public void run(){
		double t = 0.0;
		final double dt = 0.01;
		
		double currentTime = System.currentTimeMillis() / 1000.0;
		double accumulator = 0.0;
		
		while( true ){
			//Log.e(TAG, "Game loop...");
			double newTime = System.currentTimeMillis() / 1000.0;
			double frameTime = newTime - currentTime;
			currentTime = newTime;
			
			accumulator += frameTime;
			
			//Log.i(TAG, "Accumulator: "+accumulator);
			
			while( accumulator >= dt ){
				// Do some stuff here?
				
				accumulator -= dt;
				t += dt;
			}
			notifyObservers();
			
			Thread.yield();
		}
	}
	
	synchronized public boolean getRunning(){
		return isRunning;
	}
	
	synchronized public void setRunning(boolean r){
		Log.i(TAG, "Set running to :"+r);
		isRunning = r;
	}

}
