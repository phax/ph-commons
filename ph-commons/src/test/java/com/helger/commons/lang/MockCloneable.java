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
package com.helger.commons.lang;

/**
 * This class needs to be in the same package as {@link CloneHelper} so that the
 * test works!<br>
 * Should invoke the {@link #clone()} method.
 *
 * @author Philip Helger
 */
public final class MockCloneable implements Cloneable
{
  private final int m_i;

  public MockCloneable ()
  {
    this (0);
  }

  private MockCloneable (final int j)
  {
    m_i = j;
  }

  public int getI ()
  {
    return m_i;
  }

  @Override
  public MockCloneable clone ()
  {
    return new MockCloneable (m_i + 1);
  }
}
