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