package github.MichaelBeeu.threadedtexture.vos;

public class TestMesh extends Mesh {
	static final float squareCoords[] = {-0.5f, 0.5f, 0.f,
								   -0.5f, -0.5f, 0.f,
								   0.5f, -0.5f, 0.f,
								   0.5f, 0.5f, 0.f};
	private final short drawOrder[] = {0, 1, 2, 0, 2, 3};
	
	public TestMesh(){
		loadVertexBuffer( squareCoords );
		loadDrawBuffer( drawOrder );
		setNumVertices( drawOrder.length );
	}

}
