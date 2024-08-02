package com.cleanroommc.groovyscript.compat.mods.mekanism;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.FluidOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.IOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.ItemOperation;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.GasRecipeBuilder;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.IJEIRemovalGas;
import com.cleanroommc.groovyscript.compat.mods.mekanism.recipe.VirtualizedMekanismRegistry;
import com.cleanroommc.groovyscript.helper.Alias;
import com.google.common.collect.ImmutableList;
import mekanism.api.gas.GasStack;
import mekanism.common.recipe.RecipeHandler;
import mekanism.common.recipe.inputs.GasInput;
import mekanism.common.recipe.machines.WasherRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@RegistryDescription
public class Washer extends VirtualizedMekanismRegistry<WasherRecipe> {

    public Washer() {
        super(RecipeHandler.Recipe.CHEMICAL_WASHER, Alias.generateOfClassAnd(Washer.class, "ChemicalWasher"));
    }

    @RecipeBuilderDescription(example = @Example(".gasInput(gas('water') * 10).gasOutput(gas('hydrogen') * 20)"))
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example(value = "gas('water'), gas('hydrogen')", commented = true))
    public WasherRecipe add(GasStack input, GasStack output) {
        return recipeBuilder().gasOutput(output).gasInput(input).register();
    }

    @MethodDescription(example = @Example("gas('iron')"))
    public boolean removeByInput(GasStack input) {
        GroovyLog.Msg msg = GroovyLog.msg("Error removing Mekanism Washer recipe").error();
        msg.add(Mekanism.isEmpty(input), () -> "input must not be empty");
        if (msg.postIfNotEmpty()) return false;

        WasherRecipe recipe = recipeRegistry.get().remove(new GasInput(input));
        if (recipe != null) {
            addBackup(recipe);
            return true;
        }
        removeError("could not find recipe for %", input);
        return false;
    }

    @Property(property = "gasInput", valid = @Comp("1"))
    @Property(property = "gasOutput", valid = @Comp("1"))
    public static class RecipeBuilder extends GasRecipeBuilder<WasherRecipe> {

        @Override
        public String getErrorMsg() {
            return "Error adding Mekanism Washer recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg);
            validateFluids(msg);
            validateGases(msg, 1, 1, 1, 1);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable WasherRecipe register() {
            if (!validate()) return null;
            WasherRecipe recipe = new WasherRecipe(gasInput.get(0), gasOutput.get(0));
            ModSupport.MEKANISM.get().washer.add(recipe);
            return recipe;
        }
    }

    @Override
    public @NotNull List<IOperation> getJEIOperations() {
        return ImmutableList.of(ItemOperation.defaultOperation(), FluidOperation.defaultOperation().exclude(0), IJEIRemovalGas.getDefaultGas());
    }
}
