/*
 * Copyright (C) 2014-2021 Philip Helger (www.helger.com)
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

import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

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
import com.helger.xml.sax.LoggingSAXErrorHandler;

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
  public static DTDHandler getDTDHandler ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultDTDHandler);
  }

  public static void setDTDHandler (@Nullable final DTDHandler aDTDHandler)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultDTDHandler = aDTDHandler);
  }

  @Nullable
  public static ContentHandler getContentHandler ()
  {
    return RW_LOCK.readLockedGet ( () -> s_aDefaultContentHandler);
  }

  public static void setContentHandler (@Nullable final ContentHandler aContentHandler)
  {
    RW_LOCK.writeLocked ( () -> s_aDefaultContentHandler = aContentHandler);
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
    return RW_LOCK.readLockedBoolean ( () -> !DEFAULT_PROPS.isEmpty ());
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

  public static void setPropertyValue (@Nonnull final EXMLParserProperty eProperty, @Nullable final Object aPropertyValue)
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
    if (aProperties != null && !aProperties.isEmpty ())
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
    if (aValues != null && !aValues.isEmpty ())
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

      if (!DEFAULT_PROPS.isEmpty () || !DEFAULT_FEATURES.isEmpty ())
        return true;

      // Special case for JDK > 1.7.0_45 because of maximum entity expansion
      // See http://docs.oracle.com/javase/tutorial/jaxp/limits/limits.html
      return s_aDefaultEntityResolver != null;
    });
  }

  @Nonnull
  @ReturnsMutableObject
  public static CallbackList <IExceptionCallback <Throwable>> exceptionCallbacks ()
  {
    return s_aDefaultExceptionCallbacks;
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
