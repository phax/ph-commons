/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotations.ReturnsMutableCopy;
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
   * @return The number of assigned attributes. Always &ge; 0.
   */
  @Nonnegative
  int getAttributeCount ();

  /**
   * Check if this element has an attribute with the specified name.
   * 
   * @param sAttrName
   *        The attribute name to retrieve the value of.
   * @return <code>true</code> if such an attribute is present,
   *         <code>false</code> otherwise
   */
  boolean hasAttribute (@Nullable String sAttrName);

  /**
   * Get the attribute value of the given attribute name. If this element has no
   * such attribute, <code>null</code> is returned.
   * 
   * @param sAttrName
   *        The attribute name to retrieve the value of.
   * @return The assigned attribute value or <code>null</code>.
   */
  @Nullable
  String getAttribute (@Nullable String sAttrName);

  /**
   * Get the attribute value of the given attribute name. If this element has no
   * such attribute, <code>null</code> is returned. The attribute value is
   * converted via the {@link com.helger.commons.typeconvert.TypeConverter} to
   * the desired destination class. If no such attribute is present,
   * <code>null</code> is returned.
   * 
   * @param sAttrName
   *        The attribute name to retrieve the value of.
   * @param aDstClass
   *        The destination class.
   * @return The assigned attribute value or <code>null</code>.
   * @throws ClassCastException
   *         if the value cannot be converted
   */
  @Nullable
  <DSTTYPE> DSTTYPE getAttributeWithConversion (@Nullable String sAttrName, @Nonnull Class <DSTTYPE> aDstClass);

  /**
   * Get a map of all attribute names and values. Is ensured to be not
   * <code>null</code> if {@link #hasAttributes()} returns <code>true</code>.
   * 
   * @return May be <code>null</code>.
   */
  @Nullable
  @ReturnsMutableCopy
  Map <String, String> getAllAttributes ();

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
   * Set an attribute value of this element. This is a shortcut for
   * <code>setAttribute(name, Integer.toString (nValue))</code>.
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
   * Get a list of all direct child elements having the specified tag name.
   * Micro container children are inlined.
   * 
   * @param aElementNameProvider
   *        Element name provider. May not be <code>null</code>.
   * @return Never be <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IMicroElement> getAllChildElements (@Nonnull IHasElementName aElementNameProvider);

  /**
   * Get a list of all direct child elements having the specified namespace and
   * the specified tag name. Micro container children are inlined.
   * 
   * @param sNamespaceURI
   *        The namespace URI to check. May be <code>null</code>.
   * @param aElementNameProvider
   *        Element name provider. May not be <code>null</code>.
   * @return Never be <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  List <IMicroElement> getAllChildElements (@Nullable String sNamespaceURI,
                                            @Nonnull IHasElementName aElementNameProvider);

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
   * Check if this element has at least one child element with the specified tag
   * name. Micro container children are also checked.
   * 
   * @param aElementNameProvider
   *        Element name provider. May not be <code>null</code>.
   * @return <code>true</code> if this element has at least one child element
   *         with the specified tag name
   */
  boolean hasChildElements (@Nonnull IHasElementName aElementNameProvider);

  /**
   * Check if this element has at least one child element with the specified
   * namespace URI and tag name. Micro container children are also checked.
   * 
   * @param sNamespaceURI
   *        The namespace URI to check. May be <code>null</code>.
   * @param aElementNameProvider
   *        Element name provider. May not be <code>null</code>.
   * @return <code>true</code> if this element has at least one child element
   *         with the specified namespace URI and tag name
   */
  boolean hasChildElements (@Nullable String sNamespaceURI, @Nonnull IHasElementName aElementNameProvider);

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
   * Get the first child element with the given tag name. Micro container
   * children are also checked.
   * 
   * @param aElementNameProvider
   *        Element name provider. May not be <code>null</code>.
   * @return <code>null</code> if no such child element is present
   */
  @Nullable
  IMicroElement getFirstChildElement (@Nonnull IHasElementName aElementNameProvider);

  /**
   * Get the first child element with the given tag name and the given
   * namespace. Micro container children are also checked.
   * 
   * @param sNamespaceURI
   *        The namespace URL to search.
   * @param aElementNameProvider
   *        Element name provider. May not be <code>null</code>.
   * @return <code>null</code> if no such child element is present
   */
  @Nullable
  IMicroElement getFirstChildElement (@Nullable String sNamespaceURI, @Nonnull IHasElementName aElementNameProvider);

  /**
   * {@inheritDoc}
   */
  @Nonnull
  IMicroElement getClone ();
}
