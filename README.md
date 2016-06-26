#ph-commons

Java 1.8+ Library with tons of utility classes required in most of ph-* projects.

Version 6.0.0 contains major changes and more or less all other projects were released to reflect the changes of 6.x. Version 8 was re-designed to best work with JDK 8. So please ensure to not mix 5.x, 6.x and 8x. versions of ph-commons!

This project was the following modules:
  * ph-commons - the most common base library stuff
  * ph-xml - everything to read and write XML, including "MicroDOM" my personal minimum XML representation shipping with its own serializer.
  * ph-jaxb - the JAXB utility classes building upon ph-commons and ph-xml
  * ph-tree - generic tree structures and tree singleton base classes
  * ph-less-commons - previously contained in ph-commons not really used but I was afraid to dump it :)

##News

  * 2016-06-08: Version 8.0.0 was released. It splits the old big ph-commons library into slightly smaller pieces: `ph-commons`, `ph-xml`, `ph-jaxb`, `ph-tree` and `ph-less-commons`.

##Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-commons</artifactId>
  <version>8.0.0</version>
</dependency>
```

##Contents
In general I tried to make the source comments as useful as possible. Therefore here only an alphabetic package list with the respective contents is shown:

  * `com.helger.commons` - The base package that contains only a class with only constant values (`CGlobal`) and a programming utility class (`ValueEnforcer`). 
  * `com.helger.commons.aggregate` - contains a generic aggregator and multiple implementations that can aggregate multiple values to a single value.
  * `com.helger.commons.annotations` - contains all the Java annotations defined in this project. This include e.g. `@Nonempty`, `@ReturnsMutableCopy` or `@DevelopersNote`.
  * `com.helger.commons.base64` - contains the Base64 implementation from http://iharder.net/base64 in a slightly adopted version.
  * `com.helger.commons.cache` - defines generic interfaces and classes for caching data
  * `com.helger.commons.callback` - contains all kind of simple data structures that are to be used for callbacks and external exception handling. Additionally special forms of `Runnable` and `Callable` are present in this package.
  * `com.helger.commons.callback.adapter` - special adapter class implementations that can convert between `Callable`, `Runnable` etc.
  * `com.helger.commons.callback.exception` - special callback implementations to catch `Exception`s.
  * `com.helger.commons.changelog` - contains everything needed for having a structured change log (= history of changes) of software components. A complete list of all changes can be found in class `ChangeLog` containing multiple entries.
  * `com.helger.commons.charset` - contains stuff necessary for simple String character set/encoding and decoding as well as an enumeration for Unicode BOM (Byte Order Mark).
  * `com.helger.commons.cleanup` - contains a central class to cleanup and/or reset most of the runtime data and caches used in this project. This is especially helpful in unit tests when testing this project.
  * `com.helger.commons.codec` - contains a generic encode/decode interface and some basic implementations like ASCII85, ASCII Hex, DCT, Base64, LZW and RFC 1522. This package is based on `com.helger.commons.encode` package. 
  * `com.helger.commons.collation` - contains collation helper methods (`CollationHelper`). 
  * `com.helger.commons.collection` - contains very basic helper classes for easy working with arrays (class `ArrayHelper`) and collections (class `ContainerHelper`). It also contains a local implementation of `Stack` called `NonBlockingStack` which does not use synchronization - this can be seen like the different between `ArrayList` (not synchronized) and `Vector` (synchronized).
  * `com.helger.commons.collection.attr` - contains a generic attribute container which more or less is a `Map` with a modified API. This package contains both read-only as well as mutable versions of this map-based container.
  * `com.helger.commons.collection.ext` - this package contains extension classes to the default Java runtime collection classes. This package contains e.g. `CommonsArrayList<T>` a class derived from `ArrayList<T>` but with additional default methods like `findAll` or `findFirst`..   
  * `com.helger.commons.collection.impl` - contains collection implementations for specific areas not covered by the default runtime collections (soft map, single element collection, empty element collection, safe collections, ring buffer etc.).   
  * `com.helger.commons.collection.iterate` - contains special iterators/enumerators for arrays and collections. It also contains iterators for single elements as well as for "no" elements.
  * `com.helger.commons.collections.map` - contains specialized primitive based map implementations.
  * `com.helger.commons.collection.multimap` - contains "multi maps" which are `Map`s which have other `Collection`s as values. This package contains common interfaces as well as implementations for different `Map` implementations (like `HashMap`, `TreeMap`, `LinkedHashMap`, `ConcurrentHashMap` and `WeakHashMap`).
  * `com.helger.commons.collection.pair` - contains a generic pair which is a combination of 2 values with potentially different types.
  * `com.helger.commons.compare` - generic `Comparator` and `Comparable` extensions and base classes. Additionally the enumeration `ESortOrder` which defines 'ascending' and 'descending' is contained.
  * `com.helger.commons.concurrent` - contains some commonly used things for `Thread` and `ExecutorService` handling.
  * `com.helger.commons.concurrent.collector` - a generic "collector" which supports multiple inputs from multiple different threads and serializes this data stream for sequential processing (e.g. for mail queueing with a central sender).
  * `com.helger.commons.convert` - package containing unidirectional and bidirectional data converter interfaces.
  * `com.helger.commons.csv` - package containing CSV reader and writer based on OpenCSV.
  * `com.helger.commons.deadlock` - contains a thread deadlock detector based on the JMX `ThreadMXBean` class.
  * `com.helger.commons.debug` - contains the `GlobalDebug` class.
  * `com.helger.commons.dimension` - width, height and size handling methods.
  * `com.helger.commons.email` - contains the data model and a small regular expression based validator for email addresses.
  * `com.helger.commons.encode` - contains a generic encode and decode interface which is very similar to the unidirectional converter as specified in `com.helger.commons.convert` but with different method names.
  * `com.helger.commons.equals` - contains utility methods for `null`-safe equals implementations as well as a registry for `equals` implementation overloading for bogus or missing `equals`-implementations (like for arrays etc.). Especially class `EqualsUtils` is used frequently.
  * `com.helger.commons.error` - contains classes and interfaces to handle stuff related to error handling, like error ID, error level, error location and error message.
  * `com.helger.commons.errorlist` - contains classes and interfaces to handle lists of errors.
  * `com.helger.commons.exception` - contains additional exception classes.
  * `com.helger.commons.exception.mock` - contains special "mock" exception classes.
  * `com.helger.commons.factory` - contains generic factory pattern interfaces and basic implementations.
  * `com.helger.commons.filter` - contains generic filtering interfaces and basic implementations.
  * `com.helger.commons.format` - contains interfaces and class to format objects to a String value. This can be seen as a typed alternative to `String.valueOf`.
  * `com.helger.commons.function` - contains generic functional interfaces missing in the JDK (e.g. `IBooleanConsumer` or `IToBooleanFunction`).
  * `com.helger.commons.gfx` - contains utility classes for handling image metadata (especially size).
  * `com.helger.commons.hashcode` - contains utility classes for creating hash codes in a simplified way. Especially `HashCodeGenerator` is used quite frequently.
  * `com.helger.commons.hierarchy` - contains generic classes to model a hierarchy relationship between objects (e.g. parent-child relationship).
  * `com.helger.commons.hierarchy.visit` - contains a hierarchy walker which is the visitor pattern for hierarchical data structures.
  * `com.helger.commons.id` - contains interfaces for objects having identifiers (IHas...ID) as well as `int`, `long` and `String` based ID provider interfaces.
  * `com.helger.commons.idfactory` - ID factory implementations. Especially `GlobalIDFactory` is used frequently.
  * `com.helger.commons.io` - generic interfaces for input/output (IO) handling. `IReadableResource` is used quite frequently as an abstraction layer.
  * `com.helger.commons.io.channels` - contains utility methods for channeled IO.
  * `com.helger.commons.io.file` - utility classes for file based IO
  * `com.helger.commons.io.file.filter` - contains a set of `FileFilter` and `FilenameFilter` implementations.
  * `com.helger.commons.io.file.iterate` - utility classes for iterating file system directories.
  * `com.helger.commons.io.misc` - additional file utility classes.
  * `com.helger.commons.io.monitor` - a background monitor for file changes, based on Apache commons-io.
  * `com.helger.commons.io.provider` - File IO provider based on an external name.
  * `com.helger.commons.io.resource` - implementations of `IReadableResource` around `File` (`FileSystemResource`), class path (`ClassPathResource`) and `URL` (`URLResource`).
  * `com.helger.commons.io.resource.inmemory` - special implementations of `IReadableResource` that are purely in memory and not persisted.
  * `com.helger.commons.io.resource.wrapped` - Wrapper around `IReadableResource` and `IWritableResource` for usage with e.g. compression.
  * `com.helger.commons.io.resourceprovider` - implementations of `IReadableResourceProvider` and `IWritableResourceProvider`.
  * `com.helger.commons.io.stream` - special `InputStream` and `OutputStream` implementations (for bits, buffer based streams, counting streams, logging streams, non-blocking streams, non-closing streams, null streams and wrapped streams). Also `StreamHelper` is a regularly used.
  * `com.helger.commons.io.streamprovider` - implementations of `IHasInputStream` and `IHasReader`.
  * `com.helger.commons.junit` - JUnit 4 extensions.
  * `com.helger.commons.lang` - Java language extensions that don't really fit into any other category.
  * `com.helger.commons.lang.priviledged` - Privileged action extensions.
  * `com.helger.commons.lang.proxy` - Dynamic proxy object generation made easy.
  * `com.helger.commons.locale` - `Locale` related utility classes.
  * `com.helger.commons.locale.country` - country related `Locale` utility classes.
  * `com.helger.commons.locale.language` - language related `Locale` utility classes.
  * `com.helger.commons.log` - logging related utility classes
  * `com.helger.commons.mac` - Message Authentication Code algorithm wrapper.
  * `com.helger.commons.math` - basic math related utility interfaces and classes
  * `com.helger.commons.messagedigest` - contains utility methods to easily create message digest (like MD5 or SHA-1).
  * `com.helger.commons.mime` - everything related to MIME types, including a structured data model as well as a parser and a determinator from byte array.
  * `com.helger.commons.mime.convert` - contains a converter from MIME type information to Micro DOM.
  * `com.helger.commons.mock` - utility classes for unit testing with JUnit.
  * `com.helger.commons.mutable` - contains mutable object wrappers for atomic values like boolean, int, long etc. which are not thread-safe.
  * `com.helger.commons.name` - base interfaces and classes for objects having a name or a description.
  * `com.helger.commons.parent` - base interfaces for defining parent-child hierarchical relations.
  * `com.helger.commons.parent.impl` - implementations of the parent-child relation interfaces
  * `com.helger.commons.parent.utils` - utility classes for the parent-child relations.
  * `com.helger.commons.pool` - generic pool of arbitrary objects.
  * `com.helger.commons.priviledged` - a set of typed `PrivilegedAction` implementations used quite frequently.
  * `com.helger.commons.random` - contains an even securer random than `SecureRandom`
  * `com.helger.commons.regex` - contains a pool for pre-compiled regular expressions and utility classes to use this pool implicitly.
  * `com.helger.commons.scopes` - base definitions for scope handling
  * `com.helger.commons.scopes.domain` - interfaces for the five base scope types: global, application, session, session application and request
  * `com.helger.commons.scopes.factory` - factory for creating scope implementations
  * `com.helger.commons.scopes.impl` - default implementations of the five base scope types
  * `com.helger.commons.scopes.mgr` - scope manager
  * `com.helger.commons.scopes.mock` - JUnit test support for scopes
  * `com.helger.commons.scopes.singleton` - base classes for singletons in the five base scope types
  * `com.helger.commons.scopes.singleton.tree` - special tree class singletons for easy proxying of the tree interfaces.
  * `com.helger.commons.scopes.spi` - SPI interfaces for scope lifecycle interference
  * `com.helger.commons.scopes.util` - some scope aware base classes
  * `com.helger.commons.serialize.convert` - a registry for registering custom `Serializable` implementations for classes that don't implement `Serializable` themselves. Registration is done via SPI.
  * `com.helger.commons.serialize.convert.impl` - default registration implementations for the serializable type converter.
  * `com.helger.commons.state` - contains a lot of small "state" enumerations like `EChange`, `ESuccess` or `ELeftRight` plus the corresponding base interfaces.
  * `com.helger.commons.state.impl` - contains additional state classes that combine a state with an arbitrary data object.
  * `com.helger.commons.stats` - thread-safe in-memory statistics categorized into cache, counter, keyed counter, keyed size, keyed timer, size and timer.
  * `com.helger.commons.stats.util` - contains classes for exporting statistic data to XML
  * `com.helger.commons.stats.visit` - a generic visitor for statistical data to easily implement exports in other data formats.
  * `com.helger.commons.string` - contains the basic class `StringHelper` for all kind of `String` related actions as well as `StringParser` for converting Strings to numerical values and finally `ToStringGenerator` as a utility class to easily implement `toString()` methods.
  * `com.helger.commons.string.utils` - utility string classes for Levenshtein distance, roman numerals and a simple scanner.
  * `com.helger.commons.system` - contains information helper classes for the Java version, the JVM vendor, the newline mode of the current operating system, the processor architecture used etc.
  * `com.helger.commons.text` - interfaces for handling multilingual text in different aspects and ways
  * `com.helger.commons.text.impl` - the default implementations of the interfaces defined in the parent package
  * `com.helger.commons.text.resolve` - contains the stuff for multilingual text resolving based on an enumeration, and falling back to a properties file if needed.
  * `com.helger.commons.text.resource` - utility classes for handling `ResourceBundle`s in an easy way, as well as UTF-8 and XML resource bundles.
  * `com.helger.commons.thirdparty` - contains a domain model and a registry for handling referenced thirdparty libraries.
  * `com.helger.commons.timing` - contains a simple stop watch class.
  * `com.helger.commons.type` - base interfaces for "typed" objects, meaning objects that have a combination of type and ID (in case the ID is not unique).
  *  `com.helger.commons.typeconvert` - contains a generic type converter registry and different resolvers. The registration is done via SPI.
  * `com.helger.commons.typeconvert.impl` - contains the default type converters provided by this project.
  * `com.helger.commons.typeconvert.rule` - contains special "rule based" type converters, where a direct match "by class" is not useful.
  * `com.helger.commons.url` - URL related tools containing a minimum data model for URLs
  * `com.helger.commons.url.encode` - specialized URL parameter encoder and decoder.
  * `com.helger.commons.utils` - additional utils not matching a specific package.
  * `com.helger.commons.vendor` - package containing data to represent the vendor of an application
  * `com.helger.commons.version` - contains a numerical version object as well as a version range object.
  * `com.helger.commons.vminit` - specialized callback for the JVM startup and shutdown

#ph-xml
A special XML reading and writing project.

  * `com.helger.commons.microdom` - contains the "Micro DOM" interface - a DOM like structure, but much easier to use than standard DOM.
  * `com.helger.commons.microdom.convert` - contains a central registry for converting arbitrary objects from and to Micro DOM.
  * `com.helger.commons.microdom.convert.impl` - default ceonverter implementations, referenced via SPI.
  * `com.helger.commons.microdom.impl` - contains the default implementations for the Micro DOM interfaces.
  * `com.helger.commons.microdom.reader` - contains specialized readers for `List`- and `Map`-like structures.
  * `com.helger.commons.microdom.serialize` - contains reader and writer for Micro DOM elements to read from (`MicroReader`) and write to (`MicroWriter`) XML documents.
  * `com.helger.commons.microdom.utils` - contains utility classes for working with Micro DOM.
  * `com.helger.commons.xml` - utility classes to work with standard W3C nodes
  * `com.helger.commons.xml.dom` - XML utility classes only relevant for DOM
  * `com.helger.commons.xml.ls` - XML utility classes for LS (serialization) support
  * `com.helger.commons.xml.namespace` - XML namespace support classes
  * `com.helger.commons.xml.sax` - XML helper classes supporting SAX
  * `com.helger.commons.xml.schema` - utility classes to handle XML Schema (XSD)
  * `com.helger.commons.xml.serialize` - XML reading and writing classes for SAX and DOM
  * `com.helger.commons.xml.transform` - utility classes for XSLT processing and URL processing
  * `com.helger.commons.xml.xpath` - utility classes for XPath handling incl. function and variable resolver

##Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-xml</artifactId>
  <version>8.0.0</version>
</dependency>
```

#ph-jaxb
A special JAXB helper project. It supports both the JDK included JAXB as well as an external JAXB that is used via a separate Maven artefact.

  * `com.helger.commons.jaxb` - JAXB context cache.
  * `com.helger.commons.jaxb.utils` - abstract JAXB marshaller for easy reading and writing from and to different sources and targets.
  * `com.helger.commons.jaxb.validation` - validation event handler for JAXB marshalling/unmarshalling.

##Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-jaxb</artifactId>
  <version>8.0.0</version>
</dependency>
```

#ph-tree
A special tree management project.

  * `com.helger.commons.tree` - contains interfaces for a basic tree and a basic tree item
  * `com.helger.commons.tree.simple` - contains a simple generic tree implementation.
  * `com.helger.commons.tree.utils.build` - contains a utility class to build a tree from hierarchical parent-child relations.
  * `com.helger.commons.tree.utils.search` - search within a tree with ID
  * `com.helger.commons.tree.utils.sort` - utility classes to sort trees by ID or by values.
  * `com.helger.commons.tree.utils.walk` - utility classes to visit all leafs of a tree
  * `com.helger.commons.tree.utils.xml` - convert a tree into a generic XML.
  * `com.helger.commons.tree.withid` - contains a generic tree, where each item has an ID (which must not be unique)
  * `com.helger.commons.tree.withid.folder` - a specialized tree which separates into "files" and "folders"
  * `com.helger.commons.tree.withid.unique` - a special tree where each item has an ID that must be unique so that each leaf can be identified easily.

##Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-tree</artifactId>
  <version>8.0.0</version>
</dependency>
```

#ph-less-commons
A library with less common elements. First available with ph-commons 8.
A few elements from ph-commons 6 which I'm not really using. But to avoid loss of information this project was added.  

  * `com.helger.commons.i18n` - internationalization (i18n) helper classes, especially for code point handling. Ripped from Apache Abdera.
  * `com.helger.commons.jmx` - very basic JMX utility classes.

##Maven usage
Add the following to your pom.xml to use this artifact:
```
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-less-commons</artifactId>
  <version>8.0.0</version>
</dependency>
```

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodeingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a>
