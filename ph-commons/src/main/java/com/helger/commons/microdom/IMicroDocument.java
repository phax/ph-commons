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
package com.helger.commons.microdom;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a complete document.
 *
 * @author Philip Helger
 */
public interface IMicroDocument extends IMicroNodeWithChildren
{
  /**
   * @return <code>true</code> if the document is standalone, <code>false</code>
   *         if not. The default value is <code>false</code>.
   */
  boolean isStandalone ();

  /**
   * Change the standalone state of this document.
   *
   * @param bIsStandalone
   *        The new value. <code>true</code> for standalone, <code>false</code>
   *        if not.
   */
  void setStandalone (final boolean bIsStandalone);

  /**
   * @return May be <code>null</code>.
   */
  @Nullable
  IMicroDocumentType getDocType ();

  /**
   * @return May be <code>null</code>.
   */
  @Nullable
  IMicroElement getDocumentElement ();

  /**
   * {@inheritDoc}
   */
  @Nonnull
  IMicroDocument getClone ();
}
