package com.cleanroommc.groovyscript.compat.mods.gadgetrymachines;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.gadgetry.Gadgetry;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import epicsquid.gadgetry.machines.recipe.DistillingRecipe;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Distiller extends StandardListRegistry<DistillingRecipe> {

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 500).fluidOutput(fluid('water'))"),
            @Example(".fluidInput(fluid('water') * 50).fluidOutput(fluid('lava') * 10).input(item('minecraft:diamond')).output(item('minecraft:clay'))"),
            @Example(".fluidInput(fluid('fuel') * 50).fluidOutput(fluid('lava') * 10).output(item('minecraft:clay'))"),
            @Example(".fluidInput(fluid('ethanol') * 50).fluidOutput(fluid('fuel') * 1000).input(item('minecraft:clay'))"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public void afterScriptLoad() {
        DistillingRecipe.distillable_items.clear();
        DistillingRecipe.distillable_fluids.clear();
        getRecipes().forEach(x -> {
            DistillingRecipe.distillable_items.add(x.inputs.get(0));
            DistillingRecipe.distillable_fluids.add(x.fluid_in.getFluid());
        });
    }

    @Override
    public Collection<DistillingRecipe> getRecipes() {
        return DistillingRecipe.recipes;
    }

    @MethodDescription(example = {@Example("item('minecraft:wheat')"), @Example(value = "fluid('water')", commented = true)})
    public boolean removeByInput(IIngredient input) {
        return getRecipes().removeIf(r -> (Gadgetry.isMatch(input, r.inputs) || input.test(r.fluid_in)) && doAddBackup(r));
    }

    @MethodDescription(example = {@Example(value = "item('gadgetrycore:biomass')", commented = true), @Example("fluid('fuel')")})
    public boolean removeByOutput(IIngredient output) {
        // DistillingRecipe ignores the parameter of #getFluidResult(ItemStack)
        return getRecipes().removeIf(r -> (output.test(r.getOutput()) || output.test(r.getFluidResult(ItemStack.EMPTY))) && doAddBackup(r));
    }

    @Property(property = "input", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "output", comp = @Comp(gte = 0, lte = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<DistillingRecipe> {

        @Override
        protected int getMaxItemInput() {
            return 1;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Gadgetry Distillery recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 1, 0, 1);
            validateFluids(msg, 1, 1, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable DistillingRecipe register() {
            if (!validate()) return null;
            DistillingRecipe recipe = null;
            for (var item_input : Gadgetry.toGadgetryInput(input.getOrEmpty(0))) {
                recipe = new DistillingRecipe(fluidInput.get(0), fluidOutput.get(0), item_input, output.getOrEmpty(0));
                ModSupport.GADGETRY_MACHINES.get().distiller.add(recipe);
            }
            return recipe;
        }
    }
}
