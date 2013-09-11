package github.MichaelBeeu.threadedtexture.views;

import github.MichaelBeeu.threadedtexture.models.GameModel;

//import github.MichaelBeeu.threadedtexture.utils.GLTools;
import github.MichaelBeeu.threadedtexture.utils.Shader;
import github.MichaelBeeu.threadedtexture.utils.FileUtil;
import github.MichaelBeeu.threadedtexture.vos.Mesh;
import github.MichaelBeeu.threadedtexture.vos.TestMesh;
import github.MichaelBeeu.threadedtexture.daos.TextureLoader;

import java.io.IOException;
import java.util.Iterator;
import java.util.Observer;
import java.util.Observable;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;

import android.util.Log;
import android.content.Context;

public class GameView implements GLSurfaceView.Renderer, Observer {
	private static final String TAG = "GameView";
	
	private final float[] mMVPMatrix = new float[16];
	private final float[] mProjMatrix = new float[16];
	private final float[] mVMatrix = new float[16];
	private final float[] mModelMatrix = new float[16];
	private final float[] mTemp= new float[16];
	
	private final Context ctx;
	private final GameModel model;

	private TextureLoader texLoader;
	private Thread texLoadThread;
	private HashMap<String, Integer> textures;
	private TextureLoader.TextureBuffer texLoaderBuf = null;
	
	Shader shader;
	Mesh testMesh;
	
	public GameView( Context _ctx, GameModel _model ){
		Matrix.setIdentityM(mModelMatrix, 0);
		textures = new HashMap<String, Integer>();
		ctx = _ctx;
		model = _model;
		
		model.addObserver( this );
		shader = new Shader();

		// Create our texture loader class
		texLoader = new TextureLoader(ctx);
		texLoader.addObserver( this );

		// Start it
		texLoadThread = new Thread( texLoader );
		texLoadThread.start();
		
		
		testMesh = new TestMesh();
	}
	
	@Override
	public void update(Observable o, Object arg){
		// TODO: Ask for redraw
		//requestRender();
		Log.i(TAG, "Notified");
		if( o instanceof TextureLoader ){
			if( arg instanceof TextureLoader.TextureBuffer ){
				TextureLoader.TextureBuffer bmp = (TextureLoader.TextureBuffer)arg;
				texLoaderBuf = bmp;
			}
			
		} else if( o instanceof GameModel ){
			// Do something here?
		}
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

        // Add textures to thread's queue.
        texLoader.addTexture( "SomeTexture.png" );
        texLoader.addTexture( "SomeTexture1.png" );
        texLoader.addTexture( "SomeTexture2.png" );
	}
	
	public void destroy(){
		Log.i(TAG, "destroy()");
		texLoader.setRunning( false );
		boolean retry = true;

        // End texture loading thread
		while( retry ){
			try {
				texLoadThread.join();
				retry = false;
			} catch( InterruptedException e ){
				Log.i(TAG, "Could not join texLoadThread... trying again...");
			}
		}

        // Delete all textures
		Iterator it = textures.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Integer id = (Integer)pairs.getValue();
			int tmpId[] = new int[1];
			tmpId[0] = id;
			
			GLES20.glDeleteTextures(1, tmpId, 0);
			it.remove();
		}

		shader.destroy();
	}
	
	@Override
	public void onDrawFrame(GL10 unused){
		// TODO: Try to pull this out of the rendering method.
		if(texLoaderBuf != null){
			Bitmap bitmap = texLoaderBuf.getBitmap();
			Log.i(TAG, "Texture ready?!!" + texLoaderBuf.getName());
			
			int texHandle[] = new int[1];
			// Generate GL texture
			GLES20.glGenTextures( 1, texHandle, 0);
			if(texHandle[0] != 0){
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texHandle[0]);
				
				// Set texture parameters
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
				
				// Load the bitmap into the texture
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
				
				textures.put( texLoaderBuf.getName(), texHandle[0] );
			}
			// Recycle bitmap data--GL has it now.
			bitmap.recycle();
			texLoaderBuf = null;
		}

		GLES20.glClear( GLES20.GL_COLOR_BUFFER_BIT );

		shader.use();
		
		// Set up camera
		Matrix.setIdentityM(mModelMatrix, 0);
		Matrix.setLookAtM(mVMatrix, 0, 0, 0, -2, 0f, 0f, 0f, 0f, 1.f, 0f);
		
		int mMVPMatrixHandle;
		mMVPMatrixHandle = shader.getUniformLocation("uMVPMatrix");
		
		Iterator it = textures.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry pairs = (Map.Entry)it.next();
			Integer id = (Integer)pairs.getValue();
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, id);

			// other matrices
			Matrix.multiplyMM(mTemp, 0, mVMatrix, 0, mModelMatrix, 0);
			Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mTemp, 0);

			GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
			testMesh.draw( shader );
			
			Matrix.translateM(mModelMatrix, 0, -1f, 0f, 0f);
		}

		Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);
		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
		
	}
	
	@Override
	public void onSurfaceChanged(GL10 unused, int width, int height){
		GLES20.glViewport( 0,  0, width, height);
		
		float ratio = (float)width / height;
		
		Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 1.f, 10f);
	}

}
