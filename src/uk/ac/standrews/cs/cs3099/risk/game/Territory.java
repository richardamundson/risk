package uk.ac.standrews.cs.cs3099.risk.game;

import java.util.Set;
import java.util.HashSet;

/**
 * Territory Class
 * Each little piece of land has these properties
 */
public class Territory {

	private int id;
	private Continent continent;
	private Set<Territory> links;
	private Player owner = null;

	private int armies;


	public Territory(int id, Continent continent)
	{
		this.id = id;
		this.continent = continent;
		this.links = new HashSet<Territory>();

		this.armies = 0;
	}

	public int getId()
	{
		return id;
	}

	public int getArmies()
	{
		return armies;
	}

	public void setArmies(int armies)
	{
		this.armies = armies;
	}

	public void addLink(Territory t)
	{
		links.add(t);
	}

	public boolean isLinkedTo(Territory t)
	{
		return links.contains(t);
	}

	public Set<Territory> getLinkedTerritories()
	{
		return links;
	}

	public boolean addArmies(int numberOfArmies)
	{
		if (numberOfArmies < 0) {
			return false;
		} else {
			armies = armies + numberOfArmies;
			return true;
		}
	}

	public boolean removeArmies(int numberOfArmies)
	{
		if (numberOfArmies < 0 || numberOfArmies >= armies) {
			return false;
		} else {
			armies = armies - numberOfArmies;
			return true;
		}
	}

	public Player getOwner()
	{
		return owner;
	}

	public boolean isClaimed()
	{
		return owner != null;
	}

	public void claim(Player player)
	{
		owner = player;
	}
}
