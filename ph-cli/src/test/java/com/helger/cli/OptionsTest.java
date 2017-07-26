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

import java.util.Locale;

import org.junit.Test;

import com.helger.cli.ex.CommandLineParseException;
import com.helger.cli.ex.MissingOptionException;
import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

public final class OptionsTest
{
  @Test
  public void testSimple ()
  {
    final Options opts = new Options ();

    opts.addOption (Option.builder ("a").desc ("toggle -a").build ());
    opts.addOption (Option.builder ("b").oneArg ().desc ("toggle -b").build ());

    assertTrue (opts.hasOption ("a"));
    assertTrue (opts.hasOption ("b"));
  }

  @Test
  public void testDuplicateSimple ()
  {
    final Options opts = new Options ();
    opts.addOption (Option.builder ("a").desc ("toggle -a").build ());
    opts.addOption (Option.builder ("a").oneArg ().desc ("toggle -a*").build ());

    assertEquals ("last one in wins", "toggle -a*", opts.getOption ("a").getDescription ());
  }

  @Test
  public void testLong ()
  {
    final Options opts = new Options ();

    opts.addOption (Option.builder ("a").longOpt ("--a").desc ("toggle -a").build ());
    opts.addOption (Option.builder ("b").longOpt ("--b").oneArg ().desc ("set -b").build ());

    assertTrue (opts.hasOption ("a"));
    assertTrue (opts.hasOption ("b"));
  }

  @Test
  public void testDuplicateLong ()
  {
    final Options opts = new Options ();
    opts.addOption (Option.builder ("a").longOpt ("--a").desc ("toggle -a").build ());
    opts.addOption (Option.builder ("a").longOpt ("--a").desc ("toggle -a*").build ());
    assertEquals ("last one in wins", "toggle -a*", opts.getOption ("a").getDescription ());
  }

  @Test
  public void testHelpOptions ()
  {
    final Option longOnly1 = Option.builder ().longOpt ("long-only1").build ();
    final Option longOnly2 = Option.builder ().longOpt ("long-only2").build ();
    final Option shortOnly1 = Option.builder ("1").build ();
    final Option shortOnly2 = Option.builder ("2").build ();
    final Option bothA = Option.builder ("a").longOpt ("bothA").build ();
    final Option bothB = Option.builder ("b").longOpt ("bothB").build ();

    final Options options = new Options ();
    options.addOption (longOnly1);
    options.addOption (longOnly2);
    options.addOption (shortOnly1);
    options.addOption (shortOnly2);
    options.addOption (bothA);
    options.addOption (bothB);

    final ICommonsList <Option> allOptions = new CommonsArrayList <> ();
    allOptions.add (longOnly1);
    allOptions.add (longOnly2);
    allOptions.add (shortOnly1);
    allOptions.add (shortOnly2);
    allOptions.add (bothA);
    allOptions.add (bothB);

    final ICommonsList <Option> helpOptions = options.getAllOptions ();

    assertTrue ("Everything in all should be in help", helpOptions.containsAll (allOptions));
    assertTrue ("Everything in help should be in all", allOptions.containsAll (helpOptions));
  }

  @Test
  public void testMissingOptionException () throws CommandLineParseException
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("f").required ().build ());
    try
    {
      new CommandLineParser ().parse (options, new String [0]);
      fail ("Expected MissingOptionException to be thrown");
    }
    catch (final MissingOptionException e)
    {
      assertEquals ("Missing required option: f", e.getMessage ());
    }
  }

  @Test
  public void testMissingOptionsException () throws CommandLineParseException
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("f").required ().build ());
    options.addOption (Option.builder ("x").required ().build ());
    try
    {
      new CommandLineParser ().parse (options, new String [0]);
      fail ("Expected MissingOptionException to be thrown");
    }
    catch (final MissingOptionException e)
    {
      assertEquals ("Missing required options: f, x", e.getMessage ());
    }
  }

  @Test
  public void testToString ()
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("f").longOpt ("foo").oneArg ().desc ("Foo").build ());
    options.addOption (Option.builder ("b").longOpt ("bar").desc ("Bar").build ());

    final String s = options.toString ();
    assertNotNull ("null string returned", s);
    assertTrue ("foo option missing", s.toLowerCase (Locale.US).contains ("foo"));
    assertTrue ("bar option missing", s.toLowerCase (Locale.US).contains ("bar"));
  }

  @Test
  public void testGetOptionsGroups ()
  {
    final Options options = new Options ();

    final OptionGroup group1 = new OptionGroup ();
    group1.addOption (Option.builder ('a').build ());
    group1.addOption (Option.builder ('b').build ());

    final OptionGroup group2 = new OptionGroup ();
    group2.addOption (Option.builder ('x').build ());
    group2.addOption (Option.builder ('y').build ());

    options.addOptionGroup (group1);
    options.addOptionGroup (group2);

    assertNotNull (options.getAllOptionGroups ());
    assertEquals (2, options.getAllOptionGroups ().size ());
  }

  @Test
  public void testGetMatchingOpts ()
  {
    final Options options = new Options ();
    options.addOption (Option.builder ().longOpt ("version").build ());
    options.addOption (Option.builder ().longOpt ("verbose").build ());

    assertTrue (options.getAllMatchingOptions ("foo").isEmpty ());
    assertEquals (1, options.getAllMatchingOptions ("version").size ());
    assertEquals (2, options.getAllMatchingOptions ("ver").size ());
  }
}
