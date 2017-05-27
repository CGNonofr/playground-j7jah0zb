package renderers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import model.camera.Camera;
import model.objects.Entity3D;
import model.objects.Object3D;
import model.objects.Scene;
import renderers.destination.RenderDestination;

public class RasteringRenderer implements Renderer {

	private Scene scene;
	@Override
	public void prepare(Scene scene) {
		this.scene=scene;
	}
	
	@Override
	public void render(final Camera c, RenderDestination render) throws IOException {
		BufferedImage image = new BufferedImage(c.getResolution().width, c.getResolution().height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g=image.createGraphics();
        g.setBackground(new Color(255, 255, 255, 0));
        g.clearRect(0,0, image.getWidth(), image.getHeight());
        
        // TODO mettres les objets dans l'ordre
        for(Object3D object:scene.getObjects()) {
			Arrays.sort(object.getEntities(), new Comparator<Entity3D>() {
				@Override
				public int compare(Entity3D f1, Entity3D f2) {
					double diff = f1.center().distance(c.getPosition()) - f2.center().distance(c.getPosition());
					return diff == 0.0 ? 0 : (diff < 0 ? 1 : -1);
				}
			});
			object.rasterize(c, g);
        }
		g.dispose();
		
		int height = c.getResolution().height;
		int width = c.getResolution().width;
		int[] line = new int[width];
		for(int i = height - 1 ; i >= 0 ; i--) {
			image.getRGB(0, i, width, 1, line, 0, 1);
			render.writeLine(line);
		}
		
	}
}
