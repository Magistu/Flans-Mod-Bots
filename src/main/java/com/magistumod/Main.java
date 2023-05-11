package com.magistumod;

import java.io.File;
import java.util.function.Supplier;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.flansmod.common.parts.PartType;
import com.flansmod.common.types.EnumType;
import com.flansmod.common.types.TypeFile;
import com.magistumod.common.CommonProxy;
import com.magistumod.common.command.CommandCoalition;
import com.magistumod.common.network.NetworkHelper;
import com.magistumod.common.network.PacketHandler;
import com.magistumod.common.network.messages.AbstractMessage;
import com.magistumod.common.network.messages.MessageSaveData;
import com.magistumod.common.storage.CoalitionDataManager;
import com.magistumod.common.storage.StorageEvents;
import com.magistumod.common.storage.StorageHelper;
import com.magistumod.item.ItemCustomBot;
import com.magistumod.item.ItemEquipedBot;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.command.ICommand;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = Reference.MODID, name = Reference.MODNAME, version = Reference.GAMEVERSION, dependencies = "required-after:flansmod")
public class Main 
{
	public static final Logger logger = LogManager.getLogger("coalitionsmod");
	
	@Instance
	public static Main instance;
	
	@SidedProxy(clientSide = Reference.CLIENT, serverSide = Reference.SERVER)
	public static CommonProxy proxy;
	
	
	//Items
	public static Item EQUIPED_BOT;
	public static Item CUSTOM_BOT;
	
	@EventHandler
	private void preInit(FMLPreInitializationEvent event) 
	{
		MinecraftForge.EVENT_BUS.register(this);
		
		
		EQUIPED_BOT = new ItemEquipedBot();
		CUSTOM_BOT = new ItemCustomBot.ItemCustom();

		ModEntityRegistry.mainRegistry();
		proxy.registerRenderThings();
		
	    if (FMLCommonHandler.instance().getSide().isClient()) 
	    {
	    	PacketHandler.register(Side.CLIENT);
	    }
	    PacketHandler.register(Side.SERVER);

	    
		GameRegistry.registerWorldGenerator(elements, 5);
		GameRegistry.registerFuelHandler(elements);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new Elements.GuiHandler());
		elements.preInit(event);
		MinecraftForge.EVENT_BUS.register(elements);
		elements.getElements().forEach(element -> element.preInit(event));
		iproxy.preInit(event);
		
		ConfigHandler.init(event.getModConfigurationDirectory());
		MinecraftForge.EVENT_BUS.register(new ConfigHandler());
	}
	public static final String ACCEPTED_VERSIONS = "[1.12.2]";
	public static boolean doneSetup = false;
	
	@EventHandler
	private void init(FMLInitializationEvent event) 
	{
		//MinecraftForge.EVENT_BUS.register(new FriendlyFireEvent());
	    doneSetup = true;
	    
	    elements.getElements().forEach(element -> element.init(event));
		iproxy.init(event);
		
		
	}
	
	@EventHandler
	public static void postInit(FMLPostInitializationEvent event) 
	{
		iproxy.postInit(event);
	}
	
	@EventHandler
	public void serverStart(FMLServerStartingEvent event) 
	{
	    event.registerServerCommand((ICommand)new CommandCoalition());
	    StorageEvents.data = CoalitionDataManager.get(event.getServer().getEntityWorld());
	    if (event.getServer().isSinglePlayer()) 
	    {
	    	NetworkHelper.sendToAll((AbstractMessage)new MessageSaveData());
	    }
	    if (!StorageHelper.doesCoalitionExist("AXIS")) 
	    {
	    	StorageEvents.data.addCoalition("AXIS");
	    }
	    if (!StorageHelper.doesCoalitionExist("ALLIES")) 
	    {
	    	StorageEvents.data.addCoalition("ALLIES");
	    }
	}
	
	@EventHandler
	public void serverStop(FMLServerStoppingEvent event) 
	{
		StorageEvents.data.markDirty();
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> event)
	{
		event.getRegistry().register(EQUIPED_BOT);
		
		event.getRegistry().registerAll(elements.getItems().stream().map(Supplier::get).toArray(Item[]::new));
	}
	
	public static final SimpleNetworkWrapper PACKET_HANDLER = NetworkRegistry.INSTANCE.newSimpleChannel("magistumod:a");
	@SidedProxy(clientSide = "com.magistumod.client.ClientProxy", serverSide = "com.magistumod.common.CommonProxy")
	public static IProxy iproxy;
	public Elements elements = new Elements();

	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event)
	{
		elements.getElements().forEach(element -> element.serverLoad(event));
		iproxy.serverLoad(event);
	}

	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> event)
	{
		event.getRegistry().registerAll(elements.getBlocks().stream().map(Supplier::get).toArray(Block[]::new));
	}

	@SubscribeEvent
	public void registerBiomes(RegistryEvent.Register<Biome> event)
	{
		event.getRegistry().registerAll(elements.getBiomes().stream().map(Supplier::get).toArray(Biome[]::new));
	}

	@SubscribeEvent
	public void registerEntities(RegistryEvent.Register<EntityEntry> event)
	{
		event.getRegistry().registerAll(elements.getEntities().stream().map(Supplier::get).toArray(EntityEntry[]::new));
	}

	@SubscribeEvent
	public void registerPotions(RegistryEvent.Register<Potion> event)
	{
		event.getRegistry().registerAll(elements.getPotions().stream().map(Supplier::get).toArray(Potion[]::new));
	}

	@SubscribeEvent
	public void registerSounds(RegistryEvent.Register<net.minecraft.util.SoundEvent> event)
	{
		elements.registerSounds(event);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void registerModels(ModelRegistryEvent event)
	{
		ModelLoader.setCustomModelResourceLocation(EQUIPED_BOT, 0, new ModelResourceLocation("magistumod:equiped_bot", "inventory"));
		
		elements.getElements().forEach(element -> element.registerModels(event));
	}
	static
	{
		FluidRegistry.enableUniversalBucket();
	}
}
