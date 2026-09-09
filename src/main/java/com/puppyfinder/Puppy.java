package com.puppyfinder;

enum Puppy
{
	CHIHUAHUA(16398, "Chihuahua", "Uzer Oasis"),
	BORDER_COLLIE(16400, "Border Collie", "North of Crafting Guild"),
	CORGI(16402, "Corgi", "Probita's shop in Ardougne"),
	GREYHOUND(16404, "Greyhound", "South of Woodcutting Guild"),
	HUSKY(16412, "Husky", "Fishing Hamlet east of Wintertodt"),
	SAMOYED(16406, "Samoyed", "Hardwood groove in Tai Bwo Wannai"),
	BERNESE_MOUNTAIN_DOG(16408, "Bernese Mountain Dog", "East of Relekka, West of Keldagrin entrance"),
	SHIBA(16410, "Shiba", "Avium Savannah, between Pyre foxes and Hill giants"),
	YORKIE(16414, "Yorkie", "The Great Conch, west of marketplace");

	private final int npcId;
	private final String displayName;
	private final String location;

	Puppy(int npcId, String displayName, String location)
	{
		this.npcId = npcId;
		this.displayName = displayName;
		this.location = location;
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
}
