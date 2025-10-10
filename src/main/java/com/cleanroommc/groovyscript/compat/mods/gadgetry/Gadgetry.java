package com.cleanroommc.groovyscript.compat.mods.gadgetry;

import com.cleanroommc.groovyscript.api.IIngredient;
import com.cleanroommc.groovyscript.api.IOreDicts;
import com.cleanroommc.groovyscript.compat.mods.GroovyPropertyContainer;
import epicsquid.gadgetry.core.lib.util.OreStack;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Gadgetry extends GroovyPropertyContainer {

    public final AlloyFurnace alloyFurnace = new AlloyFurnace();

    public static boolean isMatch(IIngredient test, Object target) {
        if (target instanceof ItemStack is) return test.test(is);
        if (test instanceof IOreDicts oreDicts) {
            if (target instanceof OreStack stack) return oreDicts.getOreDicts().contains(stack.oreId);
            if (target instanceof String s) return oreDicts.getOreDicts().contains(s);
        }
        return false;
    }

    public static boolean isMatch(IIngredient test, Collection<Object> target) {
        for (var x : target) {
            if (isMatch(test, x)) return true;
        }
        return false;
    }

    public static List<Object> toGadgetryInput(IIngredient ingredient) {
        if (ingredient instanceof IOreDicts oreDicts) {
            List<Object> list = new ArrayList<>();
            for (var oreDict : oreDicts.getOreDicts()) {
                list.add(new OreStack(oreDict, ingredient.getAmount()));
            }
            return list;
        }
        if (ingredient.isEmpty()) return Collections.singletonList(ItemStack.EMPTY);
        List<Object> list = new ArrayList<>();
        Collections.addAll(list, ingredient.getMatchingStacks());
        return list;
    }

}
