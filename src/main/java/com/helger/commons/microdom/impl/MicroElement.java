/**
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
package com.helger.commons.microdom.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.GlobalDebug;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.Nonempty;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.microdom.EMicroNodeType;
import com.helger.commons.microdom.IHasAttributeValue;
import com.helger.commons.microdom.IHasElementName;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.microdom.IMicroNode;
import com.helger.commons.state.EChange;
import com.helger.commons.string.StringHelper;
import com.helger.commons.string.ToStringGenerator;
import com.helger.commons.typeconvert.TypeConverter;
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
  private Map <String, String> m_aAttrs;

  public MicroElement (@Nonnull final IHasElementName aElementNameProvider)
  {
    this (null, aElementNameProvider.getElementName ());
  }

  public MicroElement (@Nullable final String sNamespaceURI, @Nonnull final IHasElementName aElementNameProvider)
  {
    this (sNamespaceURI, aElementNameProvider.getElementName ());
  }

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
    return m_aAttrs != null && !m_aAttrs.isEmpty ();
  }

  @Nonnegative
  public int getAttributeCount ()
  {
    return m_aAttrs == null ? 0 : m_aAttrs.size ();
  }

  @Nullable
  @ReturnsMutableCopy
  public Map <String, String> getAllAttributes ()
  {
    return hasAttributes () ? ContainerHelper.newOrderedMap (m_aAttrs) : null;
  }

  @Nullable
  @ReturnsMutableCopy
  public Set <String> getAllAttributeNames ()
  {
    return hasAttributes () ? ContainerHelper.newOrderedSet (m_aAttrs.keySet ()) : null;
  }

  @Nullable
  @ReturnsMutableCopy
  public List <String> getAllAttributeValues ()
  {
    return hasAttributes () ? ContainerHelper.newList (m_aAttrs.values ()) : null;
  }

  @Nullable
  public String getAttribute (@Nullable final String sAttrName)
  {
    return m_aAttrs == null ? null : m_aAttrs.get (sAttrName);
  }

  @Nullable
  public <DSTTYPE> DSTTYPE getAttributeWithConversion (@Nullable final String sAttrName,
                                                       @Nonnull final Class <DSTTYPE> aDstClass)
  {
    final String sAttrVal = getAttribute (sAttrName);
    // Avoid having a conversion issue with empty strings!
    if (StringHelper.hasNoText (sAttrVal))
      return null;
    // throws IllegalArgumentException if nothing can be converted
    final DSTTYPE ret = TypeConverter.convertIfNecessary (sAttrVal, aDstClass);
    return ret;
  }

  public boolean hasAttribute (@Nullable final String sAttrName)
  {
    return m_aAttrs != null && m_aAttrs.containsKey (sAttrName);
  }

  @Nonnull
  public EChange removeAttribute (@Nullable final String sAttrName)
  {
    return EChange.valueOf (m_aAttrs != null && m_aAttrs.remove (sAttrName) != null);
  }

  @Nonnull
  public MicroElement setAttribute (@Nonnull @Nonempty final String sAttrName, @Nullable final String sAttrValue)
  {
    ValueEnforcer.notEmpty (sAttrName, "AttrName");

    // Only for the dev version
    if (GlobalDebug.isDebugMode ())
    {
      if (!CXMLRegEx.PATTERN_NAME_QUICK.matcher (sAttrName).matches ())
        if (!CXMLRegEx.PATTERN_NAME.matcher (sAttrName).matches ())
          throw new IllegalArgumentException ("The passed attribute name '" +
                                              sAttrName +
                                              "' is not a valid attribute name!");
      if (false)
        if (!CXMLRegEx.PATTERN_ATTVALUE.matcher (sAttrValue).matches ())
          throw new IllegalArgumentException ("The passed attribute value '" +
                                              sAttrValue +
                                              "' is not a valid attribute value!");
      // multi line attributes are valid in XHTML 1.0 Transitional!
      if (false)
        if (sAttrValue != null && sAttrValue.indexOf ('\n') != -1)
          throw new IllegalArgumentException ("The passed attribute value '" +
                                              sAttrValue +
                                              "' contains new line characters!");
    }

    if (sAttrValue != null)
    {
      if (m_aAttrs == null)
        m_aAttrs = new LinkedHashMap <String, String> ();
      m_aAttrs.put (sAttrName, sAttrValue);
    }
    else
      removeAttribute (sAttrName);
    return this;
  }

  @Nonnull
  public MicroElement setAttribute (@Nonnull final String sAttrName,
                                    @Nonnull final IHasAttributeValue aAttrValueProvider)
  {
    ValueEnforcer.notNull (aAttrValueProvider, "AttrValueProvider");

    return setAttribute (sAttrName, aAttrValueProvider.getAttrValue ());
  }

  @Nonnull
  public IMicroElement setAttribute (@Nonnull final String sAttrName, final int nAttrValue)
  {
    return setAttribute (sAttrName, Integer.toString (nAttrValue));
  }

  @Nonnull
  public IMicroElement setAttribute (@Nonnull final String sAttrName, final long nAttrValue)
  {
    return setAttribute (sAttrName, Long.toString (nAttrValue));
  }

  @Nonnull
  public IMicroElement setAttributeWithConversion (@Nonnull final String sAttrName, @Nullable final Object aAttrValue)
  {
    final String sValue = TypeConverter.convertIfNecessary (aAttrValue, String.class);
    return setAttribute (sAttrName, sValue);
  }

  @Nonnull
  public EChange removeAllAttributes ()
  {
    if (ContainerHelper.isEmpty (m_aAttrs))
      return EChange.UNCHANGED;
    m_aAttrs.clear ();
    return EChange.CHANGED;
  }

  @Nullable
  public String getNamespaceURI ()
  {
    return m_sNamespaceURI;
  }

  @Nonnull
  public EChange setNamespaceURI (@Nullable final String sNamespaceURI)
  {
    if (EqualsUtils.equals (m_sNamespaceURI, sNamespaceURI))
      return EChange.UNCHANGED;
    m_sNamespaceURI = sNamespaceURI;
    return EChange.CHANGED;
  }

  public boolean hasNamespaceURI ()
  {
    return StringHelper.hasText (m_sNamespaceURI);
  }

  public boolean hasNoNamespaceURI ()
  {
    return StringHelper.hasNoText (m_sNamespaceURI);
  }

  public boolean hasNamespaceURI (@Nullable final String sNamespaceURI)
  {
    return EqualsUtils.equals (m_sNamespaceURI, sNamespaceURI);
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

  @Nonnegative
  public int getChildElementCount ()
  {
    int ret = 0;
    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
      {
        if (aChild.isElement ())
        {
          ++ret;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
                ++ret;
          }
      }
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IMicroElement> getAllChildElements ()
  {
    final List <IMicroElement> ret = new ArrayList <IMicroElement> ();
    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
      {
        if (aChild.isElement ())
        {
          ret.add ((IMicroElement) aChild);
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
                ret.add ((IMicroElement) aContChild);
          }
      }
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IMicroElement> getAllChildElements (@Nullable final String sTagName)
  {
    final List <IMicroElement> ret = new ArrayList <IMicroElement> ();
    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          if (aChildElement.getTagName ().equals (sTagName))
            ret.add (aChildElement);
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
              {
                final IMicroElement aContChildElement = (IMicroElement) aContChild;
                if (aContChildElement.getTagName ().equals (sTagName))
                  ret.add (aContChildElement);
              }
          }

    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IMicroElement> getAllChildElements (@Nullable final String sNamespaceURI,
                                                   @Nullable final String sLocalName)
  {
    if (StringHelper.hasNoText (sNamespaceURI))
      return getAllChildElements (sLocalName);

    final List <IMicroElement> ret = new ArrayList <IMicroElement> ();
    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          if (aChildElement.hasNamespaceURI (sNamespaceURI) && aChildElement.getLocalName ().equals (sLocalName))
            ret.add (aChildElement);
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
              {
                final IMicroElement aContChildElement = (IMicroElement) aContChild;
                if (aContChildElement.hasNamespaceURI (sNamespaceURI) &&
                    aContChildElement.getLocalName ().equals (sLocalName))
                  ret.add (aContChildElement);
              }
          }
    return ret;
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IMicroElement> getAllChildElements (@Nonnull final IHasElementName aElementNameProvider)
  {
    ValueEnforcer.notNull (aElementNameProvider, "ElementNameProvider");
    return getAllChildElements (aElementNameProvider.getElementName ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IMicroElement> getAllChildElements (@Nullable final String sNamespaceURI,
                                                   @Nonnull final IHasElementName aElementNameProvider)
  {
    ValueEnforcer.notNull (aElementNameProvider, "ElementNameProvider");
    return getAllChildElements (sNamespaceURI, aElementNameProvider.getElementName ());
  }

  @Nonnull
  @ReturnsMutableCopy
  public List <IMicroElement> getAllChildElementsRecursive ()
  {
    final List <IMicroElement> ret = new ArrayList <IMicroElement> ();
    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          ret.add (aChildElement);
          ret.addAll (aChildElement.getAllChildElementsRecursive ());
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
              {
                final MicroElement aContChildElement = (MicroElement) aContChild;
                ret.add (aContChildElement);
                ret.addAll (aContChildElement.getAllChildElementsRecursive ());
              }
          }
    return ret;
  }

  public boolean hasChildElements ()
  {
    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
        if (aChild.isElement ())
        {
          return true;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
                return true;
          }
    return false;
  }

  public boolean hasChildElements (@Nullable final String sTagName)
  {
    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
        if (aChild.isElement ())
        {
          if (((IMicroElement) aChild).getTagName ().equals (sTagName))
            return true;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
              {
                if (((IMicroElement) aContChild).getTagName ().equals (sTagName))
                  return true;
              }
          }
    return false;
  }

  public boolean hasChildElements (@Nullable final String sNamespaceURI, @Nullable final String sLocalName)
  {
    if (StringHelper.hasNoText (sNamespaceURI))
      return hasChildElements (sLocalName);

    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          if (aChildElement.hasNamespaceURI (sNamespaceURI) && aChildElement.getLocalName ().equals (sLocalName))
            return true;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
              {
                final IMicroElement aContChildElement = (IMicroElement) aContChild;
                if (aContChildElement.hasNamespaceURI (sNamespaceURI) &&
                    aContChildElement.getLocalName ().equals (sLocalName))
                  return true;
              }
          }
    return false;
  }

  public boolean hasChildElements (@Nonnull final IHasElementName aElementNameProvider)
  {
    ValueEnforcer.notNull (aElementNameProvider, "ElementNameProvider");
    return hasChildElements (aElementNameProvider.getElementName ());
  }

  public boolean hasChildElements (@Nullable final String sNamespaceURI,
                                   @Nonnull final IHasElementName aElementNameProvider)
  {
    ValueEnforcer.notNull (aElementNameProvider, "ElementNameProvider");
    return hasChildElements (sNamespaceURI, aElementNameProvider.getElementName ());
  }

  @Nullable
  public IMicroElement getFirstChildElement ()
  {
    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
        if (aChild.isElement ())
          return (IMicroElement) aChild;
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
                return (IMicroElement) aContChild;
          }
    return null;
  }

  @Nullable
  public IMicroElement getFirstChildElement (@Nullable final String sTagName)
  {
    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          if (aChildElement.getTagName ().equals (sTagName))
            return aChildElement;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
              {
                final IMicroElement aContChildElement = (IMicroElement) aContChild;
                if (aContChildElement.getTagName ().equals (sTagName))
                  return aContChildElement;
              }
          }
    return null;
  }

  @Nullable
  public IMicroElement getFirstChildElement (@Nullable final String sNamespaceURI, @Nullable final String sLocalName)
  {
    if (StringHelper.hasNoText (sNamespaceURI))
      return getFirstChildElement (sLocalName);

    if (hasChildren ())
      for (final IMicroNode aChild : directGetChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          if (aChildElement.hasNamespaceURI (sNamespaceURI) && aChildElement.getLocalName ().equals (sLocalName))
            return aChildElement;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getChildren ())
              if (aContChild.isElement ())
              {
                final IMicroElement aContChildElement = (IMicroElement) aContChild;
                if (aContChildElement.hasNamespaceURI (sNamespaceURI) &&
                    aContChildElement.getLocalName ().equals (sLocalName))
                  return aContChildElement;
              }
          }
    return null;
  }

  @Nullable
  public IMicroElement getFirstChildElement (@Nonnull final IHasElementName aElementNameProvider)
  {
    ValueEnforcer.notNull (aElementNameProvider, "ElementNameProvider");
    return getFirstChildElement (aElementNameProvider.getElementName ());
  }

  @Nullable
  public IMicroElement getFirstChildElement (@Nullable final String sNamespaceURI,
                                             @Nonnull final IHasElementName aElementNameProvider)
  {
    ValueEnforcer.notNull (aElementNameProvider, "ElementNameProvider");
    return getFirstChildElement (sNamespaceURI, aElementNameProvider.getElementName ());
  }

  @Nonnull
  public IMicroElement getClone ()
  {
    final MicroElement ret = new MicroElement (m_sNamespaceURI, m_sTagName);

    // Copy attributes
    if (m_aAttrs != null)
      ret.m_aAttrs = ContainerHelper.newOrderedMap (m_aAttrs);

    // Deep clone all child nodes
    if (hasChildren ())
      for (final IMicroNode aChildNode : directGetChildren ())
        ret.appendChild (aChildNode.getClone ());
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
    return EqualsUtils.equals (m_sNamespaceURI, rhs.m_sNamespaceURI) &&
           m_sTagName.equals (rhs.m_sTagName) &&
           EqualsUtils.equals (m_aAttrs, rhs.m_aAttrs);
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
