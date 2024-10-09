package jakraes.betterwithvoice.mixins;

import jakraes.betterwithvoice.BetterWithVoice;
import jakraes.betterwithvoice.interfaces.INetHandlerMixin;
import jakraes.betterwithvoice.misc.PacketVoice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.NetClientHandler;
import net.minecraft.core.net.NetworkManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.sound.sampled.*;

@Mixin(value = NetClientHandler.class, remap = false)
public class NetClientHandlerMixin implements INetHandlerMixin {
	@Final
	@Shadow
	private Minecraft mc;
	@Final
	@Shadow
	private NetworkManager netManager;
	private AudioFormat format;
	private DataLine.Info info;
	private SourceDataLine speakers;

	@Inject(method = "<init>", at = @At("TAIL"))
	public void betterwithvoice$constructInjection(Minecraft minecraft, String host, int port, CallbackInfo ci) throws LineUnavailableException {
		format = new AudioFormat(44100, 16, 2, true, true);
		info = new DataLine.Info(SourceDataLine.class, format);
		speakers = (SourceDataLine) AudioSystem.getLine(info);
		speakers.open(format);
		speakers.start();
	}

	// This whole function is magic to me, about 99% of this is code I didn't write, very cool :D
	public void betterwithvoice$handleVoice(PacketVoice packetVoice) {
		if (packetVoice.bytesInBuffer <= -1) {
			return;
		}

		double playerX = mc.thePlayer.x;
		double playerZ = mc.thePlayer.z;
		float playerYaw = mc.thePlayer.yRot;
		float playerPitch = mc.thePlayer.xRot;

		double playerYawRad = Math.toRadians(playerYaw);
		double playerPitchRad = Math.toRadians(playerPitch);

		double lookX = -Math.sin(playerYawRad) * Math.cos(playerPitchRad);
		double lookZ = Math.cos(playerYawRad) * Math.cos(playerPitchRad);
		double lookY = -Math.sin(playerPitchRad);

		double soundX = packetVoice.posX - playerX;
		double soundZ = packetVoice.posZ - playerZ;
		double soundY = 0;

		double dotProduct = soundX * lookX + soundZ * lookZ + soundY * lookY;
		double soundDistance = packetVoice.distance;
		double lookDistance = Math.sqrt(lookX * lookX + lookZ * lookZ + lookY * lookY);

		double cosAngle = dotProduct / (soundDistance * lookDistance);
		double angleDifference = Math.acos(cosAngle);

		double crossProduct = soundX * lookZ - soundZ * lookX;
		float pan = (float) (Math.signum(crossProduct) * angleDifference / Math.PI);

		float leftGain = (1 - pan) / 2;
		float rightGain = (1 + pan) / 2;

		leftGain = Math.max(0, Math.min(leftGain, 1));
		rightGain = Math.max(0, Math.min(rightGain, 1));

		for (int i = 0; i < packetVoice.bytesInBuffer/4; i += 4) {
			int j = i * 4;
			short leftSample = (short) (((packetVoice.buffer[j] & 0xFF) << 8) | (packetVoice.buffer[j + 1] & 0xFF));
			leftSample = (short) (leftSample * leftGain);
			packetVoice.buffer[j] = (byte) ((leftSample >> 8) & 0xFF);
			packetVoice.buffer[j + 1] = (byte) (leftSample & 0xFF);

			short rightSample = (short) (((packetVoice.buffer[j + 2] & 0xFF) << 8) | (packetVoice.buffer[j + 3] & 0xFF));
			rightSample = (short) (rightSample * rightGain);
			packetVoice.buffer[j + 2] = (byte) ((rightSample >> 8) & 0xFF);
			packetVoice.buffer[j + 3] = (byte) (rightSample & 0xFF);
		}

		speakers.write(packetVoice.buffer, 0, packetVoice.bytesInBuffer);
	}
}
