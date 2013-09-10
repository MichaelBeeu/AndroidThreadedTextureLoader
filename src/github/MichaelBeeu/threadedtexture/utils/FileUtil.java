package github.MichaelBeeu.threadedtexture.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.IOException;

public class FileUtil {
	
	/**
	 * Returns a StringBuffer object containing the contents of
	 * stream. Mostly useful for small text files.
	 * @param stream input stream to read from
	 * @return the contents of stream
	 * @throws IOException
	 */
	public static StringBuffer readEntireFile(InputStream stream) throws IOException {
		BufferedReader br = new BufferedReader( new InputStreamReader( stream ) );
		
		StringBuffer sb = new StringBuffer();
		String line = null;
		
		while( (line = br.readLine()) != null ){
			sb.append( line ).append( '\n' );
		}
		
		return sb;
	}
}
