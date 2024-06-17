package jakraes.betterwithvoice;

import jakraes.betterwithvoice.interfaces.IGameSettingsMixin;
import jakraes.betterwithvoice.misc.PacketVoice;
import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.options.components.KeyBindingComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.net.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.ClientStartEntrypoint;
import turniplabs.halplibe.util.GameStartEntrypoint;


public class BetterWithVoice implements ModInitializer, GameStartEntrypoint, ClientStartEntrypoint {
	public static final String MOD_ID = "betterwithvoice";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		LOGGER.info("Better With Voice initialized.");
	}

	@Override
	public void beforeGameStart() {
		boolean found = false;

		// Searches for a free packet id so PacketVoice can be assigned an id
		for (int i = 0; i < 255; i++) {
			if (Packet.getNewPacket(i) == null) {
				Packet.addIdClassMapping(i, true, true, PacketVoice.class);
				found = true;
				LOGGER.info("Found free id for voice packet!");
				break;
			}
		}

		if (!found) {
			LOGGER.error("Failed to find free id for voice packet!");
			throw new RuntimeException();
		}
	}

	@Override
	public void afterGameStart() {
		OptionsCategory category = new OptionsCategory("gui.options.page.controls.category.betterwithvoice");
		category.withComponent(new KeyBindingComponent(((IGameSettingsMixin) Minecraft.getMinecraft(Minecraft.class).gameSettings).betterwithvoice$getActivateVoiceKey()));
		LOGGER.info("After client start");
	}

	@Override
	public void beforeClientStart() {

	}

	@Override
	public void afterClientStart() {
		OptionsCategory category = new OptionsCategory("gui.options.page.controls.category.betterwithvoice");
		category.withComponent(new KeyBindingComponent(((IGameSettingsMixin) Minecraft.getMinecraft(Minecraft.class).gameSettings).betterwithvoice$getActivateVoiceKey()));
		OptionsPages.CONTROLS.withComponent(category);
		LOGGER.info(String.valueOf(I18n.getInstance().getCurrentLanguage().getName()));
	}
}
