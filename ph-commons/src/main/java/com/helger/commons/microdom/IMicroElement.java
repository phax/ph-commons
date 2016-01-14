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
package com.helger.commons.microdom;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.state.EChange;

/**
 * Represents a single element (=tag) of a document.
 *
 * @author Philip Helger
 */
public interface IMicroElement extends IMicroNodeWithChildren
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
  boolean hasAttribute (@Nullable String sAttrName);

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
  boolean hasAttribute (@Nullable String sNamespaceURI, @Nullable String sAttrName);

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
  IMicroAttribute getAttributeObj (@Nullable String sAttrName);

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
  IMicroAttribute getAttributeObj (@Nullable String sNamespaceURI, @Nullable String sAttrName);

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
  String getAttributeValue (@Nullable String sAttrName);

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
  String getAttributeValue (@Nullable String sNamespaceURI, @Nullable String sAttrName);

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
  String getAttributeValue (@Nullable IMicroQName aAttrName);

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
  <DSTTYPE> DSTTYPE getAttributeValueWithConversion (@Nullable String sAttrName, @Nonnull Class <DSTTYPE> aDstClass);

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
  <DSTTYPE> DSTTYPE getAttributeValueWithConversion (@Nullable String sNamespaceURI,
                                                     @Nullable String sAttrName,
                                                     @Nonnull Class <DSTTYPE> aDstClass);

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

  /**
   * Get a list of all attributes. Is ensured to be not <code>null</code> if
   * {@link #hasAttributes()} returns <code>true</code>.
   *
   * @return May be <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  List <? extends IMicroAttribute> getAllAttributeObjs ();

  /**
   * Get an iterator over all attributes.
   *
   * @return Never <code>null</code>.
   */
  @Nonnull
  Iterator <? extends IMicroAttribute> getAttributeIterator ();

  /**
   * Get a map of all fully qualified attribute names and values. Is ensured to
   * be not <code>null</code> if {@link #hasAttributes()} returns
   * <code>true</code>.
   *
   * @return May be <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  Map <IMicroQName, String> getAllQAttributes ();

  /**
   * Get a set of all attribute names. Is ensured to be not <code>null</code> if
   * {@link #hasAttributes()} returns <code>true</code>.
   *
   * @return May be <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  Set <String> getAllAttributeNames ();

  /**
   * Get a set of all attribute names. Is ensured to be not <code>null</code> if
   * {@link #hasAttributes()} returns <code>true</code>.
   *
   * @return May be <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  Set <IMicroQName> getAllAttributeQNames ();

  /**
   * Get a set of all attribute values. Is ensured to be not <code>null</code>
   * if {@link #hasAttributes()} returns <code>true</code>.
   *
   * @return May be <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  Collection <String> getAllAttributeValues ();

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
  IMicroElement setAttribute (@Nonnull String sAttrName, @Nullable String sAttrValue);

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
  IMicroElement setAttribute (@Nullable String sNamespaceURI, @Nonnull String sAttrName, @Nullable String sAttrValue);

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
  IMicroElement setAttribute (@Nonnull IMicroQName aAttrName, @Nullable String sAttrValue);

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
  IMicroElement setAttribute (@Nonnull String sAttrName, @Nonnull IHasAttributeValue aAttrValueProvider);

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
  IMicroElement setAttribute (@Nullable String sNamespaceURI,
                              @Nonnull String sAttrName,
                              @Nonnull IHasAttributeValue aAttrValueProvider);

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
  IMicroElement setAttribute (@Nonnull IMicroQName aAttrName, @Nonnull IHasAttributeValue aAttrValueProvider);

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
  IMicroElement setAttribute (@Nonnull String sAttrName, boolean bAttrValue);

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
  IMicroElement setAttribute (@Nullable String sNamespaceURI, @Nonnull String sAttrName, boolean bAttrValue);

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
  IMicroElement setAttribute (@Nonnull IMicroQName aAttrName, boolean bAttrValue);

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
  IMicroElement setAttribute (@Nonnull String sAttrName, int nAttrValue);

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
  IMicroElement setAttribute (@Nullable String sNamespaceURI, @Nonnull String sAttrName, int nAttrValue);

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
  IMicroElement setAttribute (@Nonnull IMicroQName aAttrName, int nAttrValue);

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
  IMicroElement setAttribute (@Nonnull String sAttrName, long nAttrValue);

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
  IMicroElement setAttribute (@Nullable String sNamespaceURI, @Nonnull String sAttrName, long nAttrValue);

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
  IMicroElement setAttribute (@Nonnull IMicroQName aAttrName, long nAttrValue);

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
  IMicroElement setAttributeWithConversion (@Nonnull String sAttrName, @Nullable Object aAttrValue);

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
  IMicroElement setAttributeWithConversion (@Nullable String sNamespaceURI,
                                            @Nonnull String sAttrName,
                                            @Nullable Object aAttrValue);

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
  IMicroElement setAttributeWithConversion (@Nonnull IMicroQName aAttrName, @Nullable Object aAttrValue);

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
  EChange removeAttribute (@Nullable String sAttrName);

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
  EChange removeAttribute (@Nullable String sNamespaceURI, @Nullable String sAttrName);

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

  // Other API

  /**
   * Get the namespace URI of this element
   *
   * @return May be <code>null</code> if this element has no namespace URI.
   */
  @Nullable
  String getNamespaceURI ();

  /**
   * Set a new namespace URI for this element.
   *
   * @param sNamespaceURI
   *        The namespace URI to be set. May be <code>null</code> or empty to
   *        indicate that the namespace should be removed.
   * @return {@link EChange}
   */
  @Nonnull
  EChange setNamespaceURI (@Nullable String sNamespaceURI);

  /**
   * Check if this element has a specified namespace URI.
   *
   * @return <code>true</code> if this element has a specified namespace URI,
   *         <code>false</code> otherwise
   */
  boolean hasNamespaceURI ();

  /**
   * Check if this element has no namespace URI.
   *
   * @return <code>true</code> if this element has no namespace URI,
   *         <code>false</code> otherwise
   */
  boolean hasNoNamespaceURI ();

  /**
   * Check if this element has the specified namespace URI.
   *
   * @param sNamespaceURI
   *        The namespace URI to check. May not be <code>null</code>.
   * @return <code>true</code> if this element has the specified namespace URI,
   *         <code>false</code> otherwise
   */
  boolean hasNamespaceURI (@Nullable String sNamespaceURI);

  /**
   * Get the local name of the element. Is the same name as returned by
   * {@link #getTagName()} but it is only present, if a namespace URI is
   * present.
   *
   * @return May be <code>null</code> if no namespace is present.
   */
  @Nullable
  String getLocalName ();

  /**
   * Get the name of the tag. It never contains XML schema prefixes or the like.
   * Is the same as {@link #getLocalName()} if a namespace URI is present.
   *
   * @return May not be <code>null</code>.
   */
  @Nonnull
  String getTagName ();

  /**
   * @return The number of all direct child elements. Always &ge; 0.
   */
  @Nonnegative
  int getChildElementCount ();

  /**
   * Get a list of all direct child elements. Text nodes and other other child
   * nodes are not returned with this call. Micro container children are
   * inlined.
   *
   * @return Never be <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IMicroElement> getAllChildElements ();

  /**
   * Get a list of all direct child elements having the specified tag name.
   * Micro container children are inlined.
   *
   * @param sTagName
   *        The tag name to check. May be <code>null</code>.
   * @return Never be <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IMicroElement> getAllChildElements (@Nullable String sTagName);

  /**
   * Get a list of all direct child elements having the specified namespace and
   * the specified tag name. Micro container children are inlined.
   *
   * @param sNamespaceURI
   *        The namespace URI to check. May be <code>null</code>.
   * @param sLocalName
   *        The tag name to check. May be <code>null</code>.
   * @return Never be <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IMicroElement> getAllChildElements (@Nullable String sNamespaceURI, @Nullable String sLocalName);

  /**
   * Recursively get all child elements. Micro container children are inlined.
   *
   * @return A list containing all recursively contained elements. May not be
   *         <code>null</code> but empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IMicroElement> getAllChildElementsRecursive ();

  /**
   * Check if this element has at least one child element. Micro container
   * children are also checked.
   *
   * @return <code>true</code> if this element has at least one child element
   */
  boolean hasChildElements ();

  /**
   * Check if this element has at least one child element with the specified tag
   * name. Micro container children are also checked.
   *
   * @param sTagName
   *        The tag name to check. May be <code>null</code>.
   * @return <code>true</code> if this element has at least one child element
   *         with the specified tag name
   */
  boolean hasChildElements (@Nullable String sTagName);

  /**
   * Check if this element has at least one child element with the specified
   * namespace URI and tag name. Micro container children are also checked.
   *
   * @param sNamespaceURI
   *        The namespace URI to check. May be <code>null</code>.
   * @param sLocalName
   *        The tag name to check. May be <code>null</code>.
   * @return <code>true</code> if this element has at least one child element
   *         with the specified namespace URI and tag name
   */
  boolean hasChildElements (@Nullable String sNamespaceURI, @Nullable String sLocalName);

  /**
   * Get the first child element of this element. Micro container children are
   * also checked.
   *
   * @return The first child element or <code>null</code> if this element has no
   *         child element.
   */
  @Nullable
  IMicroElement getFirstChildElement ();

  /**
   * Get the first child element with the given tag name. Micro container
   * children are also checked.
   *
   * @param sTagName
   *        The tag name of the element to search. May be <code>null</code>.
   * @return <code>null</code> if no such child element is present
   */
  @Nullable
  IMicroElement getFirstChildElement (@Nullable String sTagName);

  /**
   * Get the first child element with the given tag name and the given
   * namespace. Micro container children are also checked.
   *
   * @param sNamespaceURI
   *        The namespace URL to search.
   * @param sLocalName
   *        The tag name of the element to search.
   * @return <code>null</code> if no such child element is present
   */
  @Nullable
  IMicroElement getFirstChildElement (@Nullable String sNamespaceURI, @Nullable String sLocalName);

  /**
   * {@inheritDoc}
   */
  @Nonnull
  IMicroElement getClone ();
}
