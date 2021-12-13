package mvi.additional

import mvi.Feature
import mvi.MviCore

public interface AffectApi<Root> {

    public fun <State : Feature.State> affect(
        tag: MviCore.FeatureTag, lambda: WantApi<Root>.(State) -> Unit
    ): MviCore<Root>

    public fun affect(lambda: WantApi<Root>.(root: Root) -> Unit): MviCore<Root>
}