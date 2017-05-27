import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import loaders.ObjFileLoader;
import model.Vector;
import model.camera.CenteredCamera;
import model.objects.Object3D;
import model.objects.Scene;
import renderers.RasteringRenderer;
import renderers.RayTracingRenderer;
import renderers.Renderer;
import renderers.destination.BufferedImageRenderDestination;


public class GMain {
	
	private static Renderer[] renderers=new Renderer[] {new RasteringRenderer(), new RayTracingRenderer()};
	private static int renderer=0;
	public static void main(String[] args) throws IOException {
		
		final Object3D obj=new ObjFileLoader().load(new File("/home/lolo/Bureau/minicooper.obj").toURI().toURL());
//		final Camera c=new FixedCamera(new Vertex(0, 0.5, 500), new Vector(0, 0, -1), 1, 1, 1, new Dimension(800, 600));
		double cameraDistance = Math.max(Math.max(obj.getBoundingBox().getWidth(), obj.getBoundingBox().getHeight()), obj.getBoundingBox().getDepth());
		final CenteredCamera c=new CenteredCamera(obj.getBoundingBox().center().translate(new Vector(0, 0, -20)), cameraDistance, new Vector(1, 1, -1), new Vector(0, 0, 1).normalize(), 1, 1400d/900d, 1, new Dimension(1000, 700));

		final BufferedImage image = new BufferedImage(c.getResolution().width, c.getResolution().height, BufferedImage.TYPE_INT_RGB);
		
		final JFrame frame=new JFrame();
		final JPanel panel=new JPanel() {
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(image, 0, 0, null);
			}
		};
		
		frame.setContentPane(panel);
		panel.setFocusable(true);
		panel.requestFocusInWindow();
		panel.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {}
			
			@Override
			public void keyReleased(KeyEvent e) {}
			
			@Override
			public void keyPressed(KeyEvent e) {
				switch(e.getKeyCode()) {
				case KeyEvent.VK_R:
					renderer=(renderer+1)%renderers.length;
					break;
				case KeyEvent.VK_LEFT:
					c.rotateY(-Math.PI/10);
					break;
				case KeyEvent.VK_RIGHT:
					c.rotateY(Math.PI/10);
					break;
				case KeyEvent.VK_UP:
					c.rotateX(Math.PI/10);
					break;
				case KeyEvent.VK_DOWN:
					c.rotateX(-Math.PI/10);
					break;
				case KeyEvent.VK_Q:
					c.rotateZ(-Math.PI/10);
					break;
				case KeyEvent.VK_D:
					c.rotateZ(Math.PI/10);
					break;
				}
				new Thread() {
					public void run() {
						try {
							renderers[renderer].render(c, new BufferedImageRenderDestination(image));
						} catch (IOException e) {
						}
					}
				}.start();
			}
		});
		frame.setSize(1400, 900);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				panel.repaint();
			}
		}, 300,300);
		obj.setTexture(ImageIO.read(new File("/home/lolo/Bureau/damier.jpg")));
		Scene sce=new Scene();
//		sce.addObject(new Object3D(ImageIO.read(new File("/home/lolo/Bureau/damier.jpg")), new Sphere(obj.getBoundingBox().center(), 10)));
		sce.addObject(obj);
		for(Renderer r:renderers) {
			r.prepare(sce);
		}
		renderers[renderer].render(c, new BufferedImageRenderDestination(image));
		
	}

}
