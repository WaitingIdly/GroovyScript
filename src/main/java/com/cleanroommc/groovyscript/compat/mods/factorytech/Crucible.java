package com.cleanroommc.groovyscript.compat.mods.factorytech;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import dalapo.factech.auxiliary.MachineRecipes;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class Crucible extends StandardListRegistry<MachineRecipes.MachineRecipe<ItemStack, FluidStack>> {

    @Override
    public Collection<MachineRecipes.MachineRecipe<ItemStack, FluidStack>> getRecipes() {
        return MachineRecipes.CRUCIBLE;
    }

    @RecipeBuilderDescription(example = {
            @Example(".input(item('minecraft:clay')).fluidOutput(fluid('water'))"),
            @Example(".input(item('minecraft:gold_ingot')).fluidOutput(fluid('lava') * 30)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('minecraft:ice')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> input.test(r.input()) && doAddBackup(r));
    }

    @MethodDescription(example = @Example("fluid('lava')"))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> output.test(r.getOutputStack()) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<MachineRecipes.MachineRecipe<ItemStack, FluidStack>> {

        @Override
        public String getErrorMsg() {
            return "Error adding Factory Tech Crucible recipe";
        }

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 1, 1, 0, 0);
            validateFluids(msg, 0, 0, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @NotNull List<MachineRecipes.MachineRecipe<ItemStack, FluidStack>> register() {
            if (!validate()) return Collections.emptyList();
            List<MachineRecipes.MachineRecipe<ItemStack, FluidStack>> list = new ArrayList<>();
            for (var stack : input.get(0).getMatchingStacks()) {
                var recipe = new MachineRecipes.MachineRecipe<>(stack, fluidOutput.get(0));
                list.add(recipe);
                ModSupport.FACTORY_TECH.get().crucible.add(recipe);
            }
            return list;
        }
    }
}
