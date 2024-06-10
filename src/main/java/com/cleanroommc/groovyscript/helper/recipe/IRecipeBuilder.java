package com.cleanroommc.groovyscript.helper.recipe;

import com.cleanroommc.groovyscript.api.documentation.annotations.RecipeBuilderRegistrationMethod;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.List;

import java.util.List;

/**
 * A groovy recipe builder template
 *
 * @param <T>
 */
public interface IRecipeBuilder<T> {

    /**
     * Validates the current values. Should be called in {@link #register()}.
     *
     * @return if a valid recipe can be build with the current values
     */
    boolean validate();

    /**
     * This method creates and registers a recipe using the values of this builder.
     * Should call {@link #validate()} and returns an empty list if the values are not valid.
     * Should never return {@code null}.
     *
     * @return the built recipe or an empty list if values are invalid
     */
    @Nonnull
    @RecipeBuilderRegistrationMethod
    List<T> register();
}
