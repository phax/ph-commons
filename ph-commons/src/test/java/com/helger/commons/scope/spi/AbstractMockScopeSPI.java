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
package com.helger.commons.scope.spi;

/**
 * Abstract base class for all Mock SPI implementations
 *
 * @author Philip Helger
 */
public abstract class AbstractMockScopeSPI
{
  private static int s_nBegin = 0;
  private static int s_nEnd = 0;

  protected static final void onBegin ()
  {
    s_nBegin++;
  }

  public static final int getBegin ()
  {
    return s_nBegin;
  }

  protected static final void onEnd ()
  {
    s_nEnd++;
  }

  public static final int getEnd ()
  {
    return s_nEnd;
  }
}
