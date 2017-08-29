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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.junit.Test;

import com.helger.commons.annotation.ReturnsMutableCopy;

public final class OptionTest
{
  private static class TestOption extends Option
  {
    protected TestOption (@Nonnull final Option rhs) throws IllegalArgumentException
    {
      super (rhs);
    }

    public TestOption (@Nonnull final Builder aBuilder)
    {
      super (aBuilder);
    }

    public boolean addValue (final String value)
    {
      addValueForProcessing (value);
      return true;
    }

    @Override
    @Nonnull
    public TestOption getClone ()
    {
      return new TestOption (this);
    }

    public static class Builder extends Option.Builder
    {
      protected Builder (@Nullable final String sOpt) throws IllegalArgumentException
      {
        super (sOpt);
      }

      @Override
      @Nonnull
      @ReturnsMutableCopy
      public TestOption build ()
      {
        return new TestOption (this);
      }
    }
  }

  @Test
  public void testClear ()
  {
    final TestOption option = (TestOption) new TestOption.Builder ("x").oneArg ().desc ("").build ();
    assertEquals (0, option.getAllValues ().size ());
    option.addValue ("a");
    assertEquals (1, option.getAllValues ().size ());
    option.clearValues ();
    assertEquals (0, option.getAllValues ().size ());
  }

  // See http://issues.apache.org/jira/browse/CLI-21
  @Test
  public void testClone ()
  {
    final TestOption a = (TestOption) new TestOption.Builder ("a").oneArg ().desc ("").build ();
    final TestOption b = a.getClone ();
    assertEquals (a, b);
    assertNotSame (a, b);
  }

  private static class DefaultOption extends Option
  {
    private final String m_sDefaultValue;

    public DefaultOption (@Nonnull final DefaultOption rhs)
    {
      super (rhs);
      m_sDefaultValue = rhs.m_sDefaultValue;
    }

    public DefaultOption (@Nonnull final Builder aBuilder)
    {
      super (aBuilder);
      m_sDefaultValue = aBuilder.m_sDefaultValue;
    }

    @Override
    public String getValue ()
    {
      String ret = super.getValue ();
      if (ret == null)
        ret = m_sDefaultValue;
      return ret;
    }

    @Override
    public DefaultOption getClone ()
    {
      return new DefaultOption (this);
    }

    public static class Builder extends Option.Builder
    {
      private String m_sDefaultValue;

      protected Builder (@Nullable final String sOpt) throws IllegalArgumentException
      {
        super (sOpt);
      }

      @Nonnull
      public Builder defaultValue (@Nullable final String s)
      {
        m_sDefaultValue = s;
        return this;
      }

      @Override
      @Nonnull
      @ReturnsMutableCopy
      public DefaultOption build ()
      {
        return new DefaultOption (this);
      }
    }
  }

  @Test
  public void testSubclass ()
  {
    final DefaultOption option = (DefaultOption) new DefaultOption.Builder ("f").defaultValue ("myfile.txt")
                                                                                .oneArg ()
                                                                                .desc ("file")
                                                                                .build ();
    final DefaultOption clone = option.getClone ();
    assertEquals ("myfile.txt", clone.getValue ());
    assertEquals (DefaultOption.class, clone.getClass ());
  }

  @Test
  public void testHasArgName ()
  {
    Option option = Option.builder ("f").argName (null).build ();
    assertFalse (option.hasArgName ());

    option = Option.builder ("f").argName ("").build ();
    assertFalse (option.hasArgName ());

    option = Option.builder ("f").argName ("file").build ();
    assertTrue (option.hasArgName ());
  }

  @Test
  public void testHasArgs ()
  {
    Option option = Option.builder ("f").numberOfArgs (0).build ();
    assertFalse (option.hasArgs ());

    option = Option.builder ("f").numberOfArgs (1).build ();
    assertFalse (option.hasArgs ());

    option = Option.builder ("f").numberOfArgs (10).build ();
    assertTrue (option.hasArgs ());

    option = Option.builder ("f").numberOfArgs (Option.UNLIMITED_VALUES).build ();
    assertTrue (option.hasArgs ());

    option = Option.builder ("f").numberOfArgs (Option.UNINITIALIZED).build ();
    assertFalse (option.hasArgs ());
  }

  @Test
  public void testGetValue ()
  {
    final Option option = Option.builder ("f").unlimitedArgs ().build ();

    assertEquals ("default", option.values ().getFirst ("default"));
    assertEquals (null, option.getValue (0));

    option.addValueForProcessing ("foo");

    assertEquals ("foo", option.getValue ());
    assertEquals ("foo", option.getValue (0));
    assertEquals ("foo", option.values ().getFirst ("default"));
  }

  @Test
  public void testBuilderMethods ()
  {
    final char defaultSeparator = (char) 0;

    _checkOption (Option.builder ("a")
                        .desc ("desc")
                        .build (),
                  "a",
                  "desc",
                  null,
                  Option.UNINITIALIZED,
                  null,
                  false,
                  false,
                  defaultSeparator);
    _checkOption (Option.builder ("a")
                        .desc ("desc")
                        .build (),
                  "a",
                  "desc",
                  null,
                  Option.UNINITIALIZED,
                  null,
                  false,
                  false,
                  defaultSeparator);
    _checkOption (Option.builder ("a").desc ("desc").longOpt ("aaa").build (),
                  "a",
                  "desc",
                  "aaa",
                  Option.UNINITIALIZED,
                  null,
                  false,
                  false,
                  defaultSeparator);
    _checkOption (Option.builder ("a").desc ("desc").oneArg ().build (),
                  "a",
                  "desc",
                  null,
                  1,
                  null,
                  false,
                  false,
                  defaultSeparator);
    _checkOption (Option.builder ("a")
                        .desc ("desc")
                        .build (),
                  "a",
                  "desc",
                  null,
                  Option.UNINITIALIZED,
                  null,
                  false,
                  false,
                  defaultSeparator);
    _checkOption (Option.builder ("a").desc ("desc").numberOfArgs (3).build (),
                  "a",
                  "desc",
                  null,
                  3,
                  null,
                  false,
                  false,
                  defaultSeparator);
    _checkOption (Option.builder ("a").desc ("desc").required (true).build (),
                  "a",
                  "desc",
                  null,
                  Option.UNINITIALIZED,
                  null,
                  true,
                  false,
                  defaultSeparator);
    _checkOption (Option.builder ("a").desc ("desc").required (false).build (),
                  "a",
                  "desc",
                  null,
                  Option.UNINITIALIZED,
                  null,
                  false,
                  false,
                  defaultSeparator);

    _checkOption (Option.builder ("a").desc ("desc").argName ("arg1").build (),
                  "a",
                  "desc",
                  null,
                  Option.UNINITIALIZED,
                  "arg1",
                  false,
                  false,
                  defaultSeparator);
    _checkOption (Option.builder ("a")
                        .desc ("desc")
                        .build (),
                  "a",
                  "desc",
                  null,
                  Option.UNINITIALIZED,
                  null,
                  false,
                  false,
                  defaultSeparator);
    _checkOption (Option.builder ("a").desc ("desc").oneOptionalArg ().build (),
                  "a",
                  "desc",
                  null,
                  1,
                  null,
                  false,
                  true,
                  defaultSeparator);
    _checkOption (Option.builder ("a").desc ("desc").valueSeparator (':').build (),
                  "a",
                  "desc",
                  null,
                  Option.UNINITIALIZED,
                  null,
                  false,
                  false,
                  ':');
    _checkOption (Option.builder ("a")
                        .desc ("desc")
                        .build (),
                  "a",
                  "desc",
                  null,
                  Option.UNINITIALIZED,
                  null,
                  false,
                  false,
                  defaultSeparator);
  }

  @Test (expected = IllegalArgumentException.class)
  public void testBuilderInsufficientParams1 ()
  {
    Option.builder ().desc ("desc").build ();
  }

  @Test (expected = IllegalArgumentException.class)
  public void testBuilderInsufficientParams2 ()
  {
    Option.builder (null).desc ("desc").build ();
  }

  private static void _checkOption (final Option option,
                                    final String opt,
                                    final String description,
                                    final String longOpt,
                                    final int numArgs,
                                    final String argName,
                                    final boolean required,
                                    final boolean optionalArg,
                                    final char valueSeparator)
  {
    assertEquals (opt, option.getOpt ());
    assertEquals (description, option.getDescription ());
    assertEquals (longOpt, option.getLongOpt ());
    assertEquals (numArgs, option.getNumberOfArgs ());
    assertEquals (argName, option.getArgName ());
    assertTrue (required == option.isRequired ());

    assertTrue (optionalArg == option.hasOptionalArg ());
    assertEquals (valueSeparator, option.getValueSeparator ());
  }

}
