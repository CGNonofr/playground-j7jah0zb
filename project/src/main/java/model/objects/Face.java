package model.objects;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.Arrays;

import model.Point3D;
import model.Ray;
import model.Vector;
import model.camera.Camera;
import model.octree.AABB;

public class Face implements Entity3D {
	private Vertex[] vertexes;
	private Vector normal;
	public Triangle[] decomposition;

	public Face(Vertex... vertexes) {
		this.vertexes = vertexes;
		normal = new Vector(vertexes[0].getPoint(), vertexes[1].getPoint())
				.cross(new Vector(vertexes[0].getPoint(), vertexes[2].getPoint()));
		decomposition = decompose();
	}

	public Vertex[] getVertexes() {
		return vertexes;
	}

	public Vector getNormal() {
		return normal;
	}

	private Triangle[] decompose() {
		Triangle[] triangles = new Triangle[vertexes.length - 2];
		for (int i = 0; i < vertexes.length - 2; ++i) {
			triangles[i] = new Triangle(vertexes[0], vertexes[i + 1], vertexes[i + 2]);
		}
		return triangles;
	}

	@Override
	public RayCollision intersect(Ray ray) {
		for (Triangle t : decomposition) {
			RayCollision collision = t.intersect(ray);
			if (collision != null)
				return collision;
		}
		return null;
	}

	public double distance(Point3D vertex) {
		double sum = 0;
		for (Vertex ver : vertexes) {
			sum += ver.getPoint().distance(vertex);
		}
		return sum / vertexes.length;
	}

	public AABB getBoundingBox() {
		double x = Double.MAX_VALUE, y = Double.MAX_VALUE, z = Double.MAX_VALUE;
		double maxx = Double.MIN_VALUE, maxy = Double.MIN_VALUE, maxz = Double.MIN_VALUE;

		for (Vertex ver : vertexes) {
			Point3D p = ver.getPoint();
			x = Math.min(x, p.getX());
			y = Math.min(y, p.getY());
			z = Math.min(z, p.getZ());
			maxx = Math.max(maxx, p.getX());
			maxy = Math.max(maxy, p.getY());
			maxz = Math.max(maxz, p.getZ());
		}
		return new AABB(x, y, z, maxx, maxy, maxz);
	}

	@Override
	public boolean contained(AABB box) {
		for (Vertex ver : vertexes) {
			if (!box.contains(ver.getPoint()))
				return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return Arrays.toString(vertexes);
	}

	@Override
	public Point3D center() {
		double minx = Double.MAX_VALUE, maxx = Double.MIN_VALUE;
		double miny = Double.MAX_VALUE, maxy = Double.MIN_VALUE;
		double minz = Double.MAX_VALUE, maxz = Double.MIN_VALUE;
		for (Vertex f : vertexes) {
			Point3D vcenter = f.getPoint();
			minx = Math.min(minx, vcenter.getX());
			maxx = Math.max(maxx, vcenter.getX());
			miny = Math.min(miny, vcenter.getY());
			maxy = Math.max(maxy, vcenter.getY());
			minz = Math.min(minz, vcenter.getZ());
			maxz = Math.max(maxz, vcenter.getZ());
		}
		return new Point3D((minx + maxx) / 2, (miny + maxy) / 2, (minz + maxz) / 2);
	}

	@Override
	public void rasterize(Camera c, Graphics2D g) {
		int[] xs = new int[getVertexes().length];
		int[] ys = new int[getVertexes().length];
		Color color = null;
		for (int i = 0; i < getVertexes().length; ++i) {
			Point2D p = c.project(getVertexes()[i].getPoint());
			double angle = getNormal().angle(c.getDirection());
			float cos = (float) Math.abs(Math.cos(angle));
			color = new Color(cos, cos, cos);
			xs[i] = (int) (p.getX() + 0.5);
			ys[i] = (int) (p.getY() + 0.5);
		}
		g.setColor(color);
		Polygon p = new Polygon(xs, ys, getVertexes().length);
		((Graphics2D) g).fill(p);
	}
}
