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
package com.helger.commons.xml.xpath;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathFunction;
import javax.xml.xpath.XPathFunctionResolver;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.collection.CollectionHelper;
import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.lang.ICloneable;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Class is used in conjunction with {@link javax.xml.xpath.XPathExpression} to
 * resolve variable values used in XPath queries at runtime. The whole QName is
 * used as the key in the map. The namespace is not-ignored in this class.
 *
 * @author Philip Helger
 */
@NotThreadSafe
public class MapBasedXPathFunctionResolver implements XPathFunctionResolver, ICloneable <MapBasedXPathFunctionResolver>
{
  private final Map <XPathFunctionKey, XPathFunction> m_aMap;

  /**
   * Default ctor.
   */
  public MapBasedXPathFunctionResolver ()
  {
    m_aMap = new HashMap <XPathFunctionKey, XPathFunction> ();
  }

  /**
   * Copy constructor
   *
   * @param aOther
   *        Object to copy data from
   */
  public MapBasedXPathFunctionResolver (@Nonnull final MapBasedXPathFunctionResolver aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    m_aMap = CollectionHelper.newMap (aOther.m_aMap);
  }

  /**
   * Add a new function.
   *
   * @param sNamespaceURI
   *        The namespace URI of the function
   * @param sLocalPart
   *        The local part of the function
   * @param nArity
   *        The number of parameters of the function
   * @param aFunction
   *        The function to be used. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange addUniqueFunction (@Nonnull final String sNamespaceURI,
                                    @Nonnull final String sLocalPart,
                                    @Nonnegative final int nArity,
                                    @Nonnull final XPathFunction aFunction)
  {
    return addUniqueFunction (new QName (sNamespaceURI, sLocalPart), nArity, aFunction);
  }

  /**
   * Add a new function.
   *
   * @param aName
   *        The qualified name of the function
   * @param nArity
   *        The number of parameters of the function
   * @param aFunction
   *        The function to be used. May not be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange addUniqueFunction (@Nonnull final QName aName,
                                    @Nonnegative final int nArity,
                                    @Nonnull final XPathFunction aFunction)
  {
    ValueEnforcer.notNull (aFunction, "Function");

    final XPathFunctionKey aFunctionKey = new XPathFunctionKey (aName, nArity);
    if (m_aMap.containsKey (aFunctionKey))
      return EChange.UNCHANGED;
    m_aMap.put (aFunctionKey, aFunction);
    return EChange.CHANGED;
  }

  /**
   * Add all functions from the other function resolver into this resolver.
   *
   * @param aOther
   *        The function resolver to import the functions from. May not be
   *        <code>null</code>.
   * @param bOverwrite
   *        if <code>true</code> existing functions will be overwritten with the
   *        new functions, otherwise the old functions are kept.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange addAllFrom (@Nonnull final MapBasedXPathFunctionResolver aOther, final boolean bOverwrite)
  {
    ValueEnforcer.notNull (aOther, "Other");
    EChange eChange = EChange.UNCHANGED;
    for (final Map.Entry <XPathFunctionKey, XPathFunction> aEntry : aOther.m_aMap.entrySet ())
      if (bOverwrite || !m_aMap.containsKey (aEntry.getKey ()))
      {
        m_aMap.put (aEntry.getKey (), aEntry.getValue ());
        eChange = EChange.CHANGED;
      }
    return eChange;
  }

  /**
   * Remove the function with the specified name.
   *
   * @param aName
   *        The name to be removed. May not be <code>null</code>.
   * @param nArity
   *        The number of parameters of the function. Must be &ge; 0.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeFunction (@Nonnull final QName aName, @Nonnegative final int nArity)
  {
    final XPathFunctionKey aKey = new XPathFunctionKey (aName, nArity);
    return removeFunction (aKey);
  }

  /**
   * Remove the function with the specified name.
   *
   * @param aKey
   *        The function key to be removed. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeFunction (@Nullable final XPathFunctionKey aKey)
  {
    return EChange.valueOf (m_aMap.remove (aKey) != null);
  }

  /**
   * Remove all functions with the same name. This can be helpful when the same
   * function is registered for multiple parameters.
   *
   * @param aName
   *        The name to be removed. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one function was removed.
   */
  @Nonnull
  public EChange removeFunctionsWithName (@Nullable final QName aName)
  {
    EChange eChange = EChange.UNCHANGED;
    if (aName != null)
    {
      // Make a copy of the key set to allow for inline modification
      for (final XPathFunctionKey aKey : CollectionHelper.newList (m_aMap.keySet ()))
        if (aKey.getFunctionName ().equals (aName))
          eChange = eChange.or (removeFunction (aKey));
    }
    return eChange;
  }

  /**
   * @return A mutable copy of all contained functions. Never <code>null</code>
   *         but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Map <XPathFunctionKey, XPathFunction> getAllFunctions ()
  {
    return CollectionHelper.newMap (m_aMap);
  }

  /**
   * @return The number of contained functions. Always &ge; 0.
   */
  @Nonnegative
  public int getFunctionCount ()
  {
    return m_aMap.size ();
  }

  /**
   * Remove all functions at once.
   *
   * @return {@link EChange#CHANGED} if at least one function was removed.
   */
  @Nonnull
  public EChange clear ()
  {
    if (m_aMap.isEmpty ())
      return EChange.UNCHANGED;
    m_aMap.clear ();
    return EChange.CHANGED;
  }

  @Nullable
  public XPathFunction resolveFunction (@Nonnull final QName aFunctionName, @Nonnegative final int nArity)
  {
    return resolveFunction (new XPathFunctionKey (aFunctionName, nArity));
  }

  @Nullable
  public XPathFunction resolveFunction (@Nullable final XPathFunctionKey aFunctionKey)
  {
    return m_aMap.get (aFunctionKey);
  }

  @Nonnull
  @ReturnsMutableCopy
  public MapBasedXPathFunctionResolver getClone ()
  {
    return new MapBasedXPathFunctionResolver (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MapBasedXPathFunctionResolver rhs = (MapBasedXPathFunctionResolver) o;
    return EqualsHelper.equals (m_aMap, rhs.m_aMap);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aMap).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("map", m_aMap).toString ();
  }
}
