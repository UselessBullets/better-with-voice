package jakraes.betterwithvoice.interfaces;

import jakraes.betterwithvoice.misc.PacketVoice;

public interface INetHandlerMixin {
	void handleVoice(PacketVoice packetVoice);
}
