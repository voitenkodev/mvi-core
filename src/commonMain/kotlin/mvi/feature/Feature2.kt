package mvi.feature

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*

public abstract class Feature2<WISH : Feature.Wish, EFFECT : Feature.Effect, STATE : Feature.State, NEWS : Feature.News>(
    initial: STATE,
    private val actor: Actor<WISH, STATE, EFFECT>,
    private val reducer: Reducer<EFFECT, STATE>,
    private val newsPublisher: (NewsPublisher<EFFECT, STATE, NEWS>)? = null,
) : Feature<WISH, STATE, NEWS>(initial) {

    @FlowPreview
    public override fun want(w: WISH): Flow<STATE> = flowOf(w)
        .flatMapConcat {
            actor.invoke(it, state.value)
        }.onEach {
            newsPublisher?.invoke(it, _state.value)?.let { _news.send(it) }
        }.map {
            reducer.invoke(it, _state.value)
        }.onEach {
            _state.emit(it)
        }.distinctUntilChanged()
        .catch { throw it }
}