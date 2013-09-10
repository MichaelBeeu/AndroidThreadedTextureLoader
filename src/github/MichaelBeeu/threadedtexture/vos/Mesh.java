package github.MichaelBeeu.threadedtexture.vos;

import github.MichaelBeeu.threadedtexture.utils.Shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class Mesh {
	private final String TAG = "Mesh";
	protected FloatBuffer vertexBuffer;
	protected FloatBuffer textureBuffer;
	protected ShortBuffer drawListBuffer;
	protected int numVertices;
	protected int coordsPerVertex = 3;
	
	public Mesh(){
	}
	
	public void draw(Shader shader){
		int mPositionHandle;
		int mTextureHandle;
		int mColorHandle;
		
		//Log.i(TAG, "Rendering mesh...");
		// Set up position array
		mPositionHandle = shader.getAttribLocation("aPosition");
		GLES20.glEnableVertexAttribArray( mPositionHandle );
		
		GLES20.glVertexAttribPointer( mPositionHandle, coordsPerVertex,
									  GLES20.GL_FLOAT, false,
									  coordsPerVertex * 4, vertexBuffer );
		
		mTextureHandle = shader.getAttribLocation("aTexCoord");
		GLES20.glEnableVertexAttribArray( mTextureHandle );
		
		GLES20.glVertexAttribPointer( mTextureHandle, 2,
									  GLES20.GL_FLOAT, false,
									  2 * 4, textureBuffer);
		
		final float color[] = {0.2f, 0.7f, 0.8f, 1.f};
		mColorHandle = shader.getUniformLocation("uColor");
		GLES20.glUniform4fv(mColorHandle, 1, color, 0);
		
		// Draw the mesh.
		GLES20.glDrawElements(GLES20.GL_TRIANGLES, numVertices,
							  GLES20.GL_UNSIGNED_SHORT, drawListBuffer);
		
	}
	
	/**
	 * Set the number of vertices in mesh.
	 * @param nVertices
	 */
	protected void setNumVertices( int nVertices ){
		numVertices = nVertices;
	}
	
	/**
	 * Load coords into native vertex float buffer.
	 * @param coords
	 */
	protected void loadVertexBuffer( float coords[] ){
		ByteBuffer bb = ByteBuffer.allocateDirect( coords.length * 4 );
		bb.order( ByteOrder.nativeOrder() );
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put( coords );
		vertexBuffer.position( 0 );
	}
	
	
	protected void loadTextureBuffer( float coords[] ){
		ByteBuffer bb = ByteBuffer.allocateDirect( coords.length * 4 );
		bb.order( ByteOrder.nativeOrder() );
		textureBuffer = bb.asFloatBuffer();
		textureBuffer.put( coords );
		textureBuffer.position( 0 );
	}
	
	/**
	 * Load order into native draw list short buffer.
	 * @param order
	 */
	protected void loadDrawBuffer( short order[] ){
		ByteBuffer bb = ByteBuffer.allocateDirect( order.length * 2 );
		bb.order( ByteOrder.nativeOrder() );
		drawListBuffer = bb.asShortBuffer();
		drawListBuffer.put( order );
		drawListBuffer.position( 0 );
	}
}
