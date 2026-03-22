package entities.components.rpgsystem;

import entities.components.Component;

/**
 * Basic RPG stats for an entity.
 */
public class Stats extends Component {
	/** Current hit points. */
	public int health = 100;
	/** Maximum hit points. */
	public int maxHealth = 100;
	/** Damage dealt. */
	public int attack = 10;
	/** Damage reduction. */
	public int defense = 5;
	/** Current level. */
	public int level = 1;
	/** Accumulated XP. */
	public int experience = 0;
}
