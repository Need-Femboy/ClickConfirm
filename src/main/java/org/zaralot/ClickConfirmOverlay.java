package org.zaralot;

import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

public class ClickConfirmOverlay extends Overlay
{
	private final Client client;
	private final ClickConfirm plugin;
	private final ClickConfirmConfig config;
	
	@Inject
	public ClickConfirmOverlay(Client client, ClickConfirm plugin, ClickConfirmConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
	}
	
	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.enableOverlay())
		{
			return null;
		}
		long currentTime = System.currentTimeMillis();
		plugin.getClickedPoints().removeIf(x -> currentTime - x.getTimeClicked() >= 601);
		for (ClickedPoint clickedPoint : plugin.getClickedPoints())
		{
			long timeElapsed = currentTime - clickedPoint.getTimeClicked();
			float progress = Math.min(1.0f, (float) timeElapsed / 600);
			
			int alpha = (int) ((1.0f - progress) * 255);
			if (alpha < 0) alpha = 0;
			
			Point canvasPoint = clickedPoint.getCanvasPoint();
			
			if (canvasPoint != null)
			{
				graphics.setColor(new Color(255, 0, 0, alpha));
				graphics.setStroke(new BasicStroke(2));
				
				int circleSize = (int) (24 + 12 * progress);
				graphics.drawOval(canvasPoint.getX() - circleSize / 2, canvasPoint.getY() - circleSize / 2, circleSize, circleSize);
			}
		}
		return null;
	}
}
