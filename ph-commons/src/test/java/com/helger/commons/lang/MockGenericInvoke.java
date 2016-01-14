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

public final class MockGenericInvoke
{
  private static int s_nStaticNoArgs = 0;
  private static int s_nStaticTwoArgs = 0;
  private int m_nNoArgs = 0;
  private int m_nTwoArgs = 0;

  public MockGenericInvoke ()
  {}

  /**
   * @param s
   *        first param
   * @param i
   *        second param
   */
  public MockGenericInvoke (final String s, final int i)
  {
    m_nTwoArgs++;
  }

  public static void staticNoArgs ()
  {
    s_nStaticNoArgs++;
  }

  public static int getStaticNoArgs ()
  {
    return s_nStaticNoArgs;
  }

  /**
   * @param s
   *        first param
   * @param i
   *        second param
   */
  public static void staticTwoArgs (final String s, final int i)
  {
    s_nStaticTwoArgs++;
  }

  public static int getStaticTwoArgs ()
  {
    return s_nStaticTwoArgs;
  }

  public void noArgs ()
  {
    m_nNoArgs++;
  }

  public int getNoArgs ()
  {
    return m_nNoArgs;
  }

  /**
   * @param s
   *        first param
   * @param i
   *        second param
   */
  public void twoArgs (final String s, final int i)
  {
    m_nTwoArgs++;
  }

  public int getTwoArgs ()
  {
    return m_nTwoArgs;
  }
}
