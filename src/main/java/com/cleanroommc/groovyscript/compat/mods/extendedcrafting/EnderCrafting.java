package com.cleanroommc.groovyscript.compat.mods.extendedcrafting;

import com.blakebr0.extendedcrafting.config.ModConfig;
import com.blakebr0.extendedcrafting.crafting.endercrafter.EnderCrafterRecipeManager;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import java.util.Collection;
import java.util.List;

@RegistryDescription
public class EnderCrafting extends StandardListRegistry<IRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:stone')).matrix('BXX', 'X B').key('B', item('minecraft:stone')).key('X', item('minecraft:gold_ingot')).time(1).mirrored()"),
            @Example(".output(item('minecraft:diamond') * 32).matrix([[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')],[item('minecraft:gold_ingot'), item('minecraft:gold_ingot'), item('minecraft:gold_ingot')]]).time(1)")
    })
    public EnderRecipeBuilder.Shaped shapedBuilder() {
        return new EnderRecipeBuilder.Shaped();
    }

    @RecipeBuilderDescription(example = {
            @Example(".output(item('minecraft:clay') * 8).input(item('minecraft:stone'), item('minecraft:stone'), item('minecraft:stone'))"),
            @Example(".output(item('minecraft:clay') * 32).input(item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond'), item('minecraft:diamond')).time(1)")
    })
    public EnderRecipeBuilder.Shapeless shapelessBuilder() {
        return new EnderRecipeBuilder.Shapeless();
    }

    @Override
    public Collection<IRecipe> getRecipes() {
        return EnderCrafterRecipeManager.getInstance().getRecipes();
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.ender_crafting.addShaped0", type = MethodDescription.Type.ADDITION)
    public IRecipe addShaped(ItemStack output, List<List<IIngredient>> input) {
        return addShaped(ModConfig.confEnderTimeRequired, output, input);
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.ender_crafting.addShaped1", type = MethodDescription.Type.ADDITION)
    public IRecipe addShaped(int time, ItemStack output, List<List<IIngredient>> input) {
        return shapedBuilder()
                .time(time)
                .matrix(input)
                .output(output)
                .register();
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.ender_crafting.addShapeless0", type = MethodDescription.Type.ADDITION)
    public IRecipe addShapeless(ItemStack output, List<IIngredient> input) {
        return addShapeless(ModConfig.confEnderTimeRequired, output, input);
    }

    @MethodDescription(description = "groovyscript.wiki.extendedcrafting.ender_crafting.addShapeless1", type = MethodDescription.Type.ADDITION)
    public IRecipe addShapeless(int time, ItemStack output, List<IIngredient> input) {
        return shapelessBuilder()
                .time(time)
                .input(input)
                .output(output)
                .register();
    }

    @MethodDescription(example = @Example("item('extendedcrafting:material:40')"))
    public boolean removeByOutput(ItemStack stack) {
        return getRecipes().removeIf(recipe -> recipe != null && recipe.getRecipeOutput().isItemEqual(stack) && doAddBackup(recipe));
    }

}
