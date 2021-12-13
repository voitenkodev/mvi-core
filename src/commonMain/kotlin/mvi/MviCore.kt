package mvi

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import mvi.additional.AffectApi
import mvi.additional.ValueApi
import mvi.additional.WantApi
import mvi.core.FeatureMap
import mvi.core.MviCoreImpl
import mvi.core.SharingMap

public interface MviCore<Root> : WantApi<Root>, ValueApi<Root>, AffectApi<Root> {

    public interface FeatureTag

    public override val root: StateFlow<Root>

    public override fun <Sync : Feature.Wish.Sync> want(tag: FeatureTag, wish: Sync)

    public override fun <Async : Feature.Wish.Async> want(tag: FeatureTag, wish: Async)

    public override fun <Side : Feature.Wish.Side> want(tag: FeatureTag, wish: Side)

    public override fun <Side : Feature.Wish.Side> side(tag: FeatureTag): Flow<Side>?

    public override fun affect(lambda: WantApi<Root>.(root: Root) -> Unit): MviCore<Root>

    public override fun <State : Feature.State> affect(
        tag: FeatureTag, lambda: WantApi<Root>.(State) -> Unit
    ): MviCore<Root>

    @Suppress("UNCHECKED_CAST")
    public class Builder<Root>(private val root: Root) {
        private val features: FeatureMap = hashMapOf()
        private val pushing: SharingMap<Root> = hashMapOf()

        public fun <Async : Feature.Wish.Async, Sync : Feature.Wish.Sync, Side : Feature.Wish.Side, State : Feature.State> feature(
            tag: FeatureTag,
            feature: (Root) -> Feature<Async, Sync, Side, State>,
            updateRoot: Root.(State) -> Root
        ): Builder<Root> {
            features[tag] = feature.invoke(root)
            (updateRoot as? Root.(Feature.State) -> Root)?.let { this.pushing[tag] = it }
            return this
        }

        public fun build(scope: CoroutineScope): MviCore<Root> =
            MviCoreImpl(root, scope, features, pushing)
    }
}