package model.objects;

import java.awt.Graphics2D;

import model.Point3D;
import model.Ray;
import model.camera.Camera;
import model.octree.AABB;

public interface Entity3D {
	public RayCollision intersect(Ray ray);
	public AABB getBoundingBox();
	public boolean contained(AABB box);
	public Point3D center();
	public void rasterize(Camera c, Graphics2D g);
}
