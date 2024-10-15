package org.zaralot;


import lombok.Getter;
import net.runelite.api.Point;

public class ClickedPoint
{
	@Getter
	private final Point canvasPoint;
	@Getter
	private final long timeClicked;
	
	public ClickedPoint(Point canvasPoint, long timeClicked)
	{
		this.canvasPoint = canvasPoint;
		this.timeClicked = timeClicked;
	}
}