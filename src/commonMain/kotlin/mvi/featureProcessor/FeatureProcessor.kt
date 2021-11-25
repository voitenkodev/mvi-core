package mvi.featureProcessor

import kotlinx.coroutines.CoroutineScope
import mvi.core.MviCore
import mvi.core.MviCoreImpl
import mvi.feature.Feature

public interface FeatureProcessor<ROOT> {

    public fun <ASYNC : Feature.Wish.Async, SYNC : Feature.Wish.Sync, STATE : Feature.State, NEWS : Feature.News> feature(
        tag: MviCore.FeatureTag,
        feature: (ROOT) -> Feature<ASYNC, SYNC, STATE, NEWS>,
        updateRoot: ROOT.(STATE) -> ROOT
    ): FeatureProcessor<ROOT>

    public fun launchIn(scope: CoroutineScope): MviCoreImpl<ROOT>
}
