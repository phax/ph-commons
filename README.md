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
 

#Contents
In general I tried to make the source comments as usefull as possible. Therefore here only a package list with the respective contents is shown:
  * `com.helger.commons` - The base package that contains very basic interface and a few classes like `CGlobal` and `GlobalDebug` which are in this package, to avoid cyclic package references. 
  * `com.helger.commons.aggregate` - contains a generic aggregator that cann aggregate multiple values to a single value.
  * `com.helger.commons.annotations` - contains all the Java annotations defined in this project. This include e.g. `@Nonempty`, `@ReturnsMutableCopy` or `@DevelopersNote`.
  
To be continued

---

On Twitter: <a href="https://twitter.com/philiphelger">Follow @philiphelger</a>
