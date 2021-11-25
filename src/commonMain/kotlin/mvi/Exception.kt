package mvi

public object MissingActorException :
    Throwable("Missing `Actor` implementation, in current feature. Please, add it!")

public object IncorrectFeatureByTag :
    Throwable("Mvi Processor have not this type of `Feature` by this `TAG`")
