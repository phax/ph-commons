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

import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
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
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsEnumMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLParserProperty;

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
  public SAXReaderSettings (@NonNull final ISAXReaderSettings aOther)
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

  /**
   * Get the entity resolver to be used.
   *
   * @return The entity resolver. May be <code>null</code>.
   */
  @Nullable
  public EntityResolver getEntityResolver ()
  {
    return m_aEntityResolver;
  }

  /**
   * Set the entity resolver to be used.
   *
   * @param aEntityResolver
   *        The entity resolver. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setEntityResolver (@Nullable final EntityResolver aEntityResolver)
  {
    m_aEntityResolver = aEntityResolver;
    return this;
  }

  /**
   * Get the DTD handler to be used.
   *
   * @return The DTD handler. May be <code>null</code>.
   */
  @Nullable
  public DTDHandler getDTDHandler ()
  {
    return m_aDTDHandler;
  }

  /**
   * Set the DTD handler to be used.
   *
   * @param aDTDHandler
   *        The DTD handler. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setDTDHandler (@Nullable final DTDHandler aDTDHandler)
  {
    m_aDTDHandler = aDTDHandler;
    return this;
  }

  /**
   * Get the content handler to be used.
   *
   * @return The content handler. May be <code>null</code>.
   */
  @Nullable
  public ContentHandler getContentHandler ()
  {
    return m_aContentHandler;
  }

  /**
   * Set the content handler to be used.
   *
   * @param aContentHandler
   *        The content handler. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setContentHandler (@Nullable final ContentHandler aContentHandler)
  {
    m_aContentHandler = aContentHandler;
    return this;
  }

  /**
   * Get the error handler to be used.
   *
   * @return The error handler. May be <code>null</code>.
   */
  @Nullable
  public ErrorHandler getErrorHandler ()
  {
    return m_aErrorHandler;
  }

  /**
   * Set the error handler to be used.
   *
   * @param aErrorHandler
   *        The error handler. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setErrorHandler (@Nullable final ErrorHandler aErrorHandler)
  {
    m_aErrorHandler = aErrorHandler;
    return this;
  }

  /**
   * Get the lexical handler to be used.
   *
   * @return The lexical handler. May be <code>null</code>.
   */
  @Nullable
  public LexicalHandler getLexicalHandler ()
  {
    return (LexicalHandler) getPropertyValue (EXMLParserProperty.SAX_LEXICAL_HANDLER);
  }

  /**
   * Set the lexical handler to be used.
   *
   * @param aLexicalHandler
   *        The lexical handler. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setLexicalHandler (@Nullable final LexicalHandler aLexicalHandler)
  {
    return setPropertyValue (EXMLParserProperty.SAX_LEXICAL_HANDLER, aLexicalHandler);
  }

  /**
   * Get the declaration handler to be used.
   *
   * @return The declaration handler. May be <code>null</code>.
   */
  @Nullable
  public DeclHandler getDeclarationHandler ()
  {
    return (DeclHandler) getPropertyValue (EXMLParserProperty.SAX_DECLARATION_HANDLER);
  }

  /**
   * Set the declaration handler to be used.
   *
   * @param aDeclHandler
   *        The declaration handler. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setDeclarationHandler (@Nullable final DeclHandler aDeclHandler)
  {
    return setPropertyValue (EXMLParserProperty.SAX_DECLARATION_HANDLER, aDeclHandler);
  }

  /**
   * Check whether any properties have been set.
   *
   * @return <code>true</code> if at least one property is set,
   *         <code>false</code> otherwise.
   */
  public boolean hasAnyProperties ()
  {
    return !m_aProperties.isEmpty ();
  }

  /**
   * Get the value of a specific XML parser property.
   *
   * @param eProperty
   *        The property to query. May be <code>null</code>.
   * @return The property value or <code>null</code> if the property is not set
   *         or the parameter is <code>null</code>.
   */
  @Nullable
  public Object getPropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    return eProperty == null ? null : m_aProperties.get (eProperty);
  }

  /**
   * Get a copy of all property values.
   *
   * @return A mutable copy of all property values. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsMap <EXMLParserProperty, Object> getAllPropertyValues ()
  {
    return m_aProperties.getClone ();
  }

  /**
   * Set a specific XML parser property value.
   *
   * @param eProperty
   *        The property to set. May not be <code>null</code>.
   * @param aPropertyValue
   *        The value to set. May be <code>null</code> to remove the property.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setPropertyValue (@NonNull final EXMLParserProperty eProperty,
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

  /**
   * Set multiple XML parser property values at once.
   *
   * @param aProperties
   *        The properties to set. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setPropertyValues (@Nullable final Map <EXMLParserProperty, ?> aProperties)
  {
    if (aProperties != null)
      m_aProperties.putAll (aProperties);
    return this;
  }

  /**
   * Remove a specific XML parser property value.
   *
   * @param eProperty
   *        The property to remove. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings removePropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    if (eProperty != null)
      m_aProperties.removeObject (eProperty);
    return this;
  }

  /**
   * Remove all XML parser property values.
   *
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings removeAllPropertyValues ()
  {
    m_aProperties.removeAll ();
    return this;
  }

  /**
   * Get the locale to be used for parsing.
   *
   * @return The locale. May be <code>null</code>.
   */
  @Nullable
  public Locale getLocale ()
  {
    return (Locale) getPropertyValue (EXMLParserProperty.GENERAL_LOCALE);
  }

  /**
   * Set the locale to be used for parsing.
   *
   * @param aLocale
   *        The locale to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public SAXReaderSettings setLocale (@Nullable final Locale aLocale)
  {
    return setPropertyValue (EXMLParserProperty.GENERAL_LOCALE, aLocale);
  }

  /**
   * Check whether any features have been set.
   *
   * @return <code>true</code> if at least one feature is set,
   *         <code>false</code> otherwise.
   */
  public boolean hasAnyFeature ()
  {
    return !m_aFeatures.isEmpty ();
  }

  /**
   * Get the value of a specific XML parser feature.
   *
   * @param eFeature
   *        The feature to query. May be <code>null</code>.
   * @return The feature value or <code>null</code> if the feature is not set
   *         or the parameter is <code>null</code>.
   */
  @Nullable
  public Boolean getFeatureValue (@Nullable final EXMLParserFeature eFeature)
  {
    return eFeature == null ? null : m_aFeatures.get (eFeature);
  }

  /**
   * Get a copy of all feature values.
   *
   * @return A mutable copy of all feature values. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsMap <EXMLParserFeature, Boolean> getAllFeatureValues ()
  {
    return m_aFeatures.getClone ();
  }

  /**
   * Set a specific XML parser feature value.
   *
   * @param eFeature
   *        The feature to set. May not be <code>null</code>.
   * @param bValue
   *        The value to set.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setFeatureValue (@NonNull final EXMLParserFeature eFeature, final boolean bValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    m_aFeatures.put (eFeature, Boolean.valueOf (bValue));
    return this;
  }

  /**
   * Set a specific XML parser feature value.
   *
   * @param eFeature
   *        The feature to set. May not be <code>null</code>.
   * @param aValue
   *        The value to set. May be <code>null</code> to remove the feature.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setFeatureValue (@NonNull final EXMLParserFeature eFeature,
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
  @NonNull
  public final SAXReaderSettings setFeatureValues (@Nullable final Map <EXMLParserFeature, Boolean> aValues)
  {
    if (aValues != null)
      m_aFeatures.putAll (aValues);
    return this;
  }

  /**
   * Remove a specific XML parser feature.
   *
   * @param eFeature
   *        The feature to remove. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings removeFeature (@Nullable final EXMLParserFeature eFeature)
  {
    if (eFeature != null)
      m_aFeatures.removeObject (eFeature);
    return this;
  }

  /**
   * Remove multiple XML parser features at once.
   *
   * @param aFeatures
   *        The features to remove. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings removeFeatures (@Nullable final EXMLParserFeature... aFeatures)
  {
    if (aFeatures != null)
      for (final EXMLParserFeature eFeature : aFeatures)
        m_aFeatures.removeObject (eFeature);
    return this;
  }

  /**
   * Remove all XML parser features.
   *
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings removeAllFeatures ()
  {
    m_aFeatures.removeAll ();
    return this;
  }

  /**
   * Check whether a new XML parser is required based on the current settings.
   *
   * @return <code>true</code> if a new XML parser needs to be created,
   *         <code>false</code> if a pooled parser can be reused.
   */
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

  /**
   * Get the mutable list of exception callbacks.
   *
   * @return The mutable list of exception callbacks. Never <code>null</code>.
   */
  @NonNull
  @ReturnsMutableObject
  public CallbackList <IExceptionCallback <Throwable>> exceptionCallbacks ()
  {
    return m_aExceptionCallbacks;
  }

  /**
   * Get the custom SAX parser factory, if any.
   *
   * @return The custom SAX parser factory. May be <code>null</code>.
   */
  @Nullable
  public SAXParserFactory getCustomSAXParserFactory ()
  {
    return m_aCustomSAXParserFactory;
  }

  /**
   * Set a custom SAX parser factory to be used.
   *
   * @param aCustomSAXParserFactory
   *        The custom SAX parser factory. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setCustomSAXParserFactory (@Nullable final SAXParserFactory aCustomSAXParserFactory)
  {
    m_aCustomSAXParserFactory = aCustomSAXParserFactory;
    return this;
  }

  /**
   * Check whether a new XML parser is explicitly required.
   *
   * @return <code>true</code> if a new XML parser is explicitly required,
   *         <code>false</code> otherwise.
   */
  public boolean isRequiresNewXMLParserExplicitly ()
  {
    return m_bRequiresNewXMLParserExplicitly;
  }

  /**
   * Set whether a new XML parser should be explicitly required, regardless of
   * other settings.
   *
   * @param bRequiresNewXMLParserExplicitly
   *        <code>true</code> to always create a new parser,
   *        <code>false</code> to allow pooled parsers.
   * @return this for chaining
   */
  @NonNull
  public final SAXReaderSettings setRequiresNewXMLParserExplicitly (final boolean bRequiresNewXMLParserExplicitly)
  {
    m_bRequiresNewXMLParserExplicitly = bRequiresNewXMLParserExplicitly;
    return this;
  }

  /**
   * Create a clone of this settings object.
   *
   * @return A new {@link SAXReaderSettings} object with the same settings.
   *         Never <code>null</code>.
   */
  @NonNull
  public SAXReaderSettings getClone ()
  {
    return new SAXReaderSettings (this);
  }

  /**
   * Apply all settings of this object to the provided SAX {@link XMLReader}.
   *
   * @param aParser
   *        The SAX XMLReader to apply the settings to. May not be
   *        <code>null</code>.
   */
  public void applyToSAXReader (@NonNull final XMLReader aParser)
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
  @NonNull
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
