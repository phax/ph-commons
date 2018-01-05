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

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.functional.IPredicate;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.state.EChange;
import com.helger.commons.state.EContinue;
import com.helger.commons.string.StringHelper;

/**
 * Represents a single element (=tag) of a document.
 *
 * @author Philip Helger
 */
public interface IMicroElement extends IMicroNodeWithChildren, IMicroAttributeContainer <IMicroElement>
{
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
  default boolean hasNamespaceURI ()
  {
    return getNamespaceURI () != null;
  }

  /**
   * Check if this element has no namespace URI.
   *
   * @return <code>true</code> if this element has no namespace URI,
   *         <code>false</code> otherwise
   */
  default boolean hasNoNamespaceURI ()
  {
    return getNamespaceURI () == null;
  }

  /**
   * Check if this element has the specified namespace URI.
   *
   * @param sNamespaceURI
   *        The namespace URI to check. May not be <code>null</code>.
   * @return <code>true</code> if this element has the specified namespace URI,
   *         <code>false</code> otherwise
   */
  default boolean hasNamespaceURI (@Nullable final String sNamespaceURI)
  {
    return EqualsHelper.equals (getNamespaceURI (), sNamespaceURI);
  }

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
   * Check if this element has the provided local name. The local name is the
   * same name as returned by {@link #getTagName()} but is only present if a
   * namespace URI is present.
   *
   * @param sLocalName
   *        The local name to compare against. May be <code>null</code>.
   * @return <code>true</code> if local name and the passed name match.
   */
  default boolean hasLocalName (@Nullable final String sLocalName)
  {
    return EqualsHelper.equals (getLocalName (), sLocalName);
  }

  /**
   * Get the name of the tag. It never contains XML schema prefixes or the like.
   * Is the same as {@link #getLocalName()} if a namespace URI is present.
   *
   * @return May not be <code>null</code>.
   */
  @Nonnull
  String getTagName ();

  /**
   * Check if this element has the provided tag name.
   *
   * @param sTagName
   *        The tag name to compare against. May be <code>null</code>.
   * @return <code>true</code> if tag name and the passed name match.
   */
  default boolean hasTagName (@Nullable final String sTagName)
  {
    return getTagName ().equals (sTagName);
  }

  /**
   * Check if this element has the provided tag name but ignoring case.
   *
   * @param sTagName
   *        The tag name to compare against. May be <code>null</code>.
   * @return <code>true</code> if tag name and the passed name match case
   *         insensitive.
   */
  default boolean hasTagNameIgnoreCase (@Nullable final String sTagName)
  {
    return getTagName ().equalsIgnoreCase (sTagName);
  }

  /**
   * Get the number of all direct child elements.
   *
   * @return The number of all direct child elements. Always &ge; 0.
   */
  @Nonnegative
  default int getChildElementCount ()
  {
    return getChildElementCount ((Predicate <? super IMicroElement>) null);
  }

  /**
   * Get the number of direct child elements that match the provided filter.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return The number of all direct child elements that match the provided
   *         filter. Always &ge; 0.
   */
  @Nonnegative
  default int getChildElementCount (@Nullable final Predicate <? super IMicroElement> aFilter)
  {
    final MutableInt ret = new MutableInt (0);
    forAllChildElements (aFilter, aChildElement -> ret.inc ());
    return ret.intValue ();
  }

  /**
   * Get a list of all direct child elements. Text nodes and other other child
   * nodes are not returned with this call. Micro container children are
   * inlined.
   *
   * @return Never be <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <IMicroElement> getAllChildElements ()
  {
    return getAllChildElements ((Predicate <? super IMicroElement>) null);
  }

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
  default ICommonsList <IMicroElement> getAllChildElements (@Nullable final String sTagName)
  {
    return getAllChildElements (filterName (sTagName));
  }

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
  default ICommonsList <IMicroElement> getAllChildElements (@Nullable final String sNamespaceURI,
                                                            @Nullable final String sLocalName)
  {
    return getAllChildElements (filterNamespaceURIAndName (sNamespaceURI, sLocalName));
  }

  /**
   * Get a list of all direct child elements matching the provided filter. Micro
   * container children are inlined.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code> in which all
   *        direct child elements are returned.
   * @return A new list and never <code>null</code>.
   */
  @Nonnull
  @ReturnsMutableCopy
  default ICommonsList <IMicroElement> getAllChildElements (@Nullable final Predicate <? super IMicroElement> aFilter)
  {
    final ICommonsList <IMicroElement> ret = new CommonsArrayList <> ();
    forAllChildElements (aFilter, ret::add);
    return ret;
  }

  /**
   * Recursively get all child elements. Micro container children are inlined.
   *
   * @return A list containing all recursively contained elements. May not be
   *         <code>null</code> but empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  ICommonsList <IMicroElement> getAllChildElementsRecursive ();

  /**
   * Check if this element has at least one direct child element. Micro
   * container children are also checked.
   *
   * @return <code>true</code> if this element has at least one child element
   */
  default boolean hasChildElements ()
  {
    return containsAnyChildElement (null);
  }

  /**
   * Check if this element has at least one direct child element with the
   * specified tag name. Micro container children are also checked.
   *
   * @param sTagName
   *        The tag name to check. May be <code>null</code>.
   * @return <code>true</code> if this element has at least one child element
   *         with the specified tag name
   */
  default boolean hasChildElements (@Nullable final String sTagName)
  {
    return containsAnyChildElement (filterName (sTagName));
  }

  /**
   * Check if this element has at least one direct child element with the
   * specified namespace URI and tag name. Micro container children are also
   * checked.
   *
   * @param sNamespaceURI
   *        The namespace URI to check. May be <code>null</code>.
   * @param sLocalName
   *        The tag name to check. May be <code>null</code>.
   * @return <code>true</code> if this element has at least one child element
   *         with the specified namespace URI and tag name
   */
  default boolean hasChildElements (@Nullable final String sNamespaceURI, @Nullable final String sLocalName)
  {
    return containsAnyChildElement (filterNamespaceURIAndName (sNamespaceURI, sLocalName));
  }

  /**
   * Check if this element has at least one direct child element that matches
   * the provided filter. Micro container children are also checked.
   *
   * @param aFilter
   *        The filter to be applied. May be <code>null</code>.
   * @return <code>true</code> if this element has at least one direct child
   *         element that matches the passed filter, <code>false</code>
   *         otherwise.
   */
  boolean containsAnyChildElement (@Nullable Predicate <? super IMicroElement> aFilter);

  /**
   * Get the first child element of this element. Micro container children are
   * also checked.
   *
   * @return The first child element or <code>null</code> if this element has no
   *         child element.
   */
  @Nullable
  default IMicroElement getFirstChildElement ()
  {
    return getFirstChildElement ((Predicate <? super IMicroElement>) null);
  }

  /**
   * Get the first child element with the given tag name. Micro container
   * children are also checked.
   *
   * @param sTagName
   *        The tag name of the element to search. May be <code>null</code>.
   * @return <code>null</code> if no such child element is present
   */
  @Nullable
  default IMicroElement getFirstChildElement (@Nullable final String sTagName)
  {
    return getFirstChildElement (filterName (sTagName));
  }

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
  default IMicroElement getFirstChildElement (@Nullable final String sNamespaceURI, @Nullable final String sLocalName)
  {
    return getFirstChildElement (filterNamespaceURIAndName (sNamespaceURI, sLocalName));
  }

  @Nullable
  IMicroElement getFirstChildElement (@Nullable Predicate <? super IMicroElement> aFilter);

  default void forAllChildElements (@Nonnull final Consumer <? super IMicroElement> aConsumer)
  {
    forAllChildElements (null, aConsumer);
  }

  void forAllChildElements (@Nullable Predicate <? super IMicroElement> aFilter,
                            @Nonnull Consumer <? super IMicroElement> aConsumer);

  @Nonnull
  default EContinue forAllChildElementsBreakable (@Nonnull final Function <? super IMicroElement, EContinue> aConsumer)
  {
    return forAllChildElementsBreakable (null, aConsumer);
  }

  @Nonnull
  EContinue forAllChildElementsBreakable (@Nullable Predicate <? super IMicroElement> aFilter,
                                          @Nonnull Function <? super IMicroElement, EContinue> aConsumer);

  /**
   * {@inheritDoc}
   */
  @Nonnull
  IMicroElement getClone ();

  @Nonnull
  static IPredicate <? super IMicroElement> filterNamespaceURI (@Nullable final String sNamespaceURI)
  {
    return aChildElement -> aChildElement.hasNamespaceURI (sNamespaceURI);
  }

  @Nonnull
  static IPredicate <? super IMicroElement> filterName (@Nullable final String sTagOrLocalName)
  {
    return aChildElement -> aChildElement.hasTagName (sTagOrLocalName);
  }

  @Nonnull
  static IPredicate <? super IMicroElement> filterNamespaceURIAndName (@Nullable final String sNamespaceURI,
                                                                       @Nullable final String sTagOrLocalName)
  {
    if (StringHelper.hasNoText (sNamespaceURI))
      return filterName (sTagOrLocalName);

    return aChildElement -> aChildElement.hasNamespaceURI (sNamespaceURI) &&
                            aChildElement.hasLocalName (sTagOrLocalName);
  }
}
