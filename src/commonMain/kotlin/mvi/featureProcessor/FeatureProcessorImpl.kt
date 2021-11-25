package mvi.featureProcessor

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import mvi.core.FeatureMap
import mvi.core.MviCore
import mvi.core.MviCoreImpl
import mvi.core.SharingMap
import mvi.feature.Feature

@Suppress("UNCHECKED_CAST")
@OptIn(FlowPreview::class)
internal data class FeatureProcessorImpl<ROOT>(private val root: ROOT) : FeatureProcessor<ROOT> {
    private val features: FeatureMap = hashMapOf()
    private val pushing: SharingMap<ROOT> = hashMapOf()

    override fun launchIn(scope: CoroutineScope): MviCoreImpl<ROOT> =
        MviCoreImpl(root, scope, features, pushing)

    override fun <ASYNC : Feature.Wish.Async, SYNC : Feature.Wish.Sync, STATE : Feature.State, NEWS : Feature.News> feature(
        tag: MviCore.FeatureTag,
        feature: (ROOT) -> Feature<ASYNC, SYNC, STATE, NEWS>,
        updateRoot: ROOT.(STATE) -> ROOT
    ): FeatureProcessor<ROOT> {
        features[tag] = feature.invoke(root)
        (updateRoot as? ROOT.(Feature.State) -> ROOT)?.let { this.pushing[tag] = it }
        return this
    }
}