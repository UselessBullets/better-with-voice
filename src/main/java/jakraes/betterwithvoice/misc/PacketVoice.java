package jakraes.betterwithvoice.misc;

import jakraes.betterwithvoice.interfaces.INetHandlerMixin;
import net.minecraft.core.net.handler.NetHandler;
import net.minecraft.core.net.packet.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketVoice extends Packet {
	public static final int BUFFER_SIZE = 1024;

	public byte[] buffer;
	public int bytesInBuffer;
	public double distance;
	public double posX;
	public double posZ;

	public PacketVoice() {
		buffer = new byte[BUFFER_SIZE];
		bytesInBuffer = 0;
		distance = -1;
		posX = -1;
		posZ = -1;
	}

	@Override
	public void readPacketData(DataInputStream dataInputStream) throws IOException {
		dataInputStream.read(buffer, 0, BUFFER_SIZE);
		bytesInBuffer = dataInputStream.readInt();
		distance = dataInputStream.readDouble();
		posX = dataInputStream.readDouble();
		posZ = dataInputStream.readDouble();
	}

	@Override
	public void writePacketData(DataOutputStream dataOutputStream) throws IOException {
		dataOutputStream.write(buffer, 0, BUFFER_SIZE);
		dataOutputStream.writeInt(bytesInBuffer);
		dataOutputStream.writeDouble(distance);
		dataOutputStream.writeDouble(posX);
		dataOutputStream.writeDouble(posZ);
	}

	@Override
	public void processPacket(NetHandler netHandler) {
		((INetHandlerMixin) netHandler).betterwithvoice$handleVoice(this);
	}

	@Override
	public int getPacketSize() {
		return BUFFER_SIZE + 4 + 8 + 8 + 8;
	}

}
