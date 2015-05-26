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

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.PresentForCodeCoverage;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.callback.exception.IExceptionCallback;
import com.helger.commons.state.EChange;
import com.helger.commons.xml.EXMLParserFeature;
import com.helger.commons.xml.EXMLParserProperty;
import com.helger.commons.xml.sax.LoggingSAXErrorHandler;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * SAX reader default settings
 *
 * @author Philip Helger
 */
@ThreadSafe
public final class SAXReaderDefaultSettings
{
  public static final boolean DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY = false;

  private static final ReadWriteLock s_aRWLock = new ReentrantReadWriteLock ();

  @GuardedBy ("s_aRWLock")
  private static EntityResolver s_aDefaultEntityResolver;
  @GuardedBy ("s_aRWLock")
  private static DTDHandler s_aDefaultDTDHandler;
  @GuardedBy ("s_aRWLock")
  private static ContentHandler s_aDefaultContentHandler;
  @GuardedBy ("s_aRWLock")
  private static ErrorHandler s_aDefaultErrorHandler = LoggingSAXErrorHandler.getInstance ();
  @GuardedBy ("s_aRWLock")
  private static final EnumMap <EXMLParserProperty, Object> s_aDefaultProperties = new EnumMap <EXMLParserProperty, Object> (EXMLParserProperty.class);
  @GuardedBy ("s_aRWLock")
  private static final EnumMap <EXMLParserFeature, Boolean> s_aDefaultFeatures = new EnumMap <EXMLParserFeature, Boolean> (EXMLParserFeature.class);
  @GuardedBy ("s_aRWLock")
  private static IExceptionCallback <Throwable> s_aDefaultExceptionHandler = new XMLLoggingExceptionHandler ();
  @GuardedBy ("s_aRWLock")
  private static boolean s_bDefaultRequiresNewXMLParserExplicitly = DEFAULT_REQUIRES_NEW_XML_PARSER_EXPLICITLY;

  static
  {
    // By default enabled in XMLFactory
    if (false)
    {
      s_aDefaultFeatures.put (EXMLParserFeature.NAMESPACES, Boolean.TRUE);
      s_aDefaultFeatures.put (EXMLParserFeature.SAX_NAMESPACE_PREFIXES, Boolean.TRUE);
    }
    if (false)
      s_aDefaultFeatures.put (EXMLParserFeature.AUGMENT_PSVI, Boolean.FALSE);
  }

  @PresentForCodeCoverage
  private static final SAXReaderDefaultSettings s_aInstance = new SAXReaderDefaultSettings ();

  private SAXReaderDefaultSettings ()
  {}

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
  public static DTDHandler getDTDHandler ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aDefaultDTDHandler;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setDTDHandler (@Nullable final DTDHandler aDTDHandler)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aDefaultDTDHandler = aDTDHandler;
    }
    finally
    {
      s_aRWLock.writeLock ().unlock ();
    }
  }

  @Nullable
  public static ContentHandler getContentHandler ()
  {
    s_aRWLock.readLock ().lock ();
    try
    {
      return s_aDefaultContentHandler;
    }
    finally
    {
      s_aRWLock.readLock ().unlock ();
    }
  }

  public static void setContentHandler (@Nullable final ContentHandler aContentHandler)
  {
    s_aRWLock.writeLock ().lock ();
    try
    {
      s_aDefaultContentHandler = aContentHandler;
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

  @Nullable
  public static LexicalHandler getLexicalHandler ()
  {
    return (LexicalHandler) getPropertyValue (EXMLParserProperty.SAX_LEXICAL_HANDLER);
  }

  public static void setLexicalHandler (@Nullable final LexicalHandler aLexicalHandler)
  {
    setPropertyValue (EXMLParserProperty.SAX_LEXICAL_HANDLER, aLexicalHandler);
  }

  @Nullable
  public static DeclHandler getDeclarationHandler ()
  {
    return (DeclHandler) getPropertyValue (EXMLParserProperty.SAX_DECLARATION_HANDLER);
  }

  public static void setDeclarationHandler (@Nullable final DeclHandler aDeclHandler)
  {
    setPropertyValue (EXMLParserProperty.SAX_DECLARATION_HANDLER, aDeclHandler);
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
    if (aProperties != null && !aProperties.isEmpty ())
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
    if (aValues != null && !aValues.isEmpty ())
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

      if (!s_aDefaultProperties.isEmpty () || !s_aDefaultFeatures.isEmpty ())
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
