**Mvi-core**

### How to start

Need to add dependency to gradle ->

```kotlin
dependencies {
    //...
    implementation("io.github.voitenkodev:mvi-core:1.0")
    //...
}
```


```kotlin
class ExampleFeature(
    initial: State = State()
) : Feature<ExampleFeature.Wish, ExampleFeature.State, ExampleFeature.News>(
    initial = initial,
    actor = ActorImpl(),
    reducer = ReducerImpl(),
    newsPublisher = NewsPublisherImpl()
) {

    sealed class Wish : Feature.Wish {
        object Expand : Wish()
        object Collapse : Wish()
        object ShowError : Wish()
        object HideError : Wish()
        data class SetText(val text: String) : Wish()
        object Pick : Wish()
    }

    sealed class News : Feature.News {    }

    data class State(
            val expander: Expander = Expander(),
            val input: Input = Input(),
            val error: Error = Error()
        ) : Feature.State {

            data class Error(
                val text: String = "You forgot to put text",
                val isShowed: Boolean = false
            )

            data class Input(
                val placeholder: String = "Text...",
                val text: String = "",
                val isFocused: Boolean = false,
                val readOnly: Boolean = false
            )

            data class Expander(
                val isOpened: Boolean = false,
                val number: String = "1",
                val notes: String = "You need to put text",
                val expandHeight: Int? = null,
            )
        }

    class ActorImpl : Actor<Wish, State> {
        override fun invoke(wish: Wish, state: State) = when (wish) {
            else -> flowOf(wish)
        }
    }

    class ReducerImpl : Reducer<Wish, State> {
        override fun invoke(wish: Wish, state: State) = when (wish) {
            is Wish.SetText -> state.copy(input = state.input.copy(text = wish.text))
            Wish.Collapse -> state.copy(expander = state.expander.copy(isOpened = false))
            Wish.Expand -> state.copy(expander = state.expander.copy(isOpened = true))
            is Wish.ShowError -> state.copy(error = state.error.copy(isShowed = true))
            is Wish.HideError -> state.copy(error = state.error.copy(isShowed = false))
        }
    }

    class NewsPublisherImpl :
        NewsPublisher<Wish, State, News> {
        override fun invoke(effect: Wish, state: State) = when (effect) {
            else -> null
        }
    }
}
```

