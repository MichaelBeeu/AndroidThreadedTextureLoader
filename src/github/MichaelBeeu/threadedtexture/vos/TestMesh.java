package github.MichaelBeeu.threadedtexture.vos;

public class TestMesh extends Mesh {
	static final float squareCoords[] = {-0.5f, 0.5f, 0.f,
								   -0.5f, -0.5f, 0.f,
								   0.5f, -0.5f, 0.f,
								   0.5f, 0.5f, 0.f};
	static final float texCoords[] = {1f, 0f,
                                      1f, 1f,
                                      0f, 1f,
		                              0f, 0f};
	private final short drawOrder[] = {0, 1, 2, 0, 2, 3};
	
	public TestMesh(){
		loadTextureBuffer( texCoords );
		loadVertexBuffer( squareCoords );
		loadDrawBuffer( drawOrder );
		setNumVertices( drawOrder.length );
	}

}
