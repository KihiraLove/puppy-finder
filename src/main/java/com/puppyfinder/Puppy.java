package com.puppyfinder;

import net.runelite.api.gameval.NpcID;

enum Puppy
{
	CHIHUAHUA(NpcID.CHIHUAHUA_WANDER, "Chihuahua", "Eastern part of Uzer Oasis", "chihuahuaFound"),
	BORDER_COLLIE(NpcID.COLLIE_WANDER, "Border Collie", "North of Crafting Guild", "borderCollieFound"),
	CORGI(NpcID.CORGI_WANDER, "Corgi", "Around Probita's shop in Ardougne", "corgiFound"),
	GREYHOUND(NpcID.GREYHOUND_WANDER, "Greyhound", "South of Woodcutting Guild", "greyhoundFound"),
	HUSKY(NpcID.HUSKY_WANDER, "Husky", "Fishing Hamlet east of Wintertodt", "huskyFound"),
	SAMOYED(NpcID.SAMOYED_WANDER, "Samoyed", "Hardwood groove in Tai Bwo Wannai", "samoyedFound"),
	BERNESE_MOUNTAIN_DOG(NpcID.SHEPARD_WANDER, "Bernese Mountain Dog", "Between Relekka and Keldagrin entrance", "berneseMountainDogFound"),
	SHIBA(NpcID.SHIBA_WANDER, "Shiba", "Avium Savannah Pyre foxes", "shibaFound"),
	YORKIE(NpcID.YORKIE_WANDER, "Yorkie", "The Great Conch west of marketplace", "yorkieFound");

	private final int npcId;
	private final String displayName;
	private final String location;
	private final String configKey;

	Puppy(int npcId, String displayName, String location, String configKey)
	{
		this.npcId = npcId;
		this.displayName = displayName;
		this.location = location;
		this.configKey = configKey;
	}

	int getNpcId()
	{
		return npcId;
	}

	String getDisplayName()
	{
		return displayName;
	}

	String getLocation()
	{
		return location;
	}

	String getConfigKey()
	{
		return configKey;
	}

	boolean isFound(PuppyFinderConfig config)
	{
		switch (this)
		{
			case CHIHUAHUA:
				return config.chihuahuaFound();
			case BORDER_COLLIE:
				return config.borderCollieFound();
			case CORGI:
				return config.corgiFound();
			case GREYHOUND:
				return config.greyhoundFound();
			case HUSKY:
				return config.huskyFound();
			case SAMOYED:
				return config.samoyedFound();
			case BERNESE_MOUNTAIN_DOG:
				return config.berneseMountainDogFound();
			case SHIBA:
				return config.shibaFound();
			case YORKIE:
				return config.yorkieFound();
			default:
				return false;
		}
	}

	boolean matchesRescueMessage(String message)
	{
		return ("The " + displayName + " heads to the dog shelter.").equalsIgnoreCase(message);
	}

	static Puppy fromNpcId(int npcId)
	{
		for (Puppy puppy : values())
		{
			if (puppy.npcId == npcId)
			{
				return puppy;
			}
		}

		return null;
	}

	static Puppy fromConfigKey(String configKey)
	{
		for (Puppy puppy : values())
		{
			if (puppy.configKey.equals(configKey))
			{
				return puppy;
			}
		}

		return null;
	}
}
