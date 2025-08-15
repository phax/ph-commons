/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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

import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.annotation.style.ReturnsMutableObject;
import com.helger.base.callback.CallbackList;
import com.helger.base.callback.exception.IExceptionCallback;
import com.helger.base.clone.ICloneable;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsEnumMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLParserProperty;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * SAX reader settings
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class SAXReaderSettings implements ISAXReaderSettings, ICloneable <SAXReaderSettings>
{
  private static final Logger LOGGER = LoggerFactory.getLogger (SAXReaderSettings.class);

  private EntityResolver m_aEntityResolver;
  private DTDHandler m_aDTDHandler;
  private ContentHandler m_aContentHandler;
  private ErrorHandler m_aErrorHandler;
  private final ICommonsMap <EXMLParserProperty, Object> m_aProperties = new CommonsEnumMap <> (EXMLParserProperty.class);
  private final ICommonsMap <EXMLParserFeature, Boolean> m_aFeatures = new CommonsEnumMap <> (EXMLParserFeature.class);
  private final CallbackList <IExceptionCallback <Throwable>> m_aExceptionCallbacks = new CallbackList <> ();
  private SAXParserFactory m_aCustomSAXParserFactory;
  private boolean m_bRequiresNewXMLParserExplicitly;

  /**
   * Default constructor
   */
  public SAXReaderSettings ()
  {
    // Set default values
    setEntityResolver (SAXReaderDefaultSettings.getEntityResolver ());
    setDTDHandler (SAXReaderDefaultSettings.getDTDHandler ());
    setContentHandler (SAXReaderDefaultSettings.getContentHandler ());
    setErrorHandler (SAXReaderDefaultSettings.getErrorHandler ());
    setPropertyValues (SAXReaderDefaultSettings.getAllPropertyValues ());
    setFeatureValues (SAXReaderDefaultSettings.getAllFeatureValues ());
    exceptionCallbacks ().set (SAXReaderDefaultSettings.exceptionCallbacks ());
    // Custom factory is always null
    setCustomSAXParserFactory (null);
    setRequiresNewXMLParserExplicitly (SAXReaderDefaultSettings.isRequiresNewXMLParserExplicitly ());
  }

  /**
   * Copy constructor
   *
   * @param aOther
   *        the object to copy the settings from. May not be <code>null</code>.
   */
  public SAXReaderSettings (@Nonnull final ISAXReaderSettings aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");

    // Set default values
    setEntityResolver (aOther.getEntityResolver ());
    setDTDHandler (aOther.getDTDHandler ());
    setContentHandler (aOther.getContentHandler ());
    setErrorHandler (aOther.getErrorHandler ());
    setPropertyValues (aOther.getAllPropertyValues ());
    setFeatureValues (aOther.getAllFeatureValues ());
    exceptionCallbacks ().set (aOther.exceptionCallbacks ());
    setCustomSAXParserFactory (aOther.getCustomSAXParserFactory ());
    setRequiresNewXMLParserExplicitly (aOther.isRequiresNewXMLParserExplicitly ());
  }

  @Nullable
  public EntityResolver getEntityResolver ()
  {
    return m_aEntityResolver;
  }

  @Nonnull
  public final SAXReaderSettings setEntityResolver (@Nullable final EntityResolver aEntityResolver)
  {
    m_aEntityResolver = aEntityResolver;
    return this;
  }

  @Nullable
  public DTDHandler getDTDHandler ()
  {
    return m_aDTDHandler;
  }

  @Nonnull
  public final SAXReaderSettings setDTDHandler (@Nullable final DTDHandler aDTDHandler)
  {
    m_aDTDHandler = aDTDHandler;
    return this;
  }

  @Nullable
  public ContentHandler getContentHandler ()
  {
    return m_aContentHandler;
  }

  @Nonnull
  public final SAXReaderSettings setContentHandler (@Nullable final ContentHandler aContentHandler)
  {
    m_aContentHandler = aContentHandler;
    return this;
  }

  @Nullable
  public ErrorHandler getErrorHandler ()
  {
    return m_aErrorHandler;
  }

  @Nonnull
  public final SAXReaderSettings setErrorHandler (@Nullable final ErrorHandler aErrorHandler)
  {
    m_aErrorHandler = aErrorHandler;
    return this;
  }

  @Nullable
  public LexicalHandler getLexicalHandler ()
  {
    return (LexicalHandler) getPropertyValue (EXMLParserProperty.SAX_LEXICAL_HANDLER);
  }

  @Nonnull
  public final SAXReaderSettings setLexicalHandler (@Nullable final LexicalHandler aLexicalHandler)
  {
    return setPropertyValue (EXMLParserProperty.SAX_LEXICAL_HANDLER, aLexicalHandler);
  }

  @Nullable
  public DeclHandler getDeclarationHandler ()
  {
    return (DeclHandler) getPropertyValue (EXMLParserProperty.SAX_DECLARATION_HANDLER);
  }

  @Nonnull
  public final SAXReaderSettings setDeclarationHandler (@Nullable final DeclHandler aDeclHandler)
  {
    return setPropertyValue (EXMLParserProperty.SAX_DECLARATION_HANDLER, aDeclHandler);
  }

  public boolean hasAnyProperties ()
  {
    return !m_aProperties.isEmpty ();
  }

  @Nullable
  public Object getPropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    return eProperty == null ? null : m_aProperties.get (eProperty);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsMap <EXMLParserProperty, Object> getAllPropertyValues ()
  {
    return m_aProperties.getClone ();
  }

  @Nonnull
  public final SAXReaderSettings setPropertyValue (@Nonnull final EXMLParserProperty eProperty,
                                                   @Nullable final Object aPropertyValue)
  {
    ValueEnforcer.notNull (eProperty, "Property");

    if (aPropertyValue != null &&
        eProperty.getValueClass () != null &&
        !eProperty.getValueClass ().isAssignableFrom (aPropertyValue.getClass ()))
      LOGGER.warn ("Setting the XML parser property '" +
                   eProperty +
                   "' to a value of " +
                   aPropertyValue.getClass () +
                   " will most likely not be interpreted!");

    if (aPropertyValue != null)
      m_aProperties.put (eProperty, aPropertyValue);
    else
      m_aProperties.remove (eProperty);
    return this;
  }

  @Nonnull
  public final SAXReaderSettings setPropertyValues (@Nullable final Map <EXMLParserProperty, ?> aProperties)
  {
    if (aProperties != null)
      m_aProperties.putAll (aProperties);
    return this;
  }

  @Nonnull
  public final SAXReaderSettings removePropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    if (eProperty != null)
      m_aProperties.removeObject (eProperty);
    return this;
  }

  @Nonnull
  public final SAXReaderSettings removeAllPropertyValues ()
  {
    m_aProperties.removeAll ();
    return this;
  }

  @Nullable
  public Locale getLocale ()
  {
    return (Locale) getPropertyValue (EXMLParserProperty.GENERAL_LOCALE);
  }

  @Nonnull
  public SAXReaderSettings setLocale (@Nullable final Locale aLocale)
  {
    return setPropertyValue (EXMLParserProperty.GENERAL_LOCALE, aLocale);
  }

  public boolean hasAnyFeature ()
  {
    return !m_aFeatures.isEmpty ();
  }

  @Nullable
  public Boolean getFeatureValue (@Nullable final EXMLParserFeature eFeature)
  {
    return eFeature == null ? null : m_aFeatures.get (eFeature);
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsMap <EXMLParserFeature, Boolean> getAllFeatureValues ()
  {
    return m_aFeatures.getClone ();
  }

  @Nonnull
  public final SAXReaderSettings setFeatureValue (@Nonnull final EXMLParserFeature eFeature, final boolean bValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    m_aFeatures.put (eFeature, Boolean.valueOf (bValue));
    return this;
  }

  @Nonnull
  public final SAXReaderSettings setFeatureValue (@Nonnull final EXMLParserFeature eFeature,
                                                  @Nullable final Boolean aValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    if (aValue == null)
      m_aFeatures.remove (eFeature);
    else
      m_aFeatures.put (eFeature, aValue);
    return this;
  }

  /**
   * Add multiple XML parser features at once.
   *
   * @param aValues
   *        The values to be added. May be <code>null</code>.
   * @return this for chaining
   */
  @Nonnull
  public final SAXReaderSettings setFeatureValues (@Nullable final Map <EXMLParserFeature, Boolean> aValues)
  {
    if (aValues != null)
      m_aFeatures.putAll (aValues);
    return this;
  }

  @Nonnull
  public final SAXReaderSettings removeFeature (@Nullable final EXMLParserFeature eFeature)
  {
    if (eFeature != null)
      m_aFeatures.removeObject (eFeature);
    return this;
  }

  @Nonnull
  public final SAXReaderSettings removeFeatures (@Nullable final EXMLParserFeature... aFeatures)
  {
    if (aFeatures != null)
      for (final EXMLParserFeature eFeature : aFeatures)
        m_aFeatures.removeObject (eFeature);
    return this;
  }

  @Nonnull
  public final SAXReaderSettings removeAllFeatures ()
  {
    m_aFeatures.removeAll ();
    return this;
  }

  public boolean requiresNewXMLParser ()
  {
    // Force a new XML parser?
    if (m_bRequiresNewXMLParserExplicitly)
      return true;

    if (!m_aProperties.isEmpty () || !m_aFeatures.isEmpty ())
      return true;

    // Special case for JDK > 1.7.0_45 because of maximum entity expansion
    // See http://docs.oracle.com/javase/tutorial/jaxp/limits/limits.html
    return m_aEntityResolver != null;
  }

  @Nonnull
  @ReturnsMutableObject
  public CallbackList <IExceptionCallback <Throwable>> exceptionCallbacks ()
  {
    return m_aExceptionCallbacks;
  }

  @Nullable
  public SAXParserFactory getCustomSAXParserFactory ()
  {
    return m_aCustomSAXParserFactory;
  }

  @Nonnull
  public final SAXReaderSettings setCustomSAXParserFactory (@Nullable final SAXParserFactory aCustomSAXParserFactory)
  {
    m_aCustomSAXParserFactory = aCustomSAXParserFactory;
    return this;
  }

  public boolean isRequiresNewXMLParserExplicitly ()
  {
    return m_bRequiresNewXMLParserExplicitly;
  }

  @Nonnull
  public final SAXReaderSettings setRequiresNewXMLParserExplicitly (final boolean bRequiresNewXMLParserExplicitly)
  {
    m_bRequiresNewXMLParserExplicitly = bRequiresNewXMLParserExplicitly;
    return this;
  }

  @Nonnull
  public SAXReaderSettings getClone ()
  {
    return new SAXReaderSettings (this);
  }

  public void applyToSAXReader (@Nonnull final XMLReader aParser)
  {
    ValueEnforcer.notNull (aParser, "Parser");

    aParser.setContentHandler (getContentHandler ());
    aParser.setDTDHandler (getDTDHandler ());
    aParser.setEntityResolver (getEntityResolver ());
    aParser.setErrorHandler (getErrorHandler ());

    // Apply properties
    if (hasAnyProperties ())
      for (final Map.Entry <EXMLParserProperty, Object> aEntry : getAllPropertyValues ().entrySet ())
        aEntry.getKey ().applyTo (aParser, aEntry.getValue ());

    // Apply features
    if (hasAnyFeature ())
      for (final Map.Entry <EXMLParserFeature, Boolean> aEntry : getAllFeatureValues ().entrySet ())
        aEntry.getKey ().applyTo (aParser, aEntry.getValue ().booleanValue ());
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("EntityResolver", m_aEntityResolver)
                                       .append ("DtdHandler", m_aDTDHandler)
                                       .append ("ContentHandler", m_aContentHandler)
                                       .append ("ErrorHandler", m_aErrorHandler)
                                       .append ("Properties", m_aProperties)
                                       .append ("Features", m_aFeatures)
                                       .append ("ExceptionHandler", m_aExceptionCallbacks)
                                       .append ("CustomSAXParserFactory", m_aCustomSAXParserFactory)
                                       .append ("RequiresNewXMLParserExplicitly", m_bRequiresNewXMLParserExplicitly)
                                       .getToString ();
  }

  /**
   * Create a clone of the passed settings, depending on the parameter. If the
   * parameter is <code>null</code> a new empty {@link SAXReaderSettings} object
   * is created, otherwise a copy of the parameter is created.
   *
   * @param aOther
   *        The parameter to be used. May be <code>null</code>.
   * @return Never <code>null</code>.
   */
  @Nonnull
  public static SAXReaderSettings createCloneOnDemand (@Nullable final ISAXReaderSettings aOther)
  {
    if (aOther == null)
    {
      // Create plain object
      return new SAXReaderSettings ();
    }
    // Create a clone
    return new SAXReaderSettings (aOther);
  }
}
