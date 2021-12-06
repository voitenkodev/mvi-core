package mvi.featureProcessor

import kotlinx.coroutines.CoroutineScope
import mvi.core.MviCore
import mvi.core.MviCoreImpl
import mvi.feature.Feature

public interface FeatureProcessor<Root> {

    public fun <Async : Feature.Wish.Async, Sync : Feature.Wish.Sync, Side : Feature.Wish.Side, State : Feature.State> feature(
        tag: MviCore.FeatureTag,
        feature: (Root) -> Feature<Async, Sync, Side, State>,
        updateRoot: Root.(State) -> Root
    ): FeatureProcessor<Root>

    public fun launchIn(scope: CoroutineScope): MviCoreImpl<Root>
}
