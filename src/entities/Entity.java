package entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import entities.components.Component;
import exceptions.DuplicateComponentException;

public abstract class Entity{

	// init
	private int id;

	private String entityName;

	private final Map<Class<? extends Component>, Component> components = new HashMap<>();// final here to prevent
																							// re-assignment.

	// constructor - no default constructor for this, must have ID and name.
	public Entity(int id, String entityName) {

		this.id = id;
		this.entityName = entityName;

	}

	// TO BREAK DOWN THIS CODE

	public int getId() {
		return id;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}

	// Map takes in a Generic Class<? extends component> which allows for a Class's
	// .class to be passed for components.
	// we then point it to the actual component!

	// Declare a generic type T which will extend Component, so then we can take it
	// in, place it according to its actual class.
	public <T extends Component> void add(T component) {
		if(components.containsKey(component.getClass())) {
			//COMPONENT ALREADY EXISTS ONLY ONE ALLOWED PER ENTITY
			throw new DuplicateComponentException("Component Duplicate Error : " + component.getClass());
		}else {
			components.put(component.getClass(), component);
		}
	}

	// same concept as above for here. Except we are casting the returned component
	// to type T extends Component.
	// Because if the return type is component, return genericComponent will throw
	// an error.
	// it would require casting and this bypasses and secures that.
	public <T extends Component> T get(Class<T> type) {
		return type.cast(components.get(type));
	}

	// Simply checks if it contains a parameter of component.class , so to check if
	// genericComponent is there
	// has(genericComponent.class); will be the valid input.
	public boolean has(Class<? extends Component> type) {
		return components.containsKey(type);
	}

	// Same parameters as the has method above, checks for component.class, then
	// removes component
	// by using it's .class as key
	public void remove(Class<? extends Component> type) {
		components.remove(type);
	}
}
