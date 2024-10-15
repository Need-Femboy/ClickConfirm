package org.zaralot;

import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Click Confirm",
	description = "Plays a client-side click sound when you perform actions."
)
public class ClickConfirm extends Plugin {
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
	protected void startUp() throws Exception {
		overlayManager.add(overlay);
	}
	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(overlay);
		clickedPoints.clear();

	}
	
	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked menuOptionClicked) {
		MenuEntry menuEntry = menuOptionClicked.getMenuEntry();
		String target = menuOptionClicked.getMenuTarget();

		if(menuEntry != null){
			if(this.handleEquip(menuEntry)) return;
			if(this.handleDrinkPotion(menuEntry)) return;
			if(this.handleEat(menuEntry)) return;
			if(this.handleRun(menuEntry)) return;
			if(this.handleSpell(menuEntry)) return;
			if(target != null){
				if(this.handlePrayer(menuEntry, target)) return;
				if(this.handleSpecialAttack(menuEntry, target)) return;
			}
		}
	}

	private boolean handleEquip(MenuEntry menuEntry) {
		if(!config.onEquip()) return false;

		String option = menuEntry.getOption();
		if (option == null) return false;
		option = option.toUpperCase();

		if (option.equals("WEAR") || option.equals("WIELD")) {
			this.playSound();
			return true;
		}

		return false;
	}
	private boolean handleDrinkPotion(MenuEntry menuEntry){
		if(!config.onDrinkPotion()) return false;

		String option = menuEntry.getOption();
		if(option == null) return false;
		option = option.toUpperCase();

		if(option.equals("DRINK") || option.equals("HEAL")){
			this.playSound();
			return true;
		}

		return false;
	}
	private boolean handleEat(MenuEntry menuEntry){
		if(!config.onEat()) return false;

		String option = menuEntry.getOption();
		if(option == null) return false;
		option = option.toUpperCase();

		if(option.equals("EAT") || option.equals("HEAL")){
			this.playSound();
			return true;
		}

		return false;
	}
	private boolean handleRun(MenuEntry menuEntry){
		if(!config.onRun()) return false;

		String option = menuEntry.getOption();
		if(option == null) return false;
		option = option.toUpperCase();

		if(option.equals("TOGGLE RUN")){
			this.playSound();
			return true;
		}

		return false;
	}
	private boolean handleSpell(MenuEntry menuEntry){
		if(!config.onSpell()) return false;

		String option = menuEntry.getOption();
		if(option == null) return false;
		option = option.toUpperCase();

		if(option.equals("CAST") || option.equals("BREAK")){
			this.playSound();
			return true;
		}

		return false;
	}
	private boolean handleSpecialAttack(MenuEntry menuEntry, String target){
		if(!config.onSpecialAttack()) return false;

		String option = menuEntry.getOption();
		if(option == null) return false;
		option = option.toUpperCase();

		if(option.contains("USE") && target.contains("Special Attack")){
			this.playSound();
			return true;
		}

		return false;
	}
	private boolean handlePrayer(MenuEntry menuEntry, String target){
		if(!config.onPrayer()) return false;

		String option = menuEntry.getOption();
		if(option == null) return false;
		option = option.toUpperCase();

		if(option.equals("ACTIVATE") || option.equals("DEACTIVATE")){
			this.playSound();
			return true;
		}

		if(target.equalsIgnoreCase("Quick-prayers")){
			this.playSound();
			return true;
		}

		return false;
	}
	private void playSound(){
		Point mousePosition = client.getMouseCanvasPosition();
		if (mousePosition != null)
		{
			clickedPoints.add(new ClickedPoint(mousePosition, System.currentTimeMillis()));
		}
		//TODO: Update this with playing a sound file instead since this doesn't seem to actually affect volume that much
		this.client.playSoundEffect(SoundEffectID.ITEM_PICKUP, config.clickVolume().volume);
	}

	@Provides
	ClickConfirmConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ClickConfirmConfig.class);
	}
}
