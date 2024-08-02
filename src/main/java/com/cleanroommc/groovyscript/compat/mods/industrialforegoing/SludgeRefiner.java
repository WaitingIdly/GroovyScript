package com.cleanroommc.groovyscript.compat.mods.industrialforegoing;

import com.buuz135.industrial.api.recipe.SludgeEntry;
import com.cleanroommc.groovyscript.api.GroovyBlacklist;
import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.documentation.annotations.Example;
import com.cleanroommc.groovyscript.api.documentation.annotations.MethodDescription;
import com.cleanroommc.groovyscript.api.documentation.annotations.RegistryDescription;
import com.cleanroommc.groovyscript.api.jeiremoval.IJEIRemoval;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.IOperation;
import com.cleanroommc.groovyscript.api.jeiremoval.operations.ItemOperation;
import com.cleanroommc.groovyscript.core.mixin.industrialforegoing.SludgeRefinerBlockAccessor;
import com.cleanroommc.groovyscript.helper.SimpleObjectStream;
import com.cleanroommc.groovyscript.registry.VirtualizedRegistry;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RegistryDescription
public class SludgeRefiner extends VirtualizedRegistry<SludgeEntry> implements IJEIRemoval.Default {

    @Override
    @GroovyBlacklist
    public void onReload() {
        SludgeEntry.SLUDGE_RECIPES.removeAll(removeScripted());
        SludgeEntry.SLUDGE_RECIPES.addAll(restoreFromBackup());
    }

    @Override
    @GroovyBlacklist
    public void afterScriptLoad() {
        // Clear the list and cause the sludge refiner to recompute outputs
        SludgeRefinerBlockAccessor.setOutputs(null);
    }

    @MethodDescription(type = MethodDescription.Type.ADDITION, example = @Example("item('minecraft:gold_ingot'), 5"))
    public SludgeEntry add(ItemStack output, int weight) {
        SludgeEntry recipe = new SludgeEntry(output, weight);
        add(recipe);
        return recipe;
    }

    public void add(SludgeEntry recipe) {
        if (recipe == null) return;
        addScripted(recipe);
        SludgeEntry.SLUDGE_RECIPES.add(recipe);
    }

    public boolean remove(SludgeEntry recipe) {
        if (recipe == null) return false;
        addBackup(recipe);
        SludgeEntry.SLUDGE_RECIPES.remove(recipe);
        return true;
    }

    @MethodDescription(example = @Example("item('minecraft:clay_ball')"))
    public boolean removeByOutput(IIngredient output) {
        return SludgeEntry.SLUDGE_RECIPES.removeIf(recipe -> {
            if (output.test(recipe.getStack())) {
                addBackup(recipe);
                return true;
            }
            return false;
        });
    }

    @MethodDescription(priority = 2000, example = @Example(commented = true))
    public void removeAll() {
        SludgeEntry.SLUDGE_RECIPES.forEach(this::addBackup);
        SludgeEntry.SLUDGE_RECIPES.clear();
    }

    @MethodDescription(type = MethodDescription.Type.QUERY)
    public SimpleObjectStream<SludgeEntry> streamRecipes() {
        return new SimpleObjectStream<>(SludgeEntry.SLUDGE_RECIPES)
                .setRemover(this::remove);
    }

    /**
     * @see com.buuz135.industrial.jei.sludge.SludgeRefinerRecipeCategory#getUid()
     */
    @Override
    public @NotNull Collection<String> getCategories() {
        return Collections.singletonList("sludge_refiner_category");
    }

    @Override
    public @NotNull List<IOperation> getJEIOperations() {
        return Collections.singletonList(ItemOperation.defaultOperation());
    }

}
