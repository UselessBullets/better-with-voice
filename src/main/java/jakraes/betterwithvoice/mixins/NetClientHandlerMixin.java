package jakraes.betterwithvoice.mixins;

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

	public void betterwithvoice$handleVoice(PacketVoice packetVoice) {
		if (packetVoice.bytesInBuffer == -1) {
			return;
		}

		FloatControl control = (FloatControl) speakers.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue((float) (-4.0f * packetVoice.distance));
		speakers.write(packetVoice.buffer, 0, packetVoice.bytesInBuffer);
	}
}
