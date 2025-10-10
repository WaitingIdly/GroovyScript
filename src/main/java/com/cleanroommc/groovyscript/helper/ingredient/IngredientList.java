package com.cleanroommc.groovyscript.helper.ingredient;

import com.cleanroommc.groovyscript.api.IIngredient;

import java.util.ArrayList;

/**
 * A ingredient list with helpers to handle empty ingredients
 */
public class IngredientList<T extends IIngredient> extends ArrayList<T> {

    public IIngredient getOrEmpty(int i) {
        if (i < 0 || i >= size()) {
            return IIngredient.EMPTY;
        }
        IIngredient item = get(i);
        return item == null ? IIngredient.EMPTY : item;
    }

    public int getRealSize() {
        if (isEmpty()) return 0;
        int realSize = 0;
        for (T t : this) {
            if (!IngredientHelper.isEmpty(t)) realSize++;
        }
        return realSize;
    }

    public void trim() {
        if (!isEmpty()) {
            removeIf(IngredientHelper::isEmpty);
        }
    }
}
