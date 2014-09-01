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
package com.helger.commons.xml.xpath;

import java.util.Map;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.xml.namespace.QName;
import javax.xml.xpath.XPathVariableResolver;

import com.helger.commons.ICloneable;
import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotations.ReturnsMutableCopy;
import com.helger.commons.collections.ContainerHelper;
import com.helger.commons.equals.EqualsUtils;
import com.helger.commons.hash.HashCodeGenerator;
import com.helger.commons.state.EChange;
import com.helger.commons.string.ToStringGenerator;

/**
 * Class is used in conjunction with {@link javax.xml.xpath.XPathExpression} to
 * resolve variable values used in XPath queries at runtime. The whole QName is
 * used as the key in the map. The namespace is not-ignored in this class.
 *
 * @author Philip Helger
 * @see MapBasedXPathVariableResolver
 */
@NotThreadSafe
public class MapBasedXPathVariableResolverQName implements XPathVariableResolver, ICloneable <MapBasedXPathVariableResolverQName>
{
  private final Map <QName, Object> m_aVars;

  /**
   * Default ctor.
   */
  public MapBasedXPathVariableResolverQName ()
  {
    this ((Map <QName, ?>) null);
  }

  /**
   * Ctor taking another map.
   *
   * @param aVars
   *        Variables to re-use. May be <code>null</code>.
   */
  public MapBasedXPathVariableResolverQName (@Nullable final Map <QName, ?> aVars)
  {
    m_aVars = ContainerHelper.newMap (aVars);
  }

  /**
   * Copy constructor
   *
   * @param aOther
   *        Object to copy data from
   */
  public MapBasedXPathVariableResolverQName (@Nonnull final MapBasedXPathVariableResolverQName aOther)
  {
    ValueEnforcer.notNull (aOther, "Other");
    m_aVars = ContainerHelper.newMap (aOther.m_aVars);
  }

  /**
   * Add a new variable.
   *
   * @param aName
   *        The qualified name of the variable
   * @param aValue
   *        The value to be used.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange addUniqueVariable (@Nonnull final QName aName, @Nonnull final Object aValue)
  {
    ValueEnforcer.notNull (aName, "Name");
    ValueEnforcer.notNull (aValue, "Value");

    if (m_aVars.containsKey (aName))
      return EChange.UNCHANGED;
    m_aVars.put (aName, aValue);
    return EChange.CHANGED;
  }

  /**
   * Remove the variable with the specified name.
   *
   * @param aName
   *        The name to be removed. May be <code>null</code>.
   * @return {@link EChange}
   */
  @Nonnull
  public EChange removeVariable (@Nullable final QName aName)
  {
    return EChange.valueOf (m_aVars.remove (aName) != null);
  }

  /**
   * Remove multiple variables at once.
   *
   * @param aNames
   *        The names to be removed. May be <code>null</code>.
   * @return {@link EChange#CHANGED} if at least one variable was removed.
   */
  @Nonnull
  public EChange removeVariables (@Nullable final Iterable <QName> aNames)
  {
    EChange eChange = EChange.UNCHANGED;
    if (aNames != null)
      for (final QName aName : aNames)
        eChange = eChange.or (removeVariable (aName));
    return eChange;
  }

  /**
   * @return A mutable copy of all contained variables. Never <code>null</code>
   *         but maybe empty.
   */
  @Nonnull
  @ReturnsMutableCopy
  public Map <QName, ?> getAllVariables ()
  {
    return ContainerHelper.newMap (m_aVars);
  }

  /**
   * @return The number of contained variables. Always &ge; 0.
   */
  @Nonnegative
  public int getVariableCount ()
  {
    return m_aVars.size ();
  }

  /**
   * Remove all variables at once.
   *
   * @return {@link EChange#CHANGED} if at least one variable was removed.
   */
  @Nonnull
  public EChange clear ()
  {
    if (m_aVars.isEmpty ())
      return EChange.UNCHANGED;
    m_aVars.clear ();
    return EChange.CHANGED;
  }

  /**
   * Set multiple variables at once.
   *
   * @param aVars
   *        The variables to be set. May be <code>null</code>.
   */
  public void setAllVariables (@Nullable final Map <QName, ?> aVars)
  {
    m_aVars.clear ();
    if (aVars != null)
      m_aVars.putAll (aVars);
  }

  @Nullable
  public Object resolveVariable (@Nonnull final QName aVariableName)
  {
    ValueEnforcer.notNull (aVariableName, "VariableName");

    return m_aVars.get (aVariableName);
  }

  @Nonnull
  @ReturnsMutableCopy
  public MapBasedXPathVariableResolverQName getClone ()
  {
    return new MapBasedXPathVariableResolverQName (this);
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final MapBasedXPathVariableResolverQName rhs = (MapBasedXPathVariableResolverQName) o;
    return EqualsUtils.equals (m_aVars, rhs.m_aVars);
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aVars).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("vars", m_aVars).toString ();
  }
}
