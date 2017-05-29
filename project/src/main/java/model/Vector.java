package model;

import java.util.HashMap;
import java.util.Map;

public class Vector {
	public static final Vector VERTICAL_NORMAL = new Vector(0, 1, 0);
	private double x, y, z;

	
	public Vector set(Vector v) {
		set(v.x, v.y, v.z);
		return this;
	}
	
	public Vector(double x, double y, double z) {
		set(x, y, z);
	}
	
	public Vector set(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return this;
	}
	
	public Vector(Point3D a, Point3D b) {
		set(a, b);
	}
	
	public Vector set(Point3D a, Point3D b) {
		this.x = b.getX() - a.getX();
		this.y = b.getY() - a.getY();
		this.z = b.getZ() - a.getZ();
		return this;
	}

	public String toString() {
		return x + " " + y + " " + z;
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
	
	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public double dot(Vector b) {
		return x * b.x + y * b.y + z * b.z;
	}

	public double angle(Vector b) {
		return Math.atan2(clone().cross(b).length(), dot(b));
	}


	public final double fullAngle(Vector v1) {
		double angle = angle(v1);
		if (clone().cross(v1).dot(Vector.VERTICAL_NORMAL) < 0)
			angle = Math.PI * 2 - angle;
		if (angle >= Math.PI * 2)
			angle = 0;
		return angle;
	}
	
	/*
	 * Not immutable methods
	 */


	public Vector mult(double m) {
		this.x *= m;
		this.y *= m;
		this.z *= m;
		return this;
	}
	
	public Vector normalize() {
		double length = length();
		x /= length;
		y /= length;
		z /= length;
		return this;
	}

	public Vector cross(Vector b) {
		double x = this.y * b.z - this.z * b.y;
		double y = this.z * b.x - this.x * b.z;
		double z = this.x * b.y - this.y * b.x;
		setX(x);
		setY(y);
		setZ(z);
		return this;
	}
	
	public Vector reflectInPlace(Vector v) {
		normalize().mult(-dot(v)).mult(2).sum(v);
		return this;
	}
	
	public Vector reflect(Vector v) {
		return clone().reflectInPlace(v);
	}

	public Vector sum(Vector v) {
		this.x += v.x;
		this.y += v.y;
		this.z += v.z;
		return this;
	}

	public Vector add(Vector vector) {
		return sum(vector);
	}

	public Vector sub(Vector v) {
		this.x -= v.x;
		this.y -= v.y;
		this.z -= v.z;
		return this;
	}

	public Vector rotateY(double angle) {
		double x = this.x * Math.cos(angle) - this.z * Math.sin(angle);
		double z = this.x * Math.sin(angle) + this.z * Math.cos(angle);
		setX(x);
		setZ(z);
		return this;
	}

	public Vector rotateXZ(double angle) {
		Vector axe = clone().cross(Vector.VERTICAL_NORMAL);
		Vector vect = clone()
			.mult(Math.cos(angle))
			.sum(
				axe.clone().mult(dot(axe) * (1 - Math.cos(angle)))
			)
			.sum(axe.clone().cross(this).mult(Math.sin(angle)));
		return vect;
	}

	public Vector rotate(Vector axis, double ang) {
		RotationMatrix.fromAxisAngle(axis, ang).apply(this);
		return this;
	}

	public Vector clone() {
		return new Vector(x, y, z);
	}
	
}
