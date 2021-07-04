### 4.1.0 (2021-07-05)
* Orient 3.1 compatibility (updated to 3.1.12)

Known issues are the same as in previous release.

### 4.0.0 (2021-07-04)
* Update to guice 5.0.1
* (breaking) Update to Orient 3.0.38
    - Dropped java 1.6 support
    - Bound connection objects changed:
        - Provider<OObjectDatabaseTx> -> Provider<ODatabaseObject>
        - Provider<ODatabaseDocumentTx> -> Provider<ODatabaseDocument>
    - With new orient api (unified document and graph api), graph pools remains for legacy thinkerpop 2 only
    - orientdb-object and orientdb-graph dependencies become optional (no more exclusions, directly specify required libs instead)
    - OrientModule:
        - As before, single uri passed which includes both system path (plocal) and db name
            Internally, it splits to create OrientDB object and open db connections with db name (new api)
        - Provider<OrientDB> may be injected now to manually open connections (OrientDB object is a new api object used for orient connections (or pools) creation)
        - OrientDBFactory bean may be injected to get access to database configuration (url, user, etc.)
            Before this information was bound with guice constants
        - New option autoCreateRemoteDatabase(user, pass, type) is available to automatically create rmote databases (mainly for tests)
        - New option withConfig(conf) allows specifying custom config for OrientDB object (by default, OrientDBConfig.defaultConfig() used)
    - Remote database creation indirect configuration support for tests (instead of direct config in module):
        OrientDBFactory.enableAutoCreationRemoteDatabase(user, pass, type)  
        OrientDBFactory.disableAutoCreationRemoteDatabase()     
    - Pools contract changed: PoolManager.start(String) now accepts database name instead of complete url    
    - DocumentPool:
        - Now use new ODatabasePool pool implementation (instead of OPartitionedDatabasePoolFactory)        
        - Removed pool recovery logic (since 1.x: when pool failed to provide correct connection it was re-created).
            Assuming new pool is more stable.             
        - Different user credentials (UserManager.executeWithUser) will create separate connection outside the pool.
    - Object api now may be used to properly remove graph nodes with edges (because graph consistency is on document level now)
    - Remove useHashIndex attribute in @FullTextIndex schema annotation 
      (OClass.INDEX_TYPE.FULLTEXT_HASH_INDEX removed in orient 3.0.38:
      https://github.com/orientechnologies/orientdb/commit/bfceffa50d3f708f5c1c05dab1f082861df01e12#diff-3371617e7407306ad4397a0835f64175314b828295d5ad88891c051915d2d8aaL226)

Known issues:
    - Repositories continue using DEPRECATED commands api (have no time now to finish rewriting into new api).
        Some bugs of object api were fixed with OObjectDatabaseTxFixed (which use fixed commands wrapper: OCommandSQLPojoWrapperFixed)
        and ObjectPool would create its instances instead of OObjectDatabaseTx.
    - Repositories does not support conversion into new types like OVertex, OEdge (only thinkerpop 2 classes, as before) 
        or new streams.
    - Remote execution of function through object api might return collection of nulls 
        (special exception will be thrown indicating this case)
    - Live query listener unsubscription method may not be called with remote connection

### 3.3.2 (2018-04-02)
* Guice 4.2.0 compatibility

### 3.3.1 (2018-01-17)
* Fix ignoreNullValues flag support for @Index and @CompositeIndex scheme extensions (#16)
(breaking) since orient 2.2 ignoreNullValues is false by default, but @Index and @CompositeIndex annotations
use ignoreNullValues = true by default and it was not applied properly before. Now flag will apply properly, 
which may change existing indexes. One consequence I know is composite index with ignoreNullValues = true is not
used for single field searches (don't know why). 

### 3.3.0 (2017-11-07)
* Update to orient 2.2
* Support [custom types installation](http://orientdb.com/docs/2.2/Object-2-Record-Java-Binding.html#custom-types)
  with new method: OrientModule#withCustomTypes(OObjectSerializer...) (#14)
* Support external connection (thread bound) re-use: when transaction started with TxConfig.external() thread bound
  connection used instead of new connection. Commits and rollbacks are not applied automatically: supposed that manual 
  connection is completely managed externally. Useful in case when already existing connection must be (re)used in guice.
    - (breaking) custom TransactionManager and/or PoolManager implementations must be updated to support external transactions
* New service RecordConverter may be used directly to:
    - convert ODocument to object or vertex 
    - apply default repository method converter (e.g. apply projection)
* @Listen parameter extension changes:
    - (breaking) no longer could be used with @Query (because it does not work properly for remote connection and not guaranteed by the documentation) 
    - wraps provided listener with an external transaction (thread bound connection (used by orient) could be used in guice)    
* AsyncQuery changes:
    - custom AsyncQueryListener interface can be used instead of OCommandResultListener to apply automatic result conversion 
      (with RecordConverter to mimic the same behaviour as for usual method result)
    - new `blocking` attribute to switch execution into non blocking mode (OSQLNonBlockingQuery).
       Non blocking methods may return Future to monitor async execution (but not able to cancel!).
    - exception appeared inside async listener is intercepted and logged but not propagated:
       only false returned to stop query processing. This is required for proper orient connection handling
       (it does not expect exceptions in some modes)          
* Add @LiveQuery repository extension (live queries support)
    - required orient OLiveResultListener parameter must be used with @Listen annotation
    - custom LiveQueryListener interface can be used instead of OLiveResultListener to apply automatic result conversion 
      (with RecordConverter to mimic the same behaviour as for usual method result).
* Support repository result projection on collections (e.g. @Query("select name from Model") List\<String> select()).           

### 3.2.0 (2016-09-25)
* Update to orient 2.1
* Update to guice 4.1
* Avoid deprecated OCommandRequest api usage (deprecated in 2.1): 
@FetchPlan, @Limit and @Timeout extensions now modifies query instead of using OCommandRequest setters
* (behavior change) @EdgeType and @VertexType extensions now assign superclass directly to annotated type (or not if hierarchy already contains required type)
* Call activateOnCurrentThread() in pools for each connection obtain (e.g. context.getConnection()) to guarantee proper db instance bound to thread
* Fix guice circular proxy (between transaction manager and pools)
* Fix child injector and private modules support for RepositoryModule (#7)
* Fix playframework compatibility (#10)

### 3.1.1 (2015-08-16)
* Improve graph connection recognition on repository methods: recognize all graph types implementing Vertex or Edge (e.g. OrientVertex, OrientEdge)

### 3.1.0 (2015-07-04)
* Add @RidElVar parameter extension to bind rid directly into query from any source (string, object, document, vertex or collections).
* Add @CaseInsensitive scheme field extension: sets collate ci for case insensitive fields comparison in queries. Also should be used for creation of ci indexes.
* Add ignoreNullValues option support for @Index and @CompositeIndex scheme extensions
* Add @FulltextIndex scheme field extension for fulltext index definitions
* Add @LuceneIndex and @CompositeLuceneIndex scheme extensions for fulltext lucene index definitions
* Fix modules usage in child injector
* Improve connection hint support in repository annotations: now hint is always used in priority, which allows to write more complex result converters
* Add ObjectVertexCrud mixin to correctly work with vertexes from object api
* Add EdgesSupport and EdgeTypeSupport mixins to simplify work with edges using object api
* Fix temporal id problem for detached objects (affects ObjectCrud, ObjectVertexCrud and @DetachResult)
* ObjectCrud: add getAllAsList, objectToDocument and documentToObject methods

### 3.0.2 (2015-05-16)
* Orient 2.0.9 compatibility: removed @LockStrategy extension, because lock setter removed from OCommandRequest (use sql [LOCK statement](http://orientdb.com/docs/last/orientdb.wiki/SQL-Query.html) instead)

### 3.0.1 (2015-05-05)
* Fix stale connections in pools
* Fix sometimes redundant connection in db auto creation check
* Fix schema extensions for remote connection (more often schema synchronization)

### 3.0.0 (2015-03-14)
* Orient 2 compatibility
* DocumentPool use OPartitionedDatabasePool instead of deprecated ODatabaseDocumentPool
* (breaking) Default pools implementation now rely on document pool only (no more separate transactions for each type).
This allows using all connection types in one transaction and all changes will be visible to each type.
* (breaking) Connection initialization moved from DatabaseManager to pool implementation (graph and object
 connections update scheme on first connection). Now each pool did db connection on startup to check connection and
 let orient properly update scheme on start.
* (breaking) TxTemplate and SpecificTxTemplate now propagate only runtime exceptions and wrap checked exceptions
 into runtime. This simplifies general usage (most orient exceptions are runtime)
* ObjectCrudMixin: added multiple detachAll methods (in orient 2 object proxies doesn't work outside of transaction,
so detaching is more important)
* DocumentCrudMixin: added create method to create document inside of transaction (in orient 2 it's now impossible to
create document outside of transaction, but document changes doesn't require ongoing transaction)
* Add PersistentContext class, which combines connection provider, both templates and provide access to
TransactionManager. It should be used instead of low level staff (simplifies usage)
* Remove deprecated finders auto scanning
* (breaking) Finders rewritten to extensions based architecture. Finder module renamed to repository to follow spring-data
style (as well known and very similar realization). Not owned annotations (@Named, @Finder, @FirstResult, @MaxResults)
replaced with spring-data like or orient specific annotations (@Param, @Query, @Function, @Skip, @Limit).
Many annotations and classes renamed due to module rename.
* Many new repository features
* Ability to change user inside transaction (for example for security checks).
* Retry annotation to catch ONeedRetryException and re-execute method.
* (breaking) schemeMappingPackage option removed from OrientModule. Shortcut modules now must be used together with
OrientModule and not substitute it. Modules renamed to AutoScanSchemeModule and PackageSchemeModule.
* Model class to scheme mapper, using orient default mapper, but with extensions support (custom annotations).
AbstractObjectInitializer now generic enough to use for custom classpath filtering method.

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