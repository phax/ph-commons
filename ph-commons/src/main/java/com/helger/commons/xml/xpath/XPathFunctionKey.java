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

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.xml.namespace.QName;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.compare.CompareHelper;
import com.helger.commons.hashcode.HashCodeGenerator;
import com.helger.commons.string.ToStringGenerator;

/**
 * This class contains a single key for a map-based function resolver.
 *
 * @author Philip Helger
 */
@Immutable
public final class XPathFunctionKey implements Comparable <XPathFunctionKey>
{
  private final QName m_aFunctionName;
  private final int m_nArity;

  /**
   * Constructor
   *
   * @param aFunctionName
   *        Function name. May not be <code>null</code>.
   * @param nArity
   *        The number of parameters the function takes. Must be &ge; 0.
   */
  public XPathFunctionKey (@Nonnull final QName aFunctionName, @Nonnegative final int nArity)
  {
    m_aFunctionName = ValueEnforcer.notNull (aFunctionName, "FunctionName");
    m_nArity = ValueEnforcer.isGE0 (nArity, "Arity");
  }

  /**
   * @return The function name and never <code>null</code>.
   */
  @Nonnull
  public QName getFunctionName ()
  {
    return m_aFunctionName;
  }

  /**
   * @return The number of parameters the function takes. Always &ge; 0.
   */
  @Nonnegative
  public int getArity ()
  {
    return m_nArity;
  }

  public int compareTo (@Nonnull final XPathFunctionKey o)
  {
    // 1st namespace URI
    int ret = CompareHelper.compare (m_aFunctionName.getNamespaceURI (), o.m_aFunctionName.getNamespaceURI ());
    if (ret == 0)
    {
      // 2nd local part
      ret = CompareHelper.compare (m_aFunctionName.getLocalPart (), o.m_aFunctionName.getLocalPart ());
      if (ret == 0)
      {
        // 3rd parameter count
        ret = CompareHelper.compare (m_nArity, o.m_nArity);
      }
    }
    return ret;
  }

  @Override
  public boolean equals (final Object o)
  {
    if (o == this)
      return true;
    if (o == null || !getClass ().equals (o.getClass ()))
      return false;
    final XPathFunctionKey rhs = (XPathFunctionKey) o;
    return m_aFunctionName.equals (rhs.m_aFunctionName) && m_nArity == rhs.m_nArity;
  }

  @Override
  public int hashCode ()
  {
    return new HashCodeGenerator (this).append (m_aFunctionName).append (m_nArity).getHashCode ();
  }

  @Override
  public String toString ()
  {
    return new ToStringGenerator (this).append ("name", m_aFunctionName).append ("arity", m_nArity).toString ();
  }
}
