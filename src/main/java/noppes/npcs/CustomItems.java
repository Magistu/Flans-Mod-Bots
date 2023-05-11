package noppes.npcs;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.client.ForgeHooksClient;
import noppes.npcs.client.renderer.blocks.BlockCopyRenderer;
import noppes.npcs.client.renderer.blocks.BlockDoorRenderer;
import noppes.npcs.blocks.tiles.TileDoor;
import noppes.npcs.client.renderer.blocks.BlockScriptedRenderer;
import noppes.npcs.blocks.tiles.TileMailbox3;
import noppes.npcs.blocks.tiles.TileMailbox2;
import noppes.npcs.client.renderer.blocks.BlockMailboxRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import noppes.npcs.client.renderer.blocks.BlockCarpentryBenchRenderer;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.block.BlockDoor;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.event.ModelRegistryEvent;
import noppes.npcs.controllers.RecipeController;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.properties.IProperty;
import net.minecraft.util.EnumFacing;
import net.minecraft.item.ItemStack;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.block.BlockDispenser;
import noppes.npcs.items.ItemNpcBlock;
import noppes.npcs.items.ItemSoulstoneFilled;
import noppes.npcs.items.ItemSoulstoneEmpty;
import noppes.npcs.items.ItemScriptedDoor;
import noppes.npcs.items.ItemTeleporter;
import noppes.npcs.items.ItemMounter;
import noppes.npcs.items.ItemNpcMovingPath;
import noppes.npcs.items.ItemNpcScripter;
import noppes.npcs.items.ItemNpcCloner;
import noppes.npcs.items.ItemNpcWand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import noppes.npcs.blocks.BlockCarpentryBench;
import noppes.npcs.blocks.BlockCopy;
import noppes.npcs.blocks.BlockInterface;
import noppes.npcs.blocks.BlockBuilder;
import noppes.npcs.blocks.BlockScriptedDoor;
import noppes.npcs.blocks.BlockScripted;
import noppes.npcs.blocks.BlockBorder;
import noppes.npcs.blocks.BlockWaypoint;
import noppes.npcs.blocks.BlockMailbox;
import net.minecraft.creativetab.CreativeTabs;
import noppes.npcs.blocks.BlockNpcRedstone;
import noppes.npcs.blocks.tiles.TileBorder;
import noppes.npcs.blocks.tiles.TileCopy;
import noppes.npcs.blocks.tiles.TileBuilder;
import noppes.npcs.blocks.tiles.TileScriptedDoor;
import noppes.npcs.blocks.tiles.TileScripted;
import noppes.npcs.blocks.tiles.TileWaypoint;
import noppes.npcs.blocks.tiles.TileMailbox;
import noppes.npcs.blocks.tiles.TileBlockAnvil;
import noppes.npcs.blocks.tiles.TileRedstoneBlock;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraft.block.Block;
import noppes.npcs.items.ItemNbtBook;
import noppes.npcs.items.ItemScripted;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

@GameRegistry.ObjectHolder("customnpcs")
public class CustomItems
{
    @GameRegistry.ObjectHolder("npcwand")
    public static Item wand;
    @GameRegistry.ObjectHolder("npcmobcloner")
    public static Item cloner;
    @GameRegistry.ObjectHolder("npcscripter")
    public static Item scripter;
    @GameRegistry.ObjectHolder("npcmovingpath")
    public static Item moving;
    @GameRegistry.ObjectHolder("npcmounter")
    public static Item mount;
    @GameRegistry.ObjectHolder("npcteleporter")
    public static Item teleporter;
    @GameRegistry.ObjectHolder("npcscripteddoortool")
    public static Item scriptedDoorTool;
    @GameRegistry.ObjectHolder("scripted_item")
    public static ItemScripted scripted_item;
    @GameRegistry.ObjectHolder("nbt_book")
    public static ItemNbtBook nbt_book;
    @GameRegistry.ObjectHolder("npcsoulstoneempty")
    public static Item soulstoneEmpty;
    @GameRegistry.ObjectHolder("npcsoulstonefilled")
    public static Item soulstoneFull;
    @GameRegistry.ObjectHolder("npcredstoneblock")
    public static Block redstoneBlock;
    @GameRegistry.ObjectHolder("npcmailbox")
    public static Block mailbox;
    @GameRegistry.ObjectHolder("npcwaypoint")
    public static Block waypoint;
    @GameRegistry.ObjectHolder("npcborder")
    public static Block border;
    @GameRegistry.ObjectHolder("npcscripted")
    public static Block scripted;
    @GameRegistry.ObjectHolder("npcscripteddoor")
    public static Block scriptedDoor;
    @GameRegistry.ObjectHolder("npcbuilderblock")
    public static Block builder;
    @GameRegistry.ObjectHolder("npccopyblock")
    public static Block copy;
    @GameRegistry.ObjectHolder("npccarpentybench")
    public static Block carpentyBench;
    public static CreativeTabNpcs tab;
    
    public static void load() {
        MinecraftForge.EVENT_BUS.register((Object)new CustomItems());
    }
    
    @SubscribeEvent
    public void registerBlocks(RegistryEvent.Register<Block> event) {
        GameRegistry.registerTileEntity((Class)TileRedstoneBlock.class, "TileRedstoneBlock");
        GameRegistry.registerTileEntity((Class)TileBlockAnvil.class, "TileBlockAnvil");
        GameRegistry.registerTileEntity((Class)TileMailbox.class, "TileMailbox");
        GameRegistry.registerTileEntity((Class)TileWaypoint.class, "TileWaypoint");
        GameRegistry.registerTileEntity((Class)TileScripted.class, "TileNPCScripted");
        GameRegistry.registerTileEntity((Class)TileScriptedDoor.class, "TileNPCScriptedDoor");
        GameRegistry.registerTileEntity((Class)TileBuilder.class, "TileNPCBuilder");
        GameRegistry.registerTileEntity((Class)TileCopy.class, "TileNPCCopy");
        GameRegistry.registerTileEntity((Class)TileBorder.class, "TileNPCBorder");
        Block redstoneBlock = ((BlockInterface) new BlockNpcRedstone().setHardness(50.0f).setResistance(2000.0f)).setTranslationKey("npcredstoneblock").setCreativeTab((CreativeTabs)CustomItems.tab);
        Block mailbox = new BlockMailbox().setTranslationKey("npcmailbox").setHardness(5.0f).setResistance(10.0f).setCreativeTab((CreativeTabs)CustomItems.tab);
        Block waypoint = new BlockWaypoint().setTranslationKey("npcwaypoint").setHardness(5.0f).setResistance(10.0f).setCreativeTab((CreativeTabs)CustomItems.tab);
        Block border = new BlockBorder().setTranslationKey("npcborder").setHardness(5.0f).setResistance(10.0f).setCreativeTab((CreativeTabs)CustomItems.tab);
        Block scripted = new BlockScripted().setTranslationKey("npcscripted").setHardness(5.0f).setResistance(10.0f).setCreativeTab((CreativeTabs)CustomItems.tab);
        Block scriptedDoor = new BlockScriptedDoor().setTranslationKey("npcscripteddoor").setHardness(5.0f).setResistance(10.0f);
        Block builder = new BlockBuilder().setTranslationKey("npcbuilderblock").setHardness(5.0f).setResistance(10.0f).setCreativeTab((CreativeTabs)CustomItems.tab);
        Block copy = new BlockCopy().setTranslationKey("npccopyblock").setHardness(5.0f).setResistance(10.0f).setCreativeTab((CreativeTabs)CustomItems.tab);
        Block carpentyBench = new BlockCarpentryBench().setTranslationKey("npccarpentybench").setHardness(5.0f).setResistance(10.0f).setCreativeTab((CreativeTabs)CustomItems.tab);
        event.getRegistry().registerAll(new Block[] { redstoneBlock, carpentyBench, mailbox, waypoint, border, scripted, scriptedDoor, builder, copy });
    }
    
    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        Item wand = new ItemNpcWand().setTranslationKey("npcwand").setFull3D();
        Item cloner = new ItemNpcCloner().setTranslationKey("npcmobcloner").setFull3D();
        Item scripter = new ItemNpcScripter().setTranslationKey("npcscripter").setFull3D();
        Item moving = new ItemNpcMovingPath().setTranslationKey("npcmovingpath").setFull3D();
        Item mount = new ItemMounter().setTranslationKey("npcmounter").setFull3D();
        Item teleporter = new ItemTeleporter().setTranslationKey("npcteleporter").setFull3D();
        Item scriptedDoorTool = new ItemScriptedDoor(CustomItems.scriptedDoor).setTranslationKey("npcscripteddoortool").setFull3D();
        Item soulstoneEmpty = new ItemSoulstoneEmpty().setTranslationKey("npcsoulstoneempty").setCreativeTab((CreativeTabs)CustomItems.tab);
        Item soulstoneFull = new ItemSoulstoneFilled().setTranslationKey("npcsoulstonefilled");
        Item scripted_item = new ItemScripted().setTranslationKey("scripted_item");
        Item nbt_book = new ItemNbtBook().setTranslationKey("nbt_book");
        event.getRegistry().registerAll(new Item[] { wand, cloner, scripter, moving, mount, teleporter, scriptedDoorTool, soulstoneEmpty, soulstoneFull, scripted_item, nbt_book });
        event.getRegistry().registerAll(new Item[] { (Item)new ItemNpcBlock(CustomItems.redstoneBlock), (Item)new ItemNpcBlock(CustomItems.carpentyBench), new ItemNpcBlock(CustomItems.mailbox).setHasSubtypes(true), (Item)new ItemNpcBlock(CustomItems.waypoint), (Item)new ItemNpcBlock(CustomItems.border), (Item)new ItemNpcBlock(CustomItems.scripted), (Item)new ItemNpcBlock(CustomItems.scriptedDoor), (Item)new ItemNpcBlock(CustomItems.builder), (Item)new ItemNpcBlock(CustomItems.copy) });
        CustomItems.tab.item = wand;
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(soulstoneFull, new BehaviorDefaultDispenseItem() {
            public ItemStack dispenseStack(IBlockSource source, ItemStack item) {
                EnumFacing enumfacing = (EnumFacing)source.getBlockState().getValue((IProperty)BlockDispenser.FACING);
                double x = source.getX() + enumfacing.getXOffset();
                double z = source.getZ() + enumfacing.getZOffset();
                ItemSoulstoneFilled.Spawn(null, item, source.getWorld(), new BlockPos(x, source.getY(), z));
                item.splitStack(1);
                return item;
            }
        });
    }
    
    @SubscribeEvent
    public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
        RecipeController.Registry = (IForgeRegistry<IRecipe>)event.getRegistry();
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void registerModels(ModelRegistryEvent event) {
        ModelLoader.setCustomStateMapper(CustomItems.mailbox, (IStateMapper)new StateMap.Builder().ignore(new IProperty[] { (IProperty)BlockMailbox.ROTATION, (IProperty)BlockMailbox.TYPE }).build());
        ModelLoader.setCustomStateMapper(CustomItems.scriptedDoor, (IStateMapper)new StateMap.Builder().ignore(new IProperty[] { (IProperty)BlockDoor.POWERED }).build());
        ModelLoader.setCustomStateMapper(CustomItems.builder, (IStateMapper)new StateMap.Builder().ignore(new IProperty[] { (IProperty)BlockBuilder.ROTATION }).build());
        ModelLoader.setCustomStateMapper(CustomItems.carpentyBench, (IStateMapper)new StateMap.Builder().ignore(new IProperty[] { (IProperty)BlockCarpentryBench.ROTATION }).build());
        ModelLoader.setCustomModelResourceLocation(CustomItems.wand, 0, new ModelResourceLocation("customnpcs:npcwand", "inventory"));
        ModelLoader.setCustomModelResourceLocation(CustomItems.cloner, 0, new ModelResourceLocation("customnpcs:npcmobcloner", "inventory"));
        ModelLoader.setCustomModelResourceLocation(CustomItems.scripter, 0, new ModelResourceLocation("customnpcs:npcscripter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(CustomItems.moving, 0, new ModelResourceLocation("customnpcs:npcmovingpath", "inventory"));
        ModelLoader.setCustomModelResourceLocation(CustomItems.mount, 0, new ModelResourceLocation("customnpcs:npcmounter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(CustomItems.teleporter, 0, new ModelResourceLocation("customnpcs:npcteleporter", "inventory"));
        ModelLoader.setCustomModelResourceLocation(CustomItems.scriptedDoorTool, 0, new ModelResourceLocation("customnpcs:npcscripteddoortool", "inventory"));
        ModelLoader.setCustomModelResourceLocation(CustomItems.soulstoneEmpty, 0, new ModelResourceLocation("customnpcs:npcsoulstoneempty", "inventory"));
        ModelLoader.setCustomModelResourceLocation(CustomItems.soulstoneFull, 0, new ModelResourceLocation("customnpcs:npcsoulstonefilled", "inventory"));
        ModelLoader.setCustomModelResourceLocation((Item)CustomItems.scripted_item, 0, new ModelResourceLocation("customnpcs:scripted_item", "inventory"));
        ModelLoader.setCustomModelResourceLocation((Item)CustomItems.nbt_book, 0, new ModelResourceLocation("customnpcs:nbt_book", "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.redstoneBlock), 0, new ModelResourceLocation(CustomItems.redstoneBlock.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.mailbox), 0, new ModelResourceLocation(CustomItems.mailbox.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.mailbox), 1, new ModelResourceLocation(CustomItems.mailbox.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.mailbox), 2, new ModelResourceLocation(CustomItems.mailbox.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.waypoint), 0, new ModelResourceLocation(CustomItems.waypoint.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.border), 0, new ModelResourceLocation(CustomItems.border.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.scripted), 0, new ModelResourceLocation(CustomItems.scripted.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.scriptedDoor), 0, new ModelResourceLocation(CustomItems.scriptedDoor.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.builder), 0, new ModelResourceLocation(CustomItems.builder.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.copy), 0, new ModelResourceLocation(CustomItems.copy.getRegistryName(), "inventory"));
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(CustomItems.carpentyBench), 0, new ModelResourceLocation(CustomItems.carpentyBench.getRegistryName(), "inventory"));
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileBlockAnvil.class, (TileEntitySpecialRenderer)new BlockCarpentryBenchRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileMailbox.class, (TileEntitySpecialRenderer)new BlockMailboxRenderer(0));
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileMailbox2.class, (TileEntitySpecialRenderer)new BlockMailboxRenderer(1));
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileMailbox3.class, (TileEntitySpecialRenderer)new BlockMailboxRenderer(2));
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileScripted.class, (TileEntitySpecialRenderer)new BlockScriptedRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileDoor.class, (TileEntitySpecialRenderer)new BlockDoorRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileCopy.class, (TileEntitySpecialRenderer)new BlockCopyRenderer());
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(CustomItems.carpentyBench), 0, (Class)TileBlockAnvil.class);
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(CustomItems.mailbox), 0, (Class)TileMailbox.class);
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(CustomItems.mailbox), 1, (Class)TileMailbox2.class);
        ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(CustomItems.mailbox), 2, (Class)TileMailbox3.class);
    }
    
    static {
        wand = null;
        cloner = null;
        scripter = null;
        moving = null;
        mount = null;
        teleporter = null;
        scriptedDoorTool = null;
        scripted_item = null;
        nbt_book = null;
        soulstoneEmpty = null;
        soulstoneFull = null;
        redstoneBlock = null;
        mailbox = null;
        waypoint = null;
        border = null;
        scripted = null;
        scriptedDoor = null;
        builder = null;
        copy = null;
        carpentyBench = null;
        CustomItems.tab = new CreativeTabNpcs("cnpcs");
    }
}
