package model.camera;

import java.awt.Dimension;

import model.Point3D;
import model.Vector;

public class CenteredCamera extends Camera {
	
	private Point3D sceneCenter;
	private double distance;
	
	private Vector direction;
	private Vector normal;
	

	public CenteredCamera(Point3D sceneCenter, double distance, Vector direction, Vector normal, double screenDistance, double screenWidth, double screenHeight, Dimension resolution) {
		super(screenDistance, screenWidth, screenHeight, resolution);
		this.sceneCenter = sceneCenter;
		this.direction = direction.clone().normalize();
		this.distance = distance;
		this.normal=normal;
		
		super.update();
	}
	

	public Vector getDirection() {
		return direction;
	}

	@Override
	public Point3D getPosition() {
		return sceneCenter.clone().translate(direction.clone().mult(-distance));
	}


	public void rotateY(double angle) {
		this.direction.rotate(normal, angle);
		super.update();
	}
	public void rotateX(double angle) {
		Vector newDirection=this.direction.clone().rotate(normal.clone().cross(direction), angle);
		this.normal.rotate(normal.clone().cross(direction), angle);
		this.direction = newDirection;
		super.update();
	}
	
	public void rotateZ(double angle) {
		this.normal.rotate(direction, angle);
		super.update();
	}
	
	public void rotateXZ(double angle) {
		this.direction.rotateXZ(angle).normalize();
		super.update();
	}
	
	public Vector screenH() {
		return getDirection().clone().cross(normal).normalize();
	}
	public Vector screenV() {
		return getDirection().clone().cross(screenH()).normalize();
	}


	@Override
	public Vector getNormal() {
		return normal;
	}
	
}
