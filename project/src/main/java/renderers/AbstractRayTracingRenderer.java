package renderers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import model.Ray;
import model.Vector;
import model.camera.Camera;
import model.objects.Entity3D;
import model.objects.Object3D;
import model.objects.RayCollision;
import model.objects.Scene;
import model.octree.OcTree;
import renderers.destination.RenderDestination;
import tools.MixColor;

public abstract class AbstractRayTracingRenderer implements Renderer {

	public int[] render(int line, final Camera c) throws IOException {
		int width = c.getResolution().width;
		int[] colors = new int[width];
		for (int x = 0; x < width; ++x) {
			Ray ray = c.getRay(x, line);
			Color col = throwRay(ray, 5);
			colors[x] = col.getRGB() & 0xffffff;
		}
		return colors;
	}

	private final class RenderTask implements Callable<int[]> {
		private Camera c;
		private int line;

		public RenderTask(Camera c, int line) {
			this.c = c;
			this.line = line;
		}

		public int[] call() throws IOException {
			return render(line, c);
		}
	}

	@Override
	public void render(final Camera c, RenderDestination render) {
		long start = System.currentTimeMillis();
		ExecutorService executor = Executors.newWorkStealingPool();

		List<Future<int[]>> results = new ArrayList<>();
		int height = c.getResolution().height;
		for (int y = height - 1; y >= 0; --y) {
			results.add(executor.submit(new RenderTask(c, y)));
		}
		try {
			for (Future<int[]> result : results) {
				render.writeLine(result.get());
			}
		} catch (IOException | ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		System.out.println(System.currentTimeMillis() - start);
	}
	
	public abstract Color throwRay(Ray ray, int depth);
}
