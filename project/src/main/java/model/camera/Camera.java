package model.camera;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Iterator;

import model.Point3D;
import model.Ray;
import model.Vector;

public abstract class Camera implements Iterable<Ray>{
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
		Vector direction=getDirection();
		this.position=getPosition();
		this.screenCenter=this.position.translate(getDirection().mult(screenDistance));
		this.screenH=direction.cross(getNormal()).normalize();
		this.screenV=direction.cross(screenH).normalize();
		this.screenTopLeft=screenCenter.translate(screenH.mult(-screenWidth/2)).translate(screenV.mult(-screenHeight/2));
	}
	
	public Ray getRay(int x, int y) {
		Point3D pt=screenTopLeft.translate(screenH.mult(x*screenWidth/resolution.getWidth())).translate(screenV.mult(y*screenHeight/resolution.getHeight()));
		return new Ray(pt, new Vector(this.position, pt).normalize());
	}
	
	public Iterator<Ray> iterator() {
		return new Iterator<Ray>() {
			int line=0, column=0;
			@Override
			public boolean hasNext() {
				return !(line==resolution.height-1 && column==resolution.width-1);
			}

			@Override
			public Ray next() {
				if(++column>=resolution.width) {
					column=0;
					line++;
				}
				return getRay(column, line);
			}
			@Override
			public void remove() {
			}
		};
	}
	
	
	public Point project(Point3D v) {
		Vector vect=new Vector(this.position, v);
		double dist=screenDistance/getDirection().dot(vect)*vect.length();
		Point3D projected=this.position.translate(vect.normalize().mult(dist));
		
		Vector screenVector=new Vector(screenTopLeft, projected);
		double x=screenH.dot(screenH.mult(screenH.dot(screenVector)))/screenWidth;
		double y=screenV.dot(screenV.mult(screenV.dot(screenVector)))/screenHeight;
		return new Point((int)(x*resolution.getWidth()+0.5), (int)(y*resolution.getHeight()+0.5));
	}
}
