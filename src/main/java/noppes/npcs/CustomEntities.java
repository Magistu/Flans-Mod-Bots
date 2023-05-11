package noppes.npcs;

import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import noppes.npcs.entity.EntityProjectile;
import noppes.npcs.entity.EntityChairMount;
import noppes.npcs.entity.EntityNpcClassicPlayer;
import noppes.npcs.entity.EntityNpcAlex;
import noppes.npcs.entity.EntityNPC64x32;
import noppes.npcs.entity.EntityCustomNpc;
import noppes.npcs.entity.EntityNPCGolem;
import noppes.npcs.entity.old.EntityNPCEnderman;
import noppes.npcs.entity.EntityNpcDragon;
import noppes.npcs.entity.EntityNpcSlime;
import noppes.npcs.entity.old.EntityNpcNagaFemale;
import noppes.npcs.entity.old.EntityNpcNagaMale;
import noppes.npcs.entity.old.EntityNpcEnderchibi;
import noppes.npcs.entity.EntityNpcCrystal;
import noppes.npcs.entity.old.EntityNPCElfFemale;
import noppes.npcs.entity.old.EntityNPCElfMale;
import noppes.npcs.entity.old.EntityNPCOrcFemale;
import noppes.npcs.entity.old.EntityNPCOrcMale;
import noppes.npcs.entity.old.EntityNPCFurryFemale;
import noppes.npcs.entity.old.EntityNPCDwarfFemale;
import noppes.npcs.entity.old.EntityNpcSkeleton;
import noppes.npcs.entity.old.EntityNpcMonsterFemale;
import noppes.npcs.entity.old.EntityNpcMonsterMale;
import noppes.npcs.entity.old.EntityNPCFurryMale;
import noppes.npcs.entity.old.EntityNPCDwarfMale;
import noppes.npcs.entity.old.EntityNPCHumanFemale;
import noppes.npcs.entity.EntityNpcPony;
import noppes.npcs.entity.old.EntityNPCVillager;
import net.minecraft.entity.Entity;
import noppes.npcs.entity.old.EntityNPCHumanMale;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.event.RegistryEvent;

public class CustomEntities
{
    private int newEntityStartId;
    
    public CustomEntities() {
        this.newEntityStartId = 0;
    }
    
    @SubscribeEvent
    public void register(RegistryEvent.Register<EntityEntry> event) {
        EntityEntry[] entries = { this.registerNpc((Class<? extends Entity>)EntityNPCHumanMale.class, "npchumanmale"), this.registerNpc((Class<? extends Entity>)EntityNPCVillager.class, "npcvillager"), this.registerNpc((Class<? extends Entity>)EntityNpcPony.class, "npcpony"), this.registerNpc((Class<? extends Entity>)EntityNPCHumanFemale.class, "npchumanfemale"), this.registerNpc((Class<? extends Entity>)EntityNPCDwarfMale.class, "npcdwarfmale"), this.registerNpc((Class<? extends Entity>)EntityNPCFurryMale.class, "npcfurrymale"), this.registerNpc((Class<? extends Entity>)EntityNpcMonsterMale.class, "npczombiemale"), this.registerNpc((Class<? extends Entity>)EntityNpcMonsterFemale.class, "npczombiefemale"), this.registerNpc((Class<? extends Entity>)EntityNpcSkeleton.class, "npcskeleton"), this.registerNpc((Class<? extends Entity>)EntityNPCDwarfFemale.class, "npcdwarffemale"), this.registerNpc((Class<? extends Entity>)EntityNPCFurryFemale.class, "npcfurryfemale"), this.registerNpc((Class<? extends Entity>)EntityNPCOrcMale.class, "npcorcfmale"), this.registerNpc((Class<? extends Entity>)EntityNPCOrcFemale.class, "npcorcfemale"), this.registerNpc((Class<? extends Entity>)EntityNPCElfMale.class, "npcelfmale"), this.registerNpc((Class<? extends Entity>)EntityNPCElfFemale.class, "npcelffemale"), this.registerNpc((Class<? extends Entity>)EntityNpcCrystal.class, "npccrystal"), this.registerNpc((Class<? extends Entity>)EntityNpcEnderchibi.class, "npcenderchibi"), this.registerNpc((Class<? extends Entity>)EntityNpcNagaMale.class, "npcnagamale"), this.registerNpc((Class<? extends Entity>)EntityNpcNagaFemale.class, "npcnagafemale"), this.registerNpc((Class<? extends Entity>)EntityNpcSlime.class, "NpcSlime"), this.registerNpc((Class<? extends Entity>)EntityNpcDragon.class, "NpcDragon"), this.registerNpc((Class<? extends Entity>)EntityNPCEnderman.class, "npcEnderman"), this.registerNpc((Class<? extends Entity>)EntityNPCGolem.class, "npcGolem"), this.registerNpc((Class<? extends Entity>)EntityCustomNpc.class, "CustomNpc"), this.registerNpc((Class<? extends Entity>)EntityNPC64x32.class, "CustomNpc64x32"), this.registerNpc((Class<? extends Entity>)EntityNpcAlex.class, "CustomNpcAlex"), this.registerNpc((Class<? extends Entity>)EntityNpcClassicPlayer.class, "CustomNpcClassic"), this.registerNewentity("CustomNpcChairMount", 64, 10, false).entity((Class)EntityChairMount.class).build(), this.registerNewentity("CustomNpcProjectile", 64, 3, true).entity((Class)EntityProjectile.class).build() };
        event.getRegistry().registerAll(entries);
    }
    
    private EntityEntry registerNpc(Class<? extends Entity> cl, String name) {
        if (CustomNpcs.FixUpdateFromPre_1_12) {
            ForgeRegistries.ENTITIES.register(new EntityEntry((Class)cl, name).setRegistryName(new ResourceLocation("customnpcs." + name)));
        }
        return this.registerNewentity(name, 64, 3, true).entity((Class)cl).build();
    }
    
    private <E extends Entity> EntityEntryBuilder<E> registerNewentity(String name, int range, int update, boolean velocity) {
        EntityEntryBuilder<E> builder = (EntityEntryBuilder<E>)EntityEntryBuilder.create();
        ResourceLocation registryName = new ResourceLocation("customnpcs", name);
        return (EntityEntryBuilder<E>)builder.id(registryName, this.newEntityStartId++).name(name).tracker(range, update, velocity);
    }
}
