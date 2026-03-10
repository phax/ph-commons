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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.callback.CallbackList;
import com.helger.base.callback.exception.IExceptionCallback;
import com.helger.base.clone.ICloneable;
import com.helger.base.enforce.ValueEnforcer;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsEnumMap;
import com.helger.collection.commons.ICommonsMap;
import com.helger.xml.EXMLParserFeature;
import com.helger.xml.EXMLParserProperty;
import com.helger.xml.XMLFactory;

/**
 * DOM reader settings
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class DOMReaderSettings implements ICloneable <DOMReaderSettings>, IDOMReaderSettings
{
  private static final Logger LOGGER = LoggerFactory.getLogger (DOMReaderSettings.class);

  // DocumentBuilderFactory properties
  private boolean m_bNamespaceAware;
  private boolean m_bValidating;
  private boolean m_bIgnoringElementContentWhitespace;
  private boolean m_bExpandEntityReferences;
  private boolean m_bIgnoringComments;
  private boolean m_bCoalescing;
  private Schema m_aSchema;
  private boolean m_bXIncludeAware;
  private final ICommonsMap <EXMLParserProperty, Object> m_aProperties = new CommonsEnumMap <> (EXMLParserProperty.class);
  private final ICommonsMap <EXMLParserFeature, Boolean> m_aFeatures = new CommonsEnumMap <> (EXMLParserFeature.class);

  // DocumentBuilder properties
  private EntityResolver m_aEntityResolver;
  private ErrorHandler m_aErrorHandler;

  // Handling properties
  private final CallbackList <IExceptionCallback <Throwable>> m_aExceptionCallbacks = new CallbackList <> ();
  private boolean m_bRequiresNewXMLParserExplicitly;

  /**
   * Constructor using the default settings from
   * {@link DOMReaderDefaultSettings}.
   */
  public DOMReaderSettings ()
  {
    // DocumentBuilderFactory
    setNamespaceAware (DOMReaderDefaultSettings.isNamespaceAware ());
    setValidating (DOMReaderDefaultSettings.isValidating ());
    setIgnoringElementContentWhitespace (DOMReaderDefaultSettings.isIgnoringElementContentWhitespace ());
    setExpandEntityReferences (DOMReaderDefaultSettings.isExpandEntityReferences ());
    setIgnoringComments (DOMReaderDefaultSettings.isIgnoringComments ());
    setCoalescing (DOMReaderDefaultSettings.isCoalescing ());
    setSchema (DOMReaderDefaultSettings.getSchema ());
    setXIncludeAware (DOMReaderDefaultSettings.isXIncludeAware ());
    setPropertyValues (DOMReaderDefaultSettings.getAllPropertyValues ());
    setFeatureValues (DOMReaderDefaultSettings.getAllFeatureValues ());
    // DocumentBuilder
    setEntityResolver (DOMReaderDefaultSettings.getEntityResolver ());
    setErrorHandler (DOMReaderDefaultSettings.getErrorHandler ());
    // Custom
    exceptionCallbacks ().set (DOMReaderDefaultSettings.exceptionCallbacks ());
    setRequiresNewXMLParserExplicitly (DOMReaderDefaultSettings.isRequiresNewXMLParserExplicitly ());
  }

  /**
   * Copy constructor.
   *
   * @param aOther
   *        The settings object to copy from. May not be <code>null</code>.
   */
  public DOMReaderSettings (@NonNull final IDOMReaderSettings aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");

    // DocumentBuilderFactory
    setNamespaceAware (aOther.isNamespaceAware ());
    setValidating (aOther.isValidating ());
    setIgnoringElementContentWhitespace (aOther.isIgnoringElementContentWhitespace ());
    setExpandEntityReferences (aOther.isExpandEntityReferences ());
    setIgnoringComments (aOther.isIgnoringComments ());
    setCoalescing (aOther.isCoalescing ());
    setSchema (aOther.getSchema ());
    setXIncludeAware (aOther.isXIncludeAware ());
    setPropertyValues (aOther.getAllPropertyValues ());
    setFeatureValues (aOther.getAllFeatureValues ());
    // DocumentBuilder
    setEntityResolver (aOther.getEntityResolver ());
    setErrorHandler (aOther.getErrorHandler ());
    // Custom
    exceptionCallbacks ().set (aOther.exceptionCallbacks ());
    setRequiresNewXMLParserExplicitly (aOther.isRequiresNewXMLParserExplicitly ());
  }

  /** {@inheritDoc} */
  public boolean isNamespaceAware ()
  {
    return m_bNamespaceAware;
  }

  /**
   * Set whether the parser should be namespace aware.
   *
   * @param bNamespaceAware
   *        <code>true</code> if the parser should be namespace aware.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setNamespaceAware (final boolean bNamespaceAware)
  {
    m_bNamespaceAware = bNamespaceAware;
    return this;
  }

  /** {@inheritDoc} */
  public boolean isValidating ()
  {
    return m_bValidating;
  }

  /**
   * Set whether the parser should validate the document.
   *
   * @param bValidating
   *        <code>true</code> if the parser should validate the document.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setValidating (final boolean bValidating)
  {
    m_bValidating = bValidating;
    return this;
  }

  /** {@inheritDoc} */
  public boolean isIgnoringElementContentWhitespace ()
  {
    return m_bIgnoringElementContentWhitespace;
  }

  /**
   * Set whether the parser should ignore element content whitespace.
   *
   * @param bIgnoringElementContentWhitespace
   *        <code>true</code> if the parser should ignore element content
   *        whitespace.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setIgnoringElementContentWhitespace (final boolean bIgnoringElementContentWhitespace)
  {
    m_bIgnoringElementContentWhitespace = bIgnoringElementContentWhitespace;
    return this;
  }

  /** {@inheritDoc} */
  public boolean isExpandEntityReferences ()
  {
    return m_bExpandEntityReferences;
  }

  /**
   * Set whether the parser should expand entity references.
   *
   * @param bExpandEntityReferences
   *        <code>true</code> if the parser should expand entity references.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setExpandEntityReferences (final boolean bExpandEntityReferences)
  {
    m_bExpandEntityReferences = bExpandEntityReferences;
    return this;
  }

  /** {@inheritDoc} */
  public boolean isIgnoringComments ()
  {
    return m_bIgnoringComments;
  }

  /**
   * Set whether the parser should ignore comments.
   *
   * @param bIgnoringComments
   *        <code>true</code> if the parser should ignore comments.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setIgnoringComments (final boolean bIgnoringComments)
  {
    m_bIgnoringComments = bIgnoringComments;
    return this;
  }

  /** {@inheritDoc} */
  public boolean isCoalescing ()
  {
    return m_bCoalescing;
  }

  /**
   * Set whether the parser should be coalescing (convert CDATA nodes to text
   * nodes and append to an adjacent text node if any).
   *
   * @param bCoalescing
   *        <code>true</code> if the parser should be coalescing.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setCoalescing (final boolean bCoalescing)
  {
    m_bCoalescing = bCoalescing;
    return this;
  }

  /** {@inheritDoc} */
  @Nullable
  public Schema getSchema ()
  {
    return m_aSchema;
  }

  /**
   * Set the XML {@link Schema} to use for validation.
   *
   * @param aSchema
   *        The schema to use. May be <code>null</code> to disable schema
   *        validation.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setSchema (@Nullable final Schema aSchema)
  {
    m_aSchema = aSchema;
    return this;
  }

  /** {@inheritDoc} */
  public boolean isXIncludeAware ()
  {
    return m_bXIncludeAware;
  }

  /**
   * Set whether the parser should be XInclude aware.
   *
   * @param bXIncludeAware
   *        <code>true</code> if the parser should be XInclude aware.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setXIncludeAware (final boolean bXIncludeAware)
  {
    m_bXIncludeAware = bXIncludeAware;
    return this;
  }

  /** {@inheritDoc} */
  public boolean hasAnyProperties ()
  {
    return m_aProperties.isNotEmpty ();
  }

  /** {@inheritDoc} */
  @Nullable
  public Object getPropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    return eProperty == null ? null : m_aProperties.get (eProperty);
  }

  /** {@inheritDoc} */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsMap <EXMLParserProperty, Object> getAllPropertyValues ()
  {
    return m_aProperties.getClone ();
  }

  /**
   * Set a parser property value.
   *
   * @param eProperty
   *        The property to set. May not be <code>null</code>.
   * @param aPropertyValue
   *        The property value to set. May be <code>null</code> to remove
   *        the property.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setPropertyValue (@NonNull final EXMLParserProperty eProperty,
                                                   @Nullable final Object aPropertyValue)
  {
    ValueEnforcer.notNull (eProperty, "Property");
    if (aPropertyValue != null &&
        eProperty.getValueClass () != null &&
        !eProperty.getValueClass ().isAssignableFrom (aPropertyValue.getClass ()))
    {
      LOGGER.warn ("Setting the XML parser property '" +
                   eProperty +
                   "' to a value of " +
                   aPropertyValue.getClass () +
                   " will most likely not be interpreted!");
    }
    if (aPropertyValue != null)
      m_aProperties.put (eProperty, aPropertyValue);
    else
      m_aProperties.remove (eProperty);
    return this;
  }

  /**
   * Set multiple parser property values at once.
   *
   * @param aProperties
   *        The properties to set. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setPropertyValues (@Nullable final Map <EXMLParserProperty, ?> aProperties)
  {
    if (aProperties != null)
      m_aProperties.putAll (aProperties);
    return this;
  }

  /**
   * Remove a specific parser property value.
   *
   * @param eProperty
   *        The property to remove. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings removePropertyValue (@Nullable final EXMLParserProperty eProperty)
  {
    if (eProperty != null)
      m_aProperties.removeObject (eProperty);
    return this;
  }

  /**
   * Remove all parser property values.
   *
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings removeAllPropertyValues ()
  {
    m_aProperties.removeAll ();
    return this;
  }

  /** {@inheritDoc} */
  @Nullable
  public Locale getLocale ()
  {
    return (Locale) getPropertyValue (EXMLParserProperty.GENERAL_LOCALE);
  }

  /**
   * Set the locale to use for error messages.
   *
   * @param aLocale
   *        The locale to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public DOMReaderSettings setLocale (@Nullable final Locale aLocale)
  {
    return setPropertyValue (EXMLParserProperty.GENERAL_LOCALE, aLocale);
  }

  /** {@inheritDoc} */
  public boolean hasAnyFeature ()
  {
    return m_aFeatures.isNotEmpty ();
  }

  /** {@inheritDoc} */
  @Nullable
  public Boolean getFeatureValue (@Nullable final EXMLParserFeature eFeature)
  {
    return eFeature == null ? null : m_aFeatures.get (eFeature);
  }

  /** {@inheritDoc} */
  @NonNull
  @ReturnsMutableCopy
  public ICommonsMap <EXMLParserFeature, Boolean> getAllFeatureValues ()
  {
    return m_aFeatures.getClone ();
  }

  /**
   * Set a parser feature value.
   *
   * @param eFeature
   *        The feature to set. May not be <code>null</code>.
   * @param bValue
   *        The feature value to set.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setFeatureValue (@NonNull final EXMLParserFeature eFeature, final boolean bValue)
  {
    ValueEnforcer.notNull (eFeature, "Feature");

    m_aFeatures.put (eFeature, Boolean.valueOf (bValue));
    return this;
  }

  /**
   * Set a parser feature value with an optional {@link Boolean} wrapper.
   *
   * @param eFeature
   *        The feature to set. May not be <code>null</code>.
   * @param aValue
   *        The feature value to set. May be <code>null</code> to remove the
   *        feature.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setFeatureValue (@NonNull final EXMLParserFeature eFeature,
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
   * Set multiple parser feature values at once.
   *
   * @param aValues
   *        The feature values to set. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setFeatureValues (@Nullable final Map <EXMLParserFeature, Boolean> aValues)
  {
    if (aValues != null)
      m_aFeatures.putAll (aValues);
    return this;
  }

  /**
   * Remove a specific parser feature setting.
   *
   * @param eFeature
   *        The feature to remove. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings removeFeature (@Nullable final EXMLParserFeature eFeature)
  {
    if (eFeature != null)
      m_aFeatures.removeObject (eFeature);
    return this;
  }

  /**
   * Remove multiple parser feature settings at once.
   *
   * @param aFeatures
   *        The features to remove. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings removeFeatures (@Nullable final EXMLParserFeature... aFeatures)
  {
    if (aFeatures != null)
      for (final EXMLParserFeature eFeature : aFeatures)
        m_aFeatures.removeObject (eFeature);
    return this;
  }

  /**
   * Remove all parser feature settings.
   *
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings removeAllFeatures ()
  {
    m_aFeatures.removeAll ();
    return this;
  }

  /** {@inheritDoc} */
  public boolean requiresNewXMLParser ()
  {
    // Force a new XML parser?
    if (m_bRequiresNewXMLParserExplicitly)
      return true;

    if (m_bNamespaceAware != XMLFactory.DEFAULT_DOM_NAMESPACE_AWARE ||
        m_bValidating != XMLFactory.DEFAULT_DOM_VALIDATING ||
        m_bIgnoringElementContentWhitespace != XMLFactory.DEFAULT_DOM_IGNORING_ELEMENT_CONTENT_WHITESPACE ||
        m_bExpandEntityReferences != XMLFactory.DEFAULT_DOM_EXPAND_ENTITY_REFERENCES ||
        m_bIgnoringComments != XMLFactory.DEFAULT_DOM_IGNORING_COMMENTS ||
        m_bCoalescing != XMLFactory.DEFAULT_DOM_COALESCING ||
        m_aSchema != null ||
        m_bXIncludeAware != XMLFactory.DEFAULT_DOM_XINCLUDE_AWARE ||
        m_aProperties.isNotEmpty () ||
        m_aFeatures.isNotEmpty ())
      return true;

    // Special case for JDK > 1.7.0_45 because of maximum entity expansion
    // See http://docs.oracle.com/javase/tutorial/jaxp/limits/limits.html
    return m_aEntityResolver != null;
  }

  /** {@inheritDoc} */
  @Nullable
  public EntityResolver getEntityResolver ()
  {
    return m_aEntityResolver;
  }

  /**
   * Set the entity resolver to use.
   *
   * @param aEntityResolver
   *        The entity resolver to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setEntityResolver (@Nullable final EntityResolver aEntityResolver)
  {
    m_aEntityResolver = aEntityResolver;
    return this;
  }

  /** {@inheritDoc} */
  @Nullable
  public ErrorHandler getErrorHandler ()
  {
    return m_aErrorHandler;
  }

  /**
   * Set the error handler to use for parsing.
   *
   * @param aErrorHandler
   *        The error handler to use. May be <code>null</code>.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setErrorHandler (@Nullable final ErrorHandler aErrorHandler)
  {
    m_aErrorHandler = aErrorHandler;
    return this;
  }

  /** {@inheritDoc} */
  @NonNull
  public CallbackList <IExceptionCallback <Throwable>> exceptionCallbacks ()
  {
    return m_aExceptionCallbacks;
  }

  /** {@inheritDoc} */
  public boolean isRequiresNewXMLParserExplicitly ()
  {
    return m_bRequiresNewXMLParserExplicitly;
  }

  /**
   * Set whether a new XML parser is explicitly required (instead of using a
   * pooled one).
   *
   * @param bRequiresNewXMLParserExplicitly
   *        <code>true</code> to always create a new XML parser.
   * @return this for chaining
   */
  @NonNull
  public final DOMReaderSettings setRequiresNewXMLParserExplicitly (final boolean bRequiresNewXMLParserExplicitly)
  {
    m_bRequiresNewXMLParserExplicitly = bRequiresNewXMLParserExplicitly;
    return this;
  }

  /** {@inheritDoc} */
  @NonNull
  public DOMReaderSettings getClone ()
  {
    return new DOMReaderSettings (this);
  }

  /** {@inheritDoc} */
  public void applyToDocumentBuilderFactory (@NonNull final DocumentBuilderFactory aDocumentBuilderFactory)
  {
    ValueEnforcer.notNull (aDocumentBuilderFactory, "DocumentBuilderFactory");

    aDocumentBuilderFactory.setNamespaceAware (isNamespaceAware ());
    aDocumentBuilderFactory.setValidating (isValidating ());
    aDocumentBuilderFactory.setIgnoringElementContentWhitespace (isIgnoringElementContentWhitespace ());
    aDocumentBuilderFactory.setExpandEntityReferences (isExpandEntityReferences ());
    aDocumentBuilderFactory.setIgnoringComments (isIgnoringComments ());
    aDocumentBuilderFactory.setCoalescing (isCoalescing ());
    try
    {
      aDocumentBuilderFactory.setSchema (getSchema ());
    }
    catch (final UnsupportedOperationException ex)
    {
      LOGGER.warn ("DocumentBuilderFactory does not support XML Schema: " + ex.getMessage ());
    }
    try
    {
      aDocumentBuilderFactory.setXIncludeAware (isXIncludeAware ());
    }
    catch (final UnsupportedOperationException ex)
    {
      LOGGER.warn ("DocumentBuilderFactory does not support XInclude setting: " + ex.getMessage ());
    }
    // Apply properties
    if (hasAnyProperties ())
      for (final Map.Entry <EXMLParserProperty, Object> aEntry : getAllPropertyValues ().entrySet ())
        aEntry.getKey ().applyTo (aDocumentBuilderFactory, aEntry.getValue ());

    // Apply features
    if (hasAnyFeature ())
      for (final Map.Entry <EXMLParserFeature, Boolean> aEntry : getAllFeatureValues ().entrySet ())
        aEntry.getKey ().applyTo (aDocumentBuilderFactory, aEntry.getValue ().booleanValue ());
  }

  /** {@inheritDoc} */
  public void applyToDocumentBuilder (@NonNull final DocumentBuilder aDocumentBuilder)
  {
    ValueEnforcer.notNull (aDocumentBuilder, "DocumentBuilder");

    // Set error handler
    aDocumentBuilder.setErrorHandler (getErrorHandler ());

    // Set optional entity resolver
    aDocumentBuilder.setEntityResolver (getEntityResolver ());
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("namespaceAware", m_bNamespaceAware)
                                       .append ("validating", m_bValidating)
                                       .append ("ignoringElementContentWhitespace", m_bIgnoringElementContentWhitespace)
                                       .append ("expandEntityReferences", m_bExpandEntityReferences)
                                       .append ("ignoringComments", m_bIgnoringComments)
                                       .append ("coalescing", m_bCoalescing)
                                       .append ("schema", m_aSchema)
                                       .append ("XIncludeAware", m_bXIncludeAware)
                                       .append ("properties", m_aProperties)
                                       .append ("features", m_aFeatures)
                                       .append ("entityResolver", m_aEntityResolver)
                                       .append ("errorHandler", m_aErrorHandler)
                                       .append ("exceptionCallbacks", m_aExceptionCallbacks)
                                       .append ("requiresNewXMLParserExplicitly", m_bRequiresNewXMLParserExplicitly)
                                       .getToString ();
  }
}
