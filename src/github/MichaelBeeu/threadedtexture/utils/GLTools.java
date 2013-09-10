package github.MichaelBeeu.threadedtexture.utils;

import android.opengl.GLES20;

public class GLTools {
    
    public static void checkGLError(String glOp) throws RuntimeException {
    	int err;
    	while( (err = GLES20.glGetError()) != GLES20.GL_NO_ERROR ){
    		throw new RuntimeException(glOp + ": " + err );
    	}
    }
}
