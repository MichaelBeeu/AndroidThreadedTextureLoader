package github.MichaelBeeu.threadedtexture.views;

import github.MichaelBeeu.threadedtexture.models.GameModel;

//import github.MichaelBeeu.threadedtexture.utils.GLTools;
import github.MichaelBeeu.threadedtexture.utils.Shader;
import github.MichaelBeeu.threadedtexture.utils.FileUtil;
import github.MichaelBeeu.threadedtexture.vos.Mesh;
import github.MichaelBeeu.threadedtexture.vos.TestMesh;

import java.io.IOException;
import java.util.Observer;
import java.util.Observable;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import android.util.Log;
import android.content.Context;

public class GameView implements GLSurfaceView.Renderer, Observer {
	private static final String TAG = "GameView";
	
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjMatrix = new float[16];
	private final float[] mVMatrix = new float[16];
	private final float[] mRotationMatrix = new float[16];
	
	private final Context ctx;
	private final GameModel model;
	
	Shader shader;
	Mesh testMesh;
	
	public GameView( Context _ctx, GameModel _model ){
		ctx = _ctx;
		model = _model;
		
		model.addObserver( this );
		shader = new Shader();
		
		testMesh = new TestMesh();
		
	}
	@Override
	public void update(Observable o, Object arg){
		// TODO: Ask for redraw
		//requestRender();
	}
	
	@Override
	public void onSurfaceCreated(GL10 unused, EGLConfig config){
		GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		
		// Load all of our shaders
		// Well, just one right now..
		Log.i(TAG, "Loading shaders...");
		try {
			String vertexCode = FileUtil.readEntireFile( ctx.getAssets().open("vertexShader.txt" ) ).toString();
			String fragmentCode = FileUtil.readEntireFile( ctx.getAssets().open("fragmentShader.txt") ).toString();
			
			shader.loadProgram( vertexCode, fragmentCode );
			if(shader.isOkay()){
				Log.i(TAG, "Shader loaded!");
			} else {
				Log.i(TAG, "Shader not loaded!");
			}
		} catch( IOException e){
			Log.e(TAG, "Error reading: " + e.getMessage() );
		}
	}
	
	@Override
	public void onDrawFrame(GL10 unused){
		GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );
		
		// TODO: Render
		shader.use();
		
		// Set up camera
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.f, 0f);
		
		// other matrices
		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		
		Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 0, -1.f);
		Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);
		
		int mMVPMatrixHandle;
		mMVPMatrixHandle = shader.getUniformLocation("uMVPMatrix");
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		testMesh.draw( shader );
		
	}
	
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height){
		GLES20.glViewport( 0,  0, width, height);
		
		float ratio = (float)width / height;
		
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
	}

}
