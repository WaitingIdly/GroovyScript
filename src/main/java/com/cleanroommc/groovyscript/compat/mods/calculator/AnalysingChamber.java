package com.cleanroommc.groovyscript.compat.mods.calculator;

import com.cleanroommc.groovyscript.api.GroovyLog;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.*;
import com.cleanroommc.groovyscript.compat.mods.ModSupport;
import com.cleanroommc.groovyscript.helper.Alias;
import com.cleanroommc.groovyscript.helper.recipe.AbstractRecipeBuilder;
import com.cleanroommc.groovyscript.registry.StandardListRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import sonar.calculator.mod.common.recipes.AnalysingChamberRecipes;
import sonar.calculator.mod.common.recipes.CalculatorRecipe;
import sonar.core.recipes.ISonarRecipeObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;


/**
 * To help with coding this horrid mess:<br>
 * {@link sonar.calculator.mod.common.recipes.AnalysingChamberRecipes#addRecipes() AnalysingChamberRecipes#addRecipes()} is where recipes are added<br>
 * {@link sonar.calculator.mod.common.item.misc.CircuitBoard#setData(ItemStack) CircuitBoard#setData} sets the data of circuits<br>
 * {@link sonar.calculator.mod.common.tileentity.machines.TileEntityAnalysingChamber TileEntityAnalysingChamber} extracts items, specifically line 164 in analyse(int)<br>
 * {@link sonar.calculator.mod.integration.jei.Recipes.Analysing Analysing} represents this in JEI, and is giving missinfo<br>
 * <pre>
 * |  Slot |   1 |   2 |    3 |    4 |     5 |     6 |
 * |-------|-----|-----|------|------|-------|-------|
 * |  Odds |  50 | 100 | 1000 | 2000 | 10000 | 20000 | Pool of possible numbers
 * |   JEI | 6.0 | 0.2 |  0.1 | 0.02 |  0.01 |   N/A | Chance shown in JEI
 * | Valid | 2.0 | 1.0 |  0.1 | 0.05 |  0.01 | 0.005 | Actual chance
 * |  True |   N |   N |    Y |    N |     Y |     N | Chance matches
 * |  Used |  17 |  15 |   10 |   15 |    10 |     0 | What numbers are used for each slot
 * </pre>
 */

@RegistryDescription(
        admonition = {
                @Admonition("groovyscript.wiki.calculator.analysing_chamber.note0"),
                @Admonition(value = "groovyscript.wiki.calculator.analysing_chamber.note1", type = Admonition.Type.WARNING, format = Admonition.Format.STANDARD)
        }
)
public class AnalysingChamber extends StandardListRegistry<CalculatorRecipe> {

    public AnalysingChamber() {
        super(Alias.generateOfClass(AnalysingChamber.class).andGenerate("AnalyzingChamber"));
    }

    @Override
    public Collection<CalculatorRecipe> getRecipes() {
        return AnalysingChamberRecipes.instance().getRecipes();
    }

    @RecipeBuilderDescription(example = {
            @Example(".slot(6).location(1).output(item('minecraft:diamond'))"),
            @Example(".slot(1).location(18).output(item('minecraft:clay'))"),
    })
    public RecipeBuilder recipeBuilder() {
        return new RecipeBuilder();
    }

    @MethodDescription(example = @Example("item('sonarcore:reinforceddirtblock')"))
    public boolean removeByOutput(IIngredient output) {
        return getRecipes().removeIf(r -> {
            for (ISonarRecipeObject recipeOutput : r.recipeOutputs) {
                for (ItemStack itemStack : recipeOutput.getJEIValue()) {
                    if (output.test(itemStack)) {
                        addBackup(r);
                        return true;
                    }
                }
            }
            return false;
        });
    }

    @Property(property = "output", comp = @Comp(eq = 1))
    public static class RecipeBuilder extends AbstractRecipeBuilder<CalculatorRecipe> {

        @Property(comp = @Comp(gte = 1, lte = 6))
        private int slot;
        @Property(comp = @Comp(gte = 1, lte = 20000))
        private int location;

        @RecipeBuilderMethodDescription
        public RecipeBuilder slot(int slot) {
            this.slot = slot;
            return this;
        }

        @RecipeBuilderMethodDescription
        public RecipeBuilder location(int location) {
            this.location = location;
            return this;
        }

        @Override
        public String getErrorMsg() {
            return "Error adding Calculator Analysing Chamber Recipe";
        }

        @Override
        public void validate(GroovyLog.Msg msg) {
            validateItems(msg, 0, 0, 1, 1);
            validateFluids(msg);
            int compValue = switch (slot) {
                case 1 -> 50;
                case 2 -> 100;
                case 3 -> 1000;
                case 4 -> 2000;
                case 5 -> 10000;
                case 6 -> 20000;
                default -> {
                    msg.add("slot must be greater than or equal to 1 and less than or equal to 6, yet it was {}", slot);
                    yield 0;
                }
            };
            msg.add(location < 1 || location > compValue, "location must be greater than or equal to 1 and less than or equal to {}, yet it was {}", compValue, location);
        }

        @Override
        @RecipeBuilderRegistrationMethod
        public @Nullable CalculatorRecipe register() {
            if (!validate()) return null;

            CalculatorRecipe recipe = AnalysingChamberRecipes.instance()
                    .buildDefaultRecipe(Arrays.asList(slot, location), output, new ArrayList<>(), false);

            ModSupport.CALCULATOR.get().analysingChamber.add(recipe);
            return recipe;
        }
    }
}
