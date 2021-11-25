package mvi.core

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import mvi.feature.Feature
import mvi.featureProcessor.FeatureProcessor
import mvi.featureProcessor.FeatureProcessorImpl

public interface MviCore<ROOT> {

    public val state: StateFlow<ROOT>

    public fun <SYNC : Feature.Wish.Sync> want(tag: FeatureTag, sync: SYNC)

    public fun <ASYNC : Feature.Wish.Async> want(tag: FeatureTag, async: ASYNC)

    public fun <NEWS : Feature.News> news(tag: FeatureTag): Flow<NEWS>?

    public fun <STATE : Feature.State> postProcessing(
        tag: FeatureTag, automatically: MviCoreImpl<ROOT>.(STATE) -> Unit
    ): MviCoreImpl<ROOT>

    public companion object {
        public fun <ROOT> featureProcessor(root: ROOT): FeatureProcessor<ROOT> =
            FeatureProcessorImpl(root)
    }

    public interface FeatureTag
}