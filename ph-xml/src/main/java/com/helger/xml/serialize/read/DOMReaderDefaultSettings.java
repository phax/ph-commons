/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
 * philip[at]helger[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.xml.serialize.read;

import java.util.Map;
import java.util.function.Supplier;

import javax.xml.validation.Schema;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

import com.helger.annotation.concurrent.GuardedBy;
import com.helger.annotation.concurrent.ThreadSafe;
import com.helger.annotation.style.PresentForCodeCoverage;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.callback.CallbackList;
import com.helger.base.callback.exception.IExceptionCallback;
import com.helger.base.concurrent.SimpleReadWriteLock;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.state.EChange;
import com.helger.collection.commons.CommonsEnumMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLParserProperty;
import com.helger.xml.XMLFactory;
import com.helger.xml.sax.LoggingSAXErrorHandler;

/**
 * DOM reader default settings
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class DOMReaderDefaultSettings
{
  public static final boolean DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY = false;

  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();

  // DocumentBuilderFactory properties
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDefaultNamespaceAware = XMLFactory.DEFAULT_DOM_NAMESPACE_AWARE;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDefaultValidating = XMLFactory.DEFAULT_DOM_VALIDATING;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDefaultIgnoringElementContentWhitespace = XMLFactory.DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDefaultExpandEntityReferences = XMLFactory.DEFAULT_DOM_EXPAND_ENTITY_REFERENCES;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDefaultIgnoringComments = XMLFactory.DEFAULT_DOM_IGNORING_COMMENTS;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDefaultCoalescing = XMLFactory.DEFAULT_DOM_COALESCING;
  @GuardedBy ("RW_LOCK")
  private static Schema s_aDefaultSchema;
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDefaultXIncludeAware = XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE;
  @GuardedBy ("RW_LOCK")
  private static final ICommonsMap <EXMLParserProperty, Object> DEFAULT_PROPS = new CommonsEnumMap <> (EXMLParserProperty.class);
  @GuardedBy ("RW_LOCK")
  private static final ICommonsMap <EXMLParserFeature, Boolean> DEFAULT_FEATURES = new CommonsEnumMap <> (EXMLParserFeature.class);

  // DocumentBuilder properties
  @GuardedBy ("RW_LOCK")
  private static EntityResolver s_aDefaultEntityResolver;
  @GuardedBy ("RW_LOCK")
  private static ErrorHandler s_aDefaultErrorHandler = new LoggingSAXErrorHandler ();

  // Handling properties
  private static final CallbackList <IExceptionCallback <Throwable>> DEFAULT_EXCEPTION_CALLBACKS = new CallbackList <> ();
  static
  {
    DEFAULT_EXCEPTION_CALLBACKS.add (new XMLLoggingExceptionCallback ());
  }

  @GuardedBy ("RW_LOCK")
  private static boolean s_bDefaultRequiresNewXMLParserExplicitly = DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY;

  @PresentForCodeCoverage
  private static final DOMReaderDefaultSettings INSTANCE = new DOMReaderDefaultSettings ();

  private DOMReaderDefaultSettings ()
  {}

  /**
   * @return <code>true</code> if the default DOM parser is namespace aware.
   */
  public static boolean isNamespaceAware ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultNamespaceAware);
  }

  /**
   * Set the default namespace awareness of the DOM parser.
   *
   * @param bNamespaceAware
   *        <code>true</code> if the parser should be namespace aware.
   */
  public static void setNamespaceAware (final boolean bNamespaceAware)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultNamespaceAware = bNamespaceAware);
  }

  /**
   * @return <code>true</code> if the default DOM parser is validating.
   */
  public static boolean isValidating ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultValidating);
  }

  /**
   * Set the default validation mode of the DOM parser.
   *
   * @param bValidating
   *        <code>true</code> if the parser should validate the document.
   */
  public static void setValidating (final boolean bValidating)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultValidating = bValidating);
  }

  /**
   * @return <code>true</code> if the default DOM parser is ignoring element
   *         content whitespace.
   */
  public static boolean isIgnoringElementContentWhitespace ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultIgnoringElementContentWhitespace);
  }

  /**
   * Set the default behavior for ignoring element content whitespace.
   *
   * @param bIgnoringElementContentWhitespace
   *        <code>true</code> if the parser should ignore element content
   *        whitespace.
   */
  public static void setIgnoringElementContentWhitespace (final boolean bIgnoringElementContentWhitespace)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultIgnoringElementContentWhitespace = bIgnoringElementContentWhitespace);
  }

  /**
   * @return <code>true</code> if the default DOM parser is expanding entity
   *         references.
   */
  public static boolean isExpandEntityReferences ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultExpandEntityReferences);
  }

  /**
   * Set the default behavior for expanding entity references.
   *
   * @param bExpandEntityReferences
   *        <code>true</code> if the parser should expand entity references.
   */
  public static void setExpandEntityReferences (final boolean bExpandEntityReferences)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultExpandEntityReferences = bExpandEntityReferences);
  }

  /**
   * @return <code>true</code> if the default DOM parser is ignoring comments.
   */
  public static boolean isIgnoringComments ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultIgnoringComments);
  }

  /**
   * Set the default behavior for ignoring comments.
   *
   * @param bIgnoringComments
   *        <code>true</code> if the parser should ignore comments.
   */
  public static void setIgnoringComments (final boolean bIgnoringComments)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultIgnoringComments = bIgnoringComments);
  }

  /**
   * @return <code>true</code> if the default DOM parser is coalescing.
   */
  public static boolean isCoalescing ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultCoalescing);
  }

  /**
   * Set the default coalescing behavior (convert CDATA nodes to text nodes).
   *
   * @param bCoalescing
   *        <code>true</code> if the parser should be coalescing.
   */
  public static void setCoalescing (final boolean bCoalescing)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultCoalescing = bCoalescing);
  }

  /**
   * @return The default XML schema to use. May be <code>null</code>.
   */
  @Nullable
  public static Schema getSchema ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultSchema);
  }

  /**
   * Set the default XML schema to use for validation.
   *
   * @param aSchema
   *        The schema to use. May be <code>null</code>.
   */
  public static void setSchema (@Nullable final Schema aSchema)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultSchema = aSchema);
  }

  /**
   * @return <code>true</code> if the default DOM parser is XInclude aware.
   */
  public static boolean isXIncludeAware ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultXIncludeAware);
  }

  /**
   * Set the default XInclude awareness.
   *
   * @param bXIncludeAware
   *        <code>true</code> if the parser should be XInclude aware.
   */
  public static void setXIncludeAware (final boolean bXIncludeAware)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultXIncludeAware = bXIncludeAware);
  }

  /**
   * @return <code>true</code> if any default parser properties are set.
   */
  public static boolean hasAnyProperties ()
  {
    return RW_LOCK.readLockedBoolean (DEFAULT_PROPS::isNotEmpty);
  }

  /**
   * Get the default value of the specified parser property.
   *
   * @param eProperty
   *        The property to query. May be <code>null</code>.
   * @return <code>null</code> if the property is not set or the parameter is
   *         <code>null</code>.
   */
  @Nullable
  public static Object getPropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    if (eProperty == null)
      return null;

    return RW_LOCK.readLockedGet ( () -> DEFAULT_PROPS.get (eProperty));
  }

  /**
   * @return A mutable copy of all default parser property values. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsMap <EXMLParserProperty, Object> getAllPropertyValues ()
  {
    return RW_LOCK.readLockedGet (DEFAULT_PROPS::getClone);
  }

  /**
   * Set a default parser property value.
   *
   * @param eProperty
   *        The property to set. May not be <code>null</code>.
   * @param aPropertyValue
   *        The value to set. May be <code>null</code> to remove the
   *        property.
   */
  public static void setPropertyValue (@NonNull final EXMLParserProperty eProperty,
                                       @Nullable final Object aPropertyValue)
  {
    ValueEnforcer.notNull (eProperty, "Property");

    RW_LOCK.writeLocked ( () -> {
      if (aPropertyValue != null)
        DEFAULT_PROPS.put (eProperty, aPropertyValue);
      else
        DEFAULT_PROPS.remove (eProperty);
    });
  }

  /**
   * Set multiple default parser property values at once.
   *
   * @param aProperties
   *        The properties to set. May be <code>null</code>.
   */
  public static void setPropertyValues (@Nullable final Map <EXMLParserProperty, ?> aProperties)
  {
    if (aProperties != null)
    {
      RW_LOCK.writeLocked ( () -> DEFAULT_PROPS.putAll (aProperties));
    }
  }

  /**
   * Remove a default parser property value.
   *
   * @param eProperty
   *        The property to remove. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the property was removed.
   */
  @NonNull
  public static EChange removePropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    if (eProperty == null)
      return EChange.UNCHANGED;

    return RW_LOCK.writeLockedGet ( () -> DEFAULT_PROPS.removeObject (eProperty));
  }

  /**
   * Remove all default parser property values.
   *
   * @return {@link EChange#CHANGED} if any property was removed.
   */
  @NonNull
  public static EChange removeAllPropertyValues ()
  {
    return RW_LOCK.writeLockedGet (DEFAULT_PROPS::removeAll);
  }

  /**
   * @return <code>true</code> if any default parser features are set.
   */
  public static boolean hasAnyFeature ()
  {
    return RW_LOCK.readLockedBoolean (DEFAULT_FEATURES::isNotEmpty);
  }

  /**
   * Get the default value of the specified parser feature.
   *
   * @param eFeature
   *        The feature to query. May be <code>null</code>.
   * @return <code>null</code> if the feature is not set or the parameter is
   *         <code>null</code>.
   */
  @Nullable
  public static Boolean getFeatureValue (@Nullable final EXMLParserFeature eFeature)
  {
    if (eFeature == null)
      return null;

    return RW_LOCK.readLockedGet ((Supplier <Boolean>) () -> DEFAULT_FEATURES.get (eFeature));
  }

  /**
   * @return A mutable copy of all default parser feature values. Never
   *         <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsMap <EXMLParserFeature, Boolean> getAllFeatureValues ()
  {
    return RW_LOCK.readLockedGet (DEFAULT_FEATURES::getClone);
  }

  /**
   * Set a default parser feature value.
   *
   * @param eFeature
   *        The feature to set. May not be <code>null</code>.
   * @param bValue
   *        The feature value to set.
   */
  public static void setFeatureValue (@NonNull final EXMLParserFeature eFeature, final boolean bValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    RW_LOCK.writeLocked ((Runnable) () -> DEFAULT_FEATURES.put (eFeature, Boolean.valueOf (bValue)));
  }

  /**
   * Set a default parser feature value with an optional {@link Boolean}
   * wrapper.
   *
   * @param eFeature
   *        The feature to set. May not be <code>null</code>.
   * @param aValue
   *        The feature value to set. May be <code>null</code> to remove the
   *        feature.
   */
  public static void setFeatureValue (@NonNull final EXMLParserFeature eFeature, @Nullable final Boolean aValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    RW_LOCK.writeLocked ( () -> {
      if (aValue == null)
        DEFAULT_FEATURES.remove (eFeature);
      else
        DEFAULT_FEATURES.put (eFeature, aValue);
    });
  }

  /**
   * Set multiple default parser feature values at once.
   *
   * @param aValues
   *        The feature values to set. May be <code>null</code>.
   */
  public static void setFeatureValues (@Nullable final Map <EXMLParserFeature, Boolean> aValues)
  {
    if (aValues != null)
    {
      RW_LOCK.writeLocked ( () -> DEFAULT_FEATURES.putAll (aValues));
    }
  }

  /**
   * Remove a default parser feature setting.
   *
   * @param eFeature
   *        The feature to remove. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the feature was removed.
   */
  @NonNull
  public static EChange removeFeature (@Nullable final EXMLParserFeature eFeature)
  {
    if (eFeature == null)
      return EChange.UNCHANGED;

    return RW_LOCK.writeLockedGet ( () -> DEFAULT_FEATURES.removeObject (eFeature));
  }

  /**
   * Remove all default parser feature settings.
   *
   * @return {@link EChange#CHANGED} if any feature was removed.
   */
  @NonNull
  public static EChange removeAllFeatures ()
  {
    return RW_LOCK.writeLockedGet (DEFAULT_FEATURES::removeAll);
  }

  /**
   * @return <code>true</code> if the default settings require a new XML
   *         parser to be created (i.e., a pooled parser cannot be used).
   */
  public static boolean requiresNewXMLParser ()
  {
    return RW_LOCK.readLockedBoolean ( () -> {
      // Force a new XML parser?
      if (s_bDefaultRequiresNewXMLParserExplicitly)
        return true;

      if (s_bDefaultNamespaceAware != XMLFactory.DEFAULT_DOM_NAMESPACE_AWARE ||
          s_bDefaultValidating != XMLFactory.DEFAULT_DOM_VALIDATING ||
          s_bDefaultIgnoringElementContentWhitespace != XMLFactory.DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE ||
          s_bDefaultExpandEntityReferences != XMLFactory.DEFAULT_DOM_EXPAND_ENTITY_REFERENCES ||
          s_bDefaultIgnoringComments != XMLFactory.DEFAULT_DOM_IGNORING_COMMENTS ||
          s_bDefaultCoalescing != XMLFactory.DEFAULT_DOM_COALESCING ||
          s_aDefaultSchema != null ||
          s_bDefaultXIncludeAware != XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE ||
          DEFAULT_PROPS.isNotEmpty () ||
          DEFAULT_FEATURES.isNotEmpty ())
        return true;

      // Special case for JDK > 1.7.0_45 because of maximum entity expansion
      // See http://docs.oracle.com/javase/tutorial/jaxp/limits/limits.html
      return s_aDefaultEntityResolver != null;
    });
  }

  /**
   * @return The default entity resolver. May be <code>null</code>.
   */
  @Nullable
  public static EntityResolver getEntityResolver ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultEntityResolver);
  }

  /**
   * Set the default entity resolver.
   *
   * @param aEntityResolver
   *        The entity resolver to use by default. May be <code>null</code>.
   */
  public static void setEntityResolver (@Nullable final EntityResolver aEntityResolver)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultEntityResolver = aEntityResolver);
  }

  /**
   * @return The default error handler. May be <code>null</code>.
   */
  @Nullable
  public static ErrorHandler getErrorHandler ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultErrorHandler);
  }

  /**
   * Set the default error handler.
   *
   * @param aErrorHandler
   *        The error handler to use by default. May be <code>null</code>.
   */
  public static void setErrorHandler (@Nullable final ErrorHandler aErrorHandler)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultErrorHandler = aErrorHandler);
  }

  /**
   * @return The default exception callback list. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableObject
  public static CallbackList <IExceptionCallback <Throwable>> exceptionCallbacks ()
  {
    return DEFAULT_EXCEPTION_CALLBACKS;
  }

  /**
   * @return <code>true</code> if a new XML parser is explicitly required by
   *         default.
   */
  public static boolean isRequiresNewXMLParserExplicitly ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultRequiresNewXMLParserExplicitly);
  }

  /**
   * Set whether a new XML parser is explicitly required by default.
   *
   * @param bDefaultRequiresNewXMLParserExplicitly
   *        <code>true</code> to always create a new XML parser by default.
   */
  public static void setRequiresNewXMLParserExplicitly (final boolean bDefaultRequiresNewXMLParserExplicitly)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultRequiresNewXMLParserExplicitly = bDefaultRequiresNewXMLParserExplicitly);
  }
}
