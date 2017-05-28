package renderers;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import model.Ray;
import model.camera.Camera;
import renderers.destination.RenderDestination;

public abstract class AbstractRayTracingRenderer implements Renderer {

	public int[] render(int line, final Camera c) throws IOException {
		int width = c.getResolution().width;
		int[] colors = new int[width];
		for (int x = 0; x < width; ++x) {
			Ray ray = c.getRay(x, line);
			colors[x] = throwRay(ray, 5);
		}
		System.gc();
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
		ExecutorService executor = Executors.newWorkStealingPool(1);

		
		BlockingQueue<Future<int[]>> results = new LinkedBlockingQueue<>();
		int height = c.getResolution().height;
		for (int y = height - 1; y >= 0; --y) {
			results.add(executor.submit(new RenderTask(c, y)));
		}
		try {
			Future<int[]> result;
			int counter = 0;
			while ((result = results.poll()) != null) {
				render.writeLine(result.get());
//				if (counter % 2 == 0) {
					System.out.println(counter + " line computed");
//				}
				counter ++;
			}
		} catch (IOException | ExecutionException | InterruptedException e) {
			e.printStackTrace();
		}
		executor.shutdown();
		System.out.println("Rendering in " + (System.currentTimeMillis() - start)+"ms");
	}
	
	public abstract Integer throwRay(Ray ray, int depth);
}
