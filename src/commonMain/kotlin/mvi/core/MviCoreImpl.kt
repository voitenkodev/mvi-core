package mvi.core

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import mvi.Feature
import mvi.MviCore
import mvi.additional.WantApi

@Suppress("UNCHECKED_CAST")
@OptIn(FlowPreview::class)
internal class MviCoreImpl<Root> internal constructor(
    root: Root,
    private val scope: CoroutineScope,
    private val features: FeatureMap = hashMapOf(),
    private val sharing: SharingMap<Root> = hashMapOf(),
    private val affectFeature: AffectMap<Root> = hashMapOf(),
    private var affect: Affect<Root>? = null
) : MviCore<Root> {

    private val _state = MutableStateFlow(root)
    public override val root: StateFlow<Root> get() = _state.asStateFlow()

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

    public override fun <STATE : Feature.State> affect(
        tag: MviCore.FeatureTag, lambda: WantApi<Root>.(STATE) -> Unit
    ): MviCoreImpl<Root> = apply {
        (lambda as? MviCoreImpl<Root>.(Feature.State) -> Unit)
            ?.let { this.affectFeature[tag] = it }
    }

    override fun affect(lambda: WantApi<Root>.(root: Root) -> Unit): MviCore<Root> = apply {
        affect = lambda
    }

    private fun CoroutineScope.launchFeatures() = features.onEach { item ->
        item.value.state.onEach {
            affectFeature[item.key]?.invoke(this@MviCoreImpl, it)
            sharing[item.key]?.invoke(root.value, it)?.let { _state.value = it }
            affect?.invoke(this@MviCoreImpl, _state.value)
        }.launchIn(this)
    }
}

@OptIn(FlowPreview::class)
internal typealias FeatureMap = HashMap<MviCore.FeatureTag, Feature<*, *, *, *>>
internal typealias SharingMap<ROOT> = HashMap<MviCore.FeatureTag, (ROOT, Feature.State) -> ROOT>
internal typealias AffectMap<ROOT> = HashMap<MviCore.FeatureTag, MviCoreImpl<ROOT>.(Feature.State) -> Unit>
internal typealias Affect<ROOT> = MviCoreImpl<ROOT>.(root: ROOT) -> Unit
