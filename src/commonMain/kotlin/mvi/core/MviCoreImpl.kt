package mvi.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import mvi.feature.Feature
import mvi.featureProcessor.FeatureProcessor
import mvi.featureProcessor.FeatureProcessorImpl

@Suppress("UNCHECKED_CAST")
@OptIn(FlowPreview::class)
public class MviCoreImpl<Root> internal constructor(
    root: Root,
    private val scope: CoroutineScope,
    private val features: FeatureMap = hashMapOf(),
    private val onUpdate: SharingMap<Root> = hashMapOf(),
    private val automaticallyMap: ObtainMap<Root> = hashMapOf()
) : MviCore<Root> {

    private val _state = MutableStateFlow(root)
    public override val state: StateFlow<Root> get() = _state.asStateFlow()

    init {
        scope.launchFeatures()
    }

    override fun <Async : Feature.Wish.Async> want(tag: MviCore.FeatureTag, wish: Async) {
        (features[tag] as? Feature<Async, *, *, *>)?.want(wish, scope)
    }

    override fun <Sync : Feature.Wish.Sync> want(tag: MviCore.FeatureTag, wish: Sync) {
        (features[tag] as? Feature<*, Sync, *, *>)?.want(wish, scope)
    }

    override fun <Side : Feature.Wish.Side> want(tag: MviCore.FeatureTag, wish: Side) {
        (features[tag] as? Feature<*, *, Side, *>)?.want(wish, scope)
    }

    override fun <Side : Feature.Wish.Side> side(tag: MviCore.FeatureTag): Flow<Side>? =
        (features[tag] as? Feature<*, *, Side, *>)?.side

    public override fun <STATE : Feature.State> postProcessing(
        tag: MviCore.FeatureTag, automatically: MviCoreImpl<Root>.(STATE) -> Unit
    ): MviCoreImpl<Root> = apply {
        (automatically as? MviCoreImpl<Root>.(Feature.State) -> Unit)
            ?.let { this.automaticallyMap[tag] = it }
    }

    private fun CoroutineScope.launchFeatures() = features.onEach { item ->
        item.value.state.onEach {
            automaticallyMap[item.key]?.invoke(this@MviCoreImpl, it)
            onUpdate[item.key]?.invoke(state.value, it)?.let { _state.value = it }
        }.launchIn(this)
    }

    public companion object {
        public fun <ROOT> featureProcessor(root: ROOT): FeatureProcessor<ROOT> =
            FeatureProcessorImpl(root)
    }
}

@OptIn(FlowPreview::class)
internal typealias FeatureMap = HashMap<MviCore.FeatureTag, Feature<*, *, *, *>>
internal typealias ObtainMap<ROOT> = HashMap<MviCore.FeatureTag, MviCoreImpl<ROOT>.(Feature.State) -> Unit>
internal typealias SharingMap<ROOT> = HashMap<MviCore.FeatureTag, (ROOT, Feature.State) -> ROOT>