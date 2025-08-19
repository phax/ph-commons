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
package com.helger.base.dimension;

import com.helger.base.compare.CompareHelper;
import com.helger.base.equals.EqualsHelper;

/**
 * Interface for objects having a width and a height.
 *
 * @author Philip Helger
 */
public interface IHasDimensionDouble extends IHasWidthDouble, IHasHeightDouble
{
  /**
   * @return <code>true</code> if width &gt; height, <code>false</code> otherwise.
   * @since 9.2.1
   */
  default boolean isLandscape ()
  {
    return CompareHelper.compare (getWidth (), getHeight ()) > 0;
  }

  /**
   * @return <code>true</code> if height &gt; width, <code>false</code> otherwise.
   * @since 9.2.1
   */
  default boolean isPortrait ()
  {
    return CompareHelper.compare (getHeight (), getWidth ()) > 0;
  }

  /**
   * @return <code>true</code> if width equals height, <code>false</code> otherwise.
   * @since 9.2.1
   */
  default boolean isQuadratic ()
  {
    return EqualsHelper.equals (getWidth (), getHeight ());
  }
}
