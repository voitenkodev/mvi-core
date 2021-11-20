**Mvi-core**


```kotlin
@Composable
fun ExampleScreen(vm: ExampleViewModel = viewModel()) {
    val state = vm.mviProcessor.state.collectAsState()
}
```

```kotlin
class ExampleViewModel : ViewModel() {

    val mviProcessor = MviCoreImpl.featureProcessor(ExampleRoot())
        .launchIn(viewModelScope)

    data class ExampleRoot(
        val text: String = "Hello World"
    )
}
```

```kotlin

```

```kotlin

```

```kotlin

```

```kotlin

```


