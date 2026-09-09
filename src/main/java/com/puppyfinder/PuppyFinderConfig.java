package com.puppyfinder;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup(PuppyFinderConfig.GROUP)
public interface PuppyFinderConfig extends Config
{
	String GROUP = "puppy-finder";

	@ConfigSection(
		name = "Found puppies",
		description = "Mark puppy breeds that you have already discovered",
		position = 1
	)
	String FOUND_PUPPIES_SECTION = "foundPuppies";

	@ConfigItem(
		keyName = "showInfoBox",
		name = "Show info box",
		description = "Show the remaining puppy locations on screen",
		position = 0
	)
	default boolean showInfoBox()
	{
		return true;
	}

	@ConfigItem(
		keyName = "chihuahuaFound",
		name = "Chihuahua",
		description = "Mark the Chihuahua as found",
		section = FOUND_PUPPIES_SECTION,
		position = 0
	)
	default boolean chihuahuaFound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "borderCollieFound",
		name = "Border Collie",
		description = "Mark the Border Collie as found",
		section = FOUND_PUPPIES_SECTION,
		position = 1
	)
	default boolean borderCollieFound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "corgiFound",
		name = "Corgi",
		description = "Mark the Corgi as found",
		section = FOUND_PUPPIES_SECTION,
		position = 2
	)
	default boolean corgiFound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "greyhoundFound",
		name = "Greyhound",
		description = "Mark the Greyhound as found",
		section = FOUND_PUPPIES_SECTION,
		position = 3
	)
	default boolean greyhoundFound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "huskyFound",
		name = "Husky",
		description = "Mark the Husky as found",
		section = FOUND_PUPPIES_SECTION,
		position = 4
	)
	default boolean huskyFound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "samoyedFound",
		name = "Samoyed",
		description = "Mark the Samoyed as found",
		section = FOUND_PUPPIES_SECTION,
		position = 5
	)
	default boolean samoyedFound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "berneseMountainDogFound",
		name = "Bernese Mountain Dog",
		description = "Mark the Bernese Mountain Dog as found",
		section = FOUND_PUPPIES_SECTION,
		position = 6
	)
	default boolean berneseMountainDogFound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "shibaFound",
		name = "Shiba",
		description = "Mark the Shiba as found",
		section = FOUND_PUPPIES_SECTION,
		position = 7
	)
	default boolean shibaFound()
	{
		return false;
	}

	@ConfigItem(
		keyName = "yorkieFound",
		name = "Yorkie",
		description = "Mark the Yorkie as found",
		section = FOUND_PUPPIES_SECTION,
		position = 8
	)
	default boolean yorkieFound()
	{
		return false;
	}
}
