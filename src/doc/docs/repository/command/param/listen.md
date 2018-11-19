# @Listen

!!! summary ""
    Command method param extension

Marks parameter as command listener. Must be used together with [@AsyncQuery](../asyncquery.md) 
or [@LiveQuery](../livequery.md) (exact annotation defines which listener interfaces may be used). 

Listener will be wrapped with an external transaction, so listener code could access orient connection instance, used for listener from guice (normal connection access).
