package github.MichaelBeeu.threadedtexture;

import github.MichaelBeeu.threadedtexture.controllers.GameController;
import github.MichaelBeeu.threadedtexture.models.GameModel;
import github.MichaelBeeu.threadedtexture.views.GameView;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.util.Log;
import android.content.res.Configuration;

public class MainActivity extends Activity {
	private final String TAG = "MainActivity";
	private GameModel model;
	private GameView view;
	private GameController controller;
	private Thread modelThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		model = new GameModel();
		view = new GameView( this, model );
		controller = new GameController( this, model, view );
		
		// Start our model thread
		modelThread = new Thread( model );
		modelThread.start();
		
		// This seems weird, but the controller will
		// catch user input.
		setContentView( controller );
	}

	@Override
	public void onConfigurationChanged( Configuration newConfig ){
		super.onConfigurationChanged( newConfig );
	}
	
	@Override
	protected void onDestroy(){
		Log.i(TAG, "onDestroy()");
		
		super.onDestroy();
		model.setRunning( false );
		
		boolean retry = false;
		while( retry ){
			try {
				modelThread.join();
				retry = false;
			} catch( InterruptedException e ){
				Log.i(TAG, "Could not join modelThread... trying again...");
			}
		}
	}
	
}
