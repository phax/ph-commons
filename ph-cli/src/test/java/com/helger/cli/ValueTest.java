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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public final class ValueTest
{
  private CommandLine m_aCommandLine;
  private final Options m_aOptions = new Options ();

  @Before
  public void setUp () throws Exception
  {
    m_aOptions.addOption (Option.builder ("a").desc ("toggle -a").build ());
    m_aOptions.addOption (Option.builder ("b").oneArg ().desc ("set -b").build ());
    m_aOptions.addOption (Option.builder ("c").longOpt ("c").desc ("toggle -c").build ());
    m_aOptions.addOption (Option.builder ("d").longOpt ("d").oneArg ().desc ("set -d").build ());

    m_aOptions.addOption (Option.builder ("e").oneOptionalArg ().build ());
    m_aOptions.addOption (Option.builder ().oneOptionalArg ().longOpt ("fish").build ());
    m_aOptions.addOption (Option.builder ().unlimitedOptionalArgs ().longOpt ("gravy").build ());
    m_aOptions.addOption (Option.builder ().optionalNumberOfArgs (2).longOpt ("hide").build ());
    m_aOptions.addOption (Option.builder ("i").optionalNumberOfArgs (2).build ());
    m_aOptions.addOption (Option.builder ("j").unlimitedOptionalArgs ().build ());

    final String [] args = new String [] { "-a", "-b", "foo", "--c", "--d", "bar" };

    final ICommandLineParser parser = new CommandLineParser ();
    m_aCommandLine = parser.parse (m_aOptions, args);
  }

  @Test
  public void testShortNoArg ()
  {
    assertTrue (m_aCommandLine.hasOption ("a"));
    assertNull (m_aCommandLine.getOptionValue ("a"));
  }

  @Test
  public void testShortWithArg ()
  {
    assertTrue (m_aCommandLine.hasOption ("b"));
    assertNotNull (m_aCommandLine.getOptionValue ("b"));
    assertEquals (m_aCommandLine.getOptionValue ("b"), "foo");
  }

  @Test
  public void testLongNoArg ()
  {
    assertTrue (m_aCommandLine.hasOption ("c"));
    assertNull (m_aCommandLine.getOptionValue ("c"));
  }

  @Test
  public void testLongWithArg ()
  {
    assertTrue (m_aCommandLine.hasOption ("d"));
    assertNotNull (m_aCommandLine.getOptionValue ("d"));
    assertEquals (m_aCommandLine.getOptionValue ("d"), "bar");
  }

  @Test
  public void testShortOptionalArgNoValue () throws Exception
  {
    final String [] args = new String [] { "-e" };

    final ICommandLineParser parser = new CommandLineParser ();
    final CommandLine cmd = parser.parse (m_aOptions, args);
    assertTrue (cmd.hasOption ("e"));
    assertNull (cmd.getOptionValue ("e"));
  }

  @Test
  public void testShortOptionalArgValue () throws Exception
  {
    final String [] args = new String [] { "-e", "everything" };

    final ICommandLineParser parser = new CommandLineParser ();
    final CommandLine cmd = parser.parse (m_aOptions, args);
    assertTrue (cmd.hasOption ("e"));
    assertEquals ("everything", cmd.getOptionValue ("e"));
  }

  @Test
  public void testLongOptionalNoValue () throws Exception
  {
    final String [] args = new String [] { "--fish" };

    final ICommandLineParser parser = new CommandLineParser ();
    final CommandLine cmd = parser.parse (m_aOptions, args);
    assertTrue (cmd.hasOption ("fish"));
    assertNull (cmd.getOptionValue ("fish"));
  }

  @Test
  public void testLongOptionalArgValue () throws Exception
  {
    final String [] args = new String [] { "--fish", "face" };

    final ICommandLineParser parser = new CommandLineParser ();
    final CommandLine cmd = parser.parse (m_aOptions, args);
    assertTrue (cmd.hasOption ("fish"));
    assertEquals ("face", cmd.getOptionValue ("fish"));
  }

  @Test
  public void testShortOptionalArgValues () throws Exception
  {
    final String [] args = new String [] { "-j", "ink", "idea" };

    final ICommandLineParser parser = new CommandLineParser ();
    final CommandLine cmd = parser.parse (m_aOptions, args);
    assertTrue (cmd.hasOption ("j"));
    assertEquals ("ink", cmd.getOptionValue ("j"));
    assertEquals ("ink", cmd.getAllOptionValues ("j").get (0));
    assertEquals ("idea", cmd.getAllOptionValues ("j").get (1));
    assertEquals (cmd.getArgs ().length, 0);
  }

  @Test
  public void testLongOptionalArgValues () throws Exception
  {
    final String [] args = new String [] { "--gravy", "gold", "garden" };

    final ICommandLineParser parser = new CommandLineParser ();
    final CommandLine cmd = parser.parse (m_aOptions, args);
    assertTrue (cmd.hasOption ("gravy"));
    assertEquals ("gold", cmd.getOptionValue ("gravy"));
    assertEquals ("gold", cmd.getAllOptionValues ("gravy").get (0));
    assertEquals ("garden", cmd.getAllOptionValues ("gravy").get (1));
    assertEquals (cmd.getArgs ().length, 0);
  }

  @Test
  public void testShortOptionalNArgValues () throws Exception
  {
    final String [] args = new String [] { "-i", "ink", "idea", "isotope", "ice" };

    final ICommandLineParser parser = new CommandLineParser ();
    final CommandLine cmd = parser.parse (m_aOptions, args);
    assertTrue (cmd.hasOption ("i"));
    assertEquals ("ink", cmd.getOptionValue ("i"));
    assertEquals ("ink", cmd.getAllOptionValues ("i").get (0));
    assertEquals ("idea", cmd.getAllOptionValues ("i").get (1));
    assertEquals (cmd.getArgs ().length, 2);
    assertEquals ("isotope", cmd.getArgs ()[0]);
    assertEquals ("ice", cmd.getArgs ()[1]);
  }

  @Test
  public void testLongOptionalNArgValues () throws Exception
  {
    final String [] args = new String [] { "--hide", "house", "hair", "head" };

    final ICommandLineParser parser = new CommandLineParser ();

    final CommandLine cmd = parser.parse (m_aOptions, args);
    assertTrue (cmd.hasOption ("hide"));
    assertEquals ("house", cmd.getOptionValue ("hide"));
    assertEquals ("house", cmd.getAllOptionValues ("hide").get (0));
    assertEquals ("hair", cmd.getAllOptionValues ("hide").get (1));
    assertEquals (cmd.getArgs ().length, 1);
    assertEquals ("head", cmd.getArgs ()[0]);
  }
}
