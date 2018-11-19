# @Repository

!!! summary ""
    Delegate method param extension

Used to reference calling repository instance. 
For example, if you sure that calling repository will contain some mixin (e.g. base mixin used for all repositories) and you need to use its methods.

```java
void doSomething(@Repository ObjectCrud repository){...}
```

Good example is [Pagination](../../mixin/pagination.md) mixin (see how `PageSupportDelegate` reference `Pagination` mixin, which defines pagination queries and use them to compose page object).
