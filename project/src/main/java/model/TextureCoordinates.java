package model;

import java.awt.image.BufferedImage;

public class TextureCoordinates {
	private double x,y;
	public TextureCoordinates(double x, double y) {
		if(x<0||x>1||y<0||y>1) throw new RuntimeException("Coordinates out of bounds : "+x+" "+y);
		this.x = x;
		this.y = y;
	}
	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public int getFrom(BufferedImage texture) {
		int x=Math.max(0, Math.min(texture.getWidth()-1, (int)Math.round(this.x*texture.getWidth())));
		int y=Math.max(0, Math.min(texture.getHeight()-1, (int)Math.round(this.y*texture.getWidth())));
		return texture.getRGB(x, y);
	}
}
