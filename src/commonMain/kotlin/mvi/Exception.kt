package mvi

public object MissingActorException :
    Throwable("Missing `AsyncReducer` implementation, in current feature. Please, add it in constructor of Feature!")

public object IncorrectFeatureByTag :
    Throwable("Mvi Processor have not this type of `Feature` by this `TAG`")
