package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.compat.jei.tablecrafting.AdvancedTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.tablecrafting.BasicTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.tablecrafting.EliteTableCategory;
import com.blakebr0.extendedcrafting.compat.jei.tablecrafting.UltimateTableCategory;
import com.blakebr0.extendedcrafting.crafting.table.ITieredRecipe;
import com.blakebr0.extendedcrafting.crafting.table.TableRecipeManager;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.IOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.ItemOperation;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import com.google.common.collect.ImmutableList;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class TableCrafting extends VirtualizedRegistry<ITieredRecipe> implements IJEIRemoval.Default {

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:stone') * 64).matrix('DLLLLLDDD', '  DNIGIND', 'DDDNIGIND', '  DLLLLLD').key('D', item('minecraft:diamond')).key('L', item('minecraft:redstone')).key('N', item('minecraft:stone')).key('I', item('minecraft:iron_ingot')).key('G', item('minecraft:gold_ingot')).tierUltimate()"),
            @Example(".tierAdvanced().output(item('minecraft:stone') * 8).matrix('BXX').mirrored().key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot'))"),
            @Example(".tierAny().output(item('minecraft:diamond')).matrix('BXXXBX').mirrored().key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot'))"),
            @Example(".matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]]).output(item('minecraft:gold_ingot') * 64).tier(4)"),
            @Example(".matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')], [item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]]).output(item('minecraft:gold_ingot') * 64)")
    })
    public TableRecipeBuilder.Shaped shapedBuilder() {
        return new TableRecipeBuilder.Shaped();
    }

    @RecipeBuilderDescription(example = @Example(".output(item('minecraft:stone') * 64).input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))"))
    public TableRecipeBuilder.Shapeless shapelessBuilder() {
        return new TableRecipeBuilder.Shapeless();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> TableRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe));
        TableRecipeManager.getInstance().getRecipes().addAll(restoreFromBackup());
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.table_crafting.addShaped0", type = MethodDescription.Type.ADDITION)
    public ITieredRecipe addShaped(ItemStack output, List<List<IIngredient>> input) {
        return addShaped(0, output, input);
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.table_crafting.addShaped1", type = MethodDescription.Type.ADDITION)
    public ITieredRecipe addShaped(int tier, ItemStack output, List<List<IIngredient>> input) {
        return shapedBuilder()
                .tier(tier)
                .matrix(input)
                .output(output)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.table_crafting.addShapeless0", type = MethodDescription.Type.ADDITION)
    public ITieredRecipe addShapeless(ItemStack output, List<IIngredient> input) {
        return addShapeless(0, output, input);
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.table_crafting.addShapeless1", type = MethodDescription.Type.ADDITION)
    public ITieredRecipe addShapeless(int tier, ItemStack output, List<IIngredient> input) {
        return shapelessBuilder()
                .tier(tier)
                .input(input)
                .output(output)
                .register();
    }

    public ITieredRecipe add(ITieredRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            TableRecipeManager.getInstance().getRecipes().add(recipe);
        }
        return recipe;
    }

    @MethodDescription(example = @Example("item('extendedcrafting:singularity_ultimate')"))
    public boolean removeByOutput(ItemStack stack) {
        return TableRecipeManager.getInstance().getRecipes().removeIf(recipe -> {
            if (recipe != null && recipe.getRecipeOutput().isItemEqual(stack)) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    public boolean remove(ITieredRecipe recipe) {
        if (TableRecipeManager.getInstance().getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<ITieredRecipe> streamRecipes() {
        return new SimpleObjectStream<>(TableRecipeManager.getInstance().getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        TableRecipeManager.getInstance().getRecipes().forEach(this::addBackup);
        TableRecipeManager.getInstance().getRecipes().clear();
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return ImmutableList.of(BasicTableCategory.UID, AdvancedTableCategory.UID, EliteTableCategory.UID, UltimateTableCategory.UID);
    }

    @Override
    public @NotNull List<IOperation> getJEIOperations() {
        return ImmutableList.of(ItemOperation.defaultOperation().include(0));
    }

}
