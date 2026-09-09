package com.puppyfinder;

import com.google.inject.Provides;
import java.awt.Color;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.npcoverlay.HighlightedNpc;
import net.runelite.client.game.npcoverlay.NpcOverlayService;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Puppy Finder",
	description = "Highlights lost puppies and shows where to find undiscovered breeds",
	tags = {"dog", "puppy", "highlight", "hint", "quest"}
)
public class PuppyFinder extends Plugin
{
	private static final Color HIGHLIGHT_COLOR = Color.YELLOW;
	private static final Color HIGHLIGHT_FILL_COLOR = new Color(255, 255, 0, 40);

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private PuppyFinderConfig config;

	@Inject
	private PuppyFinderOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NpcOverlayService npcOverlayService;

	private final Set<NPC> loadedPuppies = new HashSet<>();
	private final Function<NPC, HighlightedNpc> highlighter = this::highlightNpc;
	private NPC hintArrowNpc;

	@Override
	protected void startUp()
	{
		log.debug("Puppy Finder started");
		overlayManager.add(overlay);
		npcOverlayService.registerHighlighter(highlighter);
		clientThread.invoke(this::rebuildLoadedPuppies);
	}

	@Override
	protected void shutDown()
	{
		log.debug("Puppy Finder stopped");
		overlayManager.remove(overlay);
		npcOverlayService.unregisterHighlighter(highlighter);
		clearOwnHintArrow();
		loadedPuppies.clear();
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		NPC npc = event.getNpc();
		if (Puppy.fromNpcId(npc.getId()) != null)
		{
			loadedPuppies.add(npc);
			refreshHintArrow();
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		if (loadedPuppies.remove(event.getNpc()))
		{
			refreshHintArrow();
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		GameState gameState = event.getGameState();
		if (gameState == GameState.LOGIN_SCREEN || gameState == GameState.HOPPING)
		{
			clearOwnHintArrow();
			loadedPuppies.clear();
		}
		else if (gameState == GameState.LOGGED_IN)
		{
			rebuildLoadedPuppies();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!PuppyFinderConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}

		npcOverlayService.rebuild();
		refreshHintArrow();
	}

	private void rebuildLoadedPuppies()
	{
		loadedPuppies.clear();

		if (client.getGameState() != GameState.LOGGED_IN)
		{
			clearOwnHintArrow();
			return;
		}

		for (NPC npc : client.getNpcs())
		{
			if (Puppy.fromNpcId(npc.getId()) != null)
			{
				loadedPuppies.add(npc);
			}
		}

		refreshHintArrow();
	}

	private HighlightedNpc highlightNpc(NPC npc)
	{
		Puppy puppy = Puppy.fromNpcId(npc.getId());
		if (puppy == null || config.isFound(puppy))
		{
			return null;
		}

		return HighlightedNpc.builder()
			.npc(npc)
			.highlightColor(HIGHLIGHT_COLOR)
			.fillColor(HIGHLIGHT_FILL_COLOR)
			.hull(true)
			.tile(true)
			.borderWidth(2.0f)
			.build();
	}

	private void refreshHintArrow()
	{
		NPC target = findHintArrowTarget();

		if (target == hintArrowNpc && client.getHintArrowNpc() == target)
		{
			return;
		}

		clearOwnHintArrow();

		if (target != null)
		{
			client.setHintArrow(target);
			hintArrowNpc = target;
		}
	}

	private NPC findHintArrowTarget()
	{
		for (Puppy puppy : Puppy.values())
		{
			if (config.isFound(puppy))
			{
				continue;
			}

			for (NPC npc : loadedPuppies)
			{
				if (npc.getId() == puppy.getNpcId())
				{
					return npc;
				}
			}
		}

		return null;
	}

	private void clearOwnHintArrow()
	{
		if (hintArrowNpc != null && client.getHintArrowNpc() == hintArrowNpc)
		{
			client.clearHintArrow();
		}

		hintArrowNpc = null;
	}

	@Provides
	PuppyFinderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PuppyFinderConfig.class);
	}
}
