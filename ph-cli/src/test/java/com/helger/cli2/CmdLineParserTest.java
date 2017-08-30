package com.helger.cli2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.helger.commons.collection.impl.CommonsArrayList;
import com.helger.commons.collection.impl.ICommonsList;

/**
 * Test class for class {@link CmdLineParser}.
 *
 * @author Philip Helger
 */
public class CmdLineParserTest
{
  private static void _parse (final String... aArgs)
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .longOpt ("def")
                                                                          .desc ("Define what so ever")
                                                                          .args (1)
                                                                          .valueSeparator (';')
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);
    final ParsedCmdLine aPCL = p.parse (aArgs);
    assertNotNull (aPCL);
  }

  @Test
  public void testParse ()
  {
    _parse ((String []) null);
    _parse (new String [0]);
    _parse ("a", "bb", "ccc", "dddd");
    _parse ("-D", "a");
    _parse ("-D", "a", "b");
    _parse ("-Da");
    _parse ("-Da;b");
    _parse ("-Da;b;c");
    _parse ("--def", "a");
    _parse ("--def", "a", "b");
    _parse ("--defa");
    _parse ("--defa;b");
    _parse ("--defa;b;c");
  }

  @Test
  public void testValuesNoValueSep ()
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .longOpt ("def")
                                                                          .args (2)
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

    try
    {
      p.parse (new String [] { "-D" });
      fail ();
    }
    catch (final CmdLineParseException ex)
    {
      // Missing params
    }

    try
    {
      p.parse (new String [] { "-D", "name" });
      fail ();
    }
    catch (final CmdLineParseException ex)
    {
      // Missing params
    }

    final ParsedCmdLine aPCL = p.parse (new String [] { "-D", "name", "value" });
    assertTrue (aPCL.hasOption ("D"));
    assertTrue (aPCL.hasOption ("def"));
    assertEquals ("name", aPCL.getValue ("D"));
    assertEquals ("name", aPCL.getValue ("def"));
    assertEquals (new CommonsArrayList <> ("name", "value"), aPCL.getAllValues ("D"));
    assertEquals (new CommonsArrayList <> ("name", "value"), aPCL.getAllValues ("def"));
  }

  @Test
  public void testValuesValueSep ()
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .longOpt ("def")
                                                                          .args (2)
                                                                          .valueSeparator ('=')
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

    try
    {
      p.parse (new String [] { "-D" });
      fail ();
    }
    catch (final CmdLineParseException ex)
    {
      // Missing params
    }

    try
    {
      p.parse (new String [] { "-D", "name" });
      fail ();
    }
    catch (final CmdLineParseException ex)
    {
      // Missing params
    }

    // With value separator
    ParsedCmdLine aPCL = p.parse (new String [] { "-D", "name=value" });
    assertTrue (aPCL.hasOption ("D"));
    assertTrue (aPCL.hasOption ("def"));
    assertFalse (aPCL.hasOption ("e"));
    assertEquals ("name", aPCL.getValue ("D"));
    assertEquals ("name", aPCL.getValue ("def"));
    assertNull (aPCL.getValue ("e"));
    assertEquals (new CommonsArrayList <> ("name", "value"), aPCL.getAllValues ("D"));
    assertEquals (new CommonsArrayList <> ("name", "value"), aPCL.getAllValues ("def"));
    assertNull (aPCL.getAllValues ("e"));

    // Without value separator
    aPCL = p.parse (new String [] { "-D", "name", "value" });
    assertTrue (aPCL.hasOption ("D"));
    assertTrue (aPCL.hasOption ("def"));
    assertFalse (aPCL.hasOption ("e"));
    assertEquals ("name", aPCL.getValue ("D"));
    assertEquals ("name", aPCL.getValue ("def"));
    assertNull (aPCL.getValue ("e"));
    assertEquals (new CommonsArrayList <> ("name", "value"), aPCL.getAllValues ("D"));
    assertEquals (new CommonsArrayList <> ("name", "value"), aPCL.getAllValues ("def"));
    assertNull (aPCL.getAllValues ("e"));
  }

  @Test
  public void testValuesValueSepUnbounded ()
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .longOpt ("def")
                                                                          .minArgs (2)
                                                                          .maxArgsUnlimited ()
                                                                          .valueSeparator ('=')
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

    try
    {
      p.parse (new String [] { "-D" });
      fail ();
    }
    catch (final CmdLineParseException ex)
    {
      // Missing params
    }

    try
    {
      p.parse (new String [] { "-D", "name" });
      fail ();
    }
    catch (final CmdLineParseException ex)
    {
      // Missing params
    }

    // With value separator
    ParsedCmdLine aPCL = p.parse (new String [] { "-Dvalue0=value1=value2=value3=value4" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals ("value0", aPCL.getValue ("D"));
    assertEquals (new CommonsArrayList <> ("value0", "value1", "value2", "value3", "value4"), aPCL.getAllValues ("D"));

    // With value separator
    aPCL = p.parse (new String [] { "-D", "value0=value1", "value2=value3=value4" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals ("value0", aPCL.getValue ("D"));
    assertEquals (new CommonsArrayList <> ("value0", "value1", "value2", "value3", "value4"), aPCL.getAllValues ("D"));

    // Without value separator
    aPCL = p.parse (new String [] { "-D", "value0", "value1", "value2", "value3", "value4" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals ("value0", aPCL.getValue ("D"));
    assertEquals (new CommonsArrayList <> ("value0", "value1", "value2", "value3", "value4"), aPCL.getAllValues ("D"));
  }

  @Test
  public void testValuesMultiple ()
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .longOpt ("def")
                                                                          .minArgs (2)
                                                                          .maxArgsUnlimited ()
                                                                          .valueSeparator (';')
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

    // With value separator
    ParsedCmdLine aPCL = p.parse (new String [] { "-Dvalue0;value1", "-Dvalue2;value3;value4" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals ("value0", aPCL.getValue ("D"));
    assertEquals (new CommonsArrayList <> ("value0", "value1", "value2", "value3", "value4"), aPCL.getAllValues ("D"));

    // Without value separator
    aPCL = p.parse (new String [] { "-D", "value0", "value1", "-D", "value2", "value3", "value4" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals ("value0", aPCL.getValue ("D"));
    assertEquals (new CommonsArrayList <> ("value0", "value1", "value2", "value3", "value4"), aPCL.getAllValues ("D"));

    // Without value separator
    try
    {
      p.parse (new String [] { "-D", "value0", "value1", "-D", "value2", "value3", "-Dvalue4" });
      fail ();
    }
    catch (final CmdLineParseException ex)
    {
      // Last "-D" has too little arguments
    }
  }
}
