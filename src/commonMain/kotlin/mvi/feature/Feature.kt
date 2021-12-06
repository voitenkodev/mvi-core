package mvi.feature

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import mvi.IncorrectFeatureByTag
import mvi.MissingActorException

public typealias AsyncReducer<Async, State, Sync> = (wish: Async, state: State) -> Flow<Sync>
public typealias SyncReducer<Sync, State> = (wish: Sync, state: State) -> State
public typealias AffectConventions<State> = (wish: Feature.Wish, state: State) -> Flow<Feature.Wish>

@OptIn(FlowPreview::class)
public abstract class Feature<Async : Feature.Wish.Async, Sync : Feature.Wish.Sync, Side : Feature.Wish.Side, State : Feature.State>(
    initial: State,
    private val asyncReducer: AsyncReducer<Async, State, Sync>? = null,
    private val syncReducer: SyncReducer<Sync, State>,
    private val affectConventions: (AffectConventions<State>)? = null,
) {

    private val _side: Channel<Side> = Channel(Channel.BUFFERED)
    public val side: Flow<Side> = _side.receiveAsFlow()

    private val _state = MutableStateFlow(initial)
    public val state: StateFlow<State> get() = _state.asStateFlow()

    public interface State

    public interface Wish {
        public interface Async : Wish
        public interface Sync : Wish
        public interface Side : Wish
    }

    public fun want(wish: Async, scope: CoroutineScope): Job = flowOf(wish)
        .affectConvention(wish, scope)
        .flatMapConcat { asyncReducer?.invoke(it, _state.value) ?: throw MissingActorException }
        .onEach { want(it, scope) }
        .launchIn(scope)

    public fun want(wish: Sync, scope: CoroutineScope): Job = flowOf(wish)
        .affectConvention(wish, scope)
        .map { syncReducer.invoke(it, _state.value) }
        .distinctUntilChanged()
        .onEach { _state.emit(it) }
        .catch { throw IncorrectFeatureByTag }
        .launchIn(scope)

    public fun want(wish: Side, scope: CoroutineScope): Job = flowOf(wish)
        .affectConvention(wish, scope)
        .map { _side.send(wish) }
        .launchIn(scope)

    private fun <T> Flow<T>.affectConvention(wish: Wish, scope: CoroutineScope): Flow<T> = onEach {
        affectConventions?.invoke(wish, _state.value)
            ?.onEach { distributeAffect(it, scope) }
            ?.launchIn(scope)
    }

    @FlowPreview
    @Suppress("UNCHECKED_CAST")
    private fun distributeAffect(w: Wish, scope: CoroutineScope) =
        (w as? Sync)?.let { want(it, scope) }
            ?: (w as? Async)?.let { want(it, scope) }
            ?: (w as? Side)?.let { want(it, scope) }
            ?: flowOf(Unit)
}