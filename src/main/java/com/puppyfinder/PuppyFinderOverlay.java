package com.puppyfinder;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class PuppyFinderOverlay extends OverlayPanel
{
	private static final int PANEL_WIDTH = 300;

	private final PuppyFinder plugin;
	private final PuppyFinderConfig config;

	@Inject
	PuppyFinderOverlay(PuppyFinder plugin, PuppyFinderConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		panelComponent.setPreferredSize(new Dimension(PANEL_WIDTH, 0));
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.showInfoBox())
		{
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Puppy Finder")
			.color(Color.YELLOW)
			.build());

		int remaining = 0;
		for (Puppy puppy : Puppy.values())
		{
			if (plugin.isFound(puppy))
			{
				continue;
			}

			remaining++;
			panelComponent.getChildren().add(LineComponent.builder()
				.left(puppy.getDisplayName() + " - " + puppy.getLocation())
				.build());
		}

		if (remaining == 0)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left("All puppies found!")
				.leftColor(Color.GREEN)
				.build());
		}

		return super.render(graphics);
	}
}
