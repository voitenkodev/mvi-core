package mvi.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import mvi.feature.Feature
import mvi.featureProcessor.FeatureProcessor
import mvi.featureProcessor.FeatureProcessorImpl

@Suppress("UNCHECKED_CAST")
@OptIn(FlowPreview::class)
public class MviCoreImpl<ROOT> internal constructor(
    root: ROOT,
    private val scope: CoroutineScope,
    private val features: FeatureMap = hashMapOf(),
    private val onUpdate: SharingMap<ROOT> = hashMapOf(),
    private val automaticallyMap: ObtainMap<ROOT> = hashMapOf()
) : MviCore<ROOT> {

    private val _state = MutableStateFlow(root)
    public override val state: StateFlow<ROOT> get() = _state.asStateFlow()

    init {
        scope.launchFeatures()
    }

    override fun <ASYNC : Feature.Wish.Async> want(tag: MviCore.FeatureTag, async: ASYNC) {
        (features[tag] as? Feature<ASYNC, *, *, *>)?.want(async)?.launchIn(scope)
    }

    override fun <SYNC : Feature.Wish.Sync> want(tag: MviCore.FeatureTag, sync: SYNC) {
        (features[tag] as? Feature<*, SYNC, *, *>)?.want(sync)?.launchIn(scope)
    }

    override fun <NEWS : Feature.News> news(tag: MviCore.FeatureTag): Flow<NEWS>? {
        return (features[tag] as? Feature<*, *, *, NEWS>)?.news
    }

    public override fun <STATE : Feature.State> postProcessing(
        tag: MviCore.FeatureTag, automatically: MviCoreImpl<ROOT>.(STATE) -> Unit
    ): MviCoreImpl<ROOT> = apply {
        (automatically as? MviCoreImpl<ROOT>.(Feature.State) -> Unit)
            ?.let { this.automaticallyMap[tag] = it }
    }

    @FlowPreview
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

@FlowPreview
internal typealias FeatureMap = HashMap<MviCore.FeatureTag, Feature<*, *, *, *>>
internal typealias ObtainMap<ROOT> = HashMap<MviCore.FeatureTag, MviCoreImpl<ROOT>.(Feature.State) -> Unit>
internal typealias SharingMap<ROOT> = HashMap<MviCore.FeatureTag, (ROOT, Feature.State) -> ROOT>