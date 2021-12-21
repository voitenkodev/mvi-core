[comment]: <> (TODO)

### Root

Out model of whole screen looks like:

```kotlin
data class NewEventState(
        val title: ExpandInputFeature.State = ExpandInputFeature.State(
            error = ExpandInputFeature.State.Error(
                text = "ops, u forgot to put title",
                isShowed = false
            ),
            input = ExpandInputFeature.State.Input(
                placeholder = "Title...",
                text = "",
                isFocused = false,
                readOnly = false
            ),
            expander = ExpandInputFeature.State.Expander(
                isOpened = false,
                number = "1",
                notes = "You need to put title of event",
                expandHeight = (56 * 1.7).toInt(),
            )
        ),
        val description: ExpandInputFeature.State = ExpandInputFeature.State(
            error = ExpandInputFeature.State.Error(
                text = "ops, u forgot to put description",
                isShowed = false
            ),
            input = ExpandInputFeature.State.Input(
                placeholder = "Description...",
                text = "",
                isFocused = false,
                readOnly = false
            ),
            expander = ExpandInputFeature.State.Expander(
                isOpened = false,
                number = "2",
                notes = "After that add description",
                expandHeight = (56 * 3.2).toInt()
            )
        )
```

### Feature's
After splitting it by features we have two features:

- `val title`
- `val description`

### TAG's

The next step is TAG's, for to identify simular geatures.

```kotlin 
  enum class TAG : MviCore.FeatureTag {
        TITLE, 
        DESCRIPTION
  }
```

###  MviCore

After combininig the fatures, using Builder, we will see:

```kotlin
 override val processor = MviCore.Builder(root = NewEventState())
        .feature(
            tag = TAG.TITLE,
            feature = { ExpandInputFeature(it.title) },
            updateRoot = { copy(title = it) }
        ).feature(
            tag = TAG.DESCRIPTION,
            feature = { ExpandInputFeature(it.description) },
            updateRoot = { copy(description = it) }
        ).build(viewModelScope)
```
