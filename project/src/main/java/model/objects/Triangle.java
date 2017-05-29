package model.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.function.Supplier;

import model.Point3D;
import model.Ray;
import model.TextureCoordinates;
import model.Vector;
import model.camera.Camera;
import model.octree.AABB;

public class Triangle implements Entity3D {
	private static ThreadLocal<Vector> tmpVectorFactory = ThreadLocal.withInitial(new Supplier<Vector>() {
		@Override
		public Vector get() {
			return new Vector(0, 0, 0);
		}
	});
	private static ThreadLocal<Point3D> tmpPointFactory = ThreadLocal.withInitial(new Supplier<Point3D>() {
		@Override
		public Point3D get() {
			return new Point3D(0, 0, 0);
		}
	});
	
	private Vertex p1, p2, p3;
	public static final float SMALL_NUM = 0.00000001f;

	public Vector u, v, normal;
	public Triangle(Vertex p1, Vertex p2, Vertex p3) {
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
		normal=(u=new Vector(p1.getPoint(), p2.getPoint())).clone().cross((v=new Vector(p1.getPoint(), p3.getPoint()))).normalize();
	}


	@Override
	public RayCollision intersect(Ray ray) {
		if (normal.length() == 0) {
			return null;
		}
		
		Vector tmpVector = tmpVectorFactory.get();
		Point3D tmpPoint = tmpPointFactory.get();

		Vector w = tmpVector.set(p1.getPoint(), ray.getOrigin());

		double a = -normal.dot(w);
		double b = normal.dot(ray.getDirection());

		if (Math.abs(b) < SMALL_NUM) {
			return null;
		}

		double r = a / b;
		if (r < 0d) {
			return null;
		}
		
		

		Point3D pt = tmpPoint.set(ray.getOrigin()).translate(tmpVector.set(ray.getDirection()).mult(r));
		double uu = u.dot(u);
		double uv = u.dot(v);
		double vv = v.dot(v);

		w = tmpVector.set(p1.getPoint(), pt);

		double wu = w.dot(u);
		double wv = w.dot(v);

		double D = uv * uv - uu * vv;
		double s = (uv * wv - vv * wu) / D;
		if (s < 0.0 || s > 1.0) {
			return null;
		}
		double t = (uv * wu - uu * wv) / D;
		if (t < 0.0 || (s + t) > 1.0) {
			return null;
		}
		
		TextureCoordinates texture=null;
		if(p1.getTexture() != null && p2.getTexture() != null && p3.getTexture() != null) {
			texture = new TextureCoordinates((p1.getTexture().getX()*(1-s)+p2.getTexture().getX()*s)*(1-t)+p3.getTexture().getX()*t,(p1.getTexture().getY()*(1-s)+p2.getTexture().getY()*s)*(1-t)+p3.getTexture().getY()*t);
		}
		Vector normal=null;
		if(p1.getNormal() != null && p2.getNormal() != null && p3.getNormal() != null) {
			normal = new Vector((p1.getNormal().getX()*(1-s)+p2.getNormal().getX()*s)*(1-t)+p3.getNormal().getX()*t,(p1.getNormal().getY()*(1-s)+p2.getNormal().getY()*s)*(1-t)+p3.getNormal().getY()*t,(p1.getNormal().getZ()*(1-s)+p2.getNormal().getZ()*s)*(1-t)+p3.getNormal().getZ()*t);
		} else {
			normal = this.normal;
		}
		return new RayCollision(r, pt, normal, texture, 0, 0.5);
	}


	@Override
	public AABB getBoundingBox() {
		double x=Double.MAX_VALUE, y=Double.MAX_VALUE, z=Double.MAX_VALUE;
		double maxx=Double.MIN_VALUE, maxy=Double.MIN_VALUE, maxz=Double.MIN_VALUE;
		
		for(Point3D p:new Point3D[]{p1.getPoint(), p2.getPoint(), p3.getPoint()}) {
			x=Math.min(x, p.getX());
			y=Math.min(y, p.getY());
			z=Math.min(z, p.getZ());
			maxx=Math.max(maxx, p.getX());
			maxy=Math.max(maxy, p.getY());
			maxz=Math.max(maxz, p.getZ());
		}
		return new AABB(x, y, z, maxx, maxy, maxz);
	}


	@Override
	public boolean contained(AABB box) {
		return box.contains(p1.getPoint()) && box.contains(p2.getPoint()) && box.contains(p3.getPoint());
	}


	@Override
	public Point3D center() {
		return new Point3D((p1.getPoint().getX()+p2.getPoint().getX()+p3.getPoint().getX())/3,(p1.getPoint().getY()+p2.getPoint().getY()+p3.getPoint().getY())/3,(p1.getPoint().getZ()+p2.getPoint().getZ()+p3.getPoint().getZ())/3);
	}
	
	@Override
	public void rasterize(Camera c, Graphics2D g) {
		int[] xs=new int[3];
		int[] ys=new int[3];
		Point2D p=c.project(p1.getPoint());
		xs[0]=(int)(p.getX()+0.5);
		ys[0]=(int)(p.getY()+0.5);
		p=c.project(p2.getPoint());
		xs[0]=(int)(p.getX()+0.5);
		ys[0]=(int)(p.getY()+0.5);
		p=c.project(p3.getPoint());
		xs[0]=(int)(p.getX()+0.5);
		ys[0]=(int)(p.getY()+0.5);
			
		double angle=normal.angle(c.getDirection());
		float cos=(float) Math.abs(Math.cos(angle));
		g.setColor(new Color(cos, cos, cos));
		g.fill(new Polygon(xs, ys, 3));
	}
}
