package mvi.feature

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow

public typealias Actor<Wish, State, Effect> = (wish: Wish, state: State) -> Flow<Effect>

public typealias Reducer<Effect, State> = (effect: Effect, state: State) -> State

public typealias NewsPublisher<Effect, State, News> = (effect: Effect, state: State) -> News?

public abstract class Feature<WISH : Feature.Wish, STATE : Feature.State, NEWS : Feature.News>(
    initial: STATE
) {

    protected val _news: Channel<NEWS> = Channel(Channel.BUFFERED)
    public val news: Flow<NEWS> = _news.receiveAsFlow()

    protected val _state: MutableStateFlow<STATE> = MutableStateFlow(initial)
    public val state: StateFlow<STATE> get() = _state

    public abstract fun want(w: WISH): Flow<STATE>

    public interface Wish

    public interface Effect

    public interface State

    public interface News
}