/**
 * Original copyright by Apache Software Foundation
 * Copyright (C) 2017 Philip Helger (www.helger.com)
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
package com.helger.cli;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public final class ArgumentIsOptionTest
{
  private Options m_aOptions;
  private ICommandLineParser m_aParser;

  @Before
  public void setUp ()
  {
    m_aOptions = new Options ();
    m_aOptions.addOption (Option.builder ("p").desc ("Option p").build ());
    m_aOptions.addOption (Option.builder ("attr").oneArg ().desc ("Option accepts argument").build ());
    m_aParser = new CommandLineParser ();
  }

  @Test
  public void testOptionAndOptionWithArgument () throws Exception
  {
    final String [] args = new String [] { "-p", "-attr", "p" };

    final CommandLine cl = m_aParser.parse (m_aOptions, args);
    assertTrue ("Confirm -p is set", cl.hasOption ("p"));
    assertTrue ("Confirm -attr is set", cl.hasOption ("attr"));
    assertTrue ("Confirm arg of -attr", cl.getOptionValue ("attr").equals ("p"));
    assertTrue ("Confirm all arguments recognized", cl.getArgs ().length == 0);
  }

  @Test
  public void testOptionWithArgument () throws Exception
  {
    final String [] args = new String [] { "-attr", "p" };

    final CommandLine cl = m_aParser.parse (m_aOptions, args);
    assertFalse ("Confirm -p is set", cl.hasOption ("p"));
    assertTrue ("Confirm -attr is set", cl.hasOption ("attr"));
    assertTrue ("Confirm arg of -attr", cl.getOptionValue ("attr").equals ("p"));
    assertTrue ("Confirm all arguments recognized", cl.getArgs ().length == 0);
  }

  @Test
  public void testOption () throws Exception
  {
    final String [] args = new String [] { "-p" };

    final CommandLine cl = m_aParser.parse (m_aOptions, args);
    assertTrue ("Confirm -p is set", cl.hasOption ("p"));
    assertFalse ("Confirm -attr is not set", cl.hasOption ("attr"));
    assertTrue ("Confirm all arguments recognized", cl.getArgs ().length == 0);
  }
}
