package model.octree;

import model.Point3D;


public class AABB {
	public AABB(double x, double y, double z, double maxx, double maxy,
			double maxz) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.maxx = maxx;
		this.maxy = maxy;
		this.maxz = maxz;
	}

	private double x, y, z, maxx, maxy, maxz;

	
	public AABB() {
	}


	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}

	public double getWidth() {
		return maxx-x;
	}
	public double getHeight() {
		return maxy-y;
	}
	public double getDepth() {
		return maxz-z;
	}

	public double getMaxx() {
		return maxx;
	}

	public double getMaxy() {
		return maxy;
	}

	public double getMaxz() {
		return maxz;
	}
	
	
	public static AABB merge(AABB ... boxes) {
		double x=Double.MAX_VALUE, y=Double.MAX_VALUE, z=Double.MAX_VALUE;
		double maxx=-Double.MAX_VALUE, maxy=-Double.MAX_VALUE, maxz=-Double.MAX_VALUE;
		for(AABB box:boxes) {
			x=Math.min(x, box.getX());
			y=Math.min(y, box.getY());
			z=Math.min(z, box.getZ());
			maxx=Math.max(maxx, box.getMaxx());
			maxy=Math.max(maxy, box.getMaxy());
			maxz=Math.max(maxz, box.getMaxz());
		}
		return new AABB(x, y, z, maxx, maxy, maxz);
	}
	
	public AABB powerOf2() {
		return new AABB(x,y,z, x+powerOf2Up(this.maxx-x), y+powerOf2Up(this.maxy-y), z+powerOf2Up(this.maxz-z));
	}
	
	public static double powerOf2Up(double n) {
		if(n>=0) {
			return Math.pow(2, Math.ceil(Math.log(n)/Math.log(2)));
		} else {
			return -powerOf2Down(-n);
		}
	}
	public static double powerOf2Down(double n) {
		if(n>=0) {
			return Math.pow(2, Math.floor(Math.log(n)/Math.log(2)));
		} else {
			return -powerOf2Up(-n);
		}
	}
	
	@Override
	public String toString() {
		return "["+x+" "+y+" "+z+" | "+maxx+" "+maxy+" "+maxz+"]";
	}
	
	public boolean contains(Point3D p) {
		return p.getX()>=x && p.getY()>=y && p.getZ()>=z && p.getX()<=maxx && p.getY()<=maxy && p.getZ()<=maxz;
	}
	
	
	public Point3D center() {
		return new Point3D((x+maxx)/2, (y+maxy)/2, (z+maxz)/2);
	}
}
