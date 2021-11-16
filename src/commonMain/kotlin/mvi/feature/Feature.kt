package mvi.feature

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*


public abstract class Feature<WISH : Feature.Wish, STATE : Feature.State, NEWS : Feature.News>(
    initial: STATE,
    private val actor: Actor<WISH, STATE>,
    private val reducer: Reducer<WISH, STATE>,
    private val newsPublisher: (NewsPublisher<WISH, STATE, NEWS>)? = null,
) {

    public interface Wish
    public interface State
    public interface News

    private val _state = MutableStateFlow(initial)
    public val state: StateFlow<STATE> get() = _state

    private val _news: Channel<NEWS> = Channel(Channel.BUFFERED)
    public val news: Flow<NEWS> = _news.receiveAsFlow()

    @FlowPreview
    public fun want(w: WISH): Flow<STATE> = flowOf(w)
        .flatMapConcat {
            it.className().log("[__Wish____]")
            actor.invoke(it, state.value)
        }.onEach {
            newsPublisher?.invoke(it, _state.value)?.let {
                it.className().log("[__News____]")
                _news.send(it)
            }
        }.map {
            reducer.invoke(it, _state.value)
        }.onEach {
            "${it.className().substringBefore("::")}::${it}".log("[__State___]")
            _state.emit(it)
        }.distinctUntilChanged()
        .catch {
            println("[__CRASH___] -> $it")
            throw it
        }

    private fun Any.log(msg: String) = println("$msg -> $this")
    private fun Any.className() = this::class.simpleName ?: "unknown"
}

public typealias Actor<Wish, State> = (wish: Wish, state: State) -> Flow<Wish>
public typealias Reducer<Wish, State> = (wish: Wish, state: State) -> State
public typealias NewsPublisher<Wish, State, News> = (wish: Wish, state: State) -> News?
