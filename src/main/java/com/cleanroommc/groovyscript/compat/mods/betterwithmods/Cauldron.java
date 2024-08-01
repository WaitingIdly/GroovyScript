package com.cleanroommc.groovyscript.compat.mods.betterwithmods;

import betterwithmods.common.BWRegistry;
import betterwithmods.common.registry.bulk.recipes.CookingPotRecipe;
import betterwithmods.common.registry.heat.BWMHeatRegistry;
import betterwithmods.module.compat.jei.category.CookingPotRecipeCategory;
import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@RegistryDescription
public class Cauldron extends VirtualizedRegistry<CookingPotRecipe> implements IJEIRemovalIOutput {

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).output(item('minecraft:diamond')).heat(2)"),
            @Example(".input(item('minecraft:diamond')).output(item('minecraft:gold_ingot') * 16).ignoreHeat()")
    })
    @RecipeBuilderMethodDescription
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void onReload() {
        removeScripted().forEach(recipe -> BWRegistry.CAULDRON.getRecipes().removeIf(r -> r == recipe));
        BWRegistry.CAULDRON.getRecipes().addAll(restoreFromBackup());
    }

    public CookingPotRecipe add(CookingPotRecipe recipe) {
        if (recipe != null) {
            addScripted(recipe);
            BWRegistry.CAULDRON.getRecipes().add(recipe);
        }
        return recipe;
    }

    public boolean remove(CookingPotRecipe recipe) {
        if (BWRegistry.CAULDRON.getRecipes().removeIf(r -> r == recipe)) {
            addBackup(recipe);
            return true;
        }
        return false;
    }

    @MethodDescription(example = @Example("item('minecraft:gunpowder')"))
    public boolean removeByOutput(IIngredient output) {
        return BWRegistry.CAULDRON.getRecipes().removeIf(r -> {
            for (ItemStack itemstack : r.getOutputs()) {
                if (output.test(itemstack)) {
                    addBackup(r);
                    return true;
                }
            }
            return false;
        });
    }

    @MethodDescription(example = @Example("item('minecraft:gunpowder')"))
    public boolean removeByInput(IIngredient input) {
        return BWRegistry.CAULDRON.getRecipes().removeIf(r -> {
            for (Ingredient ingredient : r.getInputs()) {
                for (ItemStack item : ingredient.getMatchingStacks()) {
                    if (input.test(item)) {
                        addBackup(r);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<CookingPotRecipe> streamRecipes() {
        return new SimpleObjectStream<>(BWRegistry.CAULDRON.getRecipes()).setRemover(this::remove);
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        BWRegistry.CAULDRON.getRecipes().forEach(this::addBackup);
        BWRegistry.CAULDRON.getRecipes().clear();
    }

    @Override
    public @NotNull Collection<String> getCategories() {
        return Arrays.stream(BWMHeatRegistry.allHeatLevels()).mapToObj(i -> BetterWithMods.getHeatUID(CookingPotRecipeCategory.CAULDRON_UID, i)).collect(Collectors.toList());
    }

    @Property(property = "input", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "9", type = Comp.Type.LTE)})
    @Property(property = "output", valid = {@Comp(value = "1", type = Comp.Type.GTE), @Comp(value = "9", type = Comp.Type.LTE)})
    public static class RecipeBuilder extends AbstractRecipeBuilder<CookingPotRecipe> {

        @Property(defaultValue = "1")
        private int heat = 1;
        @Property
        private boolean ignoreHeat;
        @Property(defaultValue = "1")
        private int priority = 1;

        @RecipeBuilderMethodDescription
        public RecipeBuilder heat(int heat) {
            this.heat = heat;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ignoreHeat(boolean ignoreHeat) {
            this.ignoreHeat = ignoreHeat;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder ignoreHeat() {
            this.ignoreHeat = !ignoreHeat;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder priority(int priority) {
            this.priority = priority;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Better With Mods Cauldron recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 9, 1, 9);
            validateFluids(msg);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CookingPotRecipe register() {
            if (!validate()) return null;

            CookingPotRecipe recipe = new CookingPotRecipe(input.stream().map(IIngredient::toMcIngredient).collect(Collectors.toList()), output, heat);
            recipe.setIgnoreHeat(ignoreHeat);
            recipe.setPriority(priority);
            ModSupport.BETTER_WITH_MODS.get().cauldron.add(recipe);
            return recipe;
        }
    }

}
