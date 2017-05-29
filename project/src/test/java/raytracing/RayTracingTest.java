package raytracing;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
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

	@SuppressWarnings("restriction")
	private void test(Dimension resolution, Vector cameraDirection, Vector cameraNormal, Vector translate, double distance, URL url) throws Exception {
		int width = resolution.width;
		int height = resolution.height;

		final Object3D obj = new ObjFileLoader().load(url);
		double cameraDistance = Math.max(Math.max(obj.getBoundingBox().getWidth(), obj.getBoundingBox().getHeight()),
				obj.getBoundingBox().getDepth()) * distance;
		final CenteredCamera c = new CenteredCamera(obj.getBoundingBox().center().clone().translate(translate),
				cameraDistance, cameraDirection, cameraNormal, 1, width / (double) height, 1,
				new Dimension(width, height));
		Scene sce = new Scene();
		sce.addObject(obj);
		AbstractRayTracingRenderer renderer = new RayTracingRenderer();
		renderer.prepare(sce);

		final BmpRenderDestination bmp = new BmpRenderDestination(width, height);
		
        Server server = new Server(8000);
        server.setHandler(new AbstractHandler() {
			@Override
			public void handle(String target,
                    Request baseRequest,
                    HttpServletRequest request,
                    HttpServletResponse response)
					throws IOException, ServletException {
				switch(target) {
				case "/index":
					System.out.println("test");
					try {
						String answer = new String(Files.readAllBytes(Paths.get(RayTracingTest.class.getResource("/index.html").toURI())));
	
				        response.setContentType("text/html;charset=utf-8");
				        response.setStatus(HttpServletResponse.SC_OK);
				        baseRequest.setHandled(true);
				        response.getWriter().println(answer);
					} catch (URISyntaxException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case "/image":
			        response.setContentType("image/bmp");
			        response.setStatus(HttpServletResponse.SC_OK);
			        response.setHeader("Content-Disposition", "inline; filename=\"render.bmp\"");
	
					int data;
					try (InputStream stream = bmp.getInputStream()) {
						while ((data = stream.read()) >= 0) {
							response.getOutputStream().write(data);
						}
					} catch (Throwable t) {
					}
					break;
				case "/imagejpg":
			        response.setContentType("image/bmp");
			        response.setStatus(HttpServletResponse.SC_OK);
			        response.setHeader("Content-Disposition", "inline; filename=\"render.jpg\"");
	
					try (InputStream stream = bmp.getFilledInputStream()) {
						BufferedImage image = ImageIO.read(stream);
						ImageIO.write(image, "jpg", response.getOutputStream());
					}
					break;
				}

			}
        });
 
        server.start();
        
//		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 10);
//		server.setExecutor(Executors.newCachedThreadPool());
//		server.createContext("/index", new HttpHandler() {
//			@Override
//			public void handle(HttpExchange ex) throws IOException {
//				try {
//					String answer = new String(Files.readAllBytes(Paths.get(RayTracingTest.class.getResource("/index.html").toURI())));
//
//					byte[] bytes = answer.getBytes();
//					ex.sendResponseHeaders(200, 0);
//					OutputStream os = ex.getResponseBody();
//					os.write(bytes);
//					os.flush();
//					ex.close();
//				} catch (URISyntaxException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//			}
//		});
//		server.createContext("/image", new HttpHandler() {
//			@Override
//			public void handle(HttpExchange ex) throws IOException {
//				ex.getResponseHeaders().set("Content-Disposition", "inline; filename=\"render.bmp\"");
//				ex.getResponseHeaders().set("Content-Type", "image/bmp");
//				ex.sendResponseHeaders(200, 0);
//				OutputStream os = ex.getResponseBody();
//
//				
//				int data;
//				try (InputStream stream = bmp.getInputStream()) {
//					while ((data = stream.read()) >= 0) {
//						os.write(data);
//					}
//				} catch (Throwable t) {
//				}
//				os.close();
//			}
//		});
//		server.createContext("/imagejpg", new HttpHandler() {
//			@Override
//			public void handle(HttpExchange ex) throws IOException {
//				ex.getResponseHeaders().set("Content-Disposition", "inline; filename=\"render.jpg\"");
//				ex.getResponseHeaders().set("Content-Type", "image/jpg");
//				ex.sendResponseHeaders(200, 0);
//				OutputStream os = ex.getResponseBody();
//
//				try (InputStream stream = bmp.getFilledInputStream()) {
//					BufferedImage image = ImageIO.read(stream);
//					ImageIO.write(image, "jpg", os);
//				}
//				os.close();
//			}
//		});
//		server.start();
		System.out.println("TECHIO> open --port 8000 /index");
		System.out.println("TECHIO> success");
		renderer.render(c, bmp);
		
		server.join();
	}

	@Test
	public void testMiniCooper() throws Exception {
		test(new Dimension(700, 400), new Vector(1, 1, -1).normalize(), new Vector(0, 0, 1).normalize(), new Vector(20, -10, -25), 1,
				RayTracingTest.class.getResource("/minicooper.obj"));
	}

//	@Test
//	public void testTeapot() throws Exception {
//		test(new Dimension(1280, 720), new Vector(-1, -1, -1).normalize(), new Vector(0, 1, 0).normalize(), new Vector(40, 20, 15), 0.5,
//				RayTracingTest.class.getResource("/teapot.obj"));
//	}
}
