/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.xml.serialize.write;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

/**
 * Determines the bracket mode for XML elements?
 *
 * @author Philip Helger
 */
public enum EXMLSerializeBracketMode implements IHasID <String>
{
  /** Open and close tag. E.g. &lt;a&gt; and &lt;/a&gt; */
  OPEN_CLOSE ("openclose"),
  /** Self closed tag. E.g. &lt;a /&gt; */
  SELF_CLOSED ("selfclosed"),
  /**
   * Only open tag (and no closing tag). E.g. &lt;a&gt; Note: this is required
   * for some HTML versions.
   */
  OPEN_ONLY ("openonly");

  private final String m_sID;

  EXMLSerializeBracketMode (@NonNull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  /** {@inheritDoc} */
  @NonNull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return <code>true</code> if this is the self-closed bracket mode.
   */
  public boolean isSelfClosed ()
  {
    return this == SELF_CLOSED;
  }

  /**
   * @return <code>true</code> if this is the open-close bracket mode.
   */
  public boolean isOpenClose ()
  {
    return this == OPEN_CLOSE;
  }

  /**
   * Get the {@link EXMLSerializeBracketMode} matching the passed ID.
   *
   * @param sID
   *        The ID to search. May be <code>null</code>.
   * @return <code>null</code> if no such bracket mode exists.
   */
  @Nullable
  public static EXMLSerializeBracketMode getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EXMLSerializeBracketMode.class, sID);
  }
}
