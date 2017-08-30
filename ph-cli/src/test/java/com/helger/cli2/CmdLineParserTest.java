package com.helger.cli2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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
  private static void _parse (final String... aArgs) throws CmdLineParseException
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
  public void testParse () throws CmdLineParseException
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
  public void testValuesBasic () throws CmdLineParseException
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
      assertSame (ECmdLineParseError.TOO_LITTLE_REQUIRED_VALUES, ex.getError ());
    }

    try
    {
      p.parse (new String [] { "-D", "name" });
      fail ();
    }
    catch (final CmdLineParseException ex)
    {
      assertSame (ECmdLineParseError.TOO_LITTLE_REQUIRED_VALUES, ex.getError ());
    }

    final ParsedCmdLine aPCL = p.parse (new String [] { "-D", "name", "value" });
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
  public void testValuesValueSep () throws CmdLineParseException
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .args (2)
                                                                          .valueSeparator ('=')
                                                                          .repeatable (false)
                                                                          .required (true)
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

    // With value separator
    ParsedCmdLine aPCL = p.parse (new String [] { "-D", "name=value" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals ("name", aPCL.getValue ("D"));
    assertEquals (new CommonsArrayList <> ("name", "value"), aPCL.getAllValues ("D"));

    // Without value separator
    aPCL = p.parse (new String [] { "-D", "name", "value" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals ("name", aPCL.getValue ("D"));
    assertEquals (new CommonsArrayList <> ("name", "value"), aPCL.getAllValues ("D"));
  }

  @Test
  public void testValuesValueSepUnbounded () throws CmdLineParseException
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .longOpt ("def")
                                                                          .minArgs (2)
                                                                          .maxArgsInfinite ()
                                                                          .valueSeparator ('=')
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

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
  public void testValuesValueSepUnboundedRepeatable () throws CmdLineParseException
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .longOpt ("def")
                                                                          .minArgs (2)
                                                                          .maxArgsInfinite ()
                                                                          .repeatable (true)
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
      assertSame (ECmdLineParseError.TOO_LITTLE_REQUIRED_VALUES, ex.getError ());
    }
  }

  @Test
  public void testParseUndefinedActions () throws CmdLineParseException
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .longOpt ("def")
                                                                          .args (2)
                                                                          .valueSeparator ('=')
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

    ParsedCmdLine aPCL = p.parse (new String [] { "-foo", "-bar" });
    assertFalse (aPCL.hasOption ("D"));

    aPCL = p.parse (new String [] { "-Da=b", "-foo", "-bar" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals (new CommonsArrayList <> ("a", "b"), aPCL.getAllValues ("D"));

    aPCL = p.parse (new String [] { "-D", "-foo", "-bar" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals (new CommonsArrayList <> ("-foo", "-bar"), aPCL.getAllValues ("D"));
  }

  @Test
  public void testParseMultiple () throws CmdLineParseException
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .args (0, 2)
                                                                          .repeatable (true)
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

    ParsedCmdLine aPCL = p.parse (new String [] { "-D" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals (new CommonsArrayList <> (), aPCL.getAllValues ("D"));

    aPCL = p.parse (new String [] { "-D", "-D", "-D", "-D" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals (new CommonsArrayList <> (), aPCL.getAllValues ("D"));

    aPCL = p.parse (new String [] { "-D", "a" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals (new CommonsArrayList <> ("a"), aPCL.getAllValues ("D"));

    aPCL = p.parse (new String [] { "-D", "a", "-D", "-D" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals (new CommonsArrayList <> ("a"), aPCL.getAllValues ("D"));

    // Drop "c"
    aPCL = p.parse (new String [] { "-D", "a", "b", "c" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals (new CommonsArrayList <> ("a", "b"), aPCL.getAllValues ("D"));

    // Do not drop "c" - separate "-D"
    aPCL = p.parse (new String [] { "-D", "a", "-D", "b", "c" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals (new CommonsArrayList <> ("a", "b", "c"), aPCL.getAllValues ("D"));
  }

  @Test
  public void testParseRequired () throws CmdLineParseException
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .args (2)
                                                                          .valueSeparator ('=')
                                                                          .required (true)
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

    try
    {
      p.parse (new String [] { "-e" });
      fail ();
    }
    catch (final CmdLineParseException ex)
    {
      assertSame (ECmdLineParseError.REQUIRED_OPTION_IS_MISSING, ex.getError ());
    }

    final ParsedCmdLine aPCL = p.parse (new String [] { "-Dname=value", "-e", "15", "-port", "8080" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals (new CommonsArrayList <> ("name", "value"), aPCL.getAllValues ("D"));
  }

  @Test
  public void testParseNotRepeatable () throws CmdLineParseException
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("D")
                                                                          .args (2)
                                                                          .valueSeparator ('=')
                                                                          .repeatable (false)
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

    try
    {
      p.parse (new String [] { "-D", "a", "b", "-D", "c=d" });
      fail ();
    }
    catch (final CmdLineParseException ex)
    {
      assertSame (ECmdLineParseError.NON_REPEATABLE_OPTION_OCCURS_MORE_THAN_ONCE, ex.getError ());
    }

    final ParsedCmdLine aPCL = p.parse (new String [] { "-Dname=value", "-e", "15", "-port", "8080" });
    assertTrue (aPCL.hasOption ("D"));
    assertEquals (new CommonsArrayList <> ("name", "value"), aPCL.getAllValues ("D"));
  }

  @Test
  public void testParseChooseBest () throws CmdLineParseException
  {
    final ICommonsList <Option> aOptions = new CommonsArrayList <> (Option.builder ("A")
                                                                          .longOpt ("definition")
                                                                          .build (),
                                                                    Option.builder ("B").longOpt ("definitio").build (),
                                                                    Option.builder ("C").longOpt ("definiti").build (),
                                                                    Option.builder ("D").longOpt ("definit").build ());
    final CmdLineParser p = new CmdLineParser (aOptions);

    ParsedCmdLine aPCL = p.parse (new String [] { "--definition" });
    assertTrue (aPCL.hasOption ("A"));
    assertFalse (aPCL.hasOption ("B"));
    assertFalse (aPCL.hasOption ("C"));
    assertFalse (aPCL.hasOption ("D"));

    aPCL = p.parse (new String [] { "--definitionUpdateOfTheWorld" });
    assertTrue (aPCL.hasOption ("A"));
    assertFalse (aPCL.hasOption ("B"));
    assertFalse (aPCL.hasOption ("C"));
    assertFalse (aPCL.hasOption ("D"));

    aPCL = p.parse (new String [] { "--definition1234" });
    assertTrue (aPCL.hasOption ("A"));
    assertFalse (aPCL.hasOption ("B"));
    assertFalse (aPCL.hasOption ("C"));
    assertFalse (aPCL.hasOption ("D"));

    aPCL = p.parse (new String [] { "--definitio" });
    assertFalse (aPCL.hasOption ("A"));
    assertTrue (aPCL.hasOption ("B"));
    assertFalse (aPCL.hasOption ("C"));
    assertFalse (aPCL.hasOption ("D"));

    aPCL = p.parse (new String [] { "--definitio1234" });
    assertFalse (aPCL.hasOption ("A"));
    assertTrue (aPCL.hasOption ("B"));
    assertFalse (aPCL.hasOption ("C"));
    assertFalse (aPCL.hasOption ("D"));

    aPCL = p.parse (new String [] { "--definiti" });
    assertFalse (aPCL.hasOption ("A"));
    assertFalse (aPCL.hasOption ("B"));
    assertTrue (aPCL.hasOption ("C"));
    assertFalse (aPCL.hasOption ("D"));

    aPCL = p.parse (new String [] { "--definiti1234" });
    assertFalse (aPCL.hasOption ("A"));
    assertFalse (aPCL.hasOption ("B"));
    assertTrue (aPCL.hasOption ("C"));
    assertFalse (aPCL.hasOption ("D"));

    aPCL = p.parse (new String [] { "--definit" });
    assertFalse (aPCL.hasOption ("A"));
    assertFalse (aPCL.hasOption ("B"));
    assertFalse (aPCL.hasOption ("C"));
    assertTrue (aPCL.hasOption ("D"));

    aPCL = p.parse (new String [] { "--definit1234" });
    assertFalse (aPCL.hasOption ("A"));
    assertFalse (aPCL.hasOption ("B"));
    assertFalse (aPCL.hasOption ("C"));
    assertTrue (aPCL.hasOption ("D"));

    aPCL = p.parse (new String [] { "--defini" });
    assertFalse (aPCL.hasOption ("A"));
    assertFalse (aPCL.hasOption ("B"));
    assertFalse (aPCL.hasOption ("C"));
    assertFalse (aPCL.hasOption ("D"));
  }
}
