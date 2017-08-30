package com.helger.cli2;

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
                                                                          .oneRequiredArg ()
                                                                          .valueSeparator (';')
                                                                          .build ());
    final CmdLineParser p = new CmdLineParser (aOptions);
    p.parse (aArgs);
  }

  @Test
  public void testBasic ()
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
}
