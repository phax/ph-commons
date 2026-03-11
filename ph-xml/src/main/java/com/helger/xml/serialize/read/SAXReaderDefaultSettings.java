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

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

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
import com.helger.xml.sax.LoggingSAXErrorHandler;

/**
 * SAX reader default settings
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class SAXReaderDefaultSettings
{
  public static final boolean DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY = false;

  private static final SimpleReadWriteLock RW_LOCK = new SimpleReadWriteLock ();

  @GuardedBy ("RW_LOCK")
  private static EntityResolver s_aDefaultEntityResolver;
  @GuardedBy ("RW_LOCK")
  private static DTDHandler s_aDefaultDTDHandler;
  @GuardedBy ("RW_LOCK")
  private static ContentHandler s_aDefaultContentHandler;
  @GuardedBy ("RW_LOCK")
  private static ErrorHandler s_aDefaultErrorHandler = new LoggingSAXErrorHandler ();
  @GuardedBy ("RW_LOCK")
  private static final ICommonsMap <EXMLParserProperty, Object> DEFAULT_PROPS = new CommonsEnumMap <> (EXMLParserProperty.class);
  @GuardedBy ("RW_LOCK")
  private static final ICommonsMap <EXMLParserFeature, Boolean> DEFAULT_FEATURES = new CommonsEnumMap <> (EXMLParserFeature.class);
  private static CallbackList <IExceptionCallback <Throwable>> s_aDefaultExceptionCallbacks = new CallbackList <> ();
  static
  {
    s_aDefaultExceptionCallbacks.add (new XMLLoggingExceptionCallback ());
  }
  @GuardedBy ("RW_LOCK")
  private static boolean s_bDefaultRequiresNewXMLParserExplicitly = DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY;

  static
  {
    // By default enabled in XMLFactory
    if (false)
    {
      DEFAULT_FEATURES.put (EXMLParserFeature.NAMESPACES, Boolean.TRUE);
      DEFAULT_FEATURES.put (EXMLParserFeature.SAX_NAMESPACE_PREFIXES, Boolean.TRUE);
    }
    if (false)
      DEFAULT_FEATURES.put (EXMLParserFeature.AUGMENT_PSVI, Boolean.FALSE);
  }

  @PresentForCodeCoverage
  private static final SAXReaderDefaultSettings INSTANCE = new SAXReaderDefaultSettings ();

  private SAXReaderDefaultSettings ()
  {}

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
   *        The entity resolver to use. May be <code>null</code>.
   */
  public static void setEntityResolver (@Nullable final EntityResolver aEntityResolver)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultEntityResolver = aEntityResolver);
  }

  /**
   * @return The default DTD handler. May be <code>null</code>.
   */
  @Nullable
  public static DTDHandler getDTDHandler ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultDTDHandler);
  }

  /**
   * Set the default DTD handler.
   *
   * @param aDTDHandler
   *        The DTD handler to use. May be <code>null</code>.
   */
  public static void setDTDHandler (@Nullable final DTDHandler aDTDHandler)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultDTDHandler = aDTDHandler);
  }

  /**
   * @return The default content handler. May be <code>null</code>.
   */
  @Nullable
  public static ContentHandler getContentHandler ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultContentHandler);
  }

  /**
   * Set the default content handler.
   *
   * @param aContentHandler
   *        The content handler to use. May be <code>null</code>.
   */
  public static void setContentHandler (@Nullable final ContentHandler aContentHandler)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultContentHandler = aContentHandler);
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
   *        The error handler to use. May be <code>null</code>.
   */
  public static void setErrorHandler (@Nullable final ErrorHandler aErrorHandler)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultErrorHandler = aErrorHandler);
  }

  /**
   * @return The default lexical handler. May be <code>null</code>.
   */
  @Nullable
  public static LexicalHandler getLexicalHandler ()
  {
    return (LexicalHandler) getPropertyValue (EXMLParserProperty.SAX_LEXICAL_HANDLER);
  }

  /**
   * Set the default lexical handler.
   *
   * @param aLexicalHandler
   *        The lexical handler to use. May be <code>null</code>.
   */
  public static void setLexicalHandler (@Nullable final LexicalHandler aLexicalHandler)
  {
    setPropertyValue (EXMLParserProperty.SAX_LEXICAL_HANDLER, aLexicalHandler);
  }

  /**
   * @return The default declaration handler. May be <code>null</code>.
   */
  @Nullable
  public static DeclHandler getDeclarationHandler ()
  {
    return (DeclHandler) getPropertyValue (EXMLParserProperty.SAX_DECLARATION_HANDLER);
  }

  /**
   * Set the default declaration handler.
   *
   * @param aDeclHandler
   *        The declaration handler to use. May be <code>null</code>.
   */
  public static void setDeclarationHandler (@Nullable final DeclHandler aDeclHandler)
  {
    setPropertyValue (EXMLParserProperty.SAX_DECLARATION_HANDLER, aDeclHandler);
  }

  /**
   * @return <code>true</code> if at least one parser property is defined.
   */
  public static boolean hasAnyProperties ()
  {
    return RW_LOCK.readLockedBoolean ( () -> !DEFAULT_PROPS.isEmpty ());
  }

  /**
   * Get the value of the specified parser property.
   *
   * @param eProperty
   *        The property to query. May be <code>null</code>.
   * @return <code>null</code> if no such property is set or if the passed property is
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
   * @return A mutable copy of all defined property values. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsMap <EXMLParserProperty, Object> getAllPropertyValues ()
  {
    return RW_LOCK.readLockedGet (DEFAULT_PROPS::getClone);
  }

  /**
   * Set the value of the specified parser property. If the value is <code>null</code>, the
   * property is removed.
   *
   * @param eProperty
   *        The property to set. May not be <code>null</code>.
   * @param aPropertyValue
   *        The value to set. May be <code>null</code> to remove the property.
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
   * Set multiple property values at once.
   *
   * @param aProperties
   *        The properties to set. May be <code>null</code>.
   */
  public static void setPropertyValues (@Nullable final Map <EXMLParserProperty, ?> aProperties)
  {
    if (aProperties != null && !aProperties.isEmpty ())
    {
      RW_LOCK.writeLocked ( () -> DEFAULT_PROPS.putAll (aProperties));
    }
  }

  /**
   * Remove the value of the specified parser property.
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
   * Remove all property values.
   *
   * @return {@link EChange#CHANGED} if any property was removed.
   */
  @NonNull
  public static EChange removeAllPropertyValues ()
  {
    return RW_LOCK.writeLockedGet (DEFAULT_PROPS::removeAll);
  }

  /**
   * @return <code>true</code> if at least one parser feature is defined.
   */
  public static boolean hasAnyFeature ()
  {
    return RW_LOCK.readLockedBoolean (DEFAULT_FEATURES::isNotEmpty);
  }

  /**
   * Get the value of the specified parser feature.
   *
   * @param eFeature
   *        The feature to query. May be <code>null</code>.
   * @return <code>null</code> if no such feature is set or if the passed feature is
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
   * @return A mutable copy of all defined feature values. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public static ICommonsMap <EXMLParserFeature, Boolean> getAllFeatureValues ()
  {
    return RW_LOCK.readLockedGet (DEFAULT_FEATURES::getClone);
  }

  /**
   * Set the value of the specified parser feature.
   *
   * @param eFeature
   *        The feature to set. May not be <code>null</code>.
   * @param bValue
   *        The value to set.
   */
  public static void setFeatureValue (@NonNull final EXMLParserFeature eFeature, final boolean bValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    RW_LOCK.writeLocked ((Runnable) () -> DEFAULT_FEATURES.put (eFeature, Boolean.valueOf (bValue)));
  }

  /**
   * Set the value of the specified parser feature. If the value is <code>null</code>, the
   * feature is removed.
   *
   * @param eFeature
   *        The feature to set. May not be <code>null</code>.
   * @param aValue
   *        The value to set. May be <code>null</code> to remove the feature.
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
   * Set multiple feature values at once.
   *
   * @param aValues
   *        The features to set. May be <code>null</code>.
   */
  public static void setFeatureValues (@Nullable final Map <EXMLParserFeature, Boolean> aValues)
  {
    if (aValues != null && !aValues.isEmpty ())
    {
      RW_LOCK.writeLocked ( () -> DEFAULT_FEATURES.putAll (aValues));
    }
  }

  /**
   * Remove the value of the specified parser feature.
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
   * Remove all feature values.
   *
   * @return {@link EChange#CHANGED} if any feature was removed.
   */
  @NonNull
  public static EChange removeAllFeatures ()
  {
    return RW_LOCK.writeLockedGet (DEFAULT_FEATURES::removeAll);
  }

  /**
   * @return <code>true</code> if a new XML parser is required based on the current settings.
   */
  public static boolean requiresNewXMLParser ()
  {
    return RW_LOCK.readLockedBoolean ( () -> {
      // Force a new XML parser?
      if (s_bDefaultRequiresNewXMLParserExplicitly)
        return true;

      if (!DEFAULT_PROPS.isEmpty () || !DEFAULT_FEATURES.isEmpty ())
        return true;

      // Special case for JDK > 1.7.0_45 because of maximum entity expansion
      // See http://docs.oracle.com/javase/tutorial/jaxp/limits/limits.html
      return s_aDefaultEntityResolver != null;
    });
  }

  /**
   * @return The mutable list of default exception callbacks. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableObject
  public static CallbackList <IExceptionCallback <Throwable>> exceptionCallbacks ()
  {
    return s_aDefaultExceptionCallbacks;
  }

  /**
   * @return <code>true</code> if a new XML parser is explicitly required, <code>false</code>
   *         otherwise.
   */
  public static boolean isRequiresNewXMLParserExplicitly ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultRequiresNewXMLParserExplicitly);
  }

  /**
   * Set whether a new XML parser is explicitly required.
   *
   * @param bDefaultRequiresNewXMLParserExplicitly
   *        <code>true</code> to explicitly require a new XML parser, <code>false</code> otherwise.
   */
  public static void setRequiresNewXMLParserExplicitly (final boolean bDefaultRequiresNewXMLParserExplicitly)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultRequiresNewXMLParserExplicitly = bDefaultRequiresNewXMLParserExplicitly);
  }
}
