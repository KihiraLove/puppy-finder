package com.puppyfinder;

import com.google.inject.Provides;
import java.awt.Color;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
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
import net.runelite.client.util.Text;

@Slf4j
@PluginDescriptor(
	name = "Puppy Finder",
	description = "Helps finding lost puppies after A Ruff Situation",
	tags = {"dog", "puppy", "highlight", "hint"}
)
public class PuppyFinder extends Plugin
{
	private static final Color HIGHLIGHT_COLOR = Color.YELLOW;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private PuppyFinderConfig config;

	@Inject
	private ConfigManager configManager;

	@Inject
	private PuppyFinderOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private NpcOverlayService npcOverlayService;

	private final Set<NPC> loadedPuppies = new HashSet<>();
	private final Set<Puppy> sessionFoundPuppies = EnumSet.noneOf(Puppy.class);
	private final Function<NPC, HighlightedNpc> highlighter = this::highlightNpc;
	private NPC hintArrowNpc;

	@Override
	protected void startUp()
	{
		log.debug("Puppy Finder started");
		sessionFoundPuppies.clear();
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
		sessionFoundPuppies.clear();
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
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.MESBOX
			&& event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		String message = Text.removeTags(event.getMessage()).trim();
		for (Puppy puppy : Puppy.values())
		{
			if (puppy.matchesRescueMessage(message))
			{
				markPuppyFound(puppy);
				return;
			}
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

		Puppy changedPuppy = Puppy.fromConfigKey(event.getKey());
		if (changedPuppy != null)
		{
			if (Boolean.parseBoolean(event.getNewValue()))
			{
				sessionFoundPuppies.add(changedPuppy);
			}
			else
			{
				sessionFoundPuppies.remove(changedPuppy);
			}
		}

		npcOverlayService.rebuild();
		clientThread.invoke(this::refreshHintArrow);
	}

	private void markPuppyFound(Puppy puppy)
	{
		if (isFound(puppy))
		{
			return;
		}

		sessionFoundPuppies.add(puppy);

		try
		{
			configManager.setConfiguration(PuppyFinderConfig.GROUP, puppy.getConfigKey(), true);
		}
		catch (RuntimeException ex)
		{
			log.debug("Unable to persist rescued puppy {} to config", puppy.getDisplayName(), ex);
		}

		npcOverlayService.rebuild();
		refreshHintArrow();
	}

	boolean isFound(Puppy puppy)
	{
		return sessionFoundPuppies.contains(puppy) || puppy.isFound(config);
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
		if (puppy == null || isFound(puppy))
		{
			return null;
		}

		return HighlightedNpc.builder()
			.npc(npc)
			.highlightColor(HIGHLIGHT_COLOR)
			.tile(true)
			.borderWidth(1.0f)
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
			if (isFound(puppy))
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
