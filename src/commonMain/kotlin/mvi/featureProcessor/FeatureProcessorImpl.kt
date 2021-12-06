package mvi.featureProcessor

import kotlinx.coroutines.CoroutineScope
import mvi.core.FeatureMap
import mvi.core.MviCore
import mvi.core.MviCoreImpl
import mvi.core.SharingMap
import mvi.feature.Feature

@Suppress("UNCHECKED_CAST")
internal data class FeatureProcessorImpl<Root>(private val root: Root) : FeatureProcessor<Root> {
    private val features: FeatureMap = hashMapOf()
    private val pushing: SharingMap<Root> = hashMapOf()

    override fun launchIn(scope: CoroutineScope): MviCoreImpl<Root> =
        MviCoreImpl(root, scope, features, pushing)

    override fun <Async : Feature.Wish.Async, Sync : Feature.Wish.Sync, Side : Feature.Wish.Side, State : Feature.State> feature(
        tag: MviCore.FeatureTag,
        feature: (Root) -> Feature<Async, Sync, Side, State>,
        updateRoot: Root.(State) -> Root
    ): FeatureProcessor<Root> {
        features[tag] = feature.invoke(root)
        (updateRoot as? Root.(Feature.State) -> Root)?.let { this.pushing[tag] = it }
        return this
    }
}