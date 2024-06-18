package jakraes.betterwithvoice.mixins;

import jakraes.betterwithvoice.interfaces.INetHandlerMixin;
import jakraes.betterwithvoice.misc.PacketVoice;
import net.minecraft.core.net.NetworkManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.EntityPlayerMP;
import net.minecraft.server.net.handler.NetServerHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = NetServerHandler.class, remap = false)
public class NetServerHandlerMixin implements INetHandlerMixin {
	@Shadow
	public NetworkManager netManager;
	@Shadow
	private MinecraftServer mcServer;
	@Shadow
	private EntityPlayerMP playerEntity;

	public void betterwithvoice$handleVoice(PacketVoice packetVoice) {
		for (EntityPlayerMP player : mcServer.playerList.playerEntities) {
			if (player.id == playerEntity.id) {
				continue;
			}

			packetVoice.distance = playerEntity.distanceTo(player);
			packetVoice.posX = playerEntity.x;
			packetVoice.posZ = playerEntity.z;

			player.playerNetServerHandler.sendPacket(packetVoice);
		}
	}
}
