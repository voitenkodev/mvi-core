#Feature

## Sipmle option:

### This is api of simple `Feature`.
[feature-simple-structure](https://user-images.githubusercontent.com/93656470/145011641-b20a5a06-14d4-41f7-a1bf-d4a51100abc3.jpg)

## Example of "Input Field" Feature.
https://user-images.githubusercontent.com/93656470/145011161-73e31c50-4879-4c41-99e0-f8ddf43bd613.mp4


After looking a video we can to see: 
Collapse state: 
1) Number
2) Notion Field (in collapse state)

Expand State:
3) Input Field
4) Error Field (Red text)

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

### feature simple structure:
[<img src="screenshots/feature-simple-structure.jpg" width="321" height = "384" />](screenshots/feature-simple-structure.jpg)
### feature expand structure:


[<img src="screenshots/feature-structure.jpg" width="321" height = "384" />](screenshots/feature-structure.jpg)

