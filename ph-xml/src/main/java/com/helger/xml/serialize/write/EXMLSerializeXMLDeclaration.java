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
package com.helger.xml.serialize.write;

import com.helger.annotation.Nonempty;
import com.helger.base.id.IHasID;
import com.helger.base.lang.EnumHelper;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Used for creating string representation of XML. Determines whether the XML
 * declaration (<code>&lt;?xml version=... encoding=...?&gt;</code>) should be
 * emitted or ignored.
 *
 * @author Philip Helger
 */
public enum EXMLSerializeXMLDeclaration implements IHasID <String>
{
  /** Emit all fields: version, encoding, standalone */
  EMIT ("emit"),
  /** Emit fields: version, encoding */
  EMIT_NO_STANDALONE ("emitnostandalone"),
  /** Do not emit any XML declaration at all */
  IGNORE ("ignore");

  private final String m_sID;

  EXMLSerializeXMLDeclaration (@Nonnull @Nonempty final String sID)
  {
    m_sID = sID;
  }

  @Nonnull
  @Nonempty
  public String getID ()
  {
    return m_sID;
  }

  /**
   * @return <code>true</code> it emit is enabled
   */
  public boolean isEmit ()
  {
    return this != IGNORE;
  }

  /**
   * @return <code>true</code> if the "standalone" part of the XML declaration
   *         should be printed or not.
   */
  public boolean isEmitStandalone ()
  {
    return this == EMIT;
  }

  @Nullable
  public static EXMLSerializeXMLDeclaration getFromIDOrNull (@Nullable final String sID)
  {
    return EnumHelper.getFromIDOrNull (EXMLSerializeXMLDeclaration.class, sID);
  }
}
