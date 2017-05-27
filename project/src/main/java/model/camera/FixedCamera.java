package model.camera;

import java.awt.Dimension;

import model.Vector;
import model.Point3D;

public class FixedCamera extends Camera {
	
	private Point3D origin;
	private Vector direction;
	private Vector normal;
	public FixedCamera(Point3D origin, Vector direction, Vector normal, double screenDistance, double screenWidth, double screenHeight, Dimension resolution) {
		super(screenDistance, screenWidth, screenHeight, resolution);
		this.origin = origin;
		this.direction=direction;
		this.normal=normal;
		super.update();
	}
	
	public void up() {
		this.direction=this.direction.add(new Vector(0, 0.001, 0)).normalize();
		super.update();
	}
	public void down() {
		this.direction=this.direction.add(new Vector(0, -0.001, 0)).normalize();
		super.update();
	}
	
	public void translate(Vector v) {
		origin=origin.translate(v);
		super.update();
	}
	
	public Vector screenH() {
		return direction.cross(new Vector(0, 1, 0)).normalize();
	}
	public Vector screenV() {
		return direction.cross(screenH()).normalize();
	}


	public Point3D getOrigin() {
		return origin;
	}

	public Vector getDirection() {
		return direction;
	}

	@Override
	public Point3D getPosition() {
		return origin;
	}

	@Override
	public Vector getNormal() {
		return normal;
	}
}
