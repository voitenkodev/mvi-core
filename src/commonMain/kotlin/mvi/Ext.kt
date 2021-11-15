package mvi

import mvi.builder.MviCore
import mvi.feature.Feature

/**
 * @param Model is data class of whole screen
 * @return [MviCore.FeatureProcessor]  which responsible for adding [Feature] using method [MviCore.FeatureProcessor.feature]
 **/

public inline infix fun <reified Model, State : Feature.State, reified F : Feature<*, *, *>> F.push(
    noinline l: Model.(State) -> Model
): Pair<F, Model.(State) -> Model> = Pair(this, l)