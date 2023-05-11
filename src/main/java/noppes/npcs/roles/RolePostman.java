package noppes.npcs.roles;

import noppes.npcs.constants.EnumGuiType;
import noppes.npcs.CustomNpcs;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import java.util.Iterator;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import noppes.npcs.controllers.data.PlayerData;
import java.util.Collection;
import java.util.ArrayList;
import noppes.npcs.entity.EntityNPCInterface;
import net.minecraft.entity.player.EntityPlayer;
import java.util.List;
import noppes.npcs.NpcMiscInventory;

public class RolePostman extends RoleInterface
{
    public NpcMiscInventory inventory;
    private List<EntityPlayer> recentlyChecked;
    private List<EntityPlayer> toCheck;
    
    public RolePostman(EntityNPCInterface npc) {
        super(npc);
        this.inventory = new NpcMiscInventory(1);
        this.recentlyChecked = new ArrayList<EntityPlayer>();
    }
    
    @Override
    public boolean aiShouldExecute() {
        if (this.npc.ticksExisted % 20 != 0) {
            return false;
        }
        (this.toCheck = (List<EntityPlayer>)this.npc.world.getEntitiesWithinAABB((Class)EntityPlayer.class, this.npc.getEntityBoundingBox().grow(10.0, 10.0, 10.0))).removeAll(this.recentlyChecked);
        List<EntityPlayer> listMax = (List<EntityPlayer>)this.npc.world.getEntitiesWithinAABB((Class)EntityPlayer.class, this.npc.getEntityBoundingBox().grow(20.0, 20.0, 20.0));
        this.recentlyChecked.retainAll(listMax);
        this.recentlyChecked.addAll(this.toCheck);
        for (EntityPlayer player : this.toCheck) {
            if (PlayerData.get(player).mailData.hasMail()) {
                player.sendMessage((ITextComponent)new TextComponentTranslation("You've got mail", new Object[0]));
            }
        }
        return false;
    }
    
    @Override
    public boolean aiContinueExecute() {
        return false;
    }
    
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setTag("PostInv", (NBTBase)this.inventory.getToNBT());
        return nbttagcompound;
    }
    
    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        this.inventory.setFromNBT(nbttagcompound.getCompoundTag("PostInv"));
    }
    
    @Override
    public void interact(EntityPlayer player) {
        player.openGui((Object)CustomNpcs.instance, EnumGuiType.PlayerMailman.ordinal(), player.world, 1, 1, 0);
    }
}
