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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;

public final class ValuesTest
{
  private CommandLine m_aCmdLine;

  @Before
  public void setUp () throws Exception
  {
    final Options options = new Options ();

    options.addOption (Option.builder ("a").desc ("toggle -a").build ());
    options.addOption (Option.builder ("b").oneArg ().desc ("set -b").build ());
    options.addOption (Option.builder ("c").longOpt ("c").desc ("toggle -c").build ());
    options.addOption (Option.builder ("d").longOpt ("d").oneArg ().desc ("set -d").build ());

    options.addOption (Option.builder ("e").longOpt ("e").unlimitedArgs ().desc ("set -e ").build ());
    options.addOption (Option.builder ("f").longOpt ("f").desc ("jk").build ());
    options.addOption (Option.builder ("g").longOpt ("g").numberOfArgs (2).desc ("set -g").build ());
    options.addOption (Option.builder ("h").longOpt ("h").oneArg ().desc ("set -h").build ());
    options.addOption (Option.builder ("i").longOpt ("i").desc ("set -i").build ());
    options.addOption (Option.builder ("j")
                             .longOpt ("j")
                             .unlimitedArgs ()
                             .desc ("set -j")
                             .valueSeparator ('=')
                             .build ());
    options.addOption (Option.builder ("k")
                             .longOpt ("k")
                             .unlimitedArgs ()
                             .desc ("set -k")
                             .valueSeparator ('=')
                             .build ());
    options.addOption (Option.builder ("m").longOpt ("m").unlimitedArgs ().desc ("set -m").valueSeparator ().build ());

    final String [] args = new String [] { "-a",
                                           "-b",
                                           "foo",
                                           "--c",
                                           "--d",
                                           "bar",
                                           "-e",
                                           "one",
                                           "two",
                                           "-f",
                                           "arg1",
                                           "arg2",
                                           "-g",
                                           "val1",
                                           "val2",
                                           "arg3",
                                           "-h",
                                           "val1",
                                           "-i",
                                           "-h",
                                           "val2",
                                           "-jkey=value",
                                           "-j",
                                           "key=value",
                                           "-kkey1=value1",
                                           "-kkey2=value2",
                                           "-mkey=value" };

    final ICommandLineParser parser = new CommandLineParser ();

    m_aCmdLine = parser.parse (options, args);
  }

  @Test
  public void testShortArgs ()
  {
    assertTrue ("Option a is not set", m_aCmdLine.hasOption ("a"));
    assertTrue ("Option c is not set", m_aCmdLine.hasOption ("c"));

    assertTrue (m_aCmdLine.getAllOptionValues ("a").isEmpty ());
    assertTrue (m_aCmdLine.getAllOptionValues ("c").isEmpty ());
  }

  @Test
  public void testShortArgsWithValue ()
  {
    assertTrue ("Option b is not set", m_aCmdLine.hasOption ("b"));
    assertTrue (m_aCmdLine.getOptionValue ("b").equals ("foo"));
    assertEquals (1, m_aCmdLine.getAllOptionValues ("b").size ());

    assertTrue ("Option d is not set", m_aCmdLine.hasOption ("d"));
    assertTrue (m_aCmdLine.getOptionValue ("d").equals ("bar"));
    assertEquals (1, m_aCmdLine.getAllOptionValues ("d").size ());
  }

  @Test
  public void testMultipleArgValues ()
  {
    assertTrue ("Option e is not set", m_aCmdLine.hasOption ("e"));
    assertEquals (new CommonsArrayList <> ("one", "two"), m_aCmdLine.getAllOptionValues ("e"));
  }

  @Test
  public void testTwoArgValues ()
  {
    assertTrue ("Option g is not set", m_aCmdLine.hasOption ("g"));
    assertEquals (new CommonsArrayList <> ("val1", "val2"), m_aCmdLine.getAllOptionValues ("g"));
  }

  @Test
  public void testComplexValues ()
  {
    assertTrue ("Option i is not set", m_aCmdLine.hasOption ("i"));
    assertTrue ("Option h is not set", m_aCmdLine.hasOption ("h"));
    assertEquals (new CommonsArrayList <> ("val1", "val2"), m_aCmdLine.getAllOptionValues ("h"));
  }

  @Test
  public void testExtraArgs ()
  {
    assertArrayEquals ("Extra args", new String [] { "arg1", "arg2", "arg3" }, m_aCmdLine.getArgs ());
  }

  @Test
  public void testCharSeparator ()
  {
    // tests the char methods of CommandLine that delegate to the String methods
    assertTrue ("Option j is not set", m_aCmdLine.hasOption ("j"));
    assertTrue ("Option j is not set", m_aCmdLine.hasOption ('j'));
    assertEquals (new CommonsArrayList <> ("key", "value", "key", "value"), m_aCmdLine.getAllOptionValues ("j"));
    assertEquals (new CommonsArrayList <> ("key", "value", "key", "value"), m_aCmdLine.getAllOptionValues ('j'));

    assertTrue ("Option k is not set", m_aCmdLine.hasOption ("k"));
    assertTrue ("Option k is not set", m_aCmdLine.hasOption ('k'));
    assertEquals (new CommonsArrayList <> ("key1", "value1", "key2", "value2"), m_aCmdLine.getAllOptionValues ("k"));
    assertEquals (new CommonsArrayList <> ("key1", "value1", "key2", "value2"), m_aCmdLine.getAllOptionValues ('k'));

    assertTrue ("Option m is not set", m_aCmdLine.hasOption ("m"));
    assertTrue ("Option m is not set", m_aCmdLine.hasOption ('m'));
    assertEquals (new CommonsArrayList <> ("key", "value"), m_aCmdLine.getAllOptionValues ("m"));
    assertEquals (new CommonsArrayList <> ("key", "value"), m_aCmdLine.getAllOptionValues ('m'));
  }
}
