package model;

import java.util.HashMap;
import java.util.Map;

public class Point3D {
	private double x,y,z;

	public Point3D set(Point3D p) {
		set(p.x, p.y, p.z);
		return this;
	}
	
	public Point3D(double x, double y, double z) {
		set(x, y, z);
	}

	public Point3D set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
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
	
	public Point3D translate(Vector v) {
		this.x += v.getX();
		this.y += v.getY();
		this.z += v.getZ();
		return this;
	}
	
	public Point3D clone() {
		return new Point3D(x, y, z);
	}
	
	public String toString() {
		return "["+x+" "+y+" "+z+"]";
	}

	public double distance(Point3D v) {
		return new Vector(this, v).length();
	}
	
//	private static int counter = 0;
//	private static Map<StackTraceElement, Integer> stacks;
//	public static void newInstance() {
//		if (stacks == null) {
//			stacks = new HashMap<>();
//		}
//		StackTraceElement stack = Thread.currentThread().getStackTrace()[3];
//		stacks.put(stack, stacks.getOrDefault(stack, 0) + 1);
//		counter ++;
//		if (counter % 100000 == 0) {
//			System.out.println(counter+" "+stacks);
//		}
//	}
}
