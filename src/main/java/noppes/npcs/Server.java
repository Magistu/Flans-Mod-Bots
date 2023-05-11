package noppes.npcs;

import com.google.common.base.Charsets;
import java.io.DataInput;
import net.minecraft.nbt.NBTSizeTracker;
import java.io.DataInputStream;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutput;
import net.minecraft.nbt.CompressedStreamTools;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.ByteArrayOutputStream;
import net.minecraftforge.fml.common.network.internal.EntitySpawnMessageHelper;
import net.minecraftforge.fml.common.network.internal.FMLMessage;
import net.minecraft.nbt.NBTTagCompound;
import java.util.UUID;
import net.minecraft.village.MerchantRecipeList;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.Entity;
import noppes.npcs.util.CustomNPCsScheduler;
import java.io.IOException;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import io.netty.buffer.Unpooled;
import noppes.npcs.constants.EnumPacketClient;
import net.minecraft.entity.player.EntityPlayerMP;

public class Server
{
    public static void sendData(EntityPlayerMP player, EnumPacketClient enu, Object... obs) {
        sendDataDelayed(player, enu, 0, obs);
    }
    
    public static void sendDataDelayed(EntityPlayerMP player, EnumPacketClient type, int delay, Object... obs) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        CustomNPCsScheduler.runTack(() -> {
            try {
                if (!(!fillBuffer((ByteBuf)buffer, type, obs))) {
                    LogWriter.debug("Send: " + type);
                    CustomNpcs.Channel.sendTo(new FMLProxyPacket(buffer, "CustomNPCs"), player);
                }
            }
            catch (IOException e) {
                LogWriter.error(type + " Errored", e);
            }
        }, delay);
    }
    
    public static boolean sendDataChecked(EntityPlayerMP player, EnumPacketClient type, Object... obs) {
        PacketBuffer buffer = new PacketBuffer(Unpooled.buffer());
        try {
            if (!fillBuffer((ByteBuf)buffer, type, obs)) {
                return false;
            }
            LogWriter.debug("SendDataChecked: " + type);
            CustomNpcs.Channel.sendTo(new FMLProxyPacket(buffer, "CustomNPCs"), player);
        }
        catch (IOException e) {
            LogWriter.error(type + " Errored", e);
        }
        return true;
    }
    
    public static void sendAssociatedData(Entity entity, EnumPacketClient type, Object... obs) {
        List<EntityPlayerMP> list = (List<EntityPlayerMP>)entity.world.getEntitiesWithinAABB((Class)EntityPlayerMP.class, entity.getEntityBoundingBox().grow(160.0, 160.0, 160.0));
        if (list.isEmpty()) {
            return;
        }
        ByteBuf buffer = Unpooled.buffer();
        Iterator<EntityPlayerMP> iterator = list.iterator();
        CustomNPCsScheduler.runTack(() -> {
            try {
                if (!(!fillBuffer(buffer, type, obs))) {
                    LogWriter.debug("SendAssociatedData: " + type);
                    while (iterator.hasNext()) {
                    	EntityPlayerMP player = iterator.next();
                    	FMLEventChannel channel = CustomNpcs.Channel;
                    	FMLProxyPacket fmlProxyPacket = new FMLProxyPacket(new PacketBuffer(buffer.copy()), "CustomNPCs");
                        channel.sendTo(fmlProxyPacket, player);
                    }
                }
            }
            catch (IOException e) {
                LogWriter.error(type + " Errored", e);
            }
            finally {
                buffer.release();
            }
        });
    }
    
    public static void sendRangedData(Entity entity, int range, EnumPacketClient type, Object... obs) {
        List<EntityPlayerMP> list = (List<EntityPlayerMP>)entity.world.getEntitiesWithinAABB((Class)EntityPlayerMP.class, entity.getEntityBoundingBox().grow((double)range, (double)range, (double)range));
        if (list.isEmpty()) {
            return;
        }
        ByteBuf buffer = Unpooled.buffer();
        CustomNPCsScheduler.runTack(() -> {
            try {
                if (!(!fillBuffer(buffer, type, obs))) {
                    LogWriter.debug("sendRangedData: " + type);
                    Iterator<EntityPlayerMP> iterator = list.iterator();
                    while (iterator.hasNext()) {
                    	EntityPlayerMP player = iterator.next();
                    	FMLEventChannel channel = CustomNpcs.Channel;
                        FMLProxyPacket fmlProxyPacket = new FMLProxyPacket(new PacketBuffer(buffer.copy()), "CustomNPCs");
                        channel.sendTo(fmlProxyPacket, player);
                    }
                }
            }
            catch (IOException e) {
                LogWriter.error(type + " Errored", e);
            }
            finally {
                buffer.release();
            }
        });
    }
    
    public static void sendRangedData(World world, BlockPos pos, int range, EnumPacketClient type, Object... obs) {
        List<EntityPlayerMP> list = (List<EntityPlayerMP>)world.getEntitiesWithinAABB((Class)EntityPlayerMP.class, new AxisAlignedBB(pos).grow((double)range, (double)range, (double)range));
        if (list.isEmpty()) {
            return;
        }
        ByteBuf buffer = Unpooled.buffer();
        CustomNPCsScheduler.runTack(() -> {
            try {
                if (!(!fillBuffer(buffer, type, obs))) {
                    LogWriter.debug("sendRangedData: " + type);
                    Iterator<EntityPlayerMP> iterator = list.iterator();
                    while (iterator.hasNext()) {
                    	EntityPlayerMP player = iterator.next();
                        FMLEventChannel channel = CustomNpcs.Channel;
                        FMLProxyPacket fmlProxyPacket = new FMLProxyPacket(new PacketBuffer(buffer.copy()), "CustomNPCs");
                        channel.sendTo(fmlProxyPacket, player);
                    }
                }
            }
            catch (IOException e) {
                LogWriter.error(type + " Errored", e);
            }
            finally {
                buffer.release();
            }
        });
    }
    
    public static void sendToAll(MinecraftServer server, EnumPacketClient type, Object... obs) {
        List<EntityPlayerMP> list = new ArrayList<EntityPlayerMP>(server.getPlayerList().getPlayers());
        ByteBuf buffer = Unpooled.buffer();
        CustomNPCsScheduler.runTack(() -> {
            try {
                if (!(!fillBuffer(buffer, type, obs))) {
                    LogWriter.debug("SendToAll: " + type);
                    Iterator<EntityPlayerMP> iterator = list.iterator();
                    while (iterator.hasNext()) {
                    	EntityPlayerMP player = iterator.next();
                    	FMLEventChannel channel = CustomNpcs.Channel;
                    	FMLProxyPacket fmlProxyPacket = new FMLProxyPacket(new PacketBuffer(buffer.copy()), "CustomNPCs");
                        channel.sendTo(fmlProxyPacket, player);
                    }
                }
            }
            catch (IOException e) {
                LogWriter.error(type + " Errored", e);
            }
            finally {
                buffer.release();
            }
        });
    }
    
    public static boolean fillBuffer(ByteBuf buffer, Enum enu, Object... obs) throws IOException {
        buffer.writeInt(enu.ordinal());
        for (Object ob : obs) {
            if (ob != null) {
                if (ob instanceof Map) {
                    Map<String, Integer> map = (Map<String, Integer>)ob;
                    buffer.writeInt(map.size());
                    for (String key : map.keySet()) {
                        int value = map.get(key);
                        buffer.writeInt(value);
                        writeString(buffer, key);
                    }
                }
                else if (ob instanceof MerchantRecipeList) {
                    ((MerchantRecipeList)ob).writeToBuf(new PacketBuffer(buffer));
                }
                else if (ob instanceof List) {
                    List<String> list = (List<String>)ob;
                    buffer.writeInt(list.size());
                    for (String s : list) {
                        writeString(buffer, s);
                    }
                }
                else if (ob instanceof UUID) {
                    writeString(buffer, ob.toString());
                }
                else if (ob instanceof Enum) {
                    buffer.writeInt(((Enum)ob).ordinal());
                }
                else if (ob instanceof Integer) {
                    buffer.writeInt((int)ob);
                }
                else if (ob instanceof Boolean) {
                    buffer.writeBoolean((boolean)ob);
                }
                else if (ob instanceof String) {
                    writeString(buffer, (String)ob);
                }
                else if (ob instanceof Float) {
                    buffer.writeFloat((float)ob);
                }
                else if (ob instanceof Long) {
                    buffer.writeLong((long)ob);
                }
                else if (ob instanceof Double) {
                    buffer.writeDouble((double)ob);
                }
                else if (ob instanceof NBTTagCompound) {
                    writeNBT(buffer, (NBTTagCompound)ob);
                }
                else if (ob instanceof FMLMessage.EntitySpawnMessage) {
                    EntitySpawnMessageHelper.toBytes((FMLMessage.EntitySpawnMessage)ob, buffer);
                }
            }
        }
        if (buffer.array().length >= 65534) {
            LogWriter.error("Packet " + enu + " was too big to be send");
            return false;
        }
        return true;
    }
    
    public static UUID readUUID(ByteBuf buffer) {
        return UUID.fromString(readString(buffer));
    }
    
    public static void writeNBT(ByteBuf buffer, NBTTagCompound compound) throws IOException {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        DataOutputStream dataoutputstream = new DataOutputStream(new GZIPOutputStream(bytearrayoutputstream));
        try {
            CompressedStreamTools.write(compound, (DataOutput)dataoutputstream);
        }
        finally {
            dataoutputstream.close();
        }
        byte[] bytes = bytearrayoutputstream.toByteArray();
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }
    
    public static NBTTagCompound readNBT(ByteBuf buffer) throws IOException {
        byte[] bytes = new byte[buffer.readInt()];
        buffer.readBytes(bytes);
        DataInputStream datainputstream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))));
        try {
            return CompressedStreamTools.read((DataInput)datainputstream, NBTSizeTracker.INFINITE);
        }
        finally {
            datainputstream.close();
        }
    }
    
    public static void writeString(ByteBuf buffer, String s) {
        byte[] bytes = s.getBytes(Charsets.UTF_8);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
    }
    
    public static String readString(ByteBuf buffer) {
        try {
            byte[] bytes = new byte[buffer.readInt()];
            buffer.readBytes(bytes);
            return new String(bytes, Charsets.UTF_8);
        }
        catch (IndexOutOfBoundsException ex) {
            return null;
        }
    }
}
