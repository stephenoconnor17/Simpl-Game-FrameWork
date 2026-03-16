package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityManager {
	
	List<Entity> entities;
	Map<Integer, Entity> idLookup;
	Map<String, Entity> tagLookup;
	int nextId = 0;
	
	public EntityManager(){
		entities = new ArrayList<>();
		idLookup = new HashMap<Integer, Entity>();
		tagLookup = new HashMap<String, Entity>();
	}
	
	public Entity createEntity(String name) {
		Entity temp = new Entity(nextId,name);
		
		entities.add(temp);
		idLookup.put(nextId, temp);
		nextId++;
		
		return temp;
	}
	
	public Entity createEntity(Entity e) {
		Entity temp = e;
		temp.setId(nextId);
		
		entities.add(temp);
		idLookup.put(nextId, temp);
		nextId++;
		
		return temp;
	}
	
	public Entity getEntity(String tag) {
		return tagLookup.get(tag);
	}
	
	public List<Entity> getEntities(){
		return this.entities;
	}
	
	public void removeEntity(Entity e) {
		entities.remove(e);
		idLookup.remove(e.getId());
	}
	
	public void addEntity(Entity e) {
		this.entities.add(e);
		if (e.getEntityName() != null) {
			tagLookup.put(e.getEntityName(), e);
		}
	}
	
}
