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
package com.helger.cli.bug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.helger.cli.CommandLine;
import com.helger.cli.CommandLineParser;
import com.helger.cli.Option;
import com.helger.cli.Options;

/**
 * Test for CLI-265.
 * <p>
 * The issue is that a short option with an optional value will use whatever
 * comes next as value.
 */
public final class BugCLI265Test
{
  private CommandLineParser m_aParser;
  private Options m_aOptions;

  @Before
  public void setUp () throws Exception
  {
    m_aParser = new CommandLineParser ();

    final Option optionT1 = Option.builder ("t1").oneArg ().numberOfArgs (1).oneOptionalArg ().argName ("t1_path").build ();
    final Option optionA = Option.builder ("a").build ();
    final Option optionB = Option.builder ("b").build ();
    final Option optionLast = Option.builder ("last").build ();

    m_aOptions = new Options ().addOption (optionT1).addOption (optionA).addOption (optionB).addOption (optionLast);
  }

  @Test
  public void shouldParseShortOptionWithValue () throws Exception
  {
    final String [] shortOptionWithValue = new String [] { "-t1", "path/to/my/db" };

    final CommandLine commandLine = m_aParser.parse (m_aOptions, shortOptionWithValue);

    assertEquals ("path/to/my/db", commandLine.getOptionValue ("t1"));
    assertFalse (commandLine.hasOption ("last"));
  }

  @Test
  public void shouldParseShortOptionWithoutValue () throws Exception
  {
    final String [] twoShortOptions = new String [] { "-t1", "-last" };

    final CommandLine commandLine = m_aParser.parse (m_aOptions, twoShortOptions);

    assertTrue (commandLine.hasOption ("t1"));
    assertNotEquals ("Second option has been used as value for first option",
                     "-last",
                     commandLine.getOptionValue ("t1"));
    assertTrue ("Second option has not been detected", commandLine.hasOption ("last"));
  }

  @Test
  public void shouldParseConcatenatedShortOptions () throws Exception
  {
    final String [] concatenatedShortOptions = new String [] { "-t1", "-ab" };

    final CommandLine commandLine = m_aParser.parse (m_aOptions, concatenatedShortOptions);

    assertTrue (commandLine.hasOption ("t1"));
    assertNull (commandLine.getOptionValue ("t1"));
    assertTrue (commandLine.hasOption ("a"));
    assertTrue (commandLine.hasOption ("b"));
    assertFalse (commandLine.hasOption ("last"));
  }
}
