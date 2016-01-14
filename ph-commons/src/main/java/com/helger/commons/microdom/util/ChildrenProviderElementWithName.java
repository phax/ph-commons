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
package com.helger.commons.microdom.util;

import java.util.ArrayList;
import java.util.Collection;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.ValueEnforcer;
import com.helger.commons.annotation.Nonempty;
import com.helger.commons.annotation.ReturnsMutableCopy;
import com.helger.commons.hierarchy.IChildrenProvider;
import com.helger.commons.microdom.IMicroElement;
import com.helger.commons.string.StringHelper;

/**
 * Implementation of the {@link IChildrenProvider} for {@link IMicroElement}
 * objects considering only elements with a certain element name (and optionally
 * a namespace URI).
 *
 * @author Philip Helger
 */
public final class ChildrenProviderElementWithName implements IChildrenProvider <IMicroElement>
{
  private final String m_sNamespaceURI;
  private final String m_sTagName;

  public ChildrenProviderElementWithName (@Nonnull @Nonempty final String sTagName)
  {
    this (null, sTagName);
  }

  public ChildrenProviderElementWithName (@Nullable final String sNamespaceURI,
                                          @Nonnull @Nonempty final String sTagName)
  {
    m_sNamespaceURI = sNamespaceURI;
    m_sTagName = ValueEnforcer.notEmpty (sTagName, "TagName");
  }

  public boolean hasChildren (@Nullable final IMicroElement aCurrent)
  {
    // Not an element?
    if (aCurrent == null || !aCurrent.isElement ())
      return false;

    // Namespace URI defined?
    if (StringHelper.hasText (m_sNamespaceURI))
      return aCurrent.hasChildElements (m_sNamespaceURI, m_sTagName);
    return aCurrent.hasChildElements (m_sTagName);
  }

  @Nonnegative
  public int getChildCount (@Nullable final IMicroElement aCurrent)
  {
    if (aCurrent == null)
      return 0;
    return getAllChildren (aCurrent).size ();
  }

  @Nonnull
  @ReturnsMutableCopy
  public Collection <? extends IMicroElement> getAllChildren (@Nullable final IMicroElement aCurrent)
  {
    // Not an element?
    if (aCurrent == null)
      return new ArrayList <> ();

    // Namespace URI defined?
    if (StringHelper.hasText (m_sNamespaceURI))
      return aCurrent.getAllChildElements (m_sNamespaceURI, m_sTagName);

    return aCurrent.getAllChildElements (m_sTagName);
  }
}
