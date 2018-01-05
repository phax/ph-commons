/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.json.serialize;

import javax.annotation.Nonnull;

import com.helger.commons.annotation.Nonempty;
import com.helger.commons.lang.ICloneable;

/**
 * Settings for configuring the JSON write process.
 *
 * @author Philip Helger
 */
public interface IJsonWriterSettings extends ICloneable <IJsonWriterSettings>
{
  /**
   * @return <code>true</code> if indentation should be enabled,
   *         <code>false</code> if not. By default is is disabled.
   */
  boolean isIdentEnabled ();

  /**
   * @return The string to be used to indent a single level, if indentation is
   *         enabled. By default this is two spaces.
   */
  @Nonnull
  @Nonempty
  String getIndentString ();

  /**
   * @return The string to be used as the newline separator. By default it is
   *         the system newline string.
   */
  @Nonnull
  @Nonempty
  String getNewlineString ();

  /**
   * @return <code>true</code> to write a newline at the end of the writing
   *         process, <code>false</code> to end with the last Json character. By
   *         default this is disabled.
   */
  boolean isWriteNewlineAtEnd ();

  /**
   * @return <code>true</code> if names of objects should be quoted (e.g.
   *         <code>{ "a" : 1 }</code> compare to <code>{ a : 1 }</code>),
   *         <code>false</code> otherwise. Default is <code>true</code>.
   */
  boolean isQuoteNames ();
}
