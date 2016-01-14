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
package com.helger.commons.id;

public final class MockHasIDString implements IHasID <String>
{
  private final String m_sID;

  public MockHasIDString (final int nID)
  {
    this (Integer.toString (nID));
  }

  public MockHasIDString (final String sName)
  {
    m_sID = sName;
  }

  public String getID ()
  {
    return m_sID;
  }
}
