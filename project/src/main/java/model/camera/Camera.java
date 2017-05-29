package model.camera;

import java.awt.Dimension;
import java.awt.Point;

import model.Point3D;
import model.Ray;
import model.Vector;

public abstract class Camera {
	public Camera(double screenDistance, double screenWidth, double screenHeight, Dimension resolution) {
		this.screenDistance = screenDistance;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;
		this.resolution = resolution;
	}
	
	public abstract Point3D getPosition();
	public abstract Vector getDirection();
	public abstract Vector getNormal();
	private double screenDistance;
	private double screenWidth, screenHeight;
	private Dimension resolution;
	
	private Point3D screenCenter,screenTopLeft;
	private Vector screenH, screenV;
	private Point3D position;
	
	private Vector tmpVector = new Vector(0, 0, 0);
	
	public Dimension getResolution() {
		return resolution;
	}

	public double getScreenDistance() {
		return screenDistance;
	}
	
	public double getScreenWidth() {
		return screenWidth;
	}

	public void setScreenWidth(double screenWidth) {
		this.screenWidth = screenWidth;
	}

	public double getScreenHeight() {
		return screenHeight;
	}

	public void setScreenHeight(double screenHeight) {
		this.screenHeight = screenHeight;
	}

	protected void update() {
		Vector direction = getDirection();
		this.position = getPosition();
		this.screenCenter = this.position.clone().translate(getDirection().clone().mult(screenDistance));
		this.screenH = direction.clone().cross(getNormal()).normalize();
		this.screenV = direction.clone().cross(screenH).normalize();
		this.screenTopLeft = screenCenter.clone().translate(screenH.clone().mult(-screenWidth/2)).translate(screenV.clone().mult(-screenHeight/2));
	}
	
	public void getRay(int x, int y, Ray ray) {
		ray.getOrigin().set(screenTopLeft).translate(tmpVector.set(screenH).mult(x*screenWidth/resolution.getWidth())).translate(tmpVector.set(screenV).mult(y*screenHeight/resolution.getHeight()));
		ray.getDirection().set(this.position, ray.getOrigin()).normalize();
	}
	
	
	public Point project(Point3D v) {
		Vector vect = new Vector(this.position, v);
		double dist = screenDistance / getDirection().dot(vect) * vect.length();
		Point3D projected = this.position.clone().translate(vect.clone().normalize().mult(dist));
		
		Vector screenVector=new Vector(screenTopLeft, projected);
		double x=screenH.dot(screenH.clone().mult(screenH.dot(screenVector))) / screenWidth;
		double y=screenV.dot(screenV.clone().mult(screenV.dot(screenVector))) / screenHeight;
		return new Point((int)(x*resolution.getWidth()+0.5), (int)(y*resolution.getHeight()+0.5));
	}
}
