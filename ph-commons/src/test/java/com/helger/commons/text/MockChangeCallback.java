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
package com.helger.commons.text;

import static org.junit.Assert.assertNotNull;

import javax.annotation.Nonnull;

import com.helger.commons.callback.IChangeCallback;
import com.helger.commons.state.EContinue;

public final class MockChangeCallback implements IChangeCallback <IMutableMultilingualText>
{
  private int m_nCallCountBefore = 0;
  private int m_nCallCountAfter = 0;

  public MockChangeCallback ()
  {}

  @Nonnull
  public EContinue beforeChange (final IMutableMultilingualText aObjectToChange)
  {
    assertNotNull (aObjectToChange);
    m_nCallCountBefore++;
    return EContinue.CONTINUE;
  }

  public void afterChange (final IMutableMultilingualText aChangedObject)
  {
    assertNotNull (aChangedObject);
    m_nCallCountAfter++;
  }

  public int getCallCountBefore ()
  {
    return m_nCallCountBefore;
  }

  public int getCallCountAfter ()
  {
    return m_nCallCountAfter;
  }
}
