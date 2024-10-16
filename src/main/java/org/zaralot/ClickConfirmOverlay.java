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
import java.awt.Stroke;

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
		int minSize = config.circleMinSize();
		int maxSize = config.circleMaxSize();
		Color color = config.circleColour();
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();
		int baseAlpha = color.getAlpha();
		Stroke stroke = new BasicStroke(2);
		
		graphics.setStroke(stroke);
		
		plugin.getClickedPoints().removeIf(x -> currentTime - x.getTimeClicked() > 600);
		for (ClickedPoint clickedPoint : plugin.getClickedPoints())
		{
			long timeElapsed = currentTime - clickedPoint.getTimeClicked();
			float progress = (float) timeElapsed / 600;
			Point canvasPoint = clickedPoint.getCanvasPoint();
			int alpha = (int) ((1.0f - progress) * baseAlpha);
			
			alpha = Math.max(0, alpha);
			
			if (canvasPoint != null)
			{
				int circleSize = (int) (minSize + (maxSize - minSize) * progress);
				int x = canvasPoint.getX() - circleSize / 2;
				int y = canvasPoint.getY() - circleSize / 2;
				
				graphics.setColor(new Color(red, green, blue, alpha));
				graphics.drawOval(x, y, circleSize, circleSize);
			}
		}
		
		return null;
	}
}
