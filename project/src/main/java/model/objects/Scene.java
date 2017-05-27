package model.objects;

import java.util.ArrayList;
import java.util.List;

public class Scene {
	private List<Object3D> objects;

	public Scene() {
		this.objects = new ArrayList<Object3D>();
	}

	public Object3D[] getObjects() {
		return objects.toArray(new Object3D[objects.size()]);
	}
	
	public void addObject(Object3D o) {
		objects.add(o);
	}
}
