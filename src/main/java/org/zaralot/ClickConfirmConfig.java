package org.zaralot;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;
import net.runelite.client.config.Units;

import java.awt.Color;

@ConfigGroup("clickconfirm")
public interface ClickConfirmConfig extends Config
{
	@ConfigItem(
			keyName = "useCustomSoundFile",
			name = "Enable custom sound file",
			description = "You can use a custom sound file by creating a file at \".runelite\\clickconfirm\\click.wav\""
	)
	default boolean useCustomSoundFile()
	{
		return false;
	}
	@Range(
			min = 0,
			max = 127
	)
	@ConfigItem(
			keyName = "clickVolume",
			name = "Click volume",
			description = "Volume of the sound effect"
	)
	default int clickVolume()
	{
		return 64;
	}
	
	
	@ConfigItem(
			keyName = "onEquip",
			name = "Play on Equip",
			description = "Play a sound when armor or weapon is clicked on."
	)
	default boolean onEquip()
	{
		return true;
	}
	
	@ConfigItem(
			keyName = "onDrinkPotion",
			name = "Play on Potion",
			description = "Play a sound when a potion is clicked on."
	)
	default boolean onDrinkPotion()
	{
		return true;
	}
	
	@ConfigItem(
			keyName = "onEat",
			name = "Play on Eat",
			description = "Play a sound when food is clicked on."
	)
	default boolean onEat()
	{
		return true;
	}
	
	@ConfigItem(
			keyName = "onRun",
			name = "Play on Toggle Run",
			description = "Play a sound when toggle run is clicked on."
	)
	default boolean onRun()
	{
		return true;
	}
	
	@ConfigItem(
			keyName = "onSpell",
			name = "Play on Spell",
			description = "Play a sound when a spell is clicked on."
	)
	default boolean onSpell()
	{
		return true;
	}
	
	@ConfigItem(
			keyName = "onSpecialAttack",
			name = "Play on Special Attack",
			description = "Play a sound when special attack is clicked on."
	)
	default boolean onSpecialAttack()
	{
		return true;
	}
	
	@ConfigItem(
			keyName = "onPrayer",
			name = "Play on Prayer",
			description = "Play a sound when a prayer or quick-prayer is clicked on."
	)
	default boolean onPrayer()
	{
		return true;
	}
	
	@ConfigSection(
			name = "Circle settings",
			description = "",
			position = 999
	)
	String circleOverlaySettings = "Circle settings";
	
	@ConfigItem(
			keyName = "enableOverlay",
			name = "Enable Overlay",
			description = "",
			position = 0,
			section = circleOverlaySettings
	)
	default boolean enableOverlay()
	{
		return true;
	}
	
	@Units(Units.PIXELS)
	@ConfigItem(
			keyName = "circleMinSize",
			name = "Minimum circle size",
			description = "",
			position = 1,
			section = circleOverlaySettings
	)
	default int circleMinSize()
	{
		return 6;
	}
	
	@Units(Units.PIXELS)
	@ConfigItem(
			keyName = "circleMaxSize",
			name = "Maximum circle size",
			description = "",
			position = 2,
			section = circleOverlaySettings
	)
	default int circleMaxSize()
	{
		return 18;
	}
	
	@Alpha
	@ConfigItem(
			keyName = "circleColour",
			name = "Circle colour",
			description = "",
			position = 3,
			section = circleOverlaySettings
	)
	default Color circleColour()
	{
		return Color.RED;
	}
	
	
	enum ConfirmSound
	{
		CLICK,
		
	}
}
