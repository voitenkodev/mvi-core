package mvi.additional

import mvi.Feature
import mvi.MviCore

public interface WantApi<Root> {

    public fun <Sync : Feature.Wish.Sync> want(tag: MviCore.FeatureTag, wish: Sync)

    public fun <Async : Feature.Wish.Async> want(tag: MviCore.FeatureTag, wish: Async)

    public fun <Side : Feature.Wish.Side> want(tag: MviCore.FeatureTag, wish: Side)
}