/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
import com.helger.commons.callback.exception.IExceptionCallback;
import com.helger.commons.state.EChange;
import com.helger.commons.xml.EXMLParserFeature;
import com.helger.commons.xml.EXMLParserProperty;
import com.helger.commons.xml.XMLFactory;
import com.helger.commons.xml.sax.LoggingSAXErrorHandler;

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

  private static final ReadWriteLock s_aRWLock = new ReentrantReadWriteLock ();

  // DocumentBuilderFactory properties
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDefaultNamespaceAware = XMLFactory.DEFAULT_DOM_NAMESPACE_AWARE;
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDefaultValidating = XMLFactory.DEFAULT_DOM_VALIDATING;
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDefaultIgnoringElementContentWhitespace = XMLFactory.DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE;
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDefaultExpandEntityReferences = XMLFactory.DEFAULT_DOM_EXPAND_ENTITY_REFERENCES;
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDefaultIgnoringComments = XMLFactory.DEFAULT_DOM_IGNORING_COMMENTS;
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDefaultCoalescing = XMLFactory.DEFAULT_DOM_COALESCING;
  @GuardedBy ("s_aRWLock")
  private static Schema s_aDefaultSchema;
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDefaultXIncludeAware = XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE;
  @GuardedBy ("s_aRWLock")
  private static final EnumMap <EXMLParserProperty, Object> s_aDefaultProperties = new EnumMap <EXMLParserProperty, Object> (EXMLParserProperty.class);
  @GuardedBy ("s_aRWLock")
  private static final EnumMap <EXMLParserFeature, Boolean> s_aDefaultFeatures = new EnumMap <EXMLParserFeature, Boolean> (EXMLParserFeature.class);

  // DocumentBuilder properties
  @GuardedBy ("s_aRWLock")
  private static EntityResolver s_aDefaultEntityResolver;
  @GuardedBy ("s_aRWLock")
  private static ErrorHandler s_aDefaultErrorHandler = new LoggingSAXErrorHandler ();

  // Handling properties
  @GuardedBy ("s_aRWLock")
  private static IExceptionCallback <Throwable> s_aDefaultExceptionHandler = new XMLLoggingExceptionCallback ();
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDefaultRequiresNewXMLParserExplicitly = DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY;

  @PresentForCodeCoverage
  private static final DOMReaderDefaultSettings s_aInstance = new DOMReaderDefaultSettings ();

  private DOMReaderDefaultSettings ()
  {}

  public static boolean isNamespaceAware ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_bDefaultNamespaceAware;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setNamespaceAware (final boolean bNamespaceAware)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_bDefaultNamespaceAware = bNamespaceAware;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static boolean isValidating ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_bDefaultValidating;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setValidating (final boolean bValidating)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_bDefaultValidating = bValidating;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static boolean isIgnoringElementContentWhitespace ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_bDefaultIgnoringElementContentWhitespace;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setIgnoringElementContentWhitespace (final boolean bIgnoringElementContentWhitespace)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_bDefaultIgnoringElementContentWhitespace = bIgnoringElementContentWhitespace;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static boolean isExpandEntityReferences ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_bDefaultExpandEntityReferences;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setExpandEntityReferences (final boolean bExpandEntityReferences)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_bDefaultExpandEntityReferences = bExpandEntityReferences;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static boolean isIgnoringComments ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_bDefaultIgnoringComments;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setIgnoringComments (final boolean bIgnoringComments)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_bDefaultIgnoringComments = bIgnoringComments;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static boolean isCoalescing ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_bDefaultCoalescing;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setCoalescing (final boolean bCoalescing)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_bDefaultCoalescing = bCoalescing;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  @Nullable
  public static Schema getSchema ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aDefaultSchema;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setSchema (@Nullable final Schema aSchema)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aDefaultSchema = aSchema;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static boolean isXIncludeAware ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_bDefaultXIncludeAware;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setXIncludeAware (final boolean bXIncludeAware)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_bDefaultXIncludeAware = bXIncludeAware;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static boolean hasAnyProperties ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return !s_aDefaultProperties.isEmpty ();
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public static Object getPropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    if (eProperty == null)
      return null;

    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aDefaultProperties.get (eProperty);
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Map <EXMLParserProperty, Object> getAllPropertyValues ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return new EnumMap <EXMLParserProperty, Object> (s_aDefaultProperties);
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setPropertyValue (@Nonnull final EXMLParserProperty eProperty,
                                       @Nullable final Object aPropertyValue)
  {
    ValueEnforcer.notNull (eProperty, "Property");

    s_aRWLock.writeLock ().lock ();
    try
    {
      if (aPropertyValue != null)
        s_aDefaultProperties.put (eProperty, aPropertyValue);
      else
        s_aDefaultProperties.remove (eProperty);
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static void setPropertyValues (@Nullable final Map <EXMLParserProperty, ?> aProperties)
  {
    if (aProperties != null)
    {
      s_aRWLock.writeLock ().lock ();
      try
      {
        s_aDefaultProperties.putAll (aProperties);
      }
      finally
      {
        s_aRWLock.writeLock ().unlock ();
      }
    }
  }

  @Nonnull
  public static EChange removePropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    if (eProperty == null)
      return EChange.UNCHANGED;

    s_aRWLock.writeLock ().lock ();
    try
    {
      return EChange.valueOf (s_aDefaultProperties.remove (eProperty) != null);
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public static EChange removeAllPropertyValues ()
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      if (s_aDefaultProperties.isEmpty ())
        return EChange.UNCHANGED;
      s_aDefaultProperties.clear ();
      return EChange.CHANGED;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static boolean hasAnyFeature ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return !s_aDefaultFeatures.isEmpty ();
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  @SuppressFBWarnings ("NP_BOOLEAN_RETURN_NULL")
  public static Boolean getFeatureValue (@Nullable final EXMLParserFeature eFeature)
  {
    if (eFeature == null)
      return null;

    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aDefaultFeatures.get (eFeature);
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  @Nonnull
  @ReturnsMutableCopy
  public static Map <EXMLParserFeature, Boolean> getAllFeatureValues ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return new EnumMap <EXMLParserFeature, Boolean> (s_aDefaultFeatures);
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setFeatureValue (@Nonnull final EXMLParserFeature eFeature, final boolean bValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aDefaultFeatures.put (eFeature, Boolean.valueOf (bValue));
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static void setFeatureValue (@Nonnull final EXMLParserFeature eFeature, @Nullable final Boolean aValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    s_aRWLock.writeLock ().lock ();
    try
    {
      if (aValue == null)
        s_aDefaultFeatures.remove (eFeature);
      else
        s_aDefaultFeatures.put (eFeature, aValue);
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static void setFeatureValues (@Nullable final Map <EXMLParserFeature, Boolean> aValues)
  {
    if (aValues != null)
    {
      s_aRWLock.writeLock ().lock ();
      try
      {
        s_aDefaultFeatures.putAll (aValues);
      }
      finally
      {
        s_aRWLock.writeLock ().unlock ();
      }
    }
  }

  @Nonnull
  public static EChange removeFeature (@Nullable final EXMLParserFeature eFeature)
  {
    if (eFeature == null)
      return EChange.UNCHANGED;

    s_aRWLock.writeLock ().lock ();
    try
    {
      return EChange.valueOf (s_aDefaultFeatures.remove (eFeature) != null);
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public static EChange removeAllFeatures ()
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      if (s_aDefaultFeatures.isEmpty ())
        return EChange.UNCHANGED;
      s_aDefaultFeatures.clear ();
      return EChange.CHANGED;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static boolean requiresNewXMLParser ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
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
          !s_aDefaultProperties.isEmpty () ||
          !s_aDefaultFeatures.isEmpty ())
        return true;

      // Special case for JDK > 1.7.0_45 because of maximum entity expansion
      // See http://docs.oracle.com/javase/tutorial/jaxp/limits/limits.html
      return s_aDefaultEntityResolver != null;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  @Nullable
  public static EntityResolver getEntityResolver ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aDefaultEntityResolver;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setEntityResolver (@Nullable final EntityResolver aEntityResolver)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aDefaultEntityResolver = aEntityResolver;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  @Nullable
  public static ErrorHandler getErrorHandler ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aDefaultErrorHandler;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setErrorHandler (@Nullable final ErrorHandler aErrorHandler)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aDefaultErrorHandler = aErrorHandler;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  @Nonnull
  public static IExceptionCallback <Throwable> getExceptionHandler ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aDefaultExceptionHandler;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setExceptionHandler (@Nonnull final IExceptionCallback <Throwable> aExceptionHandler)
  {
    ValueEnforcer.notNull (aExceptionHandler, "ExceptionHandler");

    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aDefaultExceptionHandler = aExceptionHandler;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  public static boolean isRequiresNewXMLParserExplicitly ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_bDefaultRequiresNewXMLParserExplicitly;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setRequiresNewXMLParserExplicitly (final boolean bDefaultRequiresNewXMLParserExplicitly)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_bDefaultRequiresNewXMLParserExplicitly = bDefaultRequiresNewXMLParserExplicitly;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }
}
