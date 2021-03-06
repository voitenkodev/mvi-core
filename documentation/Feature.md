# Feature

### Example of "Input Field" Feature.

https://user-images.githubusercontent.com/93656470/145011161-73e31c50-4879-4c41-99e0-f8ddf43bd613.mp4

### After looking a video we can to see:

Collapse state:

1) Number "1"
2) Notion Field (in collapse state) "You need to put title of event"

Expand State:

1) Input Field
2) Error Field "ops, u forgot to put title"
3) Placeholder "Title..."

The `State` of this view should be like this one:

```kotlin
 data class State(
    val expander: Expander = Expander(),
    val input: Input = Input(),
    val error: Error = Error()
) : Feature.State {

    data class Error(
        val text: String = "",
        val isShowed: Boolean = false
    )

    data class Input(
        val placeholder: String = "",
        val text: String = "",
    )

    data class Expander(
        val isOpened: Boolean = false,
        val number: String = "",
        val notes: String = "",
        val expandHeight: Int? = null,
    )
}
```

And also:

- We don't have any async events.
- We don't have any single events.

Lets put type of `Async` and `Side` wish's like `Nothing`.

We need just to add sync events for this view. It looks like:

```kotlin
sealed class Sync : Wish.Sync {
    object Expand : Sync()
    object Collapse : Sync()
    data class SetText(val text: String) : Sync()
    object ShowError : Sync()
    object HideError : Sync()
}
```

It remains to add parser for `Sync` wish's to new `State`, it has name `SyncReducer`.

```kotlin
class SyncReducerImpl : SyncReducer<Sync, State> {
    override fun invoke(wish: Sync, state: State) = when (wish) {
        is Sync.SetText -> state.copy(input = state.input.copy(text = wish.text))
        is Sync.Collapse -> state.copy(expander = state.expander.copy(isOpened = false))
        is Sync.Expand -> state.copy(expander = state.expander.copy(isOpened = true))
        is Sync.HideError -> state.copy(error = state.error.copy(isShowed = false))
        is Sync.ShowError -> state.copy(error = state.error.copy(isShowed = true))
    }
}
```

For to finish this Feature, need to merge all blocks in one Class.

- Class needs to extend from `Feature<Nothing, Sync, Nothing, State>`.
- As parameter in constructor: `InitialState` + `SyncReducer`.

```kotlin
class ExampleInputFeature(
    initial: State = State()
) : Feature<Nothing, ExampleInputFeature.Sync, Nothing, ExampleInputFeature.State>(
    initial = initial,
    syncReducer = SyncReducerImpl()
) {

    sealed class Sync : Wish.Sync {
        object Expand : Sync()
        object Collapse : Sync()
        data class SetText(val text: String) : Sync()
        object ShowError : Sync()
        object HideError : Sync()
    }

    data class State(
        val expander: Expander = Expander(),
        val input: Input = Input(),
        val error: Error = Error()
    ) : Feature.State {

        data class Error(
            val text: String = "",
            val isShowed: Boolean = false
        )

        data class Input(
            val placeholder: String = "",
            val text: String = "",
        )

        data class Expander(
            val isOpened: Boolean = false,
            val number: String = "",
            val notes: String = "",
            val expandHeight: Int? = null,
        )
    }

    class SyncReducerImpl : SyncReducer<Sync, State> {
        override fun invoke(wish: Sync, state: State) = when (wish) {
            is Sync.SetText -> state.copy(input = state.input.copy(text = wish.text))
            is Sync.Collapse -> state.copy(expander = state.expander.copy(isOpened = false))
            is Sync.Expand -> state.copy(expander = state.expander.copy(isOpened = true))
            is Sync.HideError -> state.copy(error = state.error.copy(isShowed = false))
            is Sync.ShowError -> state.copy(error = state.error.copy(isShowed = true))
        }
    }
}

```

## An additional items of Feature.

### If we need to call something async. We need:

- add Wish `Async`.
- add `AsyncReducer<Async, State, Sync>`, which maps async events to sync.

```kotlin
sealed class Async : Wish.Async {
    object CallSomethingAsync : Async()
}
```

- It needs to add in constructor of `Feature`. (`asyncReducer = AsyncReducerImpl()`)

```kotlin
class AsyncReducerImpl : AsyncReducer<Async, State, Sync> {
    override fun invoke(wish: Async, state: State) = when (wish) {
        is Async.CallSomethingAsync -> flow {

            delay(2000)

            if (Random.nextBoolean()) {
                // Success request
                emit(Sync.SetColor(Color.Green))
            } else {
                // Error request
                emit(Sync.SetColor(Color.Red))
            }
        }
    }
}
```

After that to add it in current `Feature`.

### If we have some wish's which could trigger an another wish's. Let's use `AffectConventions<State>`.

For example: We need to show toast message every success status from request.

Firstly let's add `Side` (Single) wish.

```kotlin
sealed class Side : Wish.Side {
    object ShowToast : Side()
}
```

After that we can use it in `AffectConventions`. Some like this one:
 - It needs to add in constructor of `Feature`. (`affectConventions = AffectConventionsImpl()`)
```kotlin
class AffectConventionsImpl : AffectConventions<State> {
    override fun invoke(sync: Wish, state: State): Flow<Wish> = flow {
        if (sync is Async.CallSomethingAsync && state.color == Color.Green) {
            emit(Side.ShowToast)
        }
    }
}
```

