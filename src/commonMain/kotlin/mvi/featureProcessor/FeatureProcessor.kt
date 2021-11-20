package mvi.featureProcessor

import kotlinx.coroutines.CoroutineScope
import mvi.core.MviCore
import mvi.core.MviCoreImpl
import mvi.feature.Feature

public interface FeatureProcessor<ROOT> {

    public fun <WISH : Feature.Wish, STATE : Feature.State, NEWS : Feature.News> feature(
        tag: MviCore.FeatureTag,
        feature: (ROOT) -> Feature<WISH, STATE, NEWS>,
        updateRoot: ROOT.(STATE) -> ROOT
    ): FeatureProcessor<ROOT>

    public fun launchIn(scope: CoroutineScope): MviCoreImpl<ROOT>
}
