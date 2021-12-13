package mvi.additional

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import mvi.Feature
import mvi.MviCore

public interface ValueApi<Root> {

    public val root: StateFlow<Root>

    public fun <Side : Feature.Wish.Side> side(tag: MviCore.FeatureTag): Flow<Side>?
}