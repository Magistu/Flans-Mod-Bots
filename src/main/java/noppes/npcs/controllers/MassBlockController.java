package noppes.npcs.controllers;

import noppes.npcs.entity.EntityNPCInterface;
import java.util.List;
import net.minecraft.world.World;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import noppes.npcs.controllers.data.BlockData;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class MassBlockController
{
    private static Queue<IMassBlock> queue;
    private static MassBlockController Instance;
    
    public MassBlockController() {
        MassBlockController.queue = new LinkedList<IMassBlock>();
        MassBlockController.Instance = this;
    }
    
    public static void Update() {
        MassBlockController instance = MassBlockController.Instance;
        if (MassBlockController.queue.isEmpty()) {
            return;
        }
        IMassBlock imb = MassBlockController.queue.remove();
        World world = imb.getNpc().world;
        BlockPos pos = imb.getNpc().getPosition();
        int range = imb.getRange();
        List<BlockData> list = new ArrayList<BlockData>();
        for (int x = -range; x < range; ++x) {
            for (int z = -range; z < range; ++z) {
                if (world.isBlockLoaded(new BlockPos(x + pos.getX(), 64, z + pos.getZ()))) {
                    for (int y = 0; y < range; ++y) {
                        BlockPos blockPos = pos.add(x, y - range / 2, z);
                        list.add(new BlockData(blockPos, world.getBlockState(blockPos), null));
                    }
                }
            }
        }
        imb.processed(list);
    }
    
    public static void Queue(IMassBlock imb) {
        MassBlockController instance = MassBlockController.Instance;
        MassBlockController.queue.add(imb);
    }
    
    public interface IMassBlock
    {
        EntityNPCInterface getNpc();
        
        int getRange();
        
        void processed(List<BlockData> p0);
    }
}
