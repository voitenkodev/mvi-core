package mvi.feature

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

public abstract class Feature1<WISH : Feature.Wish, STATE : Feature.State, NEWS : Feature.News>(
    initial: STATE,
    private val reducer: Reducer<WISH, STATE>,
    private val newsPublisher: (NewsPublisher<WISH, STATE, NEWS>)? = null,
) : Feature<WISH, STATE, NEWS>(initial) {

    @FlowPreview
    public override fun want(w: WISH): Flow<STATE> = flowOf(w)
        .onEach {
            newsPublisher?.invoke(it, _state.value)?.let { _news.send(it) }
        }.map {
            reducer.invoke(it, _state.value)
        }.onEach {
            _state.emit(it)
        }.distinctUntilChanged()
        .catch { throw it }
}