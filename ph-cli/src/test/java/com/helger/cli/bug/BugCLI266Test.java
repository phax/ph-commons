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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.helger.cli.HelpFormatter;
import com.helger.cli.Option;
import com.helger.cli.OptionGroup;
import com.helger.cli.Options;

public final class BugCLI266Test
{
  private final List <String> insertedOrder = Arrays.asList ("h", "d", "f", "x", "s", "p", "t", "w", "o");
  private final List <String> sortOrder = Arrays.asList ("d", "f", "h", "o", "p", "s", "t", "w", "x");

  @Test
  public void testOptionComparatorDefaultOrder ()
  {
    final HelpFormatter formatter = new HelpFormatter ();
    final List <Option> options = new ArrayList <> (getOptions ().getAllOptions ());
    Collections.sort (options, formatter.getOptionComparator ());
    int i = 0;
    for (final Option o : options)
    {
      Assert.assertEquals (o.getOpt (), sortOrder.get (i));
      i++;
    }
  }

  @Test
  public void testOptionComparatorInsertedOrder ()
  {
    final Collection <Option> options = getOptions ().getAllOptions ();
    int i = 0;
    for (final Option o : options)
    {
      Assert.assertEquals (o.getOpt (), insertedOrder.get (i));
      i++;
    }
  }

  private Options getOptions ()
  {
    final Options options = new Options ();
    final Option help = Option.builder ("h").longOpt ("help").desc ("Prints this help message").build ();
    options.addOption (help);

    buildOptionsGroup (options);

    final Option t = Option.builder ("t").required ().oneArg ().argName ("file").build ();
    final Option w = Option.builder ("w").required ().oneArg ().argName ("word").build ();
    final Option o = Option.builder ("o").oneArg ().argName ("directory").build ();
    options.addOption (t);
    options.addOption (w);
    options.addOption (o);
    return options;
  }

  private void buildOptionsGroup (final Options options)
  {
    final OptionGroup firstGroup = new OptionGroup ();
    final OptionGroup secondGroup = new OptionGroup ();
    firstGroup.setRequired (true);
    secondGroup.setRequired (true);

    firstGroup.addOption (Option.builder ("d").longOpt ("db").oneArg ().argName ("table-name").build ());
    firstGroup.addOption (Option.builder ("f").longOpt ("flat-file").oneArg ().argName ("input.csv").build ());
    options.addOptionGroup (firstGroup);

    secondGroup.addOption (Option.builder ("x").oneArg ().argName ("arg1").build ());
    secondGroup.addOption (Option.builder ("s").build ());
    secondGroup.addOption (Option.builder ("p").oneArg ().argName ("arg1").build ());
    options.addOptionGroup (secondGroup);
  }
}
