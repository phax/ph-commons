/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.xml.serialize.read;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.callback.exception.IExceptionCallback;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.xml.EXMLParserFeature;
import com.helger.commons.xml.EXMLParserProperty;

/**
 * SAX reader settings
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class SAXReaderSettings implements ISAXReaderSettings, ICloneable <SAXReaderSettings>
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (SAXReaderSettings.class);

  private EntityResolver m_aEntityResolver;
  private DTDHandler m_aDTDHandler;
  private ContentHandler m_aContentHandler;
  private ErrorHandler m_aErrorHandler;
  private final EnumMap <EXMLParserProperty, Object> m_aProperties = new EnumMap <EXMLParserProperty, Object> (EXMLParserProperty.class);
  private final EnumMap <EXMLParserFeature, Boolean> m_aFeatures = new EnumMap <EXMLParserFeature, Boolean> (EXMLParserFeature.class);
  private IExceptionCallback <Throwable> m_aExceptionHandler;
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
    setExceptionHandler (SAXReaderDefaultSettings.getExceptionHandler ());
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
    setExceptionHandler (aOther.getExceptionHandler ());
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
  public Map <EXMLParserProperty, Object> getAllPropertyValues ()
  {
    return new EnumMap <EXMLParserProperty, Object> (m_aProperties);
  }

  @Nonnull
  public final SAXReaderSettings setPropertyValue (@Nonnull final EXMLParserProperty eProperty,
                                                   @Nullable final Object aPropertyValue)
  {
    ValueEnforcer.notNull (eProperty, "Property");

    if (aPropertyValue != null &&
        eProperty.getValueClass () != null &&
        !eProperty.getValueClass ().isAssignableFrom (aPropertyValue.getClass ()))
      s_aLogger.warn ("Setting the XML parser property '" +
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
  public final EChange removePropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    return EChange.valueOf (eProperty != null && m_aProperties.remove (eProperty) != null);
  }

  @Nonnull
  public final EChange removeAllPropertyValues ()
  {
    if (m_aProperties.isEmpty ())
      return EChange.UNCHANGED;
    m_aProperties.clear ();
    return EChange.CHANGED;
  }

  @Nullable
  public Locale getLocale ()
  {
    return (Locale) getPropertyValue (EXMLParserProperty.GENERAL_LOCALE);
  }

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
  public Map <EXMLParserFeature, Boolean> getAllFeatureValues ()
  {
    return new EnumMap <EXMLParserFeature, Boolean> (m_aFeatures);
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

  @Nonnull
  public final SAXReaderSettings setFeatureValues (@Nullable final Map <EXMLParserFeature, Boolean> aValues)
  {
    if (aValues != null)
      m_aFeatures.putAll (aValues);
    return this;
  }

  @Nonnull
  public final EChange removeFeature (@Nullable final EXMLParserFeature eFeature)
  {
    return EChange.valueOf (eFeature != null && m_aFeatures.remove (eFeature) != null);
  }

  @Nonnull
  public final EChange removeAllFeatures ()
  {
    if (m_aFeatures.isEmpty ())
      return EChange.UNCHANGED;
    m_aFeatures.clear ();
    return EChange.CHANGED;
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
  public IExceptionCallback <Throwable> getExceptionHandler ()
  {
    return m_aExceptionHandler;
  }

  @Nonnull
  public final SAXReaderSettings setExceptionHandler (@Nonnull final IExceptionCallback <Throwable> aExceptionHandler)
  {
    ValueEnforcer.notNull (aExceptionHandler, "ExceptionHandler");

    m_aExceptionHandler = aExceptionHandler;
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

  public void applyToSAXReader (@Nonnull final org.xml.sax.XMLReader aParser)
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
    return new ToStringGenerator (this).append ("entityResolver", m_aEntityResolver)
                                       .append ("dtdHandler", m_aDTDHandler)
                                       .append ("contentHandler", m_aContentHandler)
                                       .append ("errorHandler", m_aErrorHandler)
                                       .append ("properties", m_aProperties)
                                       .append ("features", m_aFeatures)
                                       .append ("exceptionHandler", m_aExceptionHandler)
                                       .append ("requiresNewXMLParserExplicitly", m_bRequiresNewXMLParserExplicitly)
                                       .toString ();
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
