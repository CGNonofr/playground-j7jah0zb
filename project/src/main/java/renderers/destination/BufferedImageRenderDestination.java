package renderers.destination;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class BufferedImageRenderDestination implements RenderDestination {

	private BufferedImage image; 
	private int currentLine;
	public BufferedImageRenderDestination(BufferedImage image) {
		this.image = image;
		this.currentLine = image.getHeight() - 1;
	}
	@Override
	public void writeLine(int[] line) throws IOException {
		int[] colors = new int[line.length * 3];
		for(int i = 0 ; i < line.length; ++i) {
			colors[i * 3] = (line[i] >> 16) & 0xff;
			colors[i * 3 + 1] = (line[i] >> 8) & 0xff;
			colors[i * 3 + 2] = line[i] & 0xff;
		}
		image.getRaster().setPixels(0, currentLine--, image.getWidth(), 1, colors);
		
	}

}
