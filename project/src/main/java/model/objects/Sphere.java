package model.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import model.Point3D;
import model.Ray;
import model.TextureCoordinates;
import model.Vector;
import model.camera.Camera;
import model.octree.AABB;


public class Sphere implements Entity3D {
	private Point3D center;
	private double radius;
	
	public Sphere(Point3D center, double radius) {
		this.center=center;
		this.radius=radius;
	}

	@Override
	public RayCollision intersect(Ray r) {
		double angleVersCentre = r.getDirection().clone().normalize().dot(new Vector(r.getOrigin(), center));
		Point3D proj=r.getOrigin().clone().translate(r.getDirection().clone().normalize().mult(angleVersCentre));
		double ratio=center.distance(proj)/radius;
		
		if(ratio<1)  {
			double sin=Math.sqrt(1-Math.pow(ratio, 2));
			Point3D contact = null;
			double distanceCentre=r.getOrigin().distance(center);
			if(angleVersCentre>0 && distanceCentre > radius) { // si on est a l'exterieur et qu'on regarde vers la sphere			
				contact=proj.clone().translate(r.getDirection().normalize().mult(-sin*radius));
			} else if((angleVersCentre>0 && distanceCentre <= radius) || (angleVersCentre>0 && distanceCentre <= radius)) { // si on est a l'interieur
				contact=proj.clone().translate(r.getDirection().normalize().mult(sin*radius));
			} else return null;	
			
			Vector normal=new Vector(center,contact).normalize();
			TextureCoordinates texture=new TextureCoordinates((Math.atan2(normal.getZ(),normal.getX())+Math.PI)/(2*Math.PI), normal.angle(Vector.VERTICAL_NORMAL)/(2*Math.PI));
			return new RayCollision(r.getOrigin().distance(contact), contact, normal, texture, 0, 0.4);
		}
		return null;
	}

	@Override
	public AABB getBoundingBox() {
		return new AABB(center.getX()-radius, center.getY()-radius, center.getZ()-radius, center.getX()+radius, center.getY()+radius, center.getZ()+radius);
	}

	@Override
	public boolean contained(AABB box) {
		return box.getX()<=center.getX()-radius
				&& box.getY()<=center.getY()-radius
				&& box.getZ()<=center.getZ()-radius
				&& box.getMaxx()>=center.getZ()+radius
				&& box.getMaxy()>=center.getY()+radius
				&& box.getMaxz()>=center.getZ()+radius;
	}

	@Override
	public Point3D center() {
		return center;
	}

	@Override
	public void rasterize(Camera camera, Graphics2D g2d) {
		Point p=camera.project(center);
		double factor=center.distance(camera.getPosition())/camera.getScreenDistance();
		int width=(int) (radius/factor/camera.getScreenWidth()*camera.getResolution().getWidth());
		int height=(int) (radius/factor/camera.getScreenHeight()*camera.getResolution().getHeight());
		g2d.setColor(Color.RED);
		g2d.fillOval(p.x-width,p.y-height, width*2, height *2);
	}
	

}
