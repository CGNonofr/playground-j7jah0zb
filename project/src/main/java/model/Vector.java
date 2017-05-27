package model;


public class Vector {
	public static final Vector VERTICAL_NORMAL = new Vector(0, 1, 0);
	private double x, y, z;

	private Vector normal;
	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector(Point3D a, Point3D b) {
		this.x = b.getX() - a.getX();
		this.y = b.getY() - a.getY();
		this.z = b.getZ() - a.getZ();
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

	public double length() {
		return Math.sqrt(x * x + y * y + z * z);
	}

	public Vector normalize() {
		if(normal!=null) {
			return normal;
		}
		double length = length();
		Vector v=new Vector(x / length, y / length, z / length);
		return normal=v.normal=v;
	}

	public Vector mult(double m) {
		return new Vector(x * m, y * m, z * m);
	}

	public double dot(Vector b) {
		return x * b.x + y * b.y + z * b.z;
	}

	public Vector cross(Vector b) {
		return new Vector(y * b.z - z * b.y, z * b.x - x * b.z, x * b.y - y
				* b.x);
	}

	public double angle(Vector b) {
		return Math.atan2(cross(b).length(), dot(b));
	}
	
	public Vector add(Vector v) {
		return new Vector(x + v.x, y + v.y, z + v.z);
	}

	public Vector reflect(Vector v) {
		Vector projection = normalize().mult(-normalize().dot(v));
		return projection.mult(2).sum(v);
	}

	public Vector sum(Vector v) {
		return new Vector(v.x + x, v.y + y, v.z + z);
	}

	public Vector sub(Vector v) {
		return new Vector(-v.x + x, -v.y + y, -v.z + z);
	}

	public Vector rotateY(double angle) {
		return new Vector(x * Math.cos(angle) - z * Math.sin(angle), y, x
				* Math.sin(angle) + z * Math.cos(angle));
	}

	public Vector rotateXZ(double angle) {
		Vector axe = cross(Vector.VERTICAL_NORMAL);
		Vector vect = mult(Math.cos(angle)).add(
				axe.mult(dot(axe) * (1 - Math.cos(angle)))).add(
				axe.cross(this).mult(Math.sin(angle)));
		return vect;
	}

	public final double fullAngle(Vector v1) {
		double angle = angle(v1);
		Vector cross = cross(v1);
		if (cross.dot(Vector.VERTICAL_NORMAL) < 0)
			angle = Math.PI * 2 - angle;
		if (angle >= Math.PI * 2)
			angle = 0;
		return angle;
	}

	public Vector rotate(Vector axis, double ang) {
		return RotationMatrix.fromAxisAngle(axis, ang).apply(this);
	}

}
