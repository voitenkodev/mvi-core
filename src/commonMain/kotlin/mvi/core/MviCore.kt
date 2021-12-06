package mvi.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import mvi.feature.Feature
import mvi.featureProcessor.FeatureProcessor
import mvi.featureProcessor.FeatureProcessorImpl

public interface MviCore<Root> {

    public interface FeatureTag

    public val state: StateFlow<Root>

    public fun <Sync : Feature.Wish.Sync> want(tag: FeatureTag, wish: Sync)

    public fun <Async : Feature.Wish.Async> want(tag: FeatureTag, wish: Async)

    public fun <Side : Feature.Wish.Side> want(tag: FeatureTag, wish: Side)

    public fun <Side : Feature.Wish.Side> side(tag: FeatureTag): Flow<Side>?

    public fun <State : Feature.State> postProcessing(
        tag: FeatureTag, automatically: MviCoreImpl<Root>.(State) -> Unit
    ): MviCoreImpl<Root>

    public companion object {
        public fun <ROOT> featureProcessor(root: ROOT): FeatureProcessor<ROOT> =
            FeatureProcessorImpl(root)
    }
}