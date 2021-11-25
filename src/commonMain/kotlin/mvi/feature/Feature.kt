package mvi.feature

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import mvi.IncorrectFeatureByTag
import mvi.MissingActorException

public typealias Actor<Async, State, Effect> = (async: Async, state: State) -> Flow<Effect>

public typealias Reducer<Sync, State> = (sync: Sync, state: State) -> State

public typealias NewsPublisher<Sync, State, News> = (sync: Sync, state: State) -> News?

public abstract class Feature<ASYNC : Feature.Wish.Async, SYNC : Feature.Wish.Sync, STATE : Feature.State, NEWS : Feature.News>(
    initial: STATE,
    private val actor: Actor<ASYNC, STATE, SYNC>? = null,
    private val reducer: Reducer<SYNC, STATE>,
    private val newsPublisher: (NewsPublisher<SYNC, STATE, NEWS>)? = null,
) {
    private val _news: Channel<NEWS> = Channel(Channel.BUFFERED)
    public val news: Flow<NEWS> = _news.receiveAsFlow()

    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initial)
    public val state: StateFlow<STATE> get() = _state

    public interface Wish {
        public interface Async : Wish
        public interface Sync : Wish
    }

    public interface State

    public interface News

    @FlowPreview
    public fun want(wish: ASYNC): Flow<STATE> = flowOf(wish).flatMapConcat {
        actor?.invoke(it, _state.value) ?: throw MissingActorException
    }.flatMapConcat { want(it) }

    public fun want(wish: SYNC): Flow<STATE> = flowOf(wish)
        .onEach { newsPublisher?.invoke(it, _state.value)?.let { _news.send(it) } }
        .map { reducer.invoke(it, _state.value) }
        .onEach { _state.emit(it) }
        .distinctUntilChanged()
        .catch { throw IncorrectFeatureByTag }
}