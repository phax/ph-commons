# ph-commons

Java 1.8+ Library with tons of utility classes required in most of ph-* projects.

Version 6.x contains major changes and more or less all other projects were released to reflect the changes of 6.x.
Version 7 does not exist.
Version 8.x was re-designed to best work with JDK 8.
So please ensure to not mix 5.x, 6.x and 8x. versions of ph-commons!

This project was the following modules:
  * ph-bc - BouncyCastle support library (since 9.1.2)
  * ph-cli - library with commandline parameter definition support; loosely based on commons-cli
  * ph-collection - library with extended collection related classes
  * ph-commons - the most common base library stuff
  * ph-config - a generic configuration solution supporting different sources of configuration (since 9.4.0)
  * ph-dao - file based DAO library with WAL support
  * ph-datetime - extension library for handling Java date and time special cases
  * ph-graph - generic graph library with directed and undirected graphs
  * ph-jaxb - the JAXB utility classes building upon ph-commons and ph-xml
  * ph-jaxb-adapter - JAXB utility classes required by created code, building only on ph-commons (since 10.0.0)
  * ph-json - a generic simple JSON reader, visitor and writer
  * ph-less-commons - previously contained in ph-commons not really used but I was afraid to dump it :)
  * ph-matrix - a simple library for handling matrix data structures and operations
  * ph-scopes - based library for non-web related scope handling
  * ph-security - security related topics like key store handling, message digests etc
  * ph-settings - a small library for handling configuration files in an easy way
  * ph-tree - generic tree structures and tree singleton base classes
  * ph-xml - everything to read and write XML, including "MicroDOM" my personal minimum XML representation shipping with its own serializer.

## News and noteworthy

* v10.0.0 - work in progress
    * Changed Maven groupId from `com.helger` to `com.helger.commons`
    * The submodule `ph-xml` no longer depends on `ph-collection`
    * Separated the JAXB adapter classes and `JAXBHelper` to new submodule `ph-jaxb-adapter` - the goal is to have less dependencies
    * Removed the submodule `ph-charset` - who needs UTF-7 nowadays....
    * Updated to BouncyCastle 1.68 (again with bcprov-ext-jdk15on)
    * Removed deprecated methods
    * Removed `Serializable` from base interfaces
    * Removed the serializable functional specializations (`IFunction`, `ISupplier`, `IConsumer`, `IPredicate`, `IBiConsumer`, `IBiFunction`, `IBiPredicate`, `NamespaceContext`)
    * `GenericJAXBMarshaller` now has a chaining API
    * Made `MimeTypeParserException` a checked Exception
    * Added `CertificateHelper.getEncodedCertificate()`
    * Fixed potential NPE in `GenericJAXBMarshaller.toString()`
    * Removed `TimeValue` in favour of `Duration`
    * Added `StopWatch.getLapDuration ()`
    * Removed `ResourceStreamSource` and `ResourceStreamResult` - see https://saxonica.plan.io/issues/4833 for the reasons
    * Changed `JsonReader.Builder` method names to not use `set`
    * The calling order of "useBufferedReader" and "dontCloseSource" compared to "source" in `JsonReader.Builder` is not longer of importance
    * Changed the `SingleError.builder()` to use method names without `set`
    * Added more `SingleError` builder `errorLocation` overloads
    * Removed the class `ScriptHelper` as Nashorn gets removed in JDK 15 (see JEP 335)
    * Added new interfaces `IBuilder` and `IResettableBuilder`
    * Extended `HTTPHeaderMap` API
    * Fixed the data type of the custom HTTP headers in `WSClientConfig`
    * Changed the default value `DEFAULT_QUOTE_IF_NECESSARY` from `true` to `false`
    * Deprecated all the "multi map" classes in `ph-collection`. With the `computeIfAbsent` functions, most of the simplifications can be achieved with runtime features. They will be removed in the next major version.
    * Extended `ILocalDatePeriod` based on issue #23
    * Added classes `ILocalDateTimePeriod` and `LocalDateTimePeriod`
    * Added class `PasswordHashCreatorPBKDF2_SHA256_1000_48` for better PBKDF2 usage
    * Deprecated class `PasswordHashCreatorPBKDF2_1000_48` because it uses the PBKDF2 with the SHA1 hash
    * Added new class `StringHelper.ImploderBuilder` that handles all the `StringHelper.getImploded*` stuff internally
    * Added new method `ICommonsIterable.forEachThrowing`
    * Added new class `StreamHelper.CopyByteStreamBuilder`
    * Added new class `StreamHelper.CopyCharStreamBuilder`
    * Added new method `URLHelper.isValidURN` to check RFC 2141 compliance
    * Deprecated some methods in `StreamHelper` that should be replaced with calls to the new builder
* v9.5.4 - 2020-12-15
    * Updated to BouncyCastle 1.67 (no more bcprov-ext-jdk15on)
    * Removed "double locking" in `AbstractCollector`
* v9.5.3 - 2020-10-22
    * Reverting the changes from 9.5.2
    * Made the JAXB DateTime adapter classes more resilient by trimming the input string
* v9.5.2 - 2020-09-25
    * Experimental release that uses `CommonsHashMap` instead of `CommonsWeakHashMap` for all registries having `Class<?>` as the key
* v9.5.1 - 2020-09-18
    * Build with Java 1.8 instead of Java 11 because of binary incompatibility with `ByteBuffer.flip()` signature
* v9.5.0 - 2020-09-17
    * Updated to JAXB 2.3.3 - https://eclipse-ee4j.github.io/jaxb-ri/
    * Updated to JAXWS 2.3.3 - https://eclipse-ee4j.github.io/metro-jax-ws/
* v9.4.8 - 2020-09-10
    * Added new JAXB Adapter class `AdapterZonedDateTime`
    * Added a new factory method in `ConfigFactory`.
    * Default configuration source loading is now more consistent and behaves identical for all predefined filenames.
    * Extended `IConfig` API to easily reload all resource based configuration files
    * New interface `IAddableByTrait` to be used as the constraint for the element type of `IGenericAdderTrait`
* v9.4.7 - 2020-08-28
    * Extended `IPrivilegedAction` API for `Security` provider APIs
    * Extended `IJsonArray` API to iterate only child arrays, objects or values
    * Scope debugging no longer activates itself if the log level is set to debug
    * Extended `PDTWebDateHelper` to handle `LocalTime` values as well
    * Added predefined JAXB adapters in package `com.helger.jaxb.adapter`
* v9.4.6 - 2020-07-15
    * Allow empty MIME type parameter values
    * `MimeTypeParser.safeParseMimeType` does an RFC 2616 decoding if necessary
    * Updated to BouncyCastle 1.66
* v9.4.5 - 2020-06-29
    * Added missing methods in `IMapBasedDAO`
    * Added static syntactic sugar methods in `HashCodeGenerator`
    * The default `ConfigFactory.getDefaultInstance()` now also considers system properties `config.resource`, `config.resources`, `config.file` and `config.url` or the environment variable alternatives `CONFIG_RESOURCE`, `CONFIG_RESOURCES`, `CONFIG_FILE` and `CONFIG_URL`.
    * Added possibility to `reload` for resource-based configuration sources
    * Fixed a potential NPE in `IJsonObject.getValue (String)`
    * Added `EqualsHelper.equalsCustom` using a `BiPredicate`
    * Extended `IConfig` API to receive the `IConfigurationSource` AND the value to determine the source of the configuration value (backwards incompatible change)
    * Changed the handling of the `Consumers` in the `Config` implementation in a backward incompatible way (setters vs. constructor, new parameter type) 
* v9.4.4 - 2020-05-21
    * Fixed a backwards compatibility issue with `JsonObject.add(String,IJson)`
* v9.4.3 - 2020-05-21
    * Extended tests - thanks to @dliang2000
    * Extended `NonBlockingCharArrayWriter` with non-throwing `write` overloads
    * Changed `IJsonObject` API to favour `addJson` instead of `add` because of different nullness
    * Deprecated `IHasSchema`
    * Added `IConfigurationSourceResource.getAllConfigItems`
    * `URLResource.getAsFile()` is now nullable
* v9.4.2 - 2020-04-25
    * Reduced write locked section in `ScopeManager.onGlobalEnd` to reduce the possibility of a dead-lock
    * Made `ThreadGroup` of `BasicThreadFactory` customizable
    * Updated to BouncyCastle 1.65
    * The `IMissingLocaleHandler` is now also called in `LocaleHelper.getLocale` if the input strings are all empty
    * Improved the resolution rules for `LocaleCache`, `LanguageCache` and `CountryCache` on edge cases.
    * Extracted overridable `GenericJAXBMarshaller.getJAXBContext`
    * Added possibility to cache `JAXBContext` objects created via classes 
* v9.4.1 - 2020-03-30
    * Added `SimpleLock.(read|write)LockedGet(Throwing)`
    * The exception handling of `URLHelper.urlDecode` is now backwards compatible
    * Added `URLHelper.urlDecodeOrNull` and `urlDecodeOrDefault`
* v9.4.0 - 2020-03-27
    * Added support for additional HTTP status codes (103, 308, 422, 425, 426, 428, 429, 431, 451, 506, 507, 508 and 511)
    * `JAXBContextCache` has now a method to switch silent mode on or off
    * Added new constant `GlobalDebug.DEFAULT_SILENT_MODE` for the default silent mode setting
    * Added new method `CertificateHelper.convertByteArrayToCertficateOrNull`
    * Added new class `PDTDisplayHelper`
    * Added new method `CertificateHelper.convertStringToPrivateKey`
    * Extended the `JsonWriter` API to also write to an OutputStream
    * Added new methods `EmailAddress.createOnDemand`
    * `URLHelper.urlEncode` and `URLHelper.urlDecode` now uses `URLCodec`
    * Deprecated `ICommonsIterable.forEach` in favour of `findAll`
    * Fixed a bug in the cloning of `MapBasedNamespaceContext` (see [issue #17](https://github.com/phax/ph-commons/issues/17))
    * Added `ArrayHelper.EMPTY_CLASS_ARRAY`
    * Added new predefined licenses (GPL20CP, EPL20 and EDL10)
    * Updated license URLs to https where applicable
    * New parent POM 1.11.1 updates SLF4J to 1.7.30
    * `PropertiesHelper` got new APIs with `Charset` to read properties with character sets other than ISO-8859-1
    * Started new subproject `ph-config` with a more intelligent configuration handling
    * Added JDK 14 as a known version
    * Added silent mode to `TypeConverter`, `CountryCache`, `LanguageCache`, `ObjectPool`, `LocaleCache`
    * Added new `JAXBDocumentType` constructor for more flexible use
    * Changed names of `SimpleReadWriteLock.(read|write)Locked` with primitive suppliers
    * Changed names of `SimpleLock.locked` with primitive suppliers
    * Added `SimpleReadWriteLock.(read|write)LockedGet(Throwing)`
suppliers
* v9.3.9 - 2019-12-11
    * Made `ClassLoaderHelper.getResource` more robust
    * Updated "mime-type-info.xml" list with  shared-mime-info-spec 1.15
    * Moved code from `AbstractWALDAO` down to `AbstractDAO` for later reuse.
    * Reworked `FileIntIDFactory` and `FileLongIDFactory` to be more error resistant.
    * Added support for the "jrt:" protocol
    * Added support for JDK 13
    * Extended `LocaleCache` with an API to specify what happens if a Locale is not present 
* v9.3.8 - 2019-11-07
    * Fixed method name in `RFC2616Codec` - `getMaximumEncodedLength` instead of `getEncodedLength`; added `getMaximumDecodedLength`
    * Avoid double quoting in `HttpHeaderMap.getUnifiedValue` if the value already seems to be quoted 
    * Updated to BouncyCastle 1.64
    * Added new interface `IBooleanConsumer`
    * Added new method `CertificateHelper.isCertificateValidPerNow`
    * Improved performance of `JsonParser` when position tracking is disabled
    * Added possibility to read multiple JSON objects from a single source
    * Added new classes `LoggingReader` and `LoggingWriter`
    * Added new classes `CountingReader` and `CountingWriter`
    * `JsonParser.parse` now returns an enum indicating EOI or not (incompatible change)
    * Improved performance of JsonParser by reusing buffers internally
    * Added new class `MappedCache` that is a generalization of `Cache`. It adds a mapper to determine the cache key.
    * New parent POM 1.11.0 updates SLF4J to 1.7.29
* v9.3.7 - 2019-09-25
    * Extended `IJAXBValidator` API to have a `validate` method that takes an outside `ErrorList`
    * Updated to BouncyCastle 1.63
    * Converted some methods in `IMicroQName` to default methods
    * Extended `HttpHeaderMap` API to make `quote if necessary` customizable
* v9.3.6 - 2019-08-27
    * Extended the `StreamHelper` API with an even more flexible `copyInputStreamToOutputStream` method
    * `StreamHelper.getCopy[WithLimit]` can now return `null` if copying fails
    * Added new classes around `com.helger.commons.codec.ICharArrayCodec`
    * Added new class `RFC2616Codec` to correctly encode and decode HTTP header values
    * Added new option in `HttpHeaderMap.getUnifiedValue` to automatically quote the values if necessary
    * Added new overloads in `StackTraceHelper` to use a custom line separator
    * Added new methods `CommonsAssert.assertNotEquals(boolean,boolean)`
* v9.3.5 - 2019-08-04
    * Updated to BouncyCastle 1.62
    * Added new `EURLProtocol` entries `CID` and `MID` from RFC 2392
    * Minor speed ups in several places
    * Added overload of `PDTXMLConverter.getXMLCalendarDate` with timezone offset in minutes
    * Added new `JsonReader.Builer` overloads
    * `LocaleHelper.getValidCountryCode` now convert ISO 3166 Alpha 3 codes to ALpha 2 codes where applicable
    * Made XML serialization settings on the XML declaration more fine grained
* v9.3.4 - 2019-05-28
    * Added new overloads for `JsonReader.Builder.setSource`
    * Catching exception in `VerySecureRandom` initialization - `setSeed` may throw an Exception
    * Deprecated class `RandomHelper` - causes more problems than it solves
    * Added new method `CertificateHelper.convertStringToCertficateOrNull`
    * Added new method `CertificateHelper.convertByteArrayToCertficateDirect`
    * Added complete list of HTTP response codes constants to `CHttp`
    * Added new method `Base64OutputStream.setNewLineBytes`
    * Removed methods `Base64.(encode|decode)Object` for security reasons
    * Added new option `Base64.DO_NEWLINE_CRLF` to use `\r\n` as newline separator instead of `\n`
* v9.3.3 - 2019-05-06
    * Added `ICommonsIterable` methods `findFirstIndex` and `findLastIndex`
    * Added support for Java 12
    * The default XML persistence for configuration files was changed. No more "class" attribute and no nested "value" element needed. The old layout can still be read, but only the new layout is written.
    * The class `JsonReader` now has an explicit `JsonReader.Builder` class to simplify the usage
    * New class `SettingsPersistenceJson` to be able to read and write settings in JSON format
* v9.3.2 - 2019-03-28
    * Logging a warning in `VerySecureRandom` if initial seeding takes more than 500 milliseconds
    * If the system property `ph.disable-securerandom` with the value of `true` is present, the usage of `SecureRandom` in class `RandomHelper` is disabled by default. Respective logging was added.
    * The system property `ph.securerandom-reseed-interval` with a numeric value &ge; 0 can be used to set the default "re-seed interval" for class `VerySecureRandom`
    * `VerySecureRandom` uses `NativePRNGNonBlocking` as the initial `SecureRandom` for faster Linux initialization. 
* v9.3.1 - 2019-03-06
    * Made `IJsonWriterSettings` serializable
    * Updated to BouncyCastle 1.61
    * `null` values in settings can be serialized now
    * `HttpHeaderMap` methods `forEachSingleHeader` and `forEachHeaderLine` now use the unified values
* v9.3.0 - 2019-02-07
    * Restored the `Automatic-Module-Name` of `com.helger.scopes`
    * Moved method `exceptionCallbacks` to base class `AbstractJAXBBuilder`
    * Exception handler in `GenericJAXBMarshaller` now based on ´CallbackList` - incompatible change
    * Fixed the conversion from `String` to `Integer`, `Long` and `Short` so that `null` is returned on error (therefore throwing a `TypeConverterException`)
    * `IGetter*Trait` `getAs...` methods now return `null` on error instead of throwing a `TypeConverterException`
* v9.2.1 - 2019-01-23
    * Improved API of `JsonParseException` 
    * Added additional default `MicroTypeConverter` registrations (`File`, `Path`, `URL` and `URI`)
    * Added special implementation of `IHasInputStream` for `NonBlockingByteArrayOutputStream`
    * Added enum entry `EXMLSerializeXMLDeclaration.EMIT_NO_NEWLINE`
    * Extended internal API of `AbstractMapBasedWALDAO` to allow for not invoking the callbacks
    * Added method `ConfigFileBuilder.addPathFromEnvVar` to get the configuration file path from an environment variable
    * Added new keystore type `BCFKS` - see [issue #13](https://github.com/phax/ph-commons/issues/13)
    * Added new constructor for `MapEntry` to take `Map.Entry`
    * Added new default methods to classes `IHasDimension*` (isLandscape, isPortrait and isQuadratic)
    * `MimeTypeContent` is now serializable
    * The OSGI export for submodule `ph-scopes` was corrected - see [issue #14](https://github.com/phax/ph-commons/issues/14)
    * Added static factory methods for `ByteArrayWrapper`
* v9.2.0 - 2018-11-21
    * Undo deprecations of `ValueEnforcer` short and float methods.
    * Simplified the `IMultilingualText` interface and implementations (backwards incompatible)
    * Removed all deprecated, unused methods
    * `ICommonsIterable` is no longer Serializable. Only `ICommonsCollection` is serializable.
    * `IHasDisplayText` is no longer Serializable.
    * `IDisplayTextProvider` is no longer Serializable.
    * `IHasText` is no longer Serializable.
    * Changed return type of `SystemProperties.setPropertyValue()` to `EChange`
    * `JAXBDocumentType` takes `List<ClassPathResource>` instead of `List<String>` to avoid ClassLoader issues
    * Changed `PBCProvider` initialization to log a warning instead of throwing an exception
    * Added `StringHelper` methods `getQuoted` and `appendQuoted`
    * Improved Java 10/Java 11 support
    * Moved `CloneHelper.getClonedJAXBElement` to new class `JAXBHelper` in `ph-jaxb` subproject (Java 11 issue)
    * Moved class `WSTestHelper` to subproject `ph-wsclient` (Java 11 issue)
    * First version to compile with OracleJDK 11 and OpenJDK 11
    * Added methods in `PDTFactory` to remove microseconds and nanoseconds from `(Zoned|Offset|Local)DateTime`
    * Added support for silent mode in `AbstractDAO`
    * Changed `DOMReader.readXMLDOM` to not throw an Exception. Provide a suitable `ErrorHandler` instead.
    * `WrappedCollectingSAXErrorHandler` is now derived from `CollectingSAXErrorHandler`
* v9.1.8 - 2018-10-24
    * Added special support for properties `java.runtime.version` and `java.runtime.name` in class `SystemProperties`
    * Class `JavaVersionHelper` now supports AdoptOpenJDK versions
    * `HttpHeaderMap` stores the value case sensitive internally and compares case sensitive instead (issue #11)
    * Added `PDTConfig.getUTCTimeZone ()`
    * Fixed a conversion error from `GregorianCalendar` to `XMLGregorianCalendar` if only the date part is used (issue #12)
    * Added TimeZone related methods in `PDTFactory`
    * Added method `IHasInputStream.getBufferedInputStream()`, `IHasInputStreamAndReader.getBufferedReader()` and `IHasReader.getBufferedReader()`
    * Added method `IHasOutputStream.getBufferedOutputStream()`, `IHasOutputStreamAndWriter.getBufferedWriter()` and `IHasWriter.getBufferedWriter()`
    * Opened `JsonReader` API to add the possibility to pass in an `IJsonParserCustomizeCallback` instance
    * Added `IMultilingualText.texts ()` 
    * Fixed some SpotBugs errors
    * Added `IJAXBWriter.getAsInputStream(...)`
    * Added new marker interface `IExplicitlyCloneable`
* v9.1.7 - 2018-10-11
    * Added new class `IdentityByteArrayCodec`
    * Added new classes `MacInputStream` and `MacOutputStream`
    * Deprecated some JSON APIs with `byte`, `short` and `float`
    * Overloaded `WrappedOutputStream.write (byte[],int,int)` for quicker pass-through. Please ensure that all derived classes also overload this method.
    * `JSONValue` handling for `long` values in the `int` range was changed to store `int` internally. This improves testability independent of the value range.
    * Added new `StringHelper.contains(Any|No|Only)` methods for `CharSequence` and `String` objects
* v9.1.6 - 2018-10-01
    * Added `CHttpHeader.X_CONTENT_SECURITY_POLICY_REPORT_ONLY`
    * Added `ValueEnforcer.isNE0` methods
    * Deprecated `ValueEnforcer` methods for `short` and `float`
    * Reworked the `VerySecureRandom` initialization to use a native PRNG for initial seeding
    * Improved customizability of `AbstractWALDAO` in case WAL recovery failed
    * Extended `IHas...DateTime` APIs
    * Fixed OSGI SPI configuration for ph-bc
* v9.1.5 - 2018-09-09
    * Extended `WSClientConfig` API
    * Extended `GenericJAXBMarshaller` API
    * Class `IJAXBReader` now tries to set systemID for `IReadableResource`, `File` and `Path` parameters.
    * Extended `PDTFactory` slightly
    * Fixed potential NPE when an `AbstractWALDAO` is used without a backing file
    * `DefaultTransformURIResolver` got the possibility to provide a default base URI if none is present in the call
    * Improved internal API of `AbstractMapBasedWALDAO`
    * Updated stax-ex to 1.8
* v9.1.4 - 2018-08-06
    * Added class `XMLBracketModeDeterminatorXMLC14`
    * Added XML write setting `write CDATA as Text`
    * Added XML write setting `order attributes and namespaces lexicographically`
    * XML entities are now emitted as Hexadecimal values instead of numeric (`&#D;` instead of `&#13;`)
    * Fixed error in `HttpHeaderMap.addAllHeaders` (was not doing what was expected)
* v9.1.3 - 2018-07-19
    * Updated to BouncyCastle 1.60
    * Extended `LogHelper` to have overloads with `Supplier<String>`
    * Added new `Commons...Map` constructors with parameters `Map, Function, Function`
    * Added new class `LanguageCache`
    * Added new `CommonsArrayList` static factory methods
    * Deprecated all the `ChangeLog` classes - they will be moved to ph-less-commons in v9.2
    * Added new interface `IHasByteArray`
    * Implementing `IHasByteArray` in `ReadableResourceByteArray`, `ByteArrayInputStreamProvider`, `MessageDigestValue`
    * Added new class `ByteArrayWrapper`
    * Added new method `NonBlockingByteArrayOutputStream.getBufferOrCopy()` that copies only on demand
    * Added new `ArrayHelper.startsWith` overloads
    * Added static factory methods in `CommonsCopyOnWriteArrayList`
    * The internal logger name was changed from `s_aLogger` to `LOGGER`
    * Extended `IPrivilegedAction` with new static methods
    * Added new class `Predicates` with basic typed predicates for primitive types
    * Added some static `IBooleanPredicate` factory methods
    * Added `ToStringGenerator.appendPasswordIf` method
    * Added equals implementation to `PasswordAuthentication`
* v9.1.2 - 2018-06-19
    * Made setters of `WSClientConfig` final
    * Changed method name in `CharsetHelper` from `getCharsetFromNameOrNull` to `getCharsetFromNameOrDefault`
    * Added new sub-project `ph-bc` with some common BouncyCastle helper methods
    * Added OCSP MIME type constants
    * Extended `XMLTransformerFactory` API to support creating secure instances
    * Improved code quality based on SonarQube analysis
    * Removed the Generic type from `IConcurrentCollector`
    * Added `PropertiesHelper.expandProperties`
    * Removed empty class `AbstractTreeItemFactory`
* v9.1.1 - 2018-05-14
    * Really fixed OSGI ServiceProvider configuration
* v9.1.0 - 2018-05-08
    * Fixed invalid call in `ValueEnforcer.isEqual`
    * `AbstractJAXBBuilder` got an explicit possibility to disable XML schema usage
    * Improved Java version identification by adding support for JEP 223 identifiers
    * Kicked Travis Java 9 integration - was unable to find a Maven configuration working locally, in Eclipse and in Travis :(
    * Added `IMicroNode.appendChildren` default methods
    * Extracted `ph-wsclient` project for later use with Java 9
    * Added `ICommonsCollection.setAllMapped`
    * Added class `CommonsMock` - a very simple mocking engine
    * Added `EMimeContentType.FONT`
    * Extended `TreeXMLConverter` API to be able to reuse existing trees for filling
    * Fixed OSGI ServiceProvider configuration
* v9.0.2 - 2018-03-22
    * Made setter methods of `JAXB*Builder` final so that they can be called from derived constructors
    * Improved Java 9 compliance
    * `EJavaVersion` was changed incompatible to allow support for Java 10 and 11
    * Added `ELockType.DEPENDS` enum constant
    * Added some more `MathHelper.toBig(Decimal|Integer)` overloads so that it can be used without thinking 
    * Different forbidden characters in filenames depending on OS - https://github.com/phax/as2-server/issues/20
    * Updated to parent POM 1.10.2
* v9.0.1 - 2018-02-01
    * Minor API adoptions
    * Updated to BouncyCastle 1.59
    * XML validation now works with custom locales as well
    * Added `XPathHelper.createXPathFactorySaxonFirst`
    * Fixed typo in `XPathHelper` - `createNewXPathExpression` instead of `createNewXPathExpresion`
    * Updated to parent POM 1.10.0
* v9.0.0 - 2017-11-05
    * Changed `com.helger.commons.function` package to `com.helger.commons.functional`
    * Replaced `IFilter` with `IPredicate`
    * Replaced `IFilterWithParameter` with `IBiPredicate`
    * Changed `ManagedExecutorService` to `ExecutorServiceHelper`
    * Removed class `CCharset` - use `StandardCharsets` instead
    * Added classes `PathHelper` and `PathOperations`    
    * Moved class `XMLResourceBundle` to ph-xml
    * Removed some deprecated `CharsetManager` methods
    * Reworked `format` package
    * Removed deprecated 'Mutable*' constructors because of weird defaults
    * Removed deprecated methods that handle: greater than, greater or equals, lower than and lower or equals
    * Made `MicroTypeConverter` type-safe
    * Ensure all `Predicate` usages use `? super`
    * Ensure all `Consumer` usages use `? super`
    * Ensure all `Function` usages use `? super`/`? extends` where applicable
    * Ensure all `Supplier` usages use `? extends`
    * Moved `ThreadHelper` to package `com.helger.commons.concurrent`
    * Removed deprecated methods in `SimpleLSResourceResolver`
    * Removed package `com.helger.commons.io.monitor`
    * Removed package `com.helger.commons.scope.singletons`
    * `@ReturnsMutableObject` does not require a value anymore (but you can still pass one of course)
    * Extracted `ph-scopes` project to lower size of `ph-commons` slightly
    * Extracted `ph-collection` project to lower size of `ph-commons` slightly
    * Removed default methods from `IAggregator`
    * Added reverse interface `ISplitter`
    * Changed caching API to use a function instead of a protected methods
    * Integrated `ph-cli` here
    * Improved and extended traits API
    * JAXB components can now use a safe way to write XML with special chars
    * JAXB components can now read XML documents with a BOM
    * Extracted ph-oton DAO handling into new subproject `ph-dao`
    * Replaced `SMap` with `StringMap`
    * An `Automatic-Module-Name` was added after beta 1
    * Removed support for the application and session application scopes
* v8.6.6 - 2017-07-12
    * Extended `CSVWriter` API
    * `SimpleLSResourceResolver` can now handle fat jars from Spring Boot (issue #8)
    * Extracted `DefaultResourceResolver` from `SimpleLSResourceResolver` and added in ph-commons
    * Added rudimentary support for `Path` based APIs
    * Added `WatchService` based `WatchDir` in favor of `FileMonitor`
* v8.6.5 - 2017-05-19
    * Deprecated all default Mutable* constructors, because the default values were partially confusing
    * Added new class `DefaultEntityResolver` using a base URL for resolving.
    * Extracted and renamed classes `PDTZoneID` and `PDTMask`
    * Extended `ILocalDatePeriod` API
    * Added additional specialized type converters to `BigDecimal`
    * Unified naming for methods that handle: greater than, greater or equals, lower than and lower or equals
    * Added new type converters for `Month`, `DayOfWeek` and `MonthDay`
    * `RuntimeException`s during type conversion are encapsulated in `TypeConverterException`s.
* v8.6.4 - 2017-04-12
    * Some performance improvements
    * Fixed error in `StringHelper.replaceMultipleAsString`
    * Extended `StringHelper` with encode/decode methods    
    * Added new class `NonBlockingCharArrayWriter`
    * Deprecated some `CharsetManager` methods
* v8.6.3 - 2017-03-28
    * Deprecated some String-based `FileHelper` APIs
    * Improved CertificateHelper parsing
    * Added `IPredicate` - a serializable `Predicate` interface
    * Added `IBiPredicate` - a serializable `BiPredicate` interface
    * Added `IBiConsumer` - a serializable `BiConsumer` interface
    * Added `IBiFunction` - a serializable `BiFunction` interface
    * Added `IComparable` - a serializable and extended `Comparable` interface
    * Extended `StringHelper` API
    * Extended `MicroWriter` API
    * Extended `XMLWriter` API
    * Extended `PDTHelper` API
    * Deprecated `EMonth` and `EDayOfWeek` in favour of JDK `Month` and `DayOfWeek`
* v8.6.2 - 2017-02-15
    * Extended `XMLSystemProperties` API
    * Allowing `JAXBDocumentType` objects to not have an `@XmlSchema` annotation
    * Deprecated `CCharset` - use `StandardCharsets` instead
    * Deprecated ToStringGenerator `toString()` - use `getToString()` instead
* v8.6.1 - 2017-01-23
    * `*MultilingualText` is now based on ICommonsOrderedMap
    * Extended `GlobalDebug` API slightly
    * `JAXBDocumentType` implements equals/hashCode
    * `WSClientConfig` not using chunked encoding by default
* v8.6.0 - 2017-01-09
    * Removed deprecated methods
    * Minor API extensions
* v8.5.6 - 2016-12-10
    * Extended APIs
    * Fixed a nasty bug with date/time formatting due to pattern modifications (introduced in 8.5.5)
* v8.5.5 - 2016-11-25
    * Extended APIs
* v8.5.4 - 2016-11-08
    * Fixed a nasty NPE
* v8.5.3 - 2016-11-08
    * Added support for `EntityResolver2` in MicroXML parsing
    * Extended some APIs
* v8.5.2 - 2016-09-25
    * Fixed an error with JAXB context creation if @XMLRootElement was used
    * Improved consistency of IHasText(WithArgs) and IHasDisplayText(WithArgs) so that they can be used interchangeably
    * Extended collection API slightly
* v8.5.1 - 2016-09-12
    * Fixed error text retrieval in Single Error; further API extensions and improvements
* v8.5.0 - 2016-09-09
    * Reworked the error objects so that there is now a single error handling object. The old objects are retained for backwards compatibility.
* v8.4.0 - 2016-08-21
    * Started adding JDK 9 compatibility
    * Added new interface `IWriteToStream`
    * Added support for new Message digest and Mac algorithms
    * Added new functional interfaces (IThrowingSupplier, IThrowingConsumer and IThrowingFunction) with customizable Exception type
    * Deprecated some legacy interfaces and adapters that may lead to a compiler error (!) 
* v8.3.0 - 2016-08-02
    * Moved the following sub-projects into this project: ph-datetime, ph-json, ph-charset, ph-graph, ph-matrix and ph-settings
* v8.2.2 - 2016-07-22
* v8.2.1 - 2016-07-15
* v8.2.0 - 2016-07-10
    * Added new sub-project `ph-security` that contains MessageDigest, HMac, Keystore etc. stuff
* v8.1.0 - 2016-06-26: 
    * Minor small API optimizations for JAXB.
    * `ValueEnforcer` supports Supplier` functional interface.
    * GZIP based readable and writable resources added.
* v8.0.0 - 2016-06-08: 
    * It splits the old big ph-commons library into slightly smaller pieces: `ph-commons`, `ph-xml`, `ph-jaxb`, `ph-tree` and `ph-less-commons`.
    * The first version that requires JDK 8
    * Multiple API changes to better support functional style
    * Base32 codec added

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-commons</artifactId>
  <version>x.y.z</version>
</dependency>
```

## Contents

Note: to be updated for 9.x - this is for 8.x

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
  * `com.helger.commons.mock` - utility classes for unit testing with JUnit.
  * `com.helger.commons.mutable` - contains mutable object wrappers for atomic values like boolean, int, long etc. which are not thread-safe.
  * `com.helger.commons.name` - base interfaces and classes for objects having a name or a description.
  * `com.helger.commons.pool` - generic pool of arbitrary objects.
  * `com.helger.commons.random` - contains an even securer random than `SecureRandom`
  * `com.helger.commons.regex` - contains a pool for pre-compiled regular expressions and utility classes to use this pool implicitly.
  * `com.helger.commons.scope` - base interfaces and implementations for scope handling
  * `com.helger.commons.scope.mgr` - scope manager
  * `com.helger.commons.scope.mock` - JUnit test support for scopes
  * `com.helger.commons.scope.singleton` - base classes for singletons in the five base scope types
  * `com.helger.commons.scope.singletons` - specific singleton implementations
  * `com.helger.commons.scope.spi` - SPI interfaces for scope lifecycle interference
  * `com.helger.commons.scope.util` - some scope aware base classes
  * `com.helger.commons.script` - helper class for javax.script package
  * `com.helger.commons.serialize.convert` - a registry for registering custom `Serializable` implementations for classes that don't implement `Serializable` themselves. Registration is done via SPI.
  * `com.helger.commons.state` - contains a lot of small "state" enumerations like `EChange`, `ESuccess` or `ELeftRight` plus the corresponding base interfaces.
  * `com.helger.commons.statistics` - thread-safe in-memory statistics categorized into cache, counter, keyed counter, keyed size, keyed timer, size and timer.
  * `com.helger.commons.statistics.util` - contains classes for exporting statistic data to XML
  * `com.helger.commons.string` - contains the basic class `StringHelper` for all kind of `String` related actions as well as `StringParser` for converting Strings to numerical values and finally `ToStringGenerator` as a utility class to easily implement `toString()` methods.
  * `com.helger.commons.string.util` - utility string classes for Levenshtein distance, roman numerals and a simple scanner.
  * `com.helger.commons.system` - contains information helper classes for the Java version, the JVM vendor, the newline mode of the current operating system, the processor architecture used etc.
  * `com.helger.commons.text` - interfaces for handling multilingual text in different aspects and ways
  * `com.helger.commons.text.codepoint` - helper classes for code point handling
  * `com.helger.commons.text.display` - special 'Display text' interfaces
  * `com.helger.commons.text.resolve` - contains the stuff for multilingual text resolving based on an enumeration, and falling back to a properties file if needed.
  * `com.helger.commons.text.resourcebundle` - utility classes for handling `ResourceBundle`s in an easy way, as well as UTF-8 and XML resource bundles.
  * `com.helger.commons.text.util` - text handling helper classes.
  * `com.helger.commons.thirdparty` - contains a domain model and a registry for handling referenced thirdparty libraries.
  * `com.helger.commons.thread` - thread handling helper classes.
  * `com.helger.commons.timing` - contains a simple stop watch class.
  * `com.helger.commons.traits` - contains interfaces with default methods to easily extend the functionality of an existing object.
  * `com.helger.commons.type` - base interfaces for "typed" objects, meaning objects that have a combination of type and ID (in case the ID is not unique).
  *  `com.helger.commons.typeconvert` - contains a generic type converter registry and different resolvers. The registration is done via SPI.
  * `com.helger.commons.typeconvert.rule` - contains special "rule based" type converters, where a direct match "by class" is not useful.
  * `com.helger.commons.url` - URL related tools containing a minimum data model for URLs
  * `com.helger.commons.vendor` - package containing data to represent the vendor of an application
  * `com.helger.commons.version` - contains a numerical version object as well as a version range object.
  * `com.helger.commons.wrapper` - a generic `Wrapper` interface and class
  * `com.helger.commons.ws` - some Web Service client caller goodies
    

# ph-xml
A special XML reading and writing project.

  * `com.helger.xml` - utility classes to work with standard W3C nodes
  * `com.helger.xml.dom` - XML utility classes only relevant for DOM
  * `com.helger.xml.ls` - XML utility classes for LS (serialization) support
  * `com.helger.xml.microdom` - contains the "Micro DOM" interfaces and implementations - a DOM like structure, but much easier to use than standard DOM.
  * `com.helger.xml.microdom.convert` - contains a central registry for converting arbitrary objects from and to Micro DOM.
  * `com.helger.xml.microdom.serialize` - contains reader and writer for Micro DOM elements to read from (`MicroReader`) and write to (`MicroWriter`) XML documents.
  * `com.helger.xml.microdom.util` - contains utility classes for working with Micro DOM.
  * `com.helger.xml.mock` - contains mock/testing classes
  * `com.helger.xml.namespace` - XML namespace support classes
  * `com.helger.xml.sax` - XML helper classes supporting SAX
  * `com.helger.xml.schema` - utility classes to handle XML Schema (XSD)
  * `com.helger.xml.serialize.read` - XML reading classes for SAX and DOM
  * `com.helger.xml.serialize.write` - XML writing classes for SAX and DOM
  * `com.helger.xml.transform` - utility classes for XSLT processing and URL processing
  * `com.helger.xml.util` - general helper classes.
  * `com.helger.xml.util.changelog` - changelog XML serializer.
  * `com.helger.xml.util.mime` - contains a converter from MIME type information to Micro DOM.
  * `com.helger.xml.util.statistics` - statistics to XML converter.
  * `com.helger.xml.xpath` - utility classes for XPath handling incl. function and variable resolver

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-xml</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-jaxb
A special JAXB helper project. It supports both the JDK included JAXB as well as an external JAXB that is used via a separate Maven artefact.

  * `com.helger.jaxb` - JAXB context cache, abstract reader, writer and validator interfaces.
  * `com.helger.jaxb.builder` - Abstract builder for JAXB based readers, writers and validators. 
  * `com.helger.jaxb.utils` - abstract JAXB marshaller for easy reading and writing from and to different sources and targets.
  * `com.helger.jaxb.validation` - validation event handler (factories) for JAXB marshalling/unmarshalling.

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-jaxb</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-tree
A special tree management project.

  * `com.helger.tree` - contains interfaces and implementations for a basic tree and a basic tree item
  * `com.helger.tree.io` - special file system tree
  * `com.helger.tree.singleton` - special tree class singletons for easy proxying of the tree interfaces.
  * `com.helger.tree.sort` - utility classes to sort trees by ID or by values.
  * `com.helger.tree.util` - contains a utility class to build a tree from hierarchical parent-child relations, search a tree and visit a tree.
  * `com.helger.tree.withid` - contains a generic tree, where each item has an ID (which must not be unique)
  * `com.helger.tree.withid.folder` - a specialized tree which separates into "files" and "folders"
  * `com.helger.tree.withid.unique` - a special tree where each item has an ID that must be unique so that each leaf can be identified easily.
  * `com.helger.tree.xml` - convert a tree into a generic XML.

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-tree</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-less-commons
A library with less common elements. First available with ph-commons 8.
A few elements from ph-commons 6 which I'm not really using. But to avoid loss of information this project was added.  

  * `com.helger.lesscommons.charset` - special String encoder/decoder based on ByteBuffer
  * `com.helger.lesscommons.i18n` - internationalization (i18n) helper classes, especially for code point handling. Ripped from Apache Abdera.
  * `com.helger.lesscommons.jmx` - very basic JMX utility classes.

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-less-commons</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-json
Another simple library to read and write JSON documents and streams using a custom parser.

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-json</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-settings
Another simple library to deal with configuration files and settings in general.

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-settings</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-datetime
Another library that improves the JDK 8 date time handling with some useful extensions.

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-datetime</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-graph
Another library to handle all kind of graphs in a generic way.

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-graph</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-matrix
Another library to handle matrixes in an easy way including some operations on them.

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-matrix</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-charset
This library adds support for the UTF-7 charset via Java extension SPI.

## Maven usage
Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-charset</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-scopes

Contains all the global scope handling as well as the scoped singleton base classes.

## Maven usage

Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-scopes</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-collection

Some special collections not used everywhere (like multi maps etc.)

## Maven usage

Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-collection</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-cli

Fork of Apache commons-cli 1.4 with cleansing.
* Remove deprecated stuff
* Adopted naming of variables
* Adopted class names

## Maven usage

Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-cli</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-bc

Unified BouncyCastle Provider manager. More stuff to come.

## Maven usage

Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-bc</artifactId>
  <version>x.y.z</version>
</dependency>
```

# ph-config

A multi-source configuration manager, that can use system properties, environment variables, resources and application specific values to work with.
See `ConfigFactory.getDefaultConfig ()` for the starting point. By default the following configurations sources are scanned in this order:
1. System properties - priority 400
1. Environment variables - priority 300
1. if the system property `config.resource` or the environment variable `CONFIG_RESOURCE` is present, and it points to an existing classpath resource, the first one matching is used - priority 200 or determined by the system property `config.resource.priority` or the environment variable `CONFIG_RESOURCE_PRIORITY`. Note: the file type is determined by the extension and defaults to "properties".
1. if the system property `config.resources` (note the trailing "s") or the environment variable `CONFIG_RESOURCES` is present, and it points to an existing classpath resource, all matching ones are used - priority 200 or determined by the system property `config.resources.priority` (also note the trailing "s") or the environment variable `CONFIG_RESOURCES_PRIORITY`. Note: the file type is determined by the extension and defaults to "properties".
1. if the system property `config.file` or the environment variable `CONFIG_FILE` is present, and it points to an existing file, it is used - priority 200 or determined by the system property `config.file.priority` or the environment variable `CONFIG_FILE_PRIORITY`. Note: the file type is determined by the extension and defaults to "properties".
1. if the system property `config.url` or the environment variable `CONFIG_URL` is present, and it points to an existing URL, it is used - priority 200 or determined by the system property `config.url.priority` or the environment variable `CONFIG_URL_PRIORITY`. Note: the file type is determined by the extension and defaults to "properties".
1. a JSON file called `private-application.json` - this is mainly to have an easy way to override settings - priority 195.
1. a properties file called `private-application.properties` - this is mainly to have an easy way to override settings - priority 190.
1. all JSON files called `application.json` that are in the classpath - priority 185.
1. all properties files called `application.properties` that are in the classpath - priority 180.
1. all properties files called `reference.properties` that are in the classpath - priority 1.

* Note: the default configuration does NOT contain any custom configuration files.
* Note: JSON and Properties files are expected to be UTF-8 encoded

## JSON format

The JSON configuration file must be a single large object so it must start with "{" and end with "}".
The JSON syntax is a bit relaxed and allows for unquoted names but other than that it is regular JSON.

## Maven usage

Add the following to your pom.xml to use this artifact:

```xml
<dependency>
  <groupId>com.helger</groupId>
  <artifactId>ph-config</artifactId>
  <version>x.y.z</version>
</dependency>
```

---

My personal [Coding Styleguide](https://github.com/phax/meta/blob/master/CodingStyleguide.md) |
On Twitter: <a href="https://twitter.com/philiphelger">@philiphelger</a> |
Kindly supported by [YourKit Java Profiler](https://www.yourkit.com)