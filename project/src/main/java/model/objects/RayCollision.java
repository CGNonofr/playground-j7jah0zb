package model.objects;


import model.Point3D;
import model.TextureCoordinates;
import model.Vector;

public class RayCollision {
	private Point3D point;
	private Vector normal;
	private double hitDistance;
	private double transparence;
	private double reflection;
	private TextureCoordinates textureCoordinates;
	public RayCollision(double hitDistance, Point3D point, Vector normal, TextureCoordinates textureCoordinates, double transparence, double reflection) {
		this.point = point;
		this.normal = normal;
		this.hitDistance=hitDistance;
		this.textureCoordinates=textureCoordinates;
		this.transparence=transparence;
		this.reflection=reflection;
	}
	public Point3D getPoint() {
		return point;
	}
	public Vector getNormal() {
		return normal;
	}
	
	public double getHitDistance() {
		return hitDistance;
	}
	
	public TextureCoordinates getTextureCoordinates() {
		return textureCoordinates;
	}
	public double getTransparence() {
		return transparence;
	}
	public double getReflection() {
		return reflection;
	}
	public void setReflection(double reflection) {
		this.reflection = reflection;
	}
	public void setTextureCoordinates(TextureCoordinates textureCoordinates) {
		this.textureCoordinates = textureCoordinates;
	}
	@Override
	public String toString() {
		return point+" | "+normal;
	}
	
	
}
