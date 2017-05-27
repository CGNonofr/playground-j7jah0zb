package raytracing;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Test;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import loaders.ObjFileLoader;
import model.Vector;
import model.camera.CenteredCamera;
import model.objects.Object3D;
import model.objects.Scene;
import renderers.AbstractRayTracingRenderer;
import renderers.RayTracingRenderer;
import renderers.destination.BmpRenderDestination;

public class RayTracingTest {

	private void test(URL url) throws IOException {
		int width = 500;
		int height = 300;
		
		final Object3D obj = new ObjFileLoader().load(url);
		double cameraDistance = Math.max(Math.max(obj.getBoundingBox().getWidth(), obj.getBoundingBox().getHeight()), obj.getBoundingBox().getDepth());
		final CenteredCamera c = new CenteredCamera(obj.getBoundingBox().center().translate(new Vector(0, 0, -20)), cameraDistance, new Vector(1, 1, -1), new Vector(0, 0, 1).normalize(), 1, width / (double)height, 1, new Dimension(width, height));
		Scene sce=new Scene();
		sce.addObject(obj);
        AbstractRayTracingRenderer renderer = new RayTracingRenderer();
        renderer.prepare(sce);
        
        
        final BmpRenderDestination bmp = new BmpRenderDestination(width, height);
		
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 10);
        server.setExecutor(Executors.newCachedThreadPool());
        server.createContext("/index", new HttpHandler() {
			@Override
			public void handle(HttpExchange ex) throws IOException {
				String answer = "<html><body style=\"background-image: url(/image); background-position: center; background-size: contain; background-repeat: no-repeat;\"></body></html>";
				ex.sendResponseHeaders(200, answer.length());
				OutputStream os = ex.getResponseBody();
				os.write(answer.getBytes());
				os.close();
			}
        });
        server.createContext("/image", new HttpHandler() {
			@Override
			public void handle(HttpExchange ex) throws IOException {
				ex.getResponseHeaders().set("Content-Disposition", "inline; filename=\"render.bmp\"");
				ex.getResponseHeaders().set("Content-Type", "image/bmp");
	            ex.sendResponseHeaders(200, 0);
	            OutputStream os = ex.getResponseBody();
	            
				InputStream stream = bmp.getInputStream();
				int data;
				try {
					while((data = stream.read()) >= 0) {
						os.write(data);
					}
				} catch(Throwable t) {
				}
	            os.close();
			}
		});
        server.start();
        
        System.out.println("TECHIO> open --port 8000 /index");

        renderer.render(c, bmp);
	}
	
//	@Test
//	public void testMiniCooper() throws IOException {
//		test(RayTracingTest.class.getResource("/minicooper.obj"));
//	}
	
	@Test
	public void testTeapot() throws IOException {
		test(RayTracingTest.class.getResource("/teapot.obj"));
	}
	
	@After
	public void waitViewer() throws InterruptedException {
		Thread.sleep(1000000);
	}
}
