package noppes.npcs.client;

import noppes.npcs.util.CustomNPCsScheduler;
import java.io.IOException;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import noppes.npcs.CustomNpcs;
import noppes.npcs.LogWriter;
import io.netty.buffer.ByteBuf;
import noppes.npcs.Server;
import net.minecraft.network.PacketBuffer;
import io.netty.buffer.Unpooled;
import noppes.npcs.constants.EnumPacketServer;

public class Client
{
    public static void sendData(EnumPacketServer type, Object... obs) {
        CustomNPCsScheduler.runTack(() -> {
        	PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
            try {
                if (!(!Server.fillBuffer((ByteBuf)buffer, type, obs))) {
                    LogWriter.debug("Send: " + type);
                    CustomNpcs.Channel.sendToServer(new FMLProxyPacket(buffer, "CustomNPCs"));
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    
    public static void sendDirectData(EnumPacketServer type, Object... obs) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        try {
            if (!Server.fillBuffer((ByteBuf)buffer, type, obs)) {
                return;
            }
            LogWriter.debug("Send: " + type);
            CustomNpcs.Channel.sendToServer(new FMLProxyPacket(buffer, "CustomNPCs"));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
