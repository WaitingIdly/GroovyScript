package com.cleanroommc.groovyscript.compat.mods.immersivetechnology;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import mctmods.immersivetechnology.api.crafting.RadiatorRecipe;
import mctmods.immersivetechnology.common.Config;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

@RegistryDescription
public class Radiator extends StandardListRegistry<RadiatorRecipe> {

    @Override
    public boolean isEnabled() {
        return Config.ITConfig.Machines.Multiblock.enable_radiator;
    }

    @RecipeBuilderDescription(example = {
            @Example(".fluidInput(fluid('lava') * 100).fluidOutput(fluid('hot_spring_water') * 500).time(100)"),
            @Example(".fluidInput(fluid('water') * 50).fluidOutput(fluid('lava') * 50).time(50)")
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @Override
    public Collection<RadiatorRecipe> getRecipes() {
        return RadiatorRecipe.recipeList;
    }

    @MethodDescription(example = @Example("fluid('exhauststeam')"))
    public void removeByInput(IIngredient input) {
        getRecipes().removeIf(r -> r.getFluidInputs().stream().anyMatch(input::test) && doAddBackup(r));
    }

    @MethodDescription(example = @Example(value = "fluid('distwater')",commented = true))
    public void removeByOutput(IIngredient output) {
        getRecipes().removeIf(r -> r.getFluidOutputs().stream().anyMatch(output::test) && doAddBackup(r));
    }

    @Property(property = "fluidInput", comp = @Comp(eq = 1))
    @Property(property = "fluidOutput", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<RadiatorRecipe> {

        @Property(comp = @Comp(gte = 0))
        private int time;

        @RecipeBuilderMethodDescription
        public RecipeBuilder time(int time) {
            this.time = time;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Immersive Technology Radiator recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg, 1, 1, 1, 1);
            msg.add(time <= 0, "time must be greater than or equal to 1, yet it was {}", time);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable RadiatorRecipe register() {
            if (!validate()) return null;
            RadiatorRecipe recipe = new RadiatorRecipe(fluidOutput.get(0), fluidInput.get(0), time);
            ModSupport.IMMERSIVE_TECHNOLOGY.get().radiator.add(recipe);
            return recipe;
        }
    }

}
