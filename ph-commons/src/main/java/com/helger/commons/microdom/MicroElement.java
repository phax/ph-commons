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

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.CommonsLinkedHashMap;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.collection.ext.ICommonsOrderedMap;
import com.helger.commons.collection.ext.ICommonsOrderedSet;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.mutable.MutableInt;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.typeconvert.TypeConverter;
import com.helger.commons.wrapper.Wrapper;
import com.helger.commons.xml.CXML;
import com.helger.commons.xml.CXMLRegEx;

/**
 * Default implementation of the {@link IMicroElement} interface.
 *
 * @author Philip Helger
 */
public final class MicroElement extends AbstractMicroNodeWithChildren implements IMicroElement
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (MicroElement.class);

  private String m_sNamespaceURI;
  private final String m_sTagName;
  private ICommonsOrderedMap <IMicroQName, MicroAttribute> m_aAttrs;

  public MicroElement (@Nonnull @Nonempty final String sTagName)
  {
    this (null, sTagName);
  }

  public MicroElement (@Nullable final String sNamespaceURI, @Nonnull @Nonempty final String sTagName)
  {
    ValueEnforcer.notEmpty (sTagName, "TagName");
    m_sNamespaceURI = sNamespaceURI;

    // Store only the local name (cut the prefix) if a namespace is present
    final int nPrefixEnd = sNamespaceURI != null ? sTagName.indexOf (CXML.XML_PREFIX_NAMESPACE_SEP) : -1;
    if (nPrefixEnd == -1)
      m_sTagName = sTagName;
    else
    {
      // Cut the prefix
      s_aLogger.warn ("Removing micro element namespace prefix '" +
                      sTagName.substring (0, nPrefixEnd) +
                      "' from tag name '" +
                      sTagName +
                      "'");
      m_sTagName = sTagName.substring (nPrefixEnd + 1);
    }

    // Only for the debug version, as this slows things down heavily
    if (GlobalDebug.isDebugMode ())
      if (!CXMLRegEx.PATTERN_NAME_QUICK.matcher (m_sTagName).matches ())
        if (!CXMLRegEx.PATTERN_NAME.matcher (m_sTagName).matches ())
          throw new IllegalArgumentException ("The micro element tag name '" +
                                              m_sTagName +
                                              "' is not a valid element name!");
  }

  @Nonnull
  public EMicroNodeType getType ()
  {
    return EMicroNodeType.ELEMENT;
  }

  @Nonnull
  @Nonempty
  public String getNodeName ()
  {
    return m_sTagName;
  }

  public boolean hasAttributes ()
  {
    return m_aAttrs != null && m_aAttrs.isNotEmpty ();
  }

  public boolean hasNoAttributes ()
  {
    return m_aAttrs == null || m_aAttrs.isEmpty ();
  }

  @Nonnegative
  public int getAttributeCount ()
  {
    return m_aAttrs == null ? 0 : m_aAttrs.size ();
  }

  @Nullable
  @ReturnsMutableCopy
  public ICommonsList <? extends IMicroAttribute> getAllAttributeObjs ()
  {
    if (hasNoAttributes ())
      return null;
    return m_aAttrs.copyOfValues ();
  }

  @Nonnull
  public Iterable <? extends IMicroAttribute> getAttributesIterable ()
  {
    if (hasNoAttributes ())
      return Collections.emptyList ();
    return m_aAttrs.values ();
  }

  @Nullable
  @ReturnsMutableCopy
  public ICommonsOrderedMap <IMicroQName, String> getAllQAttributes ()
  {
    if (hasNoAttributes ())
      return null;
    return new CommonsLinkedHashMap<> (m_aAttrs.values (),
                                       IMicroAttribute::getAttributeQName,
                                       IMicroAttribute::getAttributeValue);
  }

  @Nullable
  @ReturnsMutableCopy
  public ICommonsOrderedSet <String> getAllAttributeNames ()
  {
    if (hasNoAttributes ())
      return null;
    return CollectionHelper.newOrderedSetMapped (m_aAttrs.keySet (), IMicroQName::getName);
  }

  @Nullable
  @ReturnsMutableCopy
  public ICommonsOrderedSet <IMicroQName> getAllAttributeQNames ()
  {
    if (hasNoAttributes ())
      return null;
    return m_aAttrs.copyOfKeySet ();
  }

  @Nullable
  @ReturnsMutableCopy
  public ICommonsList <String> getAllAttributeValues ()
  {
    if (hasNoAttributes ())
      return null;
    return CollectionHelper.newListMapped (m_aAttrs.values (), IMicroAttribute::getAttributeValue);
  }

  @Nullable
  public MicroAttribute getAttributeObj (@Nullable final IMicroQName aQName)
  {
    return aQName == null || m_aAttrs == null ? null : m_aAttrs.get (aQName);
  }

  @Nullable
  private static <DSTTYPE> DSTTYPE _getConvertedToType (@Nullable final String sAttrValue,
                                                        @Nonnull final Class <DSTTYPE> aDstClass)
  {
    // Avoid having a conversion issue with empty strings!
    if (StringHelper.hasNoText (sAttrValue))
      return null;
    // throws IllegalArgumentException if nothing can be converted
    final DSTTYPE ret = TypeConverter.convertIfNecessary (sAttrValue, aDstClass);
    return ret;
  }

  @Nullable
  public <DSTTYPE> DSTTYPE getAttributeValueWithConversion (@Nullable final String sAttrName,
                                                            @Nonnull final Class <DSTTYPE> aDstClass)
  {
    final String sAttrValue = getAttributeValue (sAttrName);
    return _getConvertedToType (sAttrValue, aDstClass);
  }

  @Nullable
  public <DSTTYPE> DSTTYPE getAttributeValueWithConversion (@Nullable final String sNamespaceURI,
                                                            @Nullable final String sAttrName,
                                                            @Nonnull final Class <DSTTYPE> aDstClass)
  {
    final String sAttrValue = getAttributeValue (sNamespaceURI, sAttrName);
    return _getConvertedToType (sAttrValue, aDstClass);
  }

  @Nullable
  public <DSTTYPE> DSTTYPE getAttributeValueWithConversion (@Nullable final IMicroQName aAttrName,
                                                            @Nonnull final Class <DSTTYPE> aDstClass)
  {
    final String sAttrValue = getAttributeValue (aAttrName);
    return _getConvertedToType (sAttrValue, aDstClass);
  }

  public boolean hasAttribute (@Nullable final IMicroQName aAttrName)
  {
    return m_aAttrs != null && aAttrName != null && m_aAttrs.containsKey (aAttrName);
  }

  @Nonnull
  public EChange removeAttribute (@Nullable final IMicroQName aAttrName)
  {
    return EChange.valueOf (m_aAttrs != null && aAttrName != null && m_aAttrs.remove (aAttrName) != null);
  }

  @Nonnull
  public MicroElement setAttribute (@Nonnull final IMicroQName aAttrName, @Nullable final String sAttrValue)
  {
    ValueEnforcer.notNull (aAttrName, "AttrName");
    if (sAttrValue != null)
    {
      if (m_aAttrs == null)
        m_aAttrs = new CommonsLinkedHashMap<> ();
      m_aAttrs.put (aAttrName, new MicroAttribute (aAttrName, sAttrValue));
    }
    else
      removeAttribute (aAttrName);
    return this;
  }

  @Nonnull
  public EChange removeAllAttributes ()
  {
    if (m_aAttrs == null)
      return EChange.UNCHANGED;
    return m_aAttrs.removeAll ();
  }

  @Nullable
  public String getNamespaceURI ()
  {
    return m_sNamespaceURI;
  }

  @Nonnull
  public EChange setNamespaceURI (@Nullable final String sNamespaceURI)
  {
    if (EqualsHelper.equals (m_sNamespaceURI, sNamespaceURI))
      return EChange.UNCHANGED;
    m_sNamespaceURI = sNamespaceURI;
    return EChange.CHANGED;
  }

  @Nullable
  public String getLocalName ()
  {
    return m_sNamespaceURI == null ? null : m_sTagName;
  }

  @Nonnull
  public String getTagName ()
  {
    return m_sTagName;
  }

  private static void _forAllChildElements (@Nonnull final IMicroNode aStartNode,
                                            @Nonnull final Predicate <? super IMicroElement> aFilter,
                                            @Nonnull final Consumer <? super IMicroElement> aConsumer)
  {
    aStartNode.forAllChildren (aChildNode -> {
      if (aChildNode.isElement ())
      {
        final IMicroElement aChildElement = (IMicroElement) aChildNode;
        if (aFilter == null || aFilter.test (aChildElement))
          aConsumer.accept (aChildElement);
      }
      else
        if (aChildNode.isContainer ())
          _forAllChildElements (aChildNode, aFilter, aConsumer);
    });
  }

  private static IMicroElement _findFirstChildElement (@Nonnull final IMicroNode aStartNode,
                                                       @Nonnull final Predicate <? super IMicroElement> aFilter)
  {
    final Wrapper <IMicroElement> ret = new Wrapper<> ();
    _forAllChildElements (aStartNode, aFilter, aChildElement -> {
      if (ret.isNotSet ())
        ret.set (aChildElement);
    });
    return ret.get ();
  }

  private static boolean _hasChildElement (@Nonnull final IMicroNode aStartNode,
                                           @Nonnull final Predicate <? super IMicroElement> aFilter)
  {
    return aStartNode.containsAnyChild (aChildNode -> {
      if (aChildNode.isElement ())
      {
        final IMicroElement aChildElement = (IMicroElement) aChildNode;
        if (aFilter == null || aFilter.test (aChildElement))
          return true;
      }
      else
        if (aChildNode.isContainer ())
        {
          if (_hasChildElement (aChildNode, aFilter))
            return true;
        }
      return false;
    });
  }

  @Nonnegative
  public int getChildElementCount ()
  {
    final MutableInt ret = new MutableInt ();
    _forAllChildElements (this, null, aChildElement -> ret.inc ());
    return ret.intValue ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IMicroElement> getAllChildElements ()
  {
    final ICommonsList <IMicroElement> ret = new CommonsArrayList<> ();
    _forAllChildElements (this, null, ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IMicroElement> getAllChildElements (@Nullable final String sTagName)
  {
    final ICommonsList <IMicroElement> ret = new CommonsArrayList<> ();
    _forAllChildElements (this, aChildElement -> aChildElement.hasTagName (sTagName), ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IMicroElement> getAllChildElements (@Nullable final String sNamespaceURI,
                                                           @Nullable final String sLocalName)
  {
    if (StringHelper.hasNoText (sNamespaceURI))
      return getAllChildElements (sLocalName);

    final ICommonsList <IMicroElement> ret = new CommonsArrayList<> ();
    _forAllChildElements (this,
                          aChildElement -> aChildElement.hasNamespaceURI (sNamespaceURI) &&
                                           aChildElement.hasLocalName (sLocalName),
                          ret::add);
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public ICommonsList <IMicroElement> getAllChildElementsRecursive ()
  {
    final ICommonsList <IMicroElement> ret = new CommonsArrayList<> ();
    _forAllChildElements (this, null, aChildElement -> {
      ret.add (aChildElement);
      ret.addAll (aChildElement.getAllChildElementsRecursive ());
    });
    return ret;
  }

  public boolean hasChildElements ()
  {
    return _hasChildElement (this, null);
  }

  public boolean hasChildElements (@Nullable final String sTagName)
  {
    return _hasChildElement (this, aChildElement -> aChildElement.hasTagName (sTagName));
  }

  public boolean hasChildElements (@Nullable final String sNamespaceURI, @Nullable final String sLocalName)
  {
    if (StringHelper.hasNoText (sNamespaceURI))
      return hasChildElements (sLocalName);

    return _hasChildElement (this,
                             aChildElement -> aChildElement.hasNamespaceURI (sNamespaceURI) &&
                                              aChildElement.hasLocalName (sLocalName));
  }

  @Nullable
  public IMicroElement getFirstChildElement ()
  {
    return _findFirstChildElement (this, null);
  }

  @Nullable
  public IMicroElement getFirstChildElement (@Nullable final String sTagName)
  {
    return _findFirstChildElement (this, aChildElement -> aChildElement.hasTagName (sTagName));
  }

  @Nullable
  public IMicroElement getFirstChildElement (@Nullable final String sNamespaceURI, @Nullable final String sLocalName)
  {
    if (StringHelper.hasNoText (sNamespaceURI))
      return getFirstChildElement (sLocalName);

    return _findFirstChildElement (this,
                                   aChildElement -> aChildElement.hasNamespaceURI (sNamespaceURI) &&
                                                    aChildElement.hasLocalName (sLocalName));
  }

  @Nonnull
  public IMicroElement getClone ()
  {
    final MicroElement ret = new MicroElement (m_sNamespaceURI, m_sTagName);

    // Copy attributes
    if (m_aAttrs != null)
      ret.m_aAttrs = new CommonsLinkedHashMap<> (m_aAttrs);

    // Deep clone all child nodes
    forAllChildren (aChildNode -> ret.appendChild (aChildNode.getClone ()));
    return ret;
  }

  @Override
  public boolean isEqualContent (@Nullable final IMicroNode o)
  {
    if (o == this)
      return true;
    if (!super.isEqualContent (o))
      return false;
    final MicroElement rhs = (MicroElement) o;
    return EqualsHelper.equals (m_sNamespaceURI, rhs.m_sNamespaceURI) &&
           m_sTagName.equals (rhs.m_sTagName) &&
           EqualsHelper.equals (m_aAttrs, rhs.m_aAttrs);
  }

  @Override
  public String toString ()
  {
    return ToStringGenerator.getDerived (super.toString ())
                            .appendIfNotNull ("namespace", m_sNamespaceURI)
                            .append ("tagname", m_sTagName)
                            .appendIfNotNull ("attrs", m_aAttrs)
                            .toString ();
  }
}
