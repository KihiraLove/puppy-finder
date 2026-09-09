package com.puppyfinder;

enum Puppy
{
	CHIHUAHUA(16398, "Chihuahua", "Uzer Oasis", "chihuahuaFound"),
	BORDER_COLLIE(16400, "Border Collie", "North of Crafting Guild", "borderCollieFound"),
	CORGI(16402, "Corgi", "Probita's shop in Ardougne", "corgiFound"),
	GREYHOUND(16404, "Greyhound", "South of Woodcutting Guild", "greyhoundFound"),
	HUSKY(16412, "Husky", "Fishing Hamlet east of Wintertodt", "huskyFound"),
	SAMOYED(16406, "Samoyed", "Hardwood groove in Tai Bwo Wannai", "samoyedFound"),
	BERNESE_MOUNTAIN_DOG(16408, "Bernese Mountain Dog", "East of Relekka, West of Keldagrin entrance", "berneseMountainDogFound"),
	SHIBA(16410, "Shiba", "Avium Savannah, between Pyre foxes and Hill giants", "shibaFound"),
	YORKIE(16414, "Yorkie", "The Great Conch, west of marketplace", "yorkieFound");

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
