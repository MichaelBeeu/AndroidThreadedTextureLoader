package github.MichaelBeeu.threadedtexture.controllers;

import java.util.Observer;
import java.util.Observable;

import github.MichaelBeeu.threadedtexture.models.GameModel;
import github.MichaelBeeu.threadedtexture.views.GameView;

import android.opengl.GLSurfaceView;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.content.Context;
import android.util.Log;

public class GameController extends GLSurfaceView implements Observer {
    private GameView view;
    private GameModel model;
    
    public GameController(Context context, GameModel _model, GameView _view){
    	super(context);
    	
    	setEGLContextClientVersion(2);
    	
    	model = _model;
    	
    	view = _view;
    	
    	model.addObserver( this );
    	
    	setRenderer(view);
    	
    	//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder){
    	super.surfaceCreated( holder );
    }
    
    @Override
    public void update(Observable o, Object arg){
    	// Model updated... do something?
    	requestRender();
    }
}
