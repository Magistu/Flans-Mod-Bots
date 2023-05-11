package noppes.npcs.api.wrapper.gui;

import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.controllers.CustomGuiController;
import noppes.npcs.api.wrapper.PlayerWrapper;
import noppes.npcs.api.entity.IPlayer;
import java.util.Iterator;
import noppes.npcs.api.gui.IScroll;
import noppes.npcs.api.item.IItemStack;
import noppes.npcs.api.wrapper.ItemScriptedWrapper;
import noppes.npcs.api.gui.ITexturedRect;
import noppes.npcs.api.gui.ITextField;
import noppes.npcs.api.gui.ILabel;
import noppes.npcs.api.gui.IButton;
import java.util.ArrayList;
import noppes.npcs.api.gui.IItemSlot;
import noppes.npcs.api.gui.ICustomGuiComponent;
import java.util.List;
import noppes.npcs.controllers.ScriptContainer;
import noppes.npcs.api.gui.ICustomGui;

public class CustomGuiWrapper implements ICustomGui
{
    int id;
    int width;
    int height;
    int playerInvX;
    int playerInvY;
    boolean pauseGame;
    boolean showPlayerInv;
    String backgroundTexture;
    ScriptContainer scriptHandler;
    List<ICustomGuiComponent> components;
    List<IItemSlot> slots;
    
    public CustomGuiWrapper() {
        this.backgroundTexture = "";
        this.components = new ArrayList<ICustomGuiComponent>();
        this.slots = new ArrayList<IItemSlot>();
    }
    
    public CustomGuiWrapper(int id, int width, int height, boolean pauseGame) {
        this.backgroundTexture = "";
        this.components = new ArrayList<ICustomGuiComponent>();
        this.slots = new ArrayList<IItemSlot>();
        this.id = id;
        this.width = width;
        this.height = height;
        this.pauseGame = pauseGame;
        this.scriptHandler = ScriptContainer.Current;
    }
    
    @Override
    public int getID() {
        return this.id;
    }
    
    @Override
    public int getWidth() {
        return this.width;
    }
    
    @Override
    public int getHeight() {
        return this.height;
    }
    
    @Override
    public List<ICustomGuiComponent> getComponents() {
        return this.components;
    }
    
    @Override
    public List<IItemSlot> getSlots() {
        return this.slots;
    }
    
    public ScriptContainer getScriptHandler() {
        return this.scriptHandler;
    }
    
    @Override
    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public void setDoesPauseGame(boolean pauseGame) {
        this.pauseGame = pauseGame;
    }
    
    public boolean getDoesPauseGame() {
        return this.pauseGame;
    }
    
    @Override
    public void setBackgroundTexture(String resourceLocation) {
        this.backgroundTexture = resourceLocation;
    }
    
    public String getBackgroundTexture() {
        return this.backgroundTexture;
    }
    
    @Override
    public IButton addButton(int id, String label, int x, int y) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y);
        this.components.add(component);
        return (IButton) this.components.get(this.components.size() - 1);
    }
    
    @Override
    public IButton addButton(int id, String label, int x, int y, int width, int height) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height);
        this.components.add(component);
        return (IButton) this.components.get(this.components.size() - 1);
    }
    
    @Override
    public IButton addTexturedButton(int id, String label, int x, int y, int width, int height, String texture) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height, texture);
        this.components.add(component);
        return (IButton) this.components.get(this.components.size() - 1);
    }
    
    @Override
    public IButton addTexturedButton(int id, String label, int x, int y, int width, int height, String texture, int textureX, int textureY) {
        CustomGuiButtonWrapper component = new CustomGuiButtonWrapper(id, label, x, y, width, height, texture, textureX, textureY);
        this.components.add(component);
        return (IButton) this.components.get(this.components.size() - 1);
    }
    
    @Override
    public ILabel addLabel(int id, String label, int x, int y, int width, int height) {
        CustomGuiLabelWrapper component = new CustomGuiLabelWrapper(id, label, x, y, width, height);
        this.components.add(component);
        return (ILabel) this.components.get(this.components.size() - 1);
    }
    
    @Override
    public ILabel addLabel(int id, String label, int x, int y, int width, int height, int color) {
        CustomGuiLabelWrapper component = new CustomGuiLabelWrapper(id, label, x, y, width, height, color);
        this.components.add(component);
        return (ILabel) this.components.get(this.components.size() - 1);
    }
    
    @Override
    public ITextField addTextField(int id, int x, int y, int width, int height) {
        CustomGuiTextFieldWrapper component = new CustomGuiTextFieldWrapper(id, x, y, width, height);
        this.components.add(component);
        return (ITextField) this.components.get(this.components.size() - 1);
    }
    
    @Override
    public ITexturedRect addTexturedRect(int id, String texture, int x, int y, int width, int height) {
        CustomGuiTexturedRectWrapper component = new CustomGuiTexturedRectWrapper(id, texture, x, y, width, height);
        this.components.add(component);
        return (ITexturedRect) this.components.get(this.components.size() - 1);
    }
    
    @Override
    public ITexturedRect addTexturedRect(int id, String texture, int x, int y, int width, int height, int textureX, int textureY) {
        CustomGuiTexturedRectWrapper component = new CustomGuiTexturedRectWrapper(id, texture, x, y, width, height, textureX, textureY);
        this.components.add(component);
        return (ITexturedRect) this.components.get(this.components.size() - 1);
    }
    
    @Override
    public IItemSlot addItemSlot(int x, int y) {
        return this.addItemSlot(x, y, ItemScriptedWrapper.AIR);
    }
    
    @Override
    public IItemSlot addItemSlot(int x, int y, IItemStack stack) {
        CustomGuiItemSlotWrapper slot = new CustomGuiItemSlotWrapper(x, y, stack);
        this.slots.add(slot);
        return this.slots.get(this.slots.size() - 1);
    }
    
    @Override
    public IScroll addScroll(int id, int x, int y, int width, int height, String[] list) {
        CustomGuiScrollWrapper component = new CustomGuiScrollWrapper(id, x, y, width, height, list);
        this.components.add(component);
        return (IScroll) this.components.get(this.components.size() - 1);
    }
    
    @Override
    public void showPlayerInventory(int x, int y) {
        this.showPlayerInv = true;
        this.playerInvX = x;
        this.playerInvY = y;
    }
    
    @Override
    public ICustomGuiComponent getComponent(int componentID) {
        for (ICustomGuiComponent component : this.components) {
            if (component.getID() == componentID) {
                return component;
            }
        }
        return null;
    }
    
    @Override
    public void removeComponent(int componentID) {
        for (int i = 0; i < this.components.size(); ++i) {
            if (this.components.get(i).getID() == componentID) {
                this.components.remove(i);
                return;
            }
        }
    }
    
    @Override
    public void updateComponent(ICustomGuiComponent component) {
        for (int i = 0; i < this.components.size(); ++i) {
            ICustomGuiComponent c = this.components.get(i);
            if (c.getID() == component.getID()) {
                this.components.set(i, component);
                return;
            }
        }
    }
    
    @Override
    public void update(IPlayer player) {
        CustomGuiController.updateGui((PlayerWrapper)player, this);
    }
    
    public boolean getShowPlayerInv() {
        return this.showPlayerInv;
    }
    
    public int getPlayerInvX() {
        return this.playerInvX;
    }
    
    public int getPlayerInvY() {
        return this.playerInvY;
    }
    
    public ICustomGui fromNBT(NBTTagCompound tag) {
        this.id = tag.getInteger("id");
        this.width = tag.getIntArray("size")[0];
        this.height = tag.getIntArray("size")[1];
        this.pauseGame = tag.getBoolean("pause");
        this.backgroundTexture = tag.getString("bgTexture");
        List<ICustomGuiComponent> components = new ArrayList<ICustomGuiComponent>();
        NBTTagList list = tag.getTagList("components", 10);
        for (NBTBase b : list) {
            CustomGuiComponentWrapper component = CustomGuiComponentWrapper.createFromNBT((NBTTagCompound)b);
            components.add(component);
        }
        this.components = components;
        List<IItemSlot> slots = new ArrayList<IItemSlot>();
        list = tag.getTagList("slots", 10);
        for (NBTBase b2 : list) {
            CustomGuiItemSlotWrapper component2 = (CustomGuiItemSlotWrapper)CustomGuiComponentWrapper.createFromNBT((NBTTagCompound)b2);
            slots.add(component2);
        }
        this.slots = slots;
        this.showPlayerInv = tag.getBoolean("showPlayerInv");
        if (this.showPlayerInv) {
            this.playerInvX = tag.getIntArray("pInvPos")[0];
            this.playerInvY = tag.getIntArray("pInvPos")[1];
        }
        return this;
    }
    
    public NBTTagCompound toNBT() {
        NBTTagCompound tag = new NBTTagCompound();
        tag.setInteger("id", this.id);
        tag.setIntArray("size", new int[] { this.width, this.height });
        tag.setBoolean("pause", this.pauseGame);
        tag.setString("bgTexture", this.backgroundTexture);
        NBTTagList list = new NBTTagList();
        for (ICustomGuiComponent c : this.components) {
            list.appendTag((NBTBase)((CustomGuiComponentWrapper)c).toNBT(new NBTTagCompound()));
        }
        tag.setTag("components", (NBTBase)list);
        list = new NBTTagList();
        for (ICustomGuiComponent c : this.slots) {
            list.appendTag((NBTBase)((CustomGuiComponentWrapper)c).toNBT(new NBTTagCompound()));
        }
        tag.setTag("slots", (NBTBase)list);
        tag.setBoolean("showPlayerInv", this.showPlayerInv);
        if (this.showPlayerInv) {
            tag.setIntArray("pInvPos", new int[] { this.playerInvX, this.playerInvY });
        }
        return tag;
    }
}
