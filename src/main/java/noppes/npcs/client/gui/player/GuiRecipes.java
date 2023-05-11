package noppes.npcs.client.gui.player;

import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.GlStateManager;
import noppes.npcs.controllers.data.RecipeCarpentry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.math.MathHelper;
import noppes.npcs.client.gui.util.GuiButtonNextPage;
import java.util.Collection;
import noppes.npcs.controllers.RecipeController;
import java.util.ArrayList;
import net.minecraft.item.crafting.IRecipe;
import java.util.List;
import noppes.npcs.client.gui.util.GuiNpcButton;
import noppes.npcs.client.gui.util.GuiNpcLabel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import noppes.npcs.client.gui.util.GuiNPCInterface;

@SideOnly(Side.CLIENT)
public class GuiRecipes extends GuiNPCInterface
{
    private static ResourceLocation resource;
    private int page;
    private boolean npcRecipes;
    private GuiNpcLabel label;
    private GuiNpcButton left;
    private GuiNpcButton right;
    private List<IRecipe> recipes;
    
    public GuiRecipes() {
        this.page = 0;
        this.npcRecipes = true;
        this.recipes = new ArrayList<IRecipe>();
        this.ySize = 182;
        this.xSize = 256;
        this.setBackground("recipes.png");
        this.closeOnEsc = true;
        this.recipes.addAll((Collection<? extends IRecipe>)RecipeController.instance.anvilRecipes.values());
    }
    
    @Override
    public void initGui() {
        super.initGui();
        this.addLabel(new GuiNpcLabel(0, "Recipe List", this.guiLeft + 5, this.guiTop + 5));
        this.addLabel(this.label = new GuiNpcLabel(1, "", this.guiLeft + 5, this.guiTop + 168));
        this.addButton(this.left = new GuiButtonNextPage(1, this.guiLeft + 150, this.guiTop + 164, true));
        this.addButton(this.right = new GuiButtonNextPage(2, this.guiLeft + 80, this.guiTop + 164, false));
        this.updateButton();
    }
    
    private void updateButton() {
        GuiNpcButton right = this.right;
        GuiNpcButton right2 = this.right;
        boolean b = this.page > 0;
        right2.enabled = b;
        right.visible = b;
        GuiNpcButton left = this.left;
        GuiNpcButton left2 = this.left;
        boolean b2 = this.page + 1 < MathHelper.ceil(this.recipes.size() / 4.0f);
        left2.enabled = b2;
        left.visible = b2;
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
        if (!button.enabled) {
            return;
        }
        if (button == this.right) {
            --this.page;
        }
        if (button == this.left) {
            ++this.page;
        }
        this.updateButton();
    }
    
    @Override
    public void drawScreen(int xMouse, int yMouse, float f) {
        super.drawScreen(xMouse, yMouse, f);
        this.mc.renderEngine.bindTexture(GuiRecipes.resource);
        this.label.label = this.page + 1 + "/" + MathHelper.ceil(this.recipes.size() / 4.0f);
        this.label.x = this.guiLeft + (256 - Minecraft.getMinecraft().fontRenderer.getStringWidth(this.label.label)) / 2;
        for (int i = 0; i < 4; ++i) {
            int index = i + this.page * 4;
            if (index >= this.recipes.size()) {
                break;
            }
            IRecipe irecipe = this.recipes.get(index);
            if (!irecipe.getRecipeOutput().isEmpty()) {
                int x = this.guiLeft + 5 + i / 2 * 126;
                int y = this.guiTop + 15 + i % 2 * 76;
                this.drawItem(irecipe.getRecipeOutput(), x + 98, y + 28, xMouse, yMouse);
                if (irecipe instanceof RecipeCarpentry) {
                    RecipeCarpentry recipe = (RecipeCarpentry)irecipe;
                    x += (72 - recipe.recipeWidth * 18) / 2;
                    y += (72 - recipe.recipeHeight * 18) / 2;
                    for (int j = 0; j < recipe.recipeWidth; ++j) {
                        for (int k = 0; k < recipe.recipeHeight; ++k) {
                            this.mc.renderEngine.bindTexture(GuiRecipes.resource);
                            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                            this.drawTexturedModalRect(x + j * 18, y + k * 18, 0, 0, 18, 18);
                            ItemStack item = recipe.getCraftingItem(j + k * recipe.recipeWidth);
                            if (!item.isEmpty()) {
                                this.drawItem(item, x + j * 18 + 1, y + k * 18 + 1, xMouse, yMouse);
                            }
                        }
                    }
                }
            }
        }
        for (int i = 0; i < 4; ++i) {
            int index = i + this.page * 4;
            if (index >= this.recipes.size()) {
                break;
            }
            IRecipe irecipe = this.recipes.get(index);
            if (irecipe instanceof RecipeCarpentry) {
                RecipeCarpentry recipe2 = (RecipeCarpentry)irecipe;
                if (!recipe2.getRecipeOutput().isEmpty()) {
                    int x2 = this.guiLeft + 5 + i / 2 * 126;
                    int y2 = this.guiTop + 15 + i % 2 * 76;
                    this.drawOverlay(recipe2.getRecipeOutput(), x2 + 98, y2 + 22, xMouse, yMouse);
                    x2 += (72 - recipe2.recipeWidth * 18) / 2;
                    y2 += (72 - recipe2.recipeHeight * 18) / 2;
                    for (int j = 0; j < recipe2.recipeWidth; ++j) {
                        for (int k = 0; k < recipe2.recipeHeight; ++k) {
                            ItemStack item = recipe2.getCraftingItem(j + k * recipe2.recipeWidth);
                            if (!item.isEmpty()) {
                                this.drawOverlay(item, x2 + j * 18 + 1, y2 + k * 18 + 1, xMouse, yMouse);
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void drawItem(ItemStack item, int x, int y, int xMouse, int yMouse) {
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        RenderHelper.enableGUIStandardItemLighting();
        this.itemRender.zLevel = 100.0f;
        this.itemRender.renderItemAndEffectIntoGUI(item, x, y);
        this.itemRender.renderItemOverlays(this.fontRenderer, item, x, y);
        this.itemRender.zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
    }
    
    private void drawOverlay(ItemStack item, int x, int y, int xMouse, int yMouse) {
        if (this.isPointInRegion(x - this.guiLeft, y - this.guiTop, 16, 16, xMouse, yMouse)) {
            this.renderToolTip(item, xMouse, yMouse);
        }
    }
    
    protected boolean isPointInRegion(int p_146978_1_, int p_146978_2_, int p_146978_3_, int p_146978_4_, int p_146978_5_, int p_146978_6_) {
        int k1 = this.guiLeft;
        int l1 = this.guiTop;
        p_146978_5_ -= k1;
        p_146978_6_ -= l1;
        return p_146978_5_ >= p_146978_1_ - 1 && p_146978_5_ < p_146978_1_ + p_146978_3_ + 1 && p_146978_6_ >= p_146978_2_ - 1 && p_146978_6_ < p_146978_2_ + p_146978_4_ + 1;
    }
    
    @Override
    public void save() {
    }
    
    static {
        resource = new ResourceLocation("customnpcs", "textures/gui/slot.png");
    }
}
