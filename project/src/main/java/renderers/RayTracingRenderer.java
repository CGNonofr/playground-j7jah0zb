package renderers;

import java.awt.Color;
import java.awt.image.BufferedImage;

import model.Ray;
import model.Vector;
import model.objects.Entity3D;
import model.objects.Object3D;
import model.objects.RayCollision;
import model.objects.Scene;
import model.octree.OcTree;
import tools.MixColor;

public class RayTracingRenderer extends AbstractRayTracingRenderer {
	private OcTree[] trees;
	private BufferedImage[] textures;

	@Override
	public void prepare(Scene scene) {
		Object3D[] objects = scene.getObjects();
		trees = new OcTree[objects.length];
		textures = new BufferedImage[objects.length];
		for (int i = objects.length - 1; i >= 0; --i) {
			trees[i] = new OcTree(objects[i].getEntities());
			textures[i] = objects[i].getTexture();
		}
	}

	@Override
	public Color throwRay(Ray ray, int depth) {
		if (depth < 0)
			return null;
		RayCollision collision = null;
		double lastDistance = Double.MAX_VALUE;

		BufferedImage texture = null;
		for (int i = 0; i < trees.length; ++i) {
			OcTree tree = trees[i];
			for (Entity3D f : tree.getIntersectable(ray)) {
				RayCollision temp = f.intersect(ray);
				if (temp != null) {
					double distance = temp.getHitDistance();
					if (distance < lastDistance) {
						lastDistance = distance;
						collision = temp;
						texture = textures[i];
					}
				}
			}
		}
		if (collision != null) {
			Color col = null;
			if (texture != null && collision.getTextureCoordinates() != null) {
				col = collision.getTextureCoordinates().getFrom(texture);
			} else {
				double angle = collision.getNormal().angle(ray.getDirection());
				float cos = (float) Math.abs(Math.cos(angle));
				col = new Color(cos, cos, cos);
			}

			if (collision.getTransparence() > 0) {
				Ray newRay = new Ray(collision.getPoint().translate(
						ray.getDirection().mult(0.0001)), ray.getDirection());
				Color c = throwRay(newRay, depth - 1);
				if (c != null) {
					col = MixColor.mixColor(col, c,
							1 - collision.getTransparence());
				}
			}
			if (collision.getReflection() > 0) {
				Vector newDir = collision.getNormal().reflect(
						ray.getDirection());
				Ray newRay = new Ray(collision.getPoint().translate(
						newDir.mult(0.0001)), newDir);
				Color c = throwRay(newRay, depth - 1);
				if (c != null) {
					col = MixColor.mixColor(col, c,
							1 - collision.getReflection());
				}
			}
			return col;
		} else {
			return new Color(220, 220, 255);
		}
	}

}
