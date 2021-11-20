package mvi.core

import mvi.feature.Feature
import mvi.featureProcessor.FeatureProcessor
import mvi.featureProcessor.FeatureProcessorImpl
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Suppress("UNCHECKED_CAST")
@OptIn(FlowPreview::class)
public interface MviCore<ROOT> {

    public val state: StateFlow<ROOT>

    public fun <WISH : Feature.Wish> want(tag: FeatureTag, wish: WISH)

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