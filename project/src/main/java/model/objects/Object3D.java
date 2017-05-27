package model.objects;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import model.camera.Camera;
import model.octree.AABB;


public class Object3D {
	private Entity3D[] entities;
	private BufferedImage texture;
	private AABB boundingBox;
	public Object3D(Entity3D ... faces) {
		this.entities = faces;
	}
	
	public Object3D(BufferedImage texture, Entity3D ... faces) {
		this.entities = faces;
		this.texture=texture;
	}
	
	public BufferedImage getTexture() {
		return texture;
	}

	public Entity3D[] getEntities() {
		return entities;
	}
	
	public AABB getBoundingBox() {
		if(boundingBox!=null) {
			return boundingBox;
		}
		AABB[] boxes=new AABB[entities.length];
		for(int i=0;i<boxes.length;++i) {
			boxes[i]=entities[i].getBoundingBox();
		}
		return boundingBox=AABB.merge(boxes);
	}

	public void rasterize(Camera c, Graphics2D g) {
		for(Entity3D f:entities) {
			f.rasterize(c, g);
		}
	}

	public void setTexture(BufferedImage texture) {
		this.texture=texture;
	}
}
