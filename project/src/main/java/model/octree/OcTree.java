package model.octree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import model.Ray;
import model.objects.Entity3D;

public class OcTree {
	private OcTree[] childs;
	
	private List<Entity3D> objects;
	private AABB box;
	
	private OcTree(AABB box) {
		this.box=box;
		this.objects=new ArrayList<Entity3D>();
		childs=new OcTree[8];
	}
	
	public OcTree(Entity3D[] objects) {
		this.objects=new ArrayList<Entity3D>(Arrays.asList(objects));
		box=new AABB();
		for(Entity3D obj:objects) {
			box=AABB.merge(box, obj.getBoundingBox());
		}
		box=box.powerOf2();
		childs=new OcTree[8];
		contructTree(10);
	}
	
	private void contructTree(int depth) {
		if(depth<=0) return;
		
		double dwidth=box.getWidth()/2d;
		double dheight=box.getHeight()/2d;
		double ddepth=box.getDepth()/2d;
		childs[0]=new OcTree(new AABB(box.getX(), box.getY(), box.getZ(), box.getMaxx()-dwidth, box.getMaxy()-dheight, box.getMaxz()-ddepth));
		childs[1]=new OcTree(new AABB(box.getX()+dwidth, box.getY(), box.getZ(), box.getMaxx(), box.getMaxy()-dheight, box.getMaxz()-ddepth));
		childs[2]=new OcTree(new AABB(box.getX(), box.getY()+dheight, box.getZ(), box.getMaxx()-dwidth, box.getMaxy(), box.getMaxz()-ddepth));
		childs[3]=new OcTree(new AABB(box.getX()+dwidth, box.getY()+dheight, box.getZ(), box.getMaxx(), box.getMaxy(), box.getMaxz()-ddepth));
		childs[4]=new OcTree(new AABB(box.getX(), box.getY(), box.getZ()+ddepth, box.getMaxx()-dwidth, box.getMaxy()-dheight, box.getMaxz()));
		childs[5]=new OcTree(new AABB(box.getX()+dwidth, box.getY(), box.getZ()+ddepth, box.getMaxx(), box.getMaxy()-dheight, box.getMaxz()));
		childs[6]=new OcTree(new AABB(box.getX(), box.getY()+dheight, box.getZ()+ddepth, box.getMaxx()-dwidth, box.getMaxy(), box.getMaxz()));
		childs[7]=new OcTree(new AABB(box.getX()+dwidth, box.getY()+dheight, box.getZ()+ddepth, box.getMaxx(), box.getMaxy(), box.getMaxz()));
		
		Iterator<Entity3D> entities=objects.iterator();
		while(entities.hasNext()) {
			Entity3D en=entities.next();
			for(OcTree box:childs) {
				if(en.contained(box.box)) {
					box.objects.add(en);
					entities.remove();
					break;
				}
			}
		}
		
		
		for(int i=0;i<8;++i) {
			OcTree box=childs[i];
			if(box.objects.isEmpty()) {
				childs[i]=null;
			} else {
				box.contructTree(depth-1);
			}
		}
	}
	
	
	public List<Entity3D> getIntersectable(Ray ray) {
		List<Entity3D> list=new ArrayList<Entity3D>();
		getIntersectable(ray, list);
		return list;
	}
	
	private void getIntersectable(Ray ray, List<Entity3D> list) {
		if(ray.intersect(box)) {
			list.addAll(objects);
			for(OcTree ot:childs) {
				if(ot!=null) {
					ot.getIntersectable(ray, list);
				}
			}
		}
	}
	
	
}
