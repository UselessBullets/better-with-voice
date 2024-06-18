package jakraes.betterwithvoice.misc;

import jakraes.betterwithvoice.BetterWithVoice;
import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.NetClientHandler;

import javax.sound.sampled.*;

public class SenderThread extends Thread {
	private final Minecraft minecraft;
	private final NetClientHandler netClientHandler;
	private final AudioFormat format;
	private final DataLine.Info info;
	private final TargetDataLine microphone;
	private boolean running = false;

	public SenderThread(Minecraft minecraft, NetClientHandler netClientHandler) {
		this.minecraft = minecraft;
		this.netClientHandler = netClientHandler;
		format = new AudioFormat(44100, 16, 2, true, true);
		info = new DataLine.Info(TargetDataLine.class, format);

		try {
			microphone = (TargetDataLine) AudioSystem.getLine(info);
		} catch (LineUnavailableException e) {
			throw new RuntimeException(e);
		}
	}

	// For some reason interrupt() wasn't working so this is what we'll work with
	public void end() {
		running = false;
	}

	@Override
	public void run() {
		try {
			running = true;
			microphone.open(format);
			microphone.start();

			while (running) {
				PacketVoice packetVoice = new PacketVoice();

				packetVoice.bytesInBuffer = microphone.read(packetVoice.buffer, 0, PacketVoice.BUFFER_SIZE);
				netClientHandler.addToSendQueue(packetVoice);
			}

			microphone.flush();
			microphone.close();
		} catch (Exception e) {
			BetterWithVoice.LOGGER.error(e.getMessage());
		}
	}
}
