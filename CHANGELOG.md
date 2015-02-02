* Orient 2 compatibility
* DocumentPool use OPartitionedDatabasePool instead of deprecated ODatabaseDocumentPool
* (breaking) Default pools implementation now rely on document pool only (no more separate transactions for each type).
This allows using all connection types in one transaction and all changes will be visible to each type.
* (breaking) Connection initialization moved from DatabaseManager to pool implementation (graph and object
 connections update scheme on first connection). Now each pool did db connection on startup to check connection and
 let orient properly update scheme on start.
* (breaking) TxTemplate and SpecificTxTemplate now propagate only runtime exceptions and wrap checked exceptions
 into runtime. This simplifies general usage (also, most orient exceptions are runtime)
* ObjectCrudMixin: added multiple detachAll methods (in orient 2 object proxies doesn't work outside of transaction,
so detaching is more important)
* DocumentCrudMixin: added create method to create document inside of transaction (in orient 2 it's now impossible to
create document outside of transaction, but document changes doesn't require ongoing transaction)
* Remove deprecated finders auto scanning

### 2.1.0 (2015-01-06)
* Finders now managed by guice and any additional aop could be applied. Abstract beans could define finder methods.
(integration implemented in [external library](https://github.com/xvik/guice-ext-annotations))
* Finders auto scan api deprecated: now finder bean could be marked with @ProvidedBy and rely on guice JIT resolution
* Fix transactional aop warnings on jdk8
* Fix finder descriptors cache
* Add ability to disable finder descriptors cache using property and method to clear current cache

### 2.0.2 (2014-11-26)
* Update orient (1.7.9 > 1.7.10 ([changes](https://github.com/orientechnologies/orientdb/issues?q=is%3Aissue+milestone%3A1.7.10+is%3Aclosed)))
* Fix single value vertex return from finder (projection should not occur)
* Add explicit scheme synchronization for AutoScanSchemeInitializer and PackageSchemeInitializer (useful for dynamic environments, like tests)

### 2.0.1 (2014-11-19)
* Generics resolution extracted to separate lib [generics-resolver](https://github.com/xvik/generics-resolver)

### 2.0.0 (2014-11-05)
* Support different users (for example, to init schema with more powerful user and to use orient security model). Breaks pools api compatibility
* AutoScanFinderModule accept multiple packages for scanning
* Add document and vertex projection for single field (works for plain and array result): useful for count (or other aggregation functions) or selecting single field
* Support inheritance for finder beans and interfaces (mixins) and generics recognition through all hierarchy
* Support generic type query placeholders (finder class generic used as query placeholder)
* Add delegate finders (annotated interface method delegates execution to guice bean method)
* Add crud mixins for object and document finders: DocumentCrudMixin, ObjectCrudMixin
* Add pagination mixin for object and document finders: PaginationMixin  

### 1.1.1 (2014-10-01)
* Fix finder behaviour: empty collection conversion to single element

### 1.1.0 (2014-09-27)
* Fix finder module available db types detection
* Add query placeholders support for finders
* Update orient (1.7.8 > 1.7.9) - [important hotfix](https://groups.google.com/forum/#!topic/orient-database/vPF85I5Blts)
* Add Optional support as finder return type (jdk or guava Optional)
* Add orient module option to disable database auto creation
* Update guice (4.beta4 -> 4.beta5)

### 1.0.3 (2014-09-15)

* Fix remote connection support (avoid database creation)

### 1.0.2 (2014-08-16)

* Fix pmd/checkstyle warnings

### 1.0.1 (2014-08-05)

* Fix generated pom

### 1.0.0 (2014-08-05)

* Added dynamic finders (as separate module)
* Module configuration moved from constructor to chained methods
* Default object scheme initializers now support graph compatible scheme creation
* Remove dependency on reflections library 
* Important bugs fixed as a result of better tests coverage

### 0.9.0 (2014-07-29)

* Initial release