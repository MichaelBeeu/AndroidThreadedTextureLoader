package github.MichaelBeeu.threadedtexture.daos;

import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.content.Context;
import android.util.Log;

public class TextureLoader extends Observable implements Runnable {
	private final String TAG = "TextureLoader";
	
	private volatile ArrayBlockingQueue<String> queue;

	private int activeBitmap = 0;
	
	private volatile TextureBuffer texBuffer[];
	
	private volatile Boolean running = true;

	private Context ctx;

    // TODO: Set up EGL context, and load the texture into GL via the thread.
	public static class TextureBuffer {
		private Bitmap bitmap;
		private String name;
		
		public TextureBuffer(){
			bitmap = null;
			name = null;
		}

        public void copy(TextureBuffer buf){
            synchronized(bitmap){
                buf.bitmap = bitmap.copy( bitmap.getConfig(), bitmap.isMutable()?true:false );
            }
            synchronized(name){
                buf.name = name;
            }
        }
		
		public String getName(){
			synchronized( name ){
				return name;
			}
		}
		
		public Bitmap getBitmap(){
			Bitmap bmp;
			synchronized( bitmap ){
				bmp = Bitmap.createBitmap( bitmap );
			}
			return bmp;
		}
		
	}
	
	public TextureLoader(Context context){
		queue = new ArrayBlockingQueue<String>( 100 );
		texBuffer = new TextureBuffer[2];
		
		texBuffer[0] = new TextureBuffer();
		texBuffer[1] = new TextureBuffer();
		
		ctx = context;
	}
	
	@Override
	public void run(){
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;

		while( running ){
			String tex = null;
			synchronized( queue ){
				// Pull top element off queue
				tex = queue.poll();
			}
			if( tex != null ){
				synchronized( texBuffer[ activeBitmap ] ){
					try {
						Log.i(TAG, "Loading texture..." + tex );
						if( texBuffer[activeBitmap].bitmap != null )
							texBuffer[activeBitmap].bitmap.recycle();
						texBuffer[activeBitmap].bitmap = BitmapFactory.decodeStream( ctx.getAssets().open( tex ), null, options );
						texBuffer[activeBitmap].name = tex;
						Log.i(TAG, "Notifying...");
						setChanged();
						notifyObservers( texBuffer[activeBitmap] );
						activeBitmap = (activeBitmap + 1) % 2;
					} catch (IOException e){
						Log.e(TAG, "Could not load image: "+e.getMessage());
					}
				}
				//notifyObservers();
			}
		}
	}
	
	public void setRunning(Boolean r){
		synchronized( running ){
			running = false;
		}
	}
	
	public boolean addTexture(String texture){
		synchronized( queue ){
			try {
				queue.add( texture );
			} catch( IllegalStateException e ){
				Log.e(TAG, "Could not add texture '"+texture+"': "+e.getMessage() );
				return false;
			}
		}
		return true;
	}
}
