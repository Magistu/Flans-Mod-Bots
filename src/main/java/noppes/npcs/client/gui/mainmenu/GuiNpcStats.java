package noppes.npcs.client.gui.mainmenu;

import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.client.gui.SubGuiNpcResistanceProperties;
import noppes.npcs.client.gui.SubGuiNpcProjectiles;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import noppes.npcs.client.gui.SubGuiNpcRangeProperties;
import noppes.npcs.client.gui.SubGuiNpcMeleeProperties;
import noppes.npcs.client.gui.util.SubGuiInterface;
import noppes.npcs.client.gui.SubGuiNpcRespawn;
import net.minecraft.client.gui.GuiButton;
import noppes.npcs.client.gui.util.GuiNpcButtonYesNo;
import noppes.npcs.client.gui.util.GuiNpcButton;
import net.minecraft.client.gui.GuiScreen;
import noppes.npcs.client.gui.util.GuiNpcTextField;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import noppes.npcs.client.Client;
import noppes.npcs.constants.EnumCreatureSpecType;
import noppes.npcs.constants.EnumPacketServer;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataStats;
import noppes.npcs.client.gui.util.IGuiData;
import noppes.npcs.client.gui.util.ITextfieldListener;
import noppes.npcs.client.gui.util.GuiNPCInterface2;

public class GuiNpcStats extends GuiNPCInterface2 implements ITextfieldListener, IGuiData
{
    private DataStats stats;
    
    public GuiNpcStats(EntityNPCInterface npc) {
        super(npc, 2);
        this.stats = npc.stats;
        Client.sendData(EnumPacketServer.MainmenuDisplayGet, new Object[0]);
        Client.sendData(EnumPacketServer.MainmenuAIGet, new Object[0]);
        Client.sendData(EnumPacketServer.MainmenuInvGet, new Object[0]);
        Client.sendData(EnumPacketServer.MainmenuStatsGet, new Object[0]);
    }
    
    @Override
    public void initGui() {
        super.initGui();
        int y = this.guiTop + 10;
        this.addLabel(new GuiNpcLabel(0, "stats.health", this.guiLeft + 5, y + 5));
        this.addTextField(new GuiNpcTextField(0, this, this.guiLeft + 85, y, 50, 18, this.stats.maxHealth + ""));
        this.getTextField(0).numbersOnly = true;
        this.getTextField(0).setMinMaxDefault(0, Integer.MAX_VALUE, 20);
        this.addLabel(new GuiNpcLabel(1, "stats.aggro", this.guiLeft + 140, y + 5));
        this.addTextField(new GuiNpcTextField(1, this, this.fontRenderer, this.guiLeft + 220, y, 50, 18, this.stats.aggroRange + ""));
        this.getTextField(1).numbersOnly = true;
        this.getTextField(1).setMinMaxDefault(1, 64, 2);
        this.addLabel(new GuiNpcLabel(34, "stats.creaturetype", this.guiLeft + 275, y + 5));
        this.addButton(new GuiNpcButton(8, this.guiLeft + 355, y, 56, 20, new String[] { "stats.normal", "stats.undead", "stats.arthropod" }, this.stats.creatureType.ordinal()));
        int i = 0;
        int j = this.guiLeft + 82;
        y += 22;
        this.addButton(new GuiNpcButton(i, j, y, 56, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(2, "stats.respawn", this.guiLeft + 5, y + 5));
        int k = 2;
        int l = this.guiLeft + 82;
        y += 22;
        this.addButton(new GuiNpcButton(k, l, y, 56, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(5, "stats.meleeproperties", this.guiLeft + 5, y + 5));
        int m = 3;
        int j2 = this.guiLeft + 82;
        y += 22;
        this.addButton(new GuiNpcButton(m, j2, y, 56, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(6, "stats.rangedproperties", this.guiLeft + 5, y + 5));
        this.addButton(new GuiNpcButton(9, this.guiLeft + 217, y, 56, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(7, "stats.projectileproperties", this.guiLeft + 140, y + 5));
        int i2 = 15;
        int j3 = this.guiLeft + 82;
        y += 34;
        this.addButton(new GuiNpcButton(i2, j3, y, 56, 20, "selectServer.edit"));
        this.addLabel(new GuiNpcLabel(15, "effect.resistance", this.guiLeft + 5, y + 5));
        int i3 = 4;
        int j4 = this.guiLeft + 82;
        y += 34;
        this.addButton(new GuiNpcButton(i3, j4, y, 56, 20, new String[] { "gui.no", "gui.yes" }, (int)(this.npc.isImmuneToFire() ? 1 : 0)));
        this.addLabel(new GuiNpcLabel(10, "stats.fireimmune", this.guiLeft + 5, y + 5));
        this.addButton(new GuiNpcButton(5, this.guiLeft + 217, y, 56, 20, new String[] { "gui.no", "gui.yes" }, (int)(this.stats.canDrown ? 1 : 0)));
        this.addLabel(new GuiNpcLabel(11, "stats.candrown", this.guiLeft + 140, y + 5));
        this.addTextField(new GuiNpcTextField(14, this, this.guiLeft + 355, y, 56, 20, this.stats.healthRegen + "").setNumbersOnly());
        this.addLabel(new GuiNpcLabel(14, "stats.regenhealth", this.guiLeft + 275, y + 5));
        int id = 16;
        int i4 = this.guiLeft + 355;
        y += 22;
        this.addTextField(new GuiNpcTextField(id, this, i4, y, 56, 20, this.stats.combatRegen + "").setNumbersOnly());
        this.addLabel(new GuiNpcLabel(16, "stats.combatregen", this.guiLeft + 275, y + 5));
        this.addButton(new GuiNpcButton(6, this.guiLeft + 82, y, 56, 20, new String[] { "gui.no", "gui.yes" }, (int)(this.stats.burnInSun ? 1 : 0)));
        this.addLabel(new GuiNpcLabel(12, "stats.burninsun", this.guiLeft + 5, y + 5));
        this.addButton(new GuiNpcButton(7, this.guiLeft + 217, y, 56, 20, new String[] { "gui.no", "gui.yes" }, (int)(this.stats.noFallDamage ? 1 : 0)));
        this.addLabel(new GuiNpcLabel(13, "stats.nofalldamage", this.guiLeft + 140, y + 5));
        int id2 = 17;
        int x = this.guiLeft + 82;
        y += 22;
        this.addButton(new GuiNpcButtonYesNo(id2, x, y, 56, 20, this.stats.potionImmune));
        this.addLabel(new GuiNpcLabel(17, "stats.potionImmune", this.guiLeft + 5, y + 5));
        this.addLabel(new GuiNpcLabel(22, "ai.cobwebAffected", this.guiLeft + 140, y + 5));
        this.addButton(new GuiNpcButton(22, this.guiLeft + 217, y, 56, 20, new String[] { "gui.no", "gui.yes" }, (int)(this.stats.ignoreCobweb ? 0 : 1)));
		// new
        String[] lvls = new String[45];
        for (int g=0; g<lvls.length; g++) { lvls[g] = ""+(g+1); }
        this.addButton(new GuiNpcButton(40, this.guiLeft + 217, this.guiTop + 32, 56, 20, lvls , this.stats.level));
        this.addLabel(new GuiNpcLabel(41, "stats.level", this.guiLeft + 140, this.guiTop + 37));
        this.addButton(new GuiNpcButton(42, this.guiLeft + 217, this.guiTop + 54, 56, 20, new String[] { "stats.type.normal", "stats.type.elite", "stats.type.boss" }, this.stats.type.ordinal()));
        this.addLabel(new GuiNpcLabel(43, "stats.type", this.guiLeft + 140, this.guiTop + 61));
    }
    
    @Override
    public void unFocused(GuiNpcTextField textfield) {
        if (textfield.id == 0) {
            this.stats.maxHealth = textfield.getInteger();
            this.npc.heal((float)this.stats.maxHealth);
        }
        else if (textfield.id == 1) {
            this.stats.aggroRange = textfield.getInteger();
        }
        else if (textfield.id == 14) {
            this.stats.healthRegen = textfield.getInteger();
        }
        else if (textfield.id == 16) {
            this.stats.combatRegen = textfield.getInteger();
        }
    }
    
    @Override
    protected void actionPerformed(GuiButton guibutton) {
        GuiNpcButton button = (GuiNpcButton)guibutton;
        if (button.id == 0) {
            this.setSubGui(new SubGuiNpcRespawn(this.stats));
        }
        else if (button.id == 2) {
            this.setSubGui(new SubGuiNpcMeleeProperties(this.stats.melee));
        }
        else if (button.id == 3) {
            this.setSubGui(new SubGuiNpcRangeProperties(this.stats));
        }
        else if (button.id == 4) {
            this.npc.setImmuneToFire(button.getValue() == 1);
        }
        else if (button.id == 5) {
            this.stats.canDrown = (button.getValue() == 1);
        }
        else if (button.id == 6) {
            this.stats.burnInSun = (button.getValue() == 1);
        }
        else if (button.id == 7) {
            this.stats.noFallDamage = (button.getValue() == 1);
        }
        else if (button.id == 8) {
            this.stats.creatureType = EnumCreatureAttribute.values()[button.getValue()];
        }
        else if (button.id == 9) {
            this.setSubGui(new SubGuiNpcProjectiles(this.stats.ranged));
        }
        else if (button.id == 15) {
            this.setSubGui(new SubGuiNpcResistanceProperties(this.stats.resistances));
        }
        else if (button.id == 17) {
            this.stats.potionImmune = ((GuiNpcButtonYesNo)guibutton).getBoolean();
        }
        else if (button.id == 22) {
            this.stats.ignoreCobweb = (button.getValue() == 0);
        }
        else if (button.id == 40) {
            this.stats.level = (int) (button.getValue());
            setBaseStats();
        }
        else if (button.id == 42) {
        	this.stats.type = EnumCreatureSpecType.values()[button.getValue()];
            setBaseStats();
        }
    }
    
    private void setBaseStats() {
		int lv = this.stats.getLevel();
		int type = this.stats.getType();
		/*Resistance and model size*/
		int sizeModel = 5;
		int time = 180;
		float[] resist = new float[] {1.0f, 1.0f, 1.0f, 1.10f};
		if (type==2) {
			sizeModel = 7;
			resist = new float[] {1.10f, 1.25f, 1.75f, 1.95f};
		} else if (type==1) {
			sizeModel = 6;
			resist = new float[] {1.05f, 1.10f, 1.30f, 1.50f};
			if (lv<=30) {time = (int) (300.0d+(Math.round(((double) lv + 5.5d)/10.0d)-1.0d)*60.0d); }
			else {time = 480; }
		} else {
			if (lv<=30) {time = (int) (90.0d+(Math.round(((double) lv + 5.5d)/10.0d)-1.0d)*30.0d); }
			else {time = 180; }
		}
		this.npc.display.setSize(sizeModel);
		this.stats.respawnTime = time;
		this.stats.resistances.melee = resist[0];
		this.stats.resistances.arrow = resist[1];
		this.stats.resistances.explosion = resist[2];
		this.stats.resistances.knockback = resist[3];
		/*Health*/
		double hp = getHP();
		this.stats.maxHealth = (int) hp;
		this.npc.setHealth((float) hp);
		this.stats.setHealthRegen((int) (Math.ceil(hp / 50.0d) * 10.0d));
		/*Power*/
		double pow = getPower();
		this.stats.melee.setStrength((int) pow);
		this.stats.ranged.setStrength((int) Math.round(pow/1.9d));
		this.stats.ranged.setAccuracy((int) Math.round((pow+74.4286d)/1.2571d));
		/*Speed*/
		double sp = Math.round((0.000081d * Math.pow(lv,2) - 0.07272d * lv + 22.072639d)*1.0d);
		this.stats.melee.setDelay((int) sp);
		this.stats.ranged.setDelay((int) sp, (int) sp*2);
		this.npc.ais.setWalkingSpeed((int) Math.ceil((sp-7)/3));
		/*Experience*/
		double xp = Math.pow(lv, 3)*0.025d + Math.pow(lv, 2)*0.5d + lv*200.0d + 350.0d;
		if (type==2) {xp *= 4.75;} /*drop xp*/
		else if (type==1) {xp *= 1.75;}
		this.npc.inventory.setExp((int) Math.round(xp/(100*(1+lv/3))), (int) Math.round(xp/(100*(1+lv/9))));
		// naming
		String sub_name = "";
		if (type==2) { sub_name = "Boss "; }
		else if (type==1) { sub_name = "Elite "; }
		sub_name += "lv."+lv;
		this.npc.display.setTitle(sub_name);
		this.npc.reset();
		initGui();
    }

	public double getHP() {
		double hp = 0.0d;
		for (int i=1; i<=this.stats.level; i++) {
			switch(this.stats.getType()) {
				case 1:
					hp += Math.round(0.0815d * Math.pow(i, 2) + 0.48d * i + 249.5d);
					break;
				case 2:
					hp += Math.round(0.43d * Math.pow(i, 2) - 1.0205d * i + 1500.5d);
					break;
				default:
					hp += Math.round(0.0308d * Math.pow(i, 2) + 0.0785d * i + 20.0d);
			}
		}
		hp *= 1.0d;
		if (hp <=0.0d) { hp = 20.0d; }
		if (hp>10000) {hp = Math.ceil(hp/500.0d)*500.0d;}
		else if (hp>1000) {hp = Math.ceil(hp/250.0d)*250.0d;}
		else if (hp>100) {hp = Math.round(hp/10.0d)*10.0d;}
		else if (hp>10) {hp = Math.round(hp/5.0d)*5.0d;}
		else {hp = Math.round(hp);}
		return hp;
	}
	
	public double getPower() {
		double p = 0.0d;
		switch(this.stats.getType()) {
			case 1:
				p += ((double) this.stats.level+2.5d)/0.5d;
				break;
			case 2:
				p += ((double) this.stats.level+4.8d)/0.3866d;
				break;
			default:
				p += ((double) this.stats.level+0.45d)/0.725d;
		}
		p *= 1.0d;
		return p;
	}

	@Override
    public void save() {
        Client.sendData(EnumPacketServer.MainmenuDisplaySave, this.npc.display.writeToNBT(new NBTTagCompound()));
        Client.sendData(EnumPacketServer.MainmenuAISave, this.npc.ais.writeToNBT(new NBTTagCompound()));
        Client.sendData(EnumPacketServer.MainmenuInvSave, this.npc.inventory.writeEntityToNBT(new NBTTagCompound()));
        Client.sendData(EnumPacketServer.MainmenuStatsSave, this.stats.writeToNBT(new NBTTagCompound()));
    }
    
    @Override
    public void setGuiData(NBTTagCompound compound) {
        this.stats.readToNBT(compound);
        this.initGui();
    }
}
