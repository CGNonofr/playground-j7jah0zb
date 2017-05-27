package renderers;

import java.io.IOException;

import model.camera.Camera;
import model.objects.Scene;
import renderers.destination.RenderDestination;

public interface Renderer {
	public void render(Camera c, RenderDestination destination) throws IOException;
	public void prepare(Scene entity3ds);
}