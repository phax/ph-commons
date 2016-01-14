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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.collection.iterate.EmptyIterator;
import com.helger.commons.debug.GlobalDebug;
import com.helger.commons.equals.EqualsHelper;
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
  private Map <IMicroQName, MicroAttribute> m_aAttrs;

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
  public List <? extends IMicroAttribute> getAllAttributeObjs ()
  {
    if (hasNoAttributes ())
      return null;
    return CollectionHelper.newList (m_aAttrs.values ());
  }

  @Nonnull
  public Iterator <? extends IMicroAttribute> getAttributeIterator ()
  {
    if (hasNoAttributes ())
      return new EmptyIterator <IMicroAttribute> ();
    return m_aAttrs.values ().iterator ();
  }

  @Nullable
  @ReturnsMutableCopy
  public Map <IMicroQName, String> getAllQAttributes ()
  {
    if (hasNoAttributes ())
      return null;
    final Map <IMicroQName, String> ret = new LinkedHashMap <IMicroQName, String> ();
    for (final MicroAttribute aAttr : m_aAttrs.values ())
      ret.put (aAttr.getAttributeQName (), aAttr.getAttributeValue ());
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  public Set <String> getAllAttributeNames ()
  {
    if (hasNoAttributes ())
      return null;
    final Set <String> ret = new LinkedHashSet <String> ();
    for (final IMicroQName aName : m_aAttrs.keySet ())
      ret.add (aName.getName ());
    return ret;
  }

  @Nullable
  @ReturnsMutableCopy
  public Set <IMicroQName> getAllAttributeQNames ()
  {
    if (hasNoAttributes ())
      return null;
    return CollectionHelper.newOrderedSet (m_aAttrs.keySet ());
  }

  @Nullable
  @ReturnsMutableCopy
  public List <String> getAllAttributeValues ()
  {
    if (hasNoAttributes ())
      return null;
    final List <String> ret = new ArrayList <String> ();
    for (final MicroAttribute aName : m_aAttrs.values ())
      ret.add (aName.getAttributeValue ());
    return ret;
  }

  @Nullable
  public MicroAttribute getAttributeObj (@Nullable final String sAttrName)
  {
    return getAttributeObj (null, sAttrName);
  }

  @Nullable
  public MicroAttribute getAttributeObj (@Nullable final String sNamespaceURI, @Nullable final String sAttrName)
  {
    if (StringHelper.hasNoText (sAttrName))
      return null;
    return getAttributeObj (new MicroQName (sNamespaceURI, sAttrName));
  }

  @Nullable
  public MicroAttribute getAttributeObj (@Nullable final IMicroQName aQName)
  {
    return aQName == null || m_aAttrs == null ? null : m_aAttrs.get (aQName);
  }

  @Nullable
  public String getAttributeValue (@Nullable final String sAttrName)
  {
    final MicroAttribute aAttr = getAttributeObj (sAttrName);
    return aAttr == null ? null : aAttr.getAttributeValue ();
  }

  @Nullable
  public String getAttributeValue (@Nullable final String sNamespaceURI, @Nullable final String sAttrName)
  {
    final MicroAttribute aAttr = getAttributeObj (sNamespaceURI, sAttrName);
    return aAttr == null ? null : aAttr.getAttributeValue ();
  }

  @Nullable
  public String getAttributeValue (@Nullable final IMicroQName aAttrName)
  {
    final MicroAttribute aAttr = getAttributeObj (aAttrName);
    return aAttr == null ? null : aAttr.getAttributeValue ();
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

  public boolean hasAttribute (@Nullable final String sAttrName)
  {
    return hasAttribute (null, sAttrName);
  }

  public boolean hasAttribute (@Nullable final String sNamespaceURI, @Nullable final String sAttrName)
  {
    if (StringHelper.hasNoText (sAttrName))
      return false;
    return hasAttribute (new MicroQName (sNamespaceURI, sAttrName));
  }

  public boolean hasAttribute (@Nullable final IMicroQName aAttrName)
  {
    return m_aAttrs != null && aAttrName != null && m_aAttrs.containsKey (aAttrName);
  }

  @Nonnull
  public EChange removeAttribute (@Nullable final String sAttrName)
  {
    return removeAttribute (null, sAttrName);
  }

  @Nonnull
  public EChange removeAttribute (@Nullable final String sNamespaceURI, @Nullable final String sAttrName)
  {
    if (StringHelper.hasNoText (sAttrName))
      return EChange.UNCHANGED;
    return removeAttribute (new MicroQName (sNamespaceURI, sAttrName));
  }

  @Nonnull
  public EChange removeAttribute (@Nullable final IMicroQName aAttrName)
  {
    return EChange.valueOf (m_aAttrs != null && aAttrName != null && m_aAttrs.remove (aAttrName) != null);
  }

  @Nonnull
  public MicroElement setAttribute (@Nonnull @Nonempty final String sAttrName, @Nullable final String sAttrValue)
  {
    return setAttribute (new MicroQName (sAttrName), sAttrValue);
  }

  @Nonnull
  public MicroElement setAttribute (@Nullable final String sNamespaceURI,
                                    @Nonnull @Nonempty final String sAttrName,
                                    @Nullable final String sAttrValue)
  {
    return setAttribute (new MicroQName (sNamespaceURI, sAttrName), sAttrValue);
  }

  @Nonnull
  public MicroElement setAttribute (@Nonnull final IMicroQName aAttrName, @Nullable final String sAttrValue)
  {
    ValueEnforcer.notNull (aAttrName, "AttrName");
    if (sAttrValue != null)
    {
      if (m_aAttrs == null)
        m_aAttrs = new LinkedHashMap <IMicroQName, MicroAttribute> ();
      m_aAttrs.put (aAttrName, new MicroAttribute (aAttrName, sAttrValue));
    }
    else
      removeAttribute (aAttrName);
    return this;
  }

  @Nonnull
  public MicroElement setAttribute (@Nonnull final String sAttrName,
                                    @Nonnull final IHasAttributeValue aAttrValueProvider)
  {
    return setAttribute (new MicroQName (sAttrName), aAttrValueProvider);
  }

  @Nonnull
  public MicroElement setAttribute (@Nullable final String sNamespaceURI,
                                    @Nonnull final String sAttrName,
                                    @Nonnull final IHasAttributeValue aAttrValueProvider)
  {
    return setAttribute (new MicroQName (sNamespaceURI, sAttrName), aAttrValueProvider);
  }

  @Nonnull
  public MicroElement setAttribute (@Nonnull final IMicroQName aAttrName,
                                    @Nonnull final IHasAttributeValue aAttrValueProvider)
  {
    ValueEnforcer.notNull (aAttrValueProvider, "AttrValueProvider");

    return setAttribute (aAttrName, aAttrValueProvider.getAttrValue ());
  }

  @Nonnull
  public IMicroElement setAttribute (@Nonnull final String sAttrName, final boolean bAttrValue)
  {
    return setAttribute (sAttrName, Boolean.toString (bAttrValue));
  }

  @Nonnull
  public IMicroElement setAttribute (@Nullable final String sNamespaceURI,
                                     @Nonnull final String sAttrName,
                                     final boolean bAttrValue)
  {
    return setAttribute (sNamespaceURI, sAttrName, Boolean.toString (bAttrValue));
  }

  @Nonnull
  public IMicroElement setAttribute (@Nonnull final IMicroQName aAttrName, final boolean bAttrValue)
  {
    return setAttribute (aAttrName, Boolean.toString (bAttrValue));
  }

  @Nonnull
  public IMicroElement setAttribute (@Nonnull final String sAttrName, final int nAttrValue)
  {
    return setAttribute (sAttrName, Integer.toString (nAttrValue));
  }

  @Nonnull
  public IMicroElement setAttribute (@Nullable final String sNamespaceURI,
                                     @Nonnull final String sAttrName,
                                     final int nAttrValue)
  {
    return setAttribute (sNamespaceURI, sAttrName, Integer.toString (nAttrValue));
  }

  @Nonnull
  public IMicroElement setAttribute (@Nonnull final IMicroQName aAttrName, final int nAttrValue)
  {
    return setAttribute (aAttrName, Integer.toString (nAttrValue));
  }

  @Nonnull
  public IMicroElement setAttribute (@Nonnull final String sAttrName, final long nAttrValue)
  {
    return setAttribute (sAttrName, Long.toString (nAttrValue));
  }

  @Nonnull
  public IMicroElement setAttribute (@Nullable final String sNamespaceURI,
                                     @Nonnull final String sAttrName,
                                     final long nAttrValue)
  {
    return setAttribute (sNamespaceURI, sAttrName, Long.toString (nAttrValue));
  }

  @Nonnull
  public IMicroElement setAttribute (@Nonnull final IMicroQName aAttrName, final long nAttrValue)
  {
    return setAttribute (aAttrName, Long.toString (nAttrValue));
  }

  @Nonnull
  public IMicroElement setAttributeWithConversion (@Nonnull final String sAttrName, @Nullable final Object aAttrValue)
  {
    return setAttributeWithConversion (new MicroQName (sAttrName), aAttrValue);
  }

  @Nonnull
  public IMicroElement setAttributeWithConversion (@Nullable final String sNamespaceURI,
                                                   @Nonnull final String sAttrName,
                                                   @Nullable final Object aAttrValue)
  {
    return setAttributeWithConversion (new MicroQName (sNamespaceURI, sAttrName), aAttrValue);
  }

  @Nonnull
  public IMicroElement setAttributeWithConversion (@Nonnull final IMicroQName aAttrName,
                                                   @Nullable final Object aAttrValue)
  {
    final String sAttrValue = TypeConverter.convertIfNecessary (aAttrValue, String.class);
    return setAttribute (aAttrName, sAttrValue);
  }

  @Nonnull
  public EChange removeAllAttributes ()
  {
    if (CollectionHelper.isEmpty (m_aAttrs))
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
    if (EqualsHelper.equals (m_sNamespaceURI, sNamespaceURI))
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
    return EqualsHelper.equals (m_sNamespaceURI, sNamespaceURI);
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
      for (final IMicroNode aChild : directGetAllChildren ())
      {
        if (aChild.isElement ())
        {
          ++ret;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
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
      for (final IMicroNode aChild : directGetAllChildren ())
      {
        if (aChild.isElement ())
        {
          ret.add ((IMicroElement) aChild);
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
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
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          if (aChildElement.getTagName ().equals (sTagName))
            ret.add (aChildElement);
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
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
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          if (aChildElement.hasNamespaceURI (sNamespaceURI) && aChildElement.getLocalName ().equals (sLocalName))
            ret.add (aChildElement);
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
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
  public List <IMicroElement> getAllChildElementsRecursive ()
  {
    final List <IMicroElement> ret = new ArrayList <IMicroElement> ();
    if (hasChildren ())
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          ret.add (aChildElement);
          ret.addAll (aChildElement.getAllChildElementsRecursive ());
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
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
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isElement ())
        {
          return true;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
              if (aContChild.isElement ())
                return true;
          }
    return false;
  }

  public boolean hasChildElements (@Nullable final String sTagName)
  {
    if (hasChildren ())
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isElement ())
        {
          if (((IMicroElement) aChild).getTagName ().equals (sTagName))
            return true;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
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
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          if (aChildElement.hasNamespaceURI (sNamespaceURI) && aChildElement.getLocalName ().equals (sLocalName))
            return true;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
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

  @Nullable
  public IMicroElement getFirstChildElement ()
  {
    if (hasChildren ())
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isElement ())
          return (IMicroElement) aChild;
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
              if (aContChild.isElement ())
                return (IMicroElement) aContChild;
          }
    return null;
  }

  @Nullable
  public IMicroElement getFirstChildElement (@Nullable final String sTagName)
  {
    if (hasChildren ())
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          if (aChildElement.getTagName ().equals (sTagName))
            return aChildElement;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
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
      for (final IMicroNode aChild : directGetAllChildren ())
        if (aChild.isElement ())
        {
          final IMicroElement aChildElement = (IMicroElement) aChild;
          if (aChildElement.hasNamespaceURI (sNamespaceURI) && aChildElement.getLocalName ().equals (sLocalName))
            return aChildElement;
        }
        else
          if (aChild.isContainer () && aChild.hasChildren ())
          {
            for (final IMicroNode aContChild : aChild.getAllChildren ())
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

  @Nonnull
  public IMicroElement getClone ()
  {
    final MicroElement ret = new MicroElement (m_sNamespaceURI, m_sTagName);

    // Copy attributes
    if (m_aAttrs != null)
      ret.m_aAttrs = CollectionHelper.newOrderedMap (m_aAttrs);

    // Deep clone all child nodes
    if (hasChildren ())
      for (final IMicroNode aChildNode : directGetAllChildren ())
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
