package model;

import model.octree.AABB;


public class Ray {
	private Point3D origin;
	private Vector direction;

	public Ray(Point3D origin, Vector direction) {
		this.origin = origin;
		this.direction = direction;
	}

	public Point3D getOrigin() {
		return origin;
	}

	public void setOrigin(Point3D origin) {
		this.origin = origin;
	}

	public Vector getDirection() {
		return direction;
	}
	@Override
	public String toString() {
		return origin+" | "+direction;
	}

	
	public boolean intersect(AABB box) {
		double t1 = (box.getX() - origin.getX())/direction.getX();
		double t2 = (box.getMaxx() - origin.getX())/direction.getX();
		double t3 = (box.getY() - origin.getY())/direction.getY();
		double t4 = (box.getMaxy() - origin.getY())/direction.getY();
		double t5 = (box.getZ() - origin.getZ())/direction.getZ();
		double t6 = (box.getMaxz() - origin.getZ())/direction.getZ();

		double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
		double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

		// t = distance of collision
//		double t;
		// if tmax < 0, ray (line) is intersecting AABB, but whole AABB is behing us
		if (tmax < 0) {
//		    t = tmax;
		    return false;
		}
		// if tmin > tmax, ray doesn't intersect AABB
		if (tmin > tmax) {
//		    t = tmax;
		    return false;
		}

//		t = tmin;
		return true;
	}
}
