/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.xml.microdom;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.DevelopersNote;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.collection.impl.ICommonsOrderedMap;
import com.helger.commons.collection.impl.ICommonsOrderedSet;
import com.helger.commons.functional.ITriConsumer;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.StringParser;
import com.helger.commons.typeconvert.TypeConverter;

/**
 * Interface for objects having attributes. Currently this is only an element.
 *
 * @author Philip Helger
 * @param <IMPLTYPE>
 *        implementation type
 */
public interface IMicroAttributeContainer <IMPLTYPE extends IMicroAttributeContainer <IMPLTYPE>> extends IMicroNode
{
  /**
   * @return <code>true</code> if this element has at least one attribute,
   *         <code>false</code> otherwise
   */
  boolean hasAttributes ();

  /**
   * @return <code>true</code> if this element has no attribute,
   *         <code>false</code> if at least one attribute is present.
   */
  boolean hasNoAttributes ();

  /**
   * @return The number of assigned attributes. Always &ge; 0.
   */
  @Nonnegative
  int getAttributeCount ();

  /**
   * Check if this element has an attribute with the specified name.
   *
   * @param sAttrName
   *        The attribute name to check.
   * @return <code>true</code> if such an attribute is present,
   *         <code>false</code> otherwise
   */
  default boolean hasAttribute (@Nullable final String sAttrName)
  {
    return hasAttribute (null, sAttrName);
  }

  /**
   * Check if this element has an attribute with the specified name.
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        The attribute name to check.
   * @return <code>true</code> if such an attribute is present,
   *         <code>false</code> otherwise
   */
  default boolean hasAttribute (@Nullable final String sNamespaceURI, @Nullable final String sAttrName)
  {
    if (StringHelper.hasNoText (sAttrName))
      return false;
    return hasAttribute (new MicroQName (sNamespaceURI, sAttrName));
  }

  /**
   * Check if this element has an attribute with the specified name.
   *
   * @param aAttrName
   *        The qualified attribute name to check. May be <code>null</code>.
   * @return <code>true</code> if such an attribute is present,
   *         <code>false</code> otherwise
   */
  boolean hasAttribute (@Nullable IMicroQName aAttrName);

  /**
   * Get the attribute object with the specified name.
   *
   * @param sAttrName
   *        The attribute locale name to query. May be <code>null</code>.
   * @return <code>null</code> of no such attribute object exists.
   */
  @Nullable
  default IMicroAttribute getAttributeObj (@Nullable final String sAttrName)
  {
    return getAttributeObj (null, sAttrName);
  }

  /**
   * Get the attribute object with the specified namespace URI and local name.
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        The attribute locale name to query. May be <code>null</code>.
   * @return <code>null</code> of no such attribute object exists.
   */
  @Nullable
  default IMicroAttribute getAttributeObj (@Nullable final String sNamespaceURI, @Nullable final String sAttrName)
  {
    if (StringHelper.hasNoText (sAttrName))
      return null;
    return getAttributeObj (new MicroQName (sNamespaceURI, sAttrName));
  }

  /**
   * Get the attribute object with the specified qualified name.
   *
   * @param aAttrName
   *        The qualified attribute name to check. May be <code>null</code>.
   * @return <code>null</code> of no such attribute object exists.
   */
  @Nullable
  IMicroAttribute getAttributeObj (@Nullable IMicroQName aAttrName);

  /**
   * Get the attribute value of the given attribute name. If this element has no
   * such attribute, <code>null</code> is returned.
   *
   * @param sAttrName
   *        The attribute name to retrieve the value of.
   * @return The assigned attribute value or <code>null</code>.
   */
  @Nullable
  default String getAttributeValue (@Nullable final String sAttrName)
  {
    return getAttributeValue ((String) null, sAttrName);
  }

  /**
   * Get the attribute value of the given attribute name. If this element has no
   * such attribute, <code>null</code> is returned.
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        The attribute name to retrieve the value of.
   * @return The assigned attribute value or <code>null</code>.
   */
  @Nullable
  default String getAttributeValue (@Nullable final String sNamespaceURI, @Nullable final String sAttrName)
  {
    return getAttributeValue (new MicroQName (sNamespaceURI, sAttrName));
  }

  /**
   * Get the attribute value of the given attribute name. If this element has no
   * such attribute, <code>null</code> is returned.
   *
   * @param aAttrName
   *        The qualified attribute name to retrieve the value of. May be
   *        <code>null</code>.
   * @return The assigned attribute value or <code>null</code>.
   */
  @Nullable
  default String getAttributeValue (@Nullable final IMicroQName aAttrName)
  {
    final IMicroAttribute aAttr = getAttributeObj (aAttrName);
    return aAttr == null ? null : aAttr.getAttributeValue ();
  }

  /**
   * Get the attribute value of the given attribute name. If this element has no
   * such attribute, <code>null</code> is returned. The attribute value is
   * converted via the {@link com.helger.commons.typeconvert.TypeConverter} to
   * the desired destination class. If no such attribute is present,
   * <code>null</code> is returned.
   *
   * @param <DSTTYPE>
   *        Destination type
   * @param sAttrName
   *        The attribute name to retrieve the value of.
   * @param aDstClass
   *        The destination class.
   * @return The assigned attribute value or <code>null</code>.
   * @throws ClassCastException
   *         if the value cannot be converted
   */
  @Nullable
  default <DSTTYPE> DSTTYPE getAttributeValueWithConversion (@Nullable final String sAttrName,
                                                             @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return getAttributeValueWithConversion ((String) null, sAttrName, aDstClass);
  }

  /**
   * Get the attribute value of the given attribute name. If this element has no
   * such attribute, <code>null</code> is returned. The attribute value is
   * converted via the {@link com.helger.commons.typeconvert.TypeConverter} to
   * the desired destination class. If no such attribute is present,
   * <code>null</code> is returned.
   *
   * @param <DSTTYPE>
   *        Destination type
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        The attribute name to retrieve the value of.
   * @param aDstClass
   *        The destination class.
   * @return The assigned attribute value or <code>null</code>.
   * @throws ClassCastException
   *         if the value cannot be converted
   */
  @Nullable
  default <DSTTYPE> DSTTYPE getAttributeValueWithConversion (@Nullable final String sNamespaceURI,
                                                             @Nullable final String sAttrName,
                                                             @Nonnull final Class <DSTTYPE> aDstClass)
  {
    return getAttributeValueWithConversion (new MicroQName (sNamespaceURI, sAttrName), aDstClass);
  }

  /**
   * Get the attribute value of the given attribute name. If this element has no
   * such attribute, <code>null</code> is returned. The attribute value is
   * converted via the {@link com.helger.commons.typeconvert.TypeConverter} to
   * the desired destination class. If no such attribute is present,
   * <code>null</code> is returned.
   *
   * @param <DSTTYPE>
   *        Destination type
   * @param aAttrName
   *        The attribute qualified name to retrieve the value of.
   * @param aDstClass
   *        The destination class.
   * @return The assigned attribute value or <code>null</code>.
   * @throws ClassCastException
   *         if the value cannot be converted
   */
  @Nullable
  <DSTTYPE> DSTTYPE getAttributeValueWithConversion (@Nullable IMicroQName aAttrName,
                                                     @Nonnull Class <DSTTYPE> aDstClass);

  default boolean getAttributeValueAsBool (@Nullable final String sAttrName, final boolean bDefault)
  {
    return StringParser.parseBool (getAttributeValue (sAttrName), bDefault);
  }

  default boolean getAttributeValueAsBool (@Nullable final String sNamespaceURI,
                                           @Nullable final String sAttrName,
                                           final boolean bDefault)
  {
    return StringParser.parseBool (getAttributeValue (sNamespaceURI, sAttrName), bDefault);
  }

  default boolean getAttributeValueAsBool (@Nullable final IMicroQName aAttrName, final boolean bDefault)
  {
    return StringParser.parseBool (getAttributeValue (aAttrName), bDefault);
  }

  default double getAttributeValueAsDouble (@Nullable final String sAttrName, final double dDefault)
  {
    return StringParser.parseDouble (getAttributeValue (sAttrName), dDefault);
  }

  default double getAttributeValueAsDouble (@Nullable final String sNamespaceURI,
                                            @Nullable final String sAttrName,
                                            final double dDefault)
  {
    return StringParser.parseDouble (getAttributeValue (sNamespaceURI, sAttrName), dDefault);
  }

  default double getAttributeValueAsDouble (@Nullable final IMicroQName aAttrName, final double dDefault)
  {
    return StringParser.parseDouble (getAttributeValue (aAttrName), dDefault);
  }

  default float getAttributeValueAsFloat (@Nullable final String sAttrName, final float fDefault)
  {
    return StringParser.parseFloat (getAttributeValue (sAttrName), fDefault);
  }

  default float getAttributeValueAsFloat (@Nullable final String sNamespaceURI,
                                          @Nullable final String sAttrName,
                                          final float fDefault)
  {
    return StringParser.parseFloat (getAttributeValue (sNamespaceURI, sAttrName), fDefault);
  }

  default float getAttributeValueAsFloat (@Nullable final IMicroQName aAttrName, final float fDefault)
  {
    return StringParser.parseFloat (getAttributeValue (aAttrName), fDefault);
  }

  default int getAttributeValueAsInt (@Nullable final String sAttrName, final int nDefault)
  {
    return StringParser.parseInt (getAttributeValue (sAttrName), nDefault);
  }

  default int getAttributeValueAsInt (@Nullable final String sNamespaceURI,
                                      @Nullable final String sAttrName,
                                      final int nDefault)
  {
    return StringParser.parseInt (getAttributeValue (sNamespaceURI, sAttrName), nDefault);
  }

  default int getAttributeValueAsInt (@Nullable final IMicroQName aAttrName, final int nDefault)
  {
    return StringParser.parseInt (getAttributeValue (aAttrName), nDefault);
  }

  default long getAttributeValueAsLong (@Nullable final String sAttrName, final long nDefault)
  {
    return StringParser.parseLong (getAttributeValue (sAttrName), nDefault);
  }

  default long getAttributeValueAsLong (@Nullable final String sNamespaceURI,
                                        @Nullable final String sAttrName,
                                        final long nDefault)
  {
    return StringParser.parseLong (getAttributeValue (sNamespaceURI, sAttrName), nDefault);
  }

  default long getAttributeValueAsLong (@Nullable final IMicroQName aAttrName, final long nDefault)
  {
    return StringParser.parseLong (getAttributeValue (aAttrName), nDefault);
  }

  /**
   * Get a list of all attributes. Is ensured to be not <code>null</code> if
   * {@link #hasAttributes()} returns <code>true</code>.
   *
   * @return May be <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  ICommonsList <? extends IMicroAttribute> getAllAttributeObjs ();

  /**
   * Get a map of all fully qualified attribute names and values. Is ensured to
   * be not <code>null</code> if {@link #hasAttributes()} returns
   * <code>true</code>.
   *
   * @return May be <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  ICommonsOrderedMap <IMicroQName, String> getAllQAttributes ();

  /**
   * Get a set of all attribute names. Is ensured to be not <code>null</code> if
   * {@link #hasAttributes()} returns <code>true</code>.
   *
   * @return May be <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  ICommonsOrderedSet <IMicroQName> getAllAttributeQNames ();

  /**
   * Iterate all attribute objects.
   *
   * @param aConsumer
   *        The consumer. May not be <code>null</code>. May only perform reading
   *        operations!
   */
  void forAllAttributes (@Nonnull Consumer <? super IMicroAttribute> aConsumer);

  /**
   * Iterate all attribute objects.
   *
   * @param aConsumer
   *        The consumer that takes the QName and the value. May not be
   *        <code>null</code>. May only perform reading operations!
   */
  void forAllAttributes (@Nonnull BiConsumer <? super IMicroQName, ? super String> aConsumer);

  /**
   * Iterate all attribute objects.
   *
   * @param aConsumer
   *        The consumer that takes the namespace URI, the attribute local name
   *        and the attribute value. May not be <code>null</code>. May only
   *        perform reading operations!
   */
  void forAllAttributes (@Nonnull ITriConsumer <? super String, ? super String, ? super String> aConsumer);

  /**
   * Set an attribute value of this element.
   *
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param sAttrValue
   *        If the value is <code>null</code> the attribute is removed (if
   *        present)
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final String sAttrName, @Nullable final String sAttrValue)
  {
    return setAttribute (new MicroQName (sAttrName), sAttrValue);
  }

  /**
   * Set an attribute value of this element.
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param sAttrValue
   *        If the value is <code>null</code> the attribute is removed (if
   *        present)
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nullable final String sNamespaceURI,
                                 @Nonnull final String sAttrName,
                                 @Nullable final String sAttrValue)
  {
    return setAttribute (new MicroQName (sNamespaceURI, sAttrName), sAttrValue);
  }

  /**
   * Set an attribute value of this element.
   *
   * @param aAttrName
   *        Qualified name of the attribute. May neither be <code>null</code>
   *        nor empty.
   * @param sAttrValue
   *        If the value is <code>null</code> the attribute is removed (if
   *        present)
   * @return this
   */
  @Nonnull
  IMPLTYPE setAttribute (@Nonnull IMicroQName aAttrName, @Nullable String sAttrValue);

  /**
   * Set an attribute value of this element.
   *
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param aAttrValueProvider
   *        The attribute value provider. May not be <code>null</code>. If the
   *        contained attribute value is <code>null</code> the attribute is
   *        removed (if present)
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final String sAttrName, @Nonnull final IHasAttributeValue aAttrValueProvider)
  {
    return setAttribute (new MicroQName (sAttrName), aAttrValueProvider);
  }

  /**
   * Set an attribute value of this element.
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param aAttrValueProvider
   *        The attribute value provider. May not be <code>null</code>. If the
   *        contained attribute value is <code>null</code> the attribute is
   *        removed (if present)
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nullable final String sNamespaceURI,
                                 @Nonnull final String sAttrName,
                                 @Nonnull final IHasAttributeValue aAttrValueProvider)
  {
    return setAttribute (new MicroQName (sNamespaceURI, sAttrName), aAttrValueProvider);
  }

  /**
   * Set an attribute value of this element.
   *
   * @param aAttrName
   *        Qualified name of the attribute. May neither be <code>null</code>
   *        nor empty.
   * @param aAttrValueProvider
   *        The attribute value provider. May not be <code>null</code>. If the
   *        contained attribute value is <code>null</code> the attribute is
   *        removed (if present)
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final IMicroQName aAttrName,
                                 @Nonnull final IHasAttributeValue aAttrValueProvider)
  {
    ValueEnforcer.notNull (aAttrValueProvider, "AttrValueProvider");
    return setAttribute (aAttrName, aAttrValueProvider.getAttrValue ());
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(sAttrName, Boolean.toString (nValue))</code>. That
   * means, that the serialized value of the attribute is either
   * <code>true</code> or <code>false</code>. If you need something else (like
   * "yes" or "no") don't use this method.
   *
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param bAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final String sAttrName, final boolean bAttrValue)
  {
    return setAttribute (sAttrName, Boolean.toString (bAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(sNamespaceURI, sAttrName, Boolean.toString (nValue))</code>
   * . That means, that the serialized value of the attribute is either
   * <code>true</code> or <code>false</code>. If you need something else (like
   * "yes" or "no") don't use this method.
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param bAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nullable final String sNamespaceURI,
                                 @Nonnull final String sAttrName,
                                 final boolean bAttrValue)
  {
    return setAttribute (sNamespaceURI, sAttrName, Boolean.toString (bAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(aAttrName, Boolean.toString (nValue))</code>. That
   * means, that the serialized value of the attribute is either
   * <code>true</code> or <code>false</code>. If you need something else (like
   * "yes" or "no") don't use this method.
   *
   * @param aAttrName
   *        Qualified name of the attribute. May neither be <code>null</code>
   *        nor empty.
   * @param bAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final IMicroQName aAttrName, final boolean bAttrValue)
  {
    return setAttribute (aAttrName, Boolean.toString (bAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(sAttrName, Double.toString (nValue))</code>.
   *
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param dAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final String sAttrName, final double dAttrValue)
  {
    return setAttribute (sAttrName, Double.toString (dAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(sNamespaceURI, sAttrName, Double.toString (nValue))</code>
   * .
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param dAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nullable final String sNamespaceURI,
                                 @Nonnull final String sAttrName,
                                 final double dAttrValue)
  {
    return setAttribute (sNamespaceURI, sAttrName, Double.toString (dAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(aAttrName, Double.toString (nValue))</code>.
   *
   * @param aAttrName
   *        Qualified name of the attribute. May neither be <code>null</code>
   *        nor empty.
   * @param dAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final IMicroQName aAttrName, final double dAttrValue)
  {
    return setAttribute (aAttrName, Double.toString (dAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(sAttrName, Float.toString (nValue))</code>.
   *
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param fAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final String sAttrName, final float fAttrValue)
  {
    return setAttribute (sAttrName, Float.toString (fAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(sNamespaceURI, sAttrName, Float.toString (nValue))</code>
   * .
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param fAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nullable final String sNamespaceURI,
                                 @Nonnull final String sAttrName,
                                 final float fAttrValue)
  {
    return setAttribute (sNamespaceURI, sAttrName, Float.toString (fAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(aAttrName, Float.toString (nValue))</code>.
   *
   * @param aAttrName
   *        Qualified name of the attribute. May neither be <code>null</code>
   *        nor empty.
   * @param fAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final IMicroQName aAttrName, final float fAttrValue)
  {
    return setAttribute (aAttrName, Float.toString (fAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(sAttrName, Integer.toString (nValue))</code>.
   *
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param nAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final String sAttrName, final int nAttrValue)
  {
    return setAttribute (sAttrName, Integer.toString (nAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(sNamespaceURI, sAttrName, Integer.toString (nValue))</code>
   * .
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param nAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nullable final String sNamespaceURI,
                                 @Nonnull final String sAttrName,
                                 final int nAttrValue)
  {
    return setAttribute (sNamespaceURI, sAttrName, Integer.toString (nAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(aAttrName, Integer.toString (nValue))</code>.
   *
   * @param aAttrName
   *        Qualified name of the attribute. May neither be <code>null</code>
   *        nor empty.
   * @param nAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final IMicroQName aAttrName, final int nAttrValue)
  {
    return setAttribute (aAttrName, Integer.toString (nAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(name, Long.toString (nValue))</code>.
   *
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param nAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final String sAttrName, final long nAttrValue)
  {
    return setAttribute (sAttrName, Long.toString (nAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(name, Long.toString (nValue))</code>.
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param nAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nullable final String sNamespaceURI,
                                 @Nonnull final String sAttrName,
                                 final long nAttrValue)
  {
    return setAttribute (sNamespaceURI, sAttrName, Long.toString (nAttrValue));
  }

  /**
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(name, Long.toString (nValue))</code>.
   *
   * @param aAttrName
   *        Qualified name of the attribute. May neither be <code>null</code>
   *        nor empty.
   * @param nAttrValue
   *        The new value to be set.
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttribute (@Nonnull final IMicroQName aAttrName, final long nAttrValue)
  {
    return setAttribute (aAttrName, Long.toString (nAttrValue));
  }

  /**
   * Set an attribute value of this element. If the type of the value is not
   * {@link String}, the {@link com.helger.commons.typeconvert.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param aAttrValue
   *        If the value is <code>null</code> the attribute is removed (if
   *        present)
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttributeWithConversion (@Nonnull final String sAttrName, @Nullable final Object aAttrValue)
  {
    return setAttributeWithConversion (new MicroQName (sAttrName), aAttrValue);
  }

  @Deprecated
  @Nonnull
  @DevelopersNote ("No need for setAttributeWithConversion - setAttribute is enough")
  default IMPLTYPE setAttributeWithConversion (@Nonnull final String sAttrName, @Nullable final String sAttrValue)
  {
    return setAttribute (sAttrName, sAttrValue);
  }

  /**
   * Set an attribute value of this element. If the type of the value is not
   * {@link String}, the {@link com.helger.commons.typeconvert.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        Name of the attribute. May neither be <code>null</code> nor empty.
   * @param aAttrValue
   *        If the value is <code>null</code> the attribute is removed (if
   *        present)
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttributeWithConversion (@Nullable final String sNamespaceURI,
                                               @Nonnull final String sAttrName,
                                               @Nullable final Object aAttrValue)
  {
    return setAttributeWithConversion (new MicroQName (sNamespaceURI, sAttrName), aAttrValue);
  }

  @Deprecated
  @Nonnull
  @DevelopersNote ("No need for setAttributeWithConversion - setAttribute is enough")
  default IMPLTYPE setAttributeWithConversion (@Nullable final String sNamespaceURI,
                                               @Nonnull final String sAttrName,
                                               @Nullable final String sAttrValue)
  {
    return setAttribute (sNamespaceURI, sAttrName, sAttrValue);
  }

  /**
   * Set an attribute value of this element. If the type of the value is not
   * {@link String}, the {@link com.helger.commons.typeconvert.TypeConverter} is
   * invoked to convert it to a {@link String} object.
   *
   * @param aAttrName
   *        Qualified name of the attribute. May neither be <code>null</code>
   *        nor empty.
   * @param aAttrValue
   *        If the value is <code>null</code> the attribute is removed (if
   *        present)
   * @return this
   */
  @Nonnull
  default IMPLTYPE setAttributeWithConversion (@Nonnull final IMicroQName aAttrName, @Nullable final Object aAttrValue)
  {
    final String sAttrValue = TypeConverter.convert (aAttrValue, String.class);
    return setAttribute (aAttrName, sAttrValue);
  }

  @Deprecated
  @Nonnull
  @DevelopersNote ("No need for setAttributeWithConversion - setAttribute is enough")
  default IMPLTYPE setAttributeWithConversion (@Nonnull final IMicroQName aAttrName, @Nullable final String sAttrValue)
  {
    return setAttribute (aAttrName, sAttrValue);
  }

  /**
   * Remove the attribute with the given name.
   *
   * @param sAttrName
   *        The name of the attribute to be removed. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the attribute was removed,
   *         {@link EChange#UNCHANGED} if no such attribute exists at this
   *         element.
   */
  @Nonnull
  default EChange removeAttribute (@Nullable final String sAttrName)
  {
    return removeAttribute (null, sAttrName);
  }

  /**
   * Remove the attribute with the given name.
   *
   * @param sNamespaceURI
   *        Namespace URI to use. May be <code>null</code>.
   * @param sAttrName
   *        The name of the attribute to be removed. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if the attribute was removed,
   *         {@link EChange#UNCHANGED} if no such attribute exists at this
   *         element.
   */
  @Nonnull
  default EChange removeAttribute (@Nullable final String sNamespaceURI, @Nullable final String sAttrName)
  {
    if (StringHelper.hasNoText (sAttrName))
      return EChange.UNCHANGED;
    return removeAttribute (new MicroQName (sNamespaceURI, sAttrName));
  }

  /**
   * Remove the attribute with the given name.
   *
   * @param aAttrName
   *        The qualified name of the attribute to be removed. May be
   *        <code>null</code>.
   * @return {@link EChange#CHANGED} if the attribute was removed,
   *         {@link EChange#UNCHANGED} if no such attribute exists at this
   *         element.
   */
  @Nonnull
  EChange removeAttribute (@Nullable IMicroQName aAttrName);

  /**
   * Remove all attributes from this element
   *
   * @return {@link EChange}.
   */
  @Nonnull
  EChange removeAllAttributes ();
}
