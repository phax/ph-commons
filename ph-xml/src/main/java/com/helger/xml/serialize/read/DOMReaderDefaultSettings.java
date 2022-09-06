/*
 * Copyright (C) 2014-2022 Philip Helger (www.helger.com)
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import javax.xml.validation.Schema;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.PresentForCodeCoverage;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.annotation.ReturnsMutableObject;
import com.helger.commons.callback.CallbackList;
import com.helger.commons.callback.exception.IExceptionCallback;
import com.helger.commons.collection.impl.CommonsEnumMap;
import com.helger.commons.collection.impl.ICommonsMap;
import com.helger.commons.concurrent.SimpleReadWriteLock;
import com.helger.commons.state.EChange;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLParserProperty;
import com.helger.xml.XMLFactory;
import com.helger.xml.sax.LoggingSAXErrorHandler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

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

  public static boolean isNamespaceAware ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultNamespaceAware);
  }

  public static void setNamespaceAware (final boolean bNamespaceAware)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultNamespaceAware = bNamespaceAware);
  }

  public static boolean isValidating ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultValidating);
  }

  public static void setValidating (final boolean bValidating)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultValidating = bValidating);
  }

  public static boolean isIgnoringElementContentWhitespace ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultIgnoringElementContentWhitespace);
  }

  public static void setIgnoringElementContentWhitespace (final boolean bIgnoringElementContentWhitespace)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultIgnoringElementContentWhitespace = bIgnoringElementContentWhitespace);
  }

  public static boolean isExpandEntityReferences ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultExpandEntityReferences);
  }

  public static void setExpandEntityReferences (final boolean bExpandEntityReferences)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultExpandEntityReferences = bExpandEntityReferences);
  }

  public static boolean isIgnoringComments ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultIgnoringComments);
  }

  public static void setIgnoringComments (final boolean bIgnoringComments)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultIgnoringComments = bIgnoringComments);
  }

  public static boolean isCoalescing ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultCoalescing);
  }

  public static void setCoalescing (final boolean bCoalescing)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultCoalescing = bCoalescing);
  }

  @Nullable
  public static Schema getSchema ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultSchema);
  }

  public static void setSchema (@Nullable final Schema aSchema)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultSchema = aSchema);
  }

  public static boolean isXIncludeAware ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultXIncludeAware);
  }

  public static void setXIncludeAware (final boolean bXIncludeAware)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultXIncludeAware = bXIncludeAware);
  }

  public static boolean hasAnyProperties ()
  {
    return RW_LOCK.readLockedBoolean (DEFAULT_PROPS::isNotEmpty);
  }

  @Nullable
  public static Object getPropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    if (eProperty == null)
      return null;

    return RW_LOCK.readLockedGet ( () -> DEFAULT_PROPS.get (eProperty));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsMap <EXMLParserProperty, Object> getAllPropertyValues ()
  {
    return RW_LOCK.readLockedGet (DEFAULT_PROPS::getClone);
  }

  public static void setPropertyValue (@Nonnull final EXMLParserProperty eProperty,
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

  public static void setPropertyValues (@Nullable final Map <EXMLParserProperty, ?> aProperties)
  {
    if (aProperties != null)
    {
      RW_LOCK.writeLocked ( () -> DEFAULT_PROPS.putAll (aProperties));
    }
  }

  @Nonnull
  public static EChange removePropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    if (eProperty == null)
      return EChange.UNCHANGED;

    return RW_LOCK.writeLockedGet ( () -> DEFAULT_PROPS.removeObject (eProperty));
  }

  @Nonnull
  public static EChange removeAllPropertyValues ()
  {
    return RW_LOCK.writeLockedGet (DEFAULT_PROPS::removeAll);
  }

  public static boolean hasAnyFeature ()
  {
    return RW_LOCK.readLockedBoolean (DEFAULT_FEATURES::isNotEmpty);
  }

  @Nullable
  @SuppressFBWarnings ("NP_BOOLEAN_RETURN_NULL")
  public static Boolean getFeatureValue (@Nullable final EXMLParserFeature eFeature)
  {
    if (eFeature == null)
      return null;

    return RW_LOCK.readLockedGet ((Supplier <Boolean>) () -> DEFAULT_FEATURES.get (eFeature));
  }

  @Nonnull
  @ReturnsMutableCopy
  public static ICommonsMap <EXMLParserFeature, Boolean> getAllFeatureValues ()
  {
    return RW_LOCK.readLockedGet (DEFAULT_FEATURES::getClone);
  }

  public static void setFeatureValue (@Nonnull final EXMLParserFeature eFeature, final boolean bValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    RW_LOCK.writeLocked ((Runnable) () -> DEFAULT_FEATURES.put (eFeature, Boolean.valueOf (bValue)));
  }

  public static void setFeatureValue (@Nonnull final EXMLParserFeature eFeature, @Nullable final Boolean aValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    RW_LOCK.writeLocked ( () -> {
      if (aValue == null)
        DEFAULT_FEATURES.remove (eFeature);
      else
        DEFAULT_FEATURES.put (eFeature, aValue);
    });
  }

  public static void setFeatureValues (@Nullable final Map <EXMLParserFeature, Boolean> aValues)
  {
    if (aValues != null)
    {
      RW_LOCK.writeLocked ( () -> DEFAULT_FEATURES.putAll (aValues));
    }
  }

  @Nonnull
  public static EChange removeFeature (@Nullable final EXMLParserFeature eFeature)
  {
    if (eFeature == null)
      return EChange.UNCHANGED;

    return RW_LOCK.writeLockedGet ( () -> DEFAULT_FEATURES.removeObject (eFeature));
  }

  @Nonnull
  public static EChange removeAllFeatures ()
  {
    return RW_LOCK.writeLockedGet (DEFAULT_FEATURES::removeAll);
  }

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

  @Nullable
  public static EntityResolver getEntityResolver ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultEntityResolver);
  }

  public static void setEntityResolver (@Nullable final EntityResolver aEntityResolver)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultEntityResolver = aEntityResolver);
  }

  @Nullable
  public static ErrorHandler getErrorHandler ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultErrorHandler);
  }

  public static void setErrorHandler (@Nullable final ErrorHandler aErrorHandler)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultErrorHandler = aErrorHandler);
  }

  @Nonnull
  @ReturnsMutableObject
  public static CallbackList <IExceptionCallback <Throwable>> exceptionCallbacks ()
  {
    return DEFAULT_EXCEPTION_CALLBACKS;
  }

  public static boolean isRequiresNewXMLParserExplicitly ()
  {
    return RW_LOCK.readLockedBoolean ( () -> s_bDefaultRequiresNewXMLParserExplicitly);
  }

  public static void setRequiresNewXMLParserExplicitly (final boolean bDefaultRequiresNewXMLParserExplicitly)
  {
    RW_LOCK.writeLocked ( () -> s_bDefaultRequiresNewXMLParserExplicitly = bDefaultRequiresNewXMLParserExplicitly);
  }
}
