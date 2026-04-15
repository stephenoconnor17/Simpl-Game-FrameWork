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

	public Stats setHealth(int health) {
		this.health = health;
		return this;
	}

	public Stats setMaxHealth(int maxHealth) {
		this.maxHealth = maxHealth;
		return this;
	}

	public Stats setAttack(int attack) {
		this.attack = attack;
		return this;
	}

	public Stats setDefense(int defense) {
		this.defense = defense;
		return this;
	}

	public Stats setLevel(int level) {
		this.level = level;
		return this;
	}

	public Stats setExperience(int experience) {
		this.experience = experience;
		return this;
	}
}
