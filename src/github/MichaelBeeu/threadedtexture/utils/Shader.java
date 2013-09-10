package github.MichaelBeeu.threadedtexture.utils;

import android.opengl.GLES20;
import android.util.Log;

public class Shader {
	private final String TAG = "Shader";
	private int vertexShader=0, fragmentShader=0, program=0;
	
	public Shader( ){
		
	}
	
	public Shader( String vCode, String fCode ){
		loadProgram( vCode, fCode );
	}
	
	public boolean loadProgram( String vCode, String fCode ){
		try {
			program = GLES20.glCreateProgram();
			vertexShader = loadShader( GLES20.GL_VERTEX_SHADER, vCode );
			fragmentShader = loadShader( GLES20.GL_FRAGMENT_SHADER, fCode );
			
			GLES20.glAttachShader( program, vertexShader );
			GLES20.glAttachShader( program,  fragmentShader );
			GLES20.glLinkProgram( program );
			
			GLTools.checkGLError("glLinkProgram");
			return true;
		} catch (RuntimeException e){
			Log.e(TAG, "Runtime exception: " + e.getMessage() );
		}
		return false;
	}
		
	public boolean isOkay(){
		return (program != 0) && (vertexShader != 0) && (fragmentShader != 0);
	}
	
	public void use(){
		GLES20.glUseProgram( program );
	}
	
	public int getAttribLocation(String id){
		return GLES20.glGetAttribLocation(program, id);
	}
	
	public int getUniformLocation(String id){
		return GLES20.glGetUniformLocation(program, id);
	}
	public int getProgram(){
		return program;
	}

    public int loadShader(int type, String code) throws RuntimeException {
    	int shader = GLES20.glCreateShader( type );
    	
    	if(shader == 0){
    		throw new RuntimeException("Error creating shader! " + GLES20.glGetError() );
    	}
    	
    	GLES20.glShaderSource( shader, code );
    	GLES20.glCompileShader( shader );
    	
    	GLTools.checkGLError("glCompileShader");
    	
    	return shader;
    }
}
