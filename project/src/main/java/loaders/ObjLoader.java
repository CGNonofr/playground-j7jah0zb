package loaders;

import java.io.IOException;
import java.net.URL;

import model.objects.Object3D;

public interface ObjLoader {
	public Object3D load(URL ressource) throws IOException;
}
