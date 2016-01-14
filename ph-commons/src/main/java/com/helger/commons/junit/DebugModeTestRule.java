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
package com.helger.commons.junit;

import org.junit.rules.ExternalResource;

import com.helger.commons.debug.GlobalDebug;

/**
 * A JUnit test rule that sets global debug and trace flag for a test
 *
 * @author Philip Helger
 */
public final class DebugModeTestRule extends ExternalResource
{
  /** When this rule is used, global debug is enabled. */
  public static final boolean ENABLE_GLOBAL_DEBUG = true;

  private final boolean m_bDebug;
  private boolean m_bOldDebug;

  public DebugModeTestRule ()
  {
    this (ENABLE_GLOBAL_DEBUG);
  }

  public DebugModeTestRule (final boolean bDebug)
  {
    m_bDebug = bDebug;
  }

  @Override
  public void before () throws Throwable
  {
    // Remember old states
    m_bOldDebug = GlobalDebug.isDebugMode ();

    // Init debug stuff to state specified in ctor
    GlobalDebug.setDebugModeDirect (m_bDebug);
  }

  @Override
  public void after ()
  {
    // Reset debug stuff to previous state
    GlobalDebug.setDebugModeDirect (m_bOldDebug);
  }
}
