/*
 * Copyright (C) 2014-2025 Philip Helger (www.helger.com)
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
package com.helger.xml.xpath;

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

import com.helger.annotation.Nonnegative;
import com.helger.annotation.concurrent.NotThreadSafe;
import com.helger.annotation.style.ReturnsMutableCopy;
import com.helger.base.clone.ICloneable;
import com.helger.base.equals.EqualsHelper;
import com.helger.base.equals.ValueEnforcer;
import com.helger.base.hashcode.HashCodeGenerator;
import com.helger.base.state.EChange;
import com.helger.base.tostring.ToStringGenerator;
import com.helger.collection.commons.CommonsHashMap;
import com.helger.collection.commons.ICommonsMap;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Class is used in conjunction with {@link javax.xml.xpath.XPathExpression} to
 * resolve variable values used in XPath queries at runtime. The local part of
 * the QName to resolve is used as the key in the map. The namespace is ignored
 * in this class.
 *
 * @author Philip Helger
 * @see MapBasedXPathVariableResolverQName
 */
@NotThreadSafe
public class MapBasedXPathVariableResolver implements XPathVariableResolver, ICloneable <MapBasedXPathVariableResolver>
{
  private final ICommonsMap <String, Object> m_aMap;

  /**
   * Default ctor.
   */
  public MapBasedXPathVariableResolver ()
  {
    this ((Map <String, ?>) null);
  }

  /**
   * Ctor taking another map.
   *
   * @param aVars
   *        Variables to re-use. May be <code>null</code>.
   */
  public MapBasedXPathVariableResolver (@Nullable final Map <String, ?> aVars)
  {
    m_aMap = new CommonsHashMap <> (aVars);
  }

  /**
   * Copy constructor
   *
   * @param aOther
   *        Object to copy data from
   */
  public MapBasedXPathVariableResolver (@Nonnull final MapBasedXPathVariableResolver aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    m_aMap = aOther.m_aMap.getClone ();
  }

  /**
   * Add a new variable.
   *
   * @param sName
   *        The name (=local part) of the variable
   * @param aValue
   *        The value to be used.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange addUniqueVariable (@Nonnull final String sName, @Nonnull final Object aValue)
  {
    ValueEnforcer.notNull (sName, "Name");
    ValueEnforcer.notNull (aValue, "Value");

    if (m_aMap.containsKey (sName))
      return EChange.UNCHANGED;
    m_aMap.put (sName, aValue);
    return EChange.CHANGED;
  }

  /**
   * Add all variables from the other variable resolver into this resolver.
   *
   * @param aOther
   *        The variable resolver to import the variable from. May not be
   *        <code>null</code>.
   * @param bOverwrite
   *        if <code>true</code> existing variables will be overwritten with the
   *        new variables, otherwise the old variables are kept.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange addAllFrom (@Nonnull final MapBasedXPathVariableResolver aOther, final boolean bOverwrite)
  {
    ValueEnforcer.notNull (aOther, "Other");
    EChange eChange = EChange.UNCHANGED;
    for (final Map.Entry <String, Object> aEntry : aOther.m_aMap.entrySet ())
      if (bOverwrite || !m_aMap.containsKey (aEntry.getKey ()))
      {
        m_aMap.put (aEntry.getKey (), aEntry.getValue ());
        eChange = EChange.CHANGED;
      }
    return eChange;
  }

  /**
   * Add all variables from the other variable resolver into this resolver. This
   * methods takes only the local part of the QName and loses the namespace URI.
   *
   * @param aOther
   *        The variable resolver to import the variable from. May not be
   *        <code>null</code>.
   * @param bOverwrite
   *        if <code>true</code> existing variables will be overwritten with the
   *        new variables, otherwise the old variables are kept.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange addAllFrom (@Nonnull final MapBasedXPathVariableResolverQName aOther, final boolean bOverwrite)
  {
    ValueEnforcer.notNull (aOther, "Other");
    EChange eChange = EChange.UNCHANGED;
    for (final Map.Entry <QName, ?> aEntry : aOther.getAllVariables ().entrySet ())
    {
      // QName to local part only
      final String sKey = aEntry.getKey ().getLocalPart ();
      if (bOverwrite || !m_aMap.containsKey (sKey))
      {
        m_aMap.put (sKey, aEntry.getValue ());
        eChange = EChange.CHANGED;
      }
    }
    return eChange;
  }

  /**
   * Remove the variable with the specified name.
   *
   * @param sName
   *        The name to be removed. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeVariable (@Nullable final String sName)
  {
    if (sName == null)
      return EChange.UNCHANGED;
    return m_aMap.removeObject (sName);
  }

  /**
   * Remove multiple variables at once.
   *
   * @param aNames
   *        The names to be removed. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one variable was removed.
   */
  @Nonnull
  public EChange removeVariables (@Nullable final Iterable <String> aNames)
  {
    EChange eChange = EChange.UNCHANGED;
    if (aNames != null)
      for (final String sName : aNames)
        eChange = eChange.or (removeVariable (sName));
    return eChange;
  }

  /**
   * @return A mutable copy of all contained variables. Never <code>null</code>
   *         but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public ICommonsMap <String, ?> getAllVariables ()
  {
    return m_aMap.getClone ();
  }

  /**
   * @return The number of contained variables. Always &ge; 0.
   */
  @Nonnegative
  public int getVariableCount ()
  {
    return m_aMap.size ();
  }

  /**
   * Remove all variables at once.
   *
   * @return {@link EChange#CHANGED} if at least one variable was removed.
   */
  @Nonnull
  public EChange clear ()
  {
    return m_aMap.removeAll ();
  }

  /**
   * Set multiple variables at once.
   *
   * @param aVars
   *        The variables to be set. May be <code>null</code>.
   */
  public void setAllVariables (@Nullable final Map <String, ?> aVars)
  {
    m_aMap.setAll (aVars);
  }

  @Nullable
  public Object resolveVariable (@Nonnull final QName aVariableName)
  {
    ValueEnforcer.notNull (aVariableName, "VariableName");

    final String sLocalName = aVariableName.getLocalPart ();
    return m_aMap.get (sLocalName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public MapBasedXPathVariableResolver getClone ()
  {
    return new MapBasedXPathVariableResolver (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MapBasedXPathVariableResolver rhs = (MapBasedXPathVariableResolver) o;
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
    return new ToStringGenerator (this).append ("map", m_aMap).getToString ();
  }
}
