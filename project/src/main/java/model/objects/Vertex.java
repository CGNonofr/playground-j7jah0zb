package model.objects;

import model.Point3D;
import model.TextureCoordinates;
import model.Vector;

public class Vertex {
	private Point3D point;
	private TextureCoordinates texture;
	private Vector normal;
	public Vertex(Point3D vertex, TextureCoordinates texture,
			Vector normal) {
		this.point = vertex;
		this.texture = texture;
		this.normal = normal;
	}
	public Point3D getPoint() {
		return point;
	}
	public TextureCoordinates getTexture() {
		return texture;
	}
	public Vector getNormal() {
		return normal;
	}
	
	@Override
	public String toString() {
		return point.toString();
	}
}
