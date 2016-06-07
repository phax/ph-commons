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
package com.helger.commons.xml.serialize.write;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.id.IHasID;
import com.helger.commons.lang.EnumHelper;

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

  private EXMLSerializeBracketMode (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  public boolean isSelfClosed ()
  {
    return this == SELF_CLOSED;
  }

  public boolean isOpenClose ()
  {
    return this == OPEN_CLOSE;
  }

  @Nullable
  public static EXMLSerializeBracketMode getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EXMLSerializeBracketMode.class, sID);
  }
}
