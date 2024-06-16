package jakraes.betterwithvoice;

import jakraes.betterwithvoice.misc.PacketVoice;
import net.fabricmc.api.ModInitializer;
import net.minecraft.core.net.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.GameStartEntrypoint;


public class BetterWithVoice implements ModInitializer, GameStartEntrypoint {
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
	}
}
