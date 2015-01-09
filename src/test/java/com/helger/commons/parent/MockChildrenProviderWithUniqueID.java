/**
 * Copyright (C) 2014-2015 Philip Helger (www.helger.com)
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
package com.helger.commons.parent;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public final class MockChildrenProviderWithUniqueID extends MockChildrenProvider implements
                                                                                IChildrenProviderWithUniqueID <String, MockHasChildren>
{
  private final Map <String, MockHasChildren> m_aMap = new HashMap <String, MockHasChildren> ();

  public MockChildrenProviderWithUniqueID (@Nullable final MockHasChildren... aChildren)
  {
    if (aChildren != null)
      for (final MockHasChildren aChild : aChildren)
        m_aMap.put (aChild.getID (), aChild);
  }

  @Nullable
  public MockHasChildren getItemWithID (@Nullable final String sID)
  {
    return m_aMap.get (sID);
  }
}
