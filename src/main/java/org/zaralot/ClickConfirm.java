package org.zaralot;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;
import net.runelite.api.MenuEntry;
import net.runelite.api.Point;
import net.runelite.api.SoundEffectID;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.RuneLite;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import java.awt.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
		name = "Click Confirm",
		description = "Plays a client-side click sound when you perform actions."
)
public class ClickConfirm extends Plugin
{
	@Inject
	private Client client;
	@Inject
	private ClickConfirmConfig config;
	
	@Inject
	private OverlayManager overlayManager;
	
	@Inject
	private ClickConfirmOverlay overlay;
	
	@Getter
	private final List<ClickedPoint> clickedPoints = new ArrayList<>();
	
	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}
	
	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		clickedPoints.clear();
		
	}
	
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked)
	{
		MenuEntry menuEntry = menuOptionClicked.getMenuEntry();
		String target = menuOptionClicked.getMenuTarget();
		
		if (menuEntry != null)
		{
			if (this.handleEquip(menuEntry))
			{
				return;
			}
			if (this.handleDrinkPotion(menuEntry))
			{
				return;
			}
			if (this.handleEat(menuEntry))
			{
				return;
			}
			if (this.handleRun(menuEntry))
			{
				return;
			}
			if (this.handleSpell(menuEntry))
			{
				return;
			}
			if (target != null)
			{
				if (this.handlePrayer(menuEntry, target))
				{
					return;
				}
				if (this.handleSpecialAttack(menuEntry, target))
				{
					return;
				}
			}
		}
	}
	
	private boolean handleEquip(MenuEntry menuEntry)
	{
		if (!config.onEquip())
		{
			return false;
		}
		
		String option = menuEntry.getOption();
		if (option == null)
		{
			return false;
		}
		option = option.toUpperCase();
		
		if (option.equals("WEAR") || option.equals("WIELD"))
		{
			this.processClickLocation(menuEntry);
			return true;
		}
		
		return false;
	}
	
	private boolean handleDrinkPotion(MenuEntry menuEntry)
	{
		if (!config.onDrinkPotion())
		{
			return false;
		}
		
		String option = menuEntry.getOption();
		if (option == null)
		{
			return false;
		}
		option = option.toUpperCase();
		
		if (option.equals("DRINK") || option.equals("HEAL"))
		{
			this.processClickLocation(menuEntry);
			return true;
		}
		
		return false;
	}
	
	private boolean handleEat(MenuEntry menuEntry)
	{
		if (!config.onEat())
		{
			return false;
		}
		
		String option = menuEntry.getOption();
		if (option == null)
		{
			return false;
		}
		option = option.toUpperCase();
		
		if (option.equals("EAT") || option.equals("HEAL"))
		{
			this.processClickLocation(menuEntry);
			return true;
		}
		
		return false;
	}
	
	private boolean handleRun(MenuEntry menuEntry)
	{
		if (!config.onRun())
		{
			return false;
		}
		
		String option = menuEntry.getOption();
		if (option == null)
		{
			return false;
		}
		option = option.toUpperCase();
		
		if (option.equals("TOGGLE RUN"))
		{
			this.processClickLocation(menuEntry);
			return true;
		}
		
		return false;
	}
	
	private boolean handleSpell(MenuEntry menuEntry)
	{
		if (!config.onSpell())
		{
			return false;
		}
		
		String option = menuEntry.getOption();
		if (option == null)
		{
			return false;
		}
		option = option.toUpperCase();
		
		if (option.equals("CAST") || option.equals("BREAK"))
		{
			this.processClickLocation(menuEntry);
			return true;
		}
		
		return false;
	}
	
	private boolean handleSpecialAttack(MenuEntry menuEntry, String target)
	{
		if (!config.onSpecialAttack())
		{
			return false;
		}
		
		String option = menuEntry.getOption();
		if (option == null)
		{
			return false;
		}
		option = option.toUpperCase();
		
		if (option.contains("USE") && target.contains("Special Attack"))
		{
			this.processClickLocation(menuEntry);
			return true;
		}
		
		return false;
	}
	
	private boolean handlePrayer(MenuEntry menuEntry, String target)
	{
		if (!config.onPrayer())
		{
			return false;
		}
		
		String option = menuEntry.getOption();
		if (option == null)
		{
			return false;
		}
		option = option.toUpperCase();
		
		if (option.equals("ACTIVATE") || option.equals("DEACTIVATE"))
		{
			this.processClickLocation(menuEntry);
			return true;
		}
		
		if (target.equalsIgnoreCase("Quick-prayers"))
		{
			this.processClickLocation(menuEntry);
			return true;
		}
		
		return false;
	}
	
	private void processClickLocation(MenuEntry entry)
	{
		Point mousePosition = null;
		int param1 = entry.getParam1(); //Widget ID
		if (config.centreOnWidget() && param1 != -1 && (entry.getType().equals(MenuAction.CC_OP) || entry.getType().equals(MenuAction.CC_OP_LOW_PRIORITY)))
		{
			Widget widget;
			
			if (param1 == ComponentID.INVENTORY_CONTAINER)
			{
				widget = client.getWidget(ComponentID.INVENTORY_CONTAINER).getChild(entry.getParam0());
			}
			else
			{
				widget = client.getWidget(param1);
			}
			
			if (widget != null && !widget.isHidden())
			{
				Rectangle bounds = widget.getBounds();
				int x = bounds.x + (bounds.width / 2);
				int y = bounds.y + (bounds.height / 2);
				mousePosition = new Point(x, y);
			}
		}
		else
		{
			mousePosition = client.getMouseCanvasPosition();
		}
		
		if (mousePosition != null)
		{
			clickedPoints.add(new ClickedPoint(mousePosition, System.currentTimeMillis()));
		}
		
		playSound(false);
	}
	
	private void playSound(boolean skipcheck)
	{
		if (config.useCustomSoundFile() && !skipcheck)
		{
			playSoundFile();
		}
		else
		{
			this.client.playSoundEffect(SoundEffectID.ITEM_PICKUP, config.clickVolume());
		}
	}
	
	private void playSoundFile()
	{
		File fileLocation = new File(RuneLite.RUNELITE_DIR, "clickconfirm\\click.wav");
		
		if (!Files.exists(fileLocation.toPath()))
		{
			playSound(true);
			return;
		}
		
		Clip clip = null;
		try
		{
			clip = AudioSystem.getClip();
			InputStream fileStream = new BufferedInputStream(
					new FileInputStream(fileLocation)
			);
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(fileStream);
			clip.open(inputStream);
			if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN))
			{
				BooleanControl muteControl = (BooleanControl) clip.getControl(BooleanControl.Type.MUTE);
				muteControl.setValue(false);
				FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
				int soundVol = (int) Math.round(config.clickVolume() / 1.27);
				float newVol = (float) (Math.log((double) soundVol / 100) / Math.log(10.0) * 20.0);
				gainControl.setValue(newVol);
			}
			clip.start();
			Clip finalClip = clip;
			clip.addLineListener(myLineEvent ->
			{
				if (myLineEvent.getType() == LineEvent.Type.STOP)
				{
					finalClip.close();
				}
			});
		} catch (Exception e)
		{
			log.warn("Could not play custom sound file: " + e.getMessage() + " (" + fileLocation + ")");
			clip.close();
		}
	}
	
	@Provides
	ClickConfirmConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(ClickConfirmConfig.class);
	}
}
