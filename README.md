#ph-commons

Java 1.6+ Library with tons of utility classes required in most of ph-* projects.

#Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-commons</artifactId>
  <version>5.2.0</version>
</dependency>
```

#Coding conventions

The following list gives a short overview of special programming techniques that are used inside ph-commons

  * All interfaces are named starting with a capital 'I' followed by a second uppercase character (like in `IHasID`)
  * All enumerations are named starting with a capital 'E' followed by a second uppercase character (like in `EUnicodeBOM`)
  * All member variables are private or protected and use the Hungarian notation (like `aList`). The used prefixes are:
    * `a` for all kind of objects that do not fall into any other category
    * `b` for boolean variables
    * `c` for character variables
    * `d` for double variables
    * `e` for enum variables
    * `f` for float variables
    * `n` for byte, int, long and short variables
    * `s` for String variables
  * The scope of a field is indicated by either the prefix `m_` for instance (member) fields, and `s_` for static fields. A special case are "static final" fields which may omit this prefix and use only upper case character (e.g. `DEFAULT_VALUE` as in `public static final boolean DEFAULT_VALUE = true;`)
  * All methods returning collections (lists, sets, maps etc.) are usually returning copies of the content. This helps ensuring thread-safety (where applicable) but also means that modifying returned collections has no impact on the content of the "owning" object. In more or less all cases, there are "add", "remove" and "clear" methods available to modify the content of an object directly. All the methods returning copies of collections should be annotated with `@ReturnsMutableCopy`. In contrast if the inner collection is returned directly (for whatever reason) it should be annotated with `@ReturnsMutableObject`. If an unmodifiable collection is returned, the corresponding annotation is `@ReturnsImmutableObject` (e.g. for `Collections.unmodifiableList` etc.)
  * For all non primitive parameter the annotations `@Nonnull` or `@Nullable` are used, indicating whether a parameter can be `null` or not. Additionally for Strings and collections the annotation `@Nonempty` may be present, indicating that empty values are also not allowed. All these annotations have no impact on the runtime of an application. They are just meant as hints for the developers.
  * All logging is done via SLF4J. 
 

#Contents
In general I tried to make the source comments as usefull as possible. Therefore here only an alphabetic package list with the respective contents is shown:
  * `com.helger.commons` - The base package that contains very basic interface and a few classes like `CGlobal` and `GlobalDebug` which are in this package, to avoid cyclic package references. 
  * `com.helger.commons.aggregate` - contains a generic aggregator that cann aggregate multiple values to a single value.
  * `com.helger.commons.annotations` - contains all the Java annotations defined in this project. This include e.g. `@Nonempty`, `@ReturnsMutableCopy` or `@DevelopersNote`.
  * `com.helger.commons.base64` - contains the Base64 implementation from http://iharder.net/base64 in a slightly adopted version plus some utility methods in class `Base64Helper`.
  * `com.helger.commons.cache` - defines basic classes used for caching data
  * `com.helger.commons.cache.convert` - special version of a cache that uses a data converter to create the cachable content
  * `com.helger.commons.callback` - contains all kind of simple data structures that are to be used for callbacks and external exception handling. Additionally special forms of `Runnable` and `Callable` are present in this package.
  * `com.helger.commons.changelog` - contains everything needed for having a structured change log (= history of changes) of software components. A complete list of all changes can be found in class `ChangeLog` containing multiple entries.
  * `com.helger.commons.charset` - contains stuff necessary for simple String character set/encoding and decoding as well as an enumeration for Unicode BOM (Byte Order Mark).
  * `com.helger.commons.cleanup` - contains a central class to cleanup and/or reset most of the runtime data and caches used in this project. This is especially helpful in unit tests when testing this project.
  *  `com.helger.commons.codec` - contains a generic encode/decode interface and some basic implementations like ASCII85, ASCII Hex, DCT, Base64, LZW and RFC 1522. This package is based on `com.helger.commons.encode` package. 
  * `com.helger.commons.collections` - contains very basic helper classes for easy working with arrays (class `ArrayHelper`) and collections (class `ContainerHelper`). It also contains a local implementation of `Stack` called `NonBlockingStack` which does not use synchronization - this can be seen like the different between `ArrayList` (not synchronized) and `Vector` (synchronized).
  * `com.helger.commons.collections.attrs` - contains a generic attribute container which more or less is a `Map` with a modified API. This package contains both read-only as well as mutable versions of this map-based container.
terfaces.
  * `com.helger.commons.collections.convert` - a special package that contains array and container helper classes in combination with converters.   
  * `com.helger.commons.collections.flags` - contains as well a generic container which is more or less a `Set` with a modified API. This package was not proven to be useful, as it is a special version of the attribute container, just without a value. Therefore this package will be removed in the next major release.
  * `com.helger.commons.collections.iterate` - contains special iterators/enumerators for arrays and collections. It also contains iterators for single elements as well as for "no" elements.
  * `com.helger.commons.collections.list` - contains specialized `List` implementations.
  * `com.helger.commons.collections.multimap` - contains "multi maps" which are `Map`s which have other `Collection`s as values. This package contains common interfaces as well as implementations for different `Map` implementations (like `HashMap`, `TreeMap`, `LinkedHashMap`, `ConcurrentHashMap` and `WeakHashMap`).
  * `com.helger.commons.collections.pair` - contains a generic pair which is a combination of 2 values with potentially different types.
  * `com.helger.commons.collections.triple` - contains a generic triple which is a combination of 3 values with potentially different types.
  * `com.helger.commons.combine` - contains a generic combinator for Strings. This package is very basic.
  * `com.helger.commons.compare` - generic `Comparator` and `Comparable` extensions and base classes. Additionally the enumeration `ESortOrder` which defines 'ascending' and 'descending' is contained.
  * `com.helger.commons.concurrent` - contains some commonly used things for `Thread` and `ExecutorService` handling.
  * `com.helger.commons.concurrent.collector` - a generic "collector" which supports multiple inputs from multiple different threads and serializes this data stream for sequential processing (e.g. for mail queueing with a central sender).
  * `com.helger.commons.convert` - package containing unidirectional and bidirectional data converter interfaces.
  * `com.helger.commons.convert.collections` - a special package that contains array and container helper classes in combination with converters. This package will be removed in the next major release in favour of the `com.helger.commons.collections.convert` package. 
  * `com.helger.commons.crypto` - contains a small helper class to determine, if the Java crypto extensions are installed or not (based on OWASP).
  * `com.helger.commons.deadlock` - contains a thread deadlock detector based on the JMX `ThreadMXBean` class.
  * `com.helger.commons.email` - contains the data model and a small regular expression based validator for email addresses.
  * `com.helger.commons.encode` - contains a generic encode and decode interface which is very similar to the unidirectional converter as specified in `com.helger.commons.convert` but with different method names.
  * `com.helger.commons.equals` - contains utility methods for `null`-safe equals implementations as well as a registry for `equals` implementation overloading for bogus or missing `equals`-implementations (like for arrays etc.).
  * `com.helger.commons.error` - contains classes and interfaces to handle stuff related to error handling, like error ID, error level, error location and error message.
  * `com.helger.commons.exceptions` - contains additional exception classes that includes logging.

  To be continued

---

On Twitter: <a href="https://twitter.com/philiphelger">Follow @philiphelger</a>
