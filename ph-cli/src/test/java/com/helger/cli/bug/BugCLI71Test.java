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
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.helger.cli.CommandLine;
import com.helger.cli.CommandLineParser;
import com.helger.cli.ICommandLineParser;
import com.helger.cli.Option;
import com.helger.cli.Options;
import com.helger.cli.ex.MissingArgumentException;

public final class BugCLI71Test
{
  private Options m_aOptions;
  private ICommandLineParser m_aParser;

  @Before
  public void setUp ()
  {
    m_aOptions = new Options ();

    final Option algorithm = Option.builder ("a")
                                   .longOpt ("algo")
                                   .oneArg ()
                                   .desc ("the algorithm which it to perform executing")
                                   .argName ("algorithm name")
                                   .build ();
    m_aOptions.addOption (algorithm);

    final Option key = Option.builder ("k")
                             .longOpt ("key")
                             .oneArg ()
                             .desc ("the key the setted algorithm uses to process")
                             .argName ("value")
                             .build ();
    m_aOptions.addOption (key);

    m_aParser = new CommandLineParser ();
  }

  @Test
  public void testBasic () throws Exception
  {
    final String [] args = new String [] { "-a", "Caesar", "-k", "A" };
    final CommandLine line = m_aParser.parse (m_aOptions, args);
    assertEquals ("Caesar", line.getOptionValue ("a"));
    assertEquals ("A", line.getOptionValue ("k"));
  }

  @Test
  public void testMistakenArgument () throws Exception
  {
    String [] args = new String [] { "-a", "Caesar", "-k", "A" };
    CommandLine line = m_aParser.parse (m_aOptions, args);
    args = new String [] { "-a", "Caesar", "-k", "a" };
    line = m_aParser.parse (m_aOptions, args);
    assertEquals ("Caesar", line.getOptionValue ("a"));
    assertEquals ("a", line.getOptionValue ("k"));
  }

  @Test
  public void testLackOfError () throws Exception
  {
    final String [] args = new String [] { "-k", "-a", "Caesar" };
    try
    {
      m_aParser.parse (m_aOptions, args);
      fail ("MissingArgumentException expected");
    }
    catch (final MissingArgumentException e)
    {
      assertEquals ("option missing an argument", "k", e.getOption ().getOpt ());
    }
  }

  @Test
  public void testGetsDefaultIfOptional () throws Exception
  {
    final Options aOptions = new Options ();
    aOptions.addOption (Option.builder ("a")
                              .longOpt ("algo")
                              .oneArg ()
                              .desc ("the algorithm which it to perform executing")
                              .argName ("algorithm name")
                              .build ());
    aOptions.addOption (Option.builder ("k")
                              .longOpt ("key")
                              .oneOptionalArg ()
                              .desc ("the key the setted algorithm uses to process")
                              .argName ("value")
                              .build ());

    final String [] args = new String [] { "-k", "-a", "Caesar" };
    final CommandLine line = m_aParser.parse (aOptions, args);

    assertEquals ("Caesar", line.getOptionValue ("a"));
    assertEquals ("a", line.getOptionValue ('k', "a"));
  }
}
