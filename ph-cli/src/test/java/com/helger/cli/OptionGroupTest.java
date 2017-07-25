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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import com.helger.cli.ex.AlreadySelectedException;
import com.helger.commons.lang.NonBlockingProperties;

public final class OptionGroupTest
{
  private Options m_aOptions;
  private final CommandLineParser m_aParser = new CommandLineParser ();

  @Before
  public void setUp ()
  {
    final Option file = Option.builder ("f").longOpt ("file").desc ("file to process").build ();
    final Option dir = Option.builder ("d").longOpt ("directory").desc ("directory to process").build ();
    final OptionGroup group = new OptionGroup ();
    group.addOption (file);
    group.addOption (dir);
    m_aOptions = new Options ().addOptionGroup (group);

    final Option section = Option.builder ("s").longOpt ("section").desc ("section to process").build ();
    final Option chapter = Option.builder ("c").longOpt ("chapter").desc ("chapter to process").build ();
    final OptionGroup group2 = new OptionGroup ();
    group2.addOption (section);
    group2.addOption (chapter);

    m_aOptions.addOptionGroup (group2);

    final Option importOpt = Option.builder ().longOpt ("import").desc ("section to process").build ();
    final Option exportOpt = Option.builder ().longOpt ("export").desc ("chapter to process").build ();
    final OptionGroup group3 = new OptionGroup ();
    group3.addOption (importOpt);
    group3.addOption (exportOpt);
    m_aOptions.addOptionGroup (group3);

    m_aOptions.addOption (Option.builder ("r").longOpt ("revision").desc ("revision number").build ());
  }

  @Test
  public void testSingleOptionFromGroup () throws Exception
  {
    final String [] args = new String [] { "-f" };

    final CommandLine cl = m_aParser.parse (m_aOptions, args);

    assertTrue ("Confirm -r is NOT set", !cl.hasOption ("r"));
    assertTrue ("Confirm -f is set", cl.hasOption ("f"));
    assertTrue ("Confirm -d is NOT set", !cl.hasOption ("d"));
    assertTrue ("Confirm -s is NOT set", !cl.hasOption ("s"));
    assertTrue ("Confirm -c is NOT set", !cl.hasOption ("c"));
    assertTrue ("Confirm no extra args", cl.getAllArgs ().size () == 0);
  }

  @Test
  public void testSingleOption () throws Exception
  {
    final String [] args = new String [] { "-r" };

    final CommandLine cl = m_aParser.parse (m_aOptions, args);

    assertTrue ("Confirm -r is set", cl.hasOption ("r"));
    assertTrue ("Confirm -f is NOT set", !cl.hasOption ("f"));
    assertTrue ("Confirm -d is NOT set", !cl.hasOption ("d"));
    assertTrue ("Confirm -s is NOT set", !cl.hasOption ("s"));
    assertTrue ("Confirm -c is NOT set", !cl.hasOption ("c"));
    assertTrue ("Confirm no extra args", cl.getAllArgs ().size () == 0);
  }

  @Test
  public void testTwoValidOptions () throws Exception
  {
    final String [] args = new String [] { "-r", "-f" };

    final CommandLine cl = m_aParser.parse (m_aOptions, args);

    assertTrue ("Confirm -r is set", cl.hasOption ("r"));
    assertTrue ("Confirm -f is set", cl.hasOption ("f"));
    assertTrue ("Confirm -d is NOT set", !cl.hasOption ("d"));
    assertTrue ("Confirm -s is NOT set", !cl.hasOption ("s"));
    assertTrue ("Confirm -c is NOT set", !cl.hasOption ("c"));
    assertTrue ("Confirm no extra args", cl.getAllArgs ().size () == 0);
  }

  @Test
  public void testSingleLongOption () throws Exception
  {
    final String [] args = new String [] { "--file" };

    final CommandLine cl = m_aParser.parse (m_aOptions, args);

    assertTrue ("Confirm -r is NOT set", !cl.hasOption ("r"));
    assertTrue ("Confirm -f is set", cl.hasOption ("f"));
    assertTrue ("Confirm -d is NOT set", !cl.hasOption ("d"));
    assertTrue ("Confirm -s is NOT set", !cl.hasOption ("s"));
    assertTrue ("Confirm -c is NOT set", !cl.hasOption ("c"));
    assertTrue ("Confirm no extra args", cl.getAllArgs ().isEmpty ());
  }

  @Test
  public void testTwoValidLongOptions () throws Exception
  {
    final String [] args = new String [] { "--revision", "--file" };

    final CommandLine cl = m_aParser.parse (m_aOptions, args);

    assertTrue ("Confirm -r is set", cl.hasOption ("r"));
    assertTrue ("Confirm -f is set", cl.hasOption ("f"));
    assertTrue ("Confirm -d is NOT set", !cl.hasOption ("d"));
    assertTrue ("Confirm -s is NOT set", !cl.hasOption ("s"));
    assertTrue ("Confirm -c is NOT set", !cl.hasOption ("c"));
    assertTrue ("Confirm no extra args", cl.getAllArgs ().size () == 0);
  }

  @Test
  public void testNoOptionsExtraArgs () throws Exception
  {
    final String [] args = new String [] { "arg1", "arg2" };

    final CommandLine cl = m_aParser.parse (m_aOptions, args);

    assertTrue ("Confirm -r is NOT set", !cl.hasOption ("r"));
    assertTrue ("Confirm -f is NOT set", !cl.hasOption ("f"));
    assertTrue ("Confirm -d is NOT set", !cl.hasOption ("d"));
    assertTrue ("Confirm -s is NOT set", !cl.hasOption ("s"));
    assertTrue ("Confirm -c is NOT set", !cl.hasOption ("c"));
    assertTrue ("Confirm TWO extra args", cl.getAllArgs ().size () == 2);
  }

  @Test
  public void testTwoOptionsFromGroup () throws Exception
  {
    final String [] args = new String [] { "-f", "-d" };

    try
    {
      m_aParser.parse (m_aOptions, args);
      fail ("two arguments from group not allowed");
    }
    catch (final AlreadySelectedException e)
    {
      assertNotNull ("null option group", e.getOptionGroup ());
      assertEquals ("selected option", "f", e.getOptionGroup ().getSelected ());
      assertEquals ("option", "d", e.getOption ().getOpt ());
    }
  }

  @Test
  public void testTwoLongOptionsFromGroup () throws Exception
  {
    final String [] args = new String [] { "--file", "--directory" };

    try
    {
      m_aParser.parse (m_aOptions, args);
      fail ("two arguments from group not allowed");
    }
    catch (final AlreadySelectedException e)
    {
      assertNotNull ("null option group", e.getOptionGroup ());
      assertEquals ("selected option", "f", e.getOptionGroup ().getSelected ());
      assertEquals ("option", "d", e.getOption ().getOpt ());
    }
  }

  @Test
  public void testTwoOptionsFromDifferentGroup () throws Exception
  {
    final String [] args = new String [] { "-f", "-s" };

    final CommandLine cl = m_aParser.parse (m_aOptions, args);
    assertTrue ("Confirm -r is NOT set", !cl.hasOption ("r"));
    assertTrue ("Confirm -f is set", cl.hasOption ("f"));
    assertTrue ("Confirm -d is NOT set", !cl.hasOption ("d"));
    assertTrue ("Confirm -s is set", cl.hasOption ("s"));
    assertTrue ("Confirm -c is NOT set", !cl.hasOption ("c"));
    assertTrue ("Confirm NO extra args", cl.getAllArgs ().size () == 0);
  }

  @Test
  public void testTwoOptionsFromGroupWithProperties () throws Exception
  {
    final String [] args = new String [] { "-f" };

    final NonBlockingProperties properties = new NonBlockingProperties ();
    properties.put ("d", "true");

    final CommandLine cl = m_aParser.parse (m_aOptions, args, properties);
    assertTrue (cl.hasOption ("f"));
    assertTrue (!cl.hasOption ("d"));
  }

  @Test
  public void testValidLongOnlyOptions () throws Exception
  {
    final CommandLine cl1 = m_aParser.parse (m_aOptions, new String [] { "--export" });
    assertTrue ("Confirm --export is set", cl1.hasOption ("export"));

    final CommandLine cl2 = m_aParser.parse (m_aOptions, new String [] { "--import" });
    assertTrue ("Confirm --import is set", cl2.hasOption ("import"));
  }

  @Test
  public void testGetAsString ()
  {
    final OptionGroup group1 = new OptionGroup ();
    group1.addOption (Option.builder ().longOpt ("foo").desc ("Foo").build ());
    group1.addOption (Option.builder ().longOpt ("bar").desc ("Bar").build ());
    assertEquals ("[--foo Foo, --bar Bar]", group1.getAsString ());

    final OptionGroup group2 = new OptionGroup ();
    group2.addOption (Option.builder ("f").longOpt ("foo").desc ("Foo").build ());
    group2.addOption (Option.builder ("b").longOpt ("bar").desc ("Bar").build ());
    assertEquals ("[-f Foo, -b Bar]", group2.getAsString ());
  }

  @Test
  public void testGetNames ()
  {
    final OptionGroup group = new OptionGroup ();
    group.addOption (Option.builder ('a').build ());
    group.addOption (Option.builder ('b').build ());

    assertNotNull ("null names", group.getAllNames ());
    assertEquals (2, group.getAllNames ().size ());
    assertTrue (group.getAllNames ().contains ("a"));
    assertTrue (group.getAllNames ().contains ("b"));
  }
}
