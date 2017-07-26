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

import java.io.PrintWriter;

import org.junit.Test;

import com.helger.cli.HelpFormatter;
import com.helger.cli.Option;
import com.helger.cli.Options;
import com.helger.commons.io.stream.NonBlockingStringWriter;

/**
 * http://issues.apache.org/jira/browse/CLI-18
 */
public final class BugCLI18Test
{
  @Test
  public void testCLI18 ()
  {
    final Options options = new Options ();
    options.addOption (Option.builder ("a").longOpt ("aaa").desc ("aaaaaaa").build ());
    options.addOption (Option.builder ()
                             .longOpt ("bbb")
                             .desc ("bbbbbbb dksh fkshd fkhs dkfhsdk fhskd hksdks dhfowehfsdhfkjshf skfhkshf sf jkshfk sfh skfh skf f")
                             .build ());
    options.addOption (Option.builder ("c").desc ("ccccccc").build ());

    final HelpFormatter formatter = new HelpFormatter ();
    final NonBlockingStringWriter out = new NonBlockingStringWriter ();

    formatter.printHelp (new PrintWriter (out),
                         80,
                         "foobar",
                         "dsfkfsh kdh hsd hsdh fkshdf ksdh fskdh fsdh fkshfk sfdkjhskjh fkjh fkjsh khsdkj hfskdhf skjdfh ksf khf s",
                         options,
                         2,
                         2,
                         "blort j jgj j jg jhghjghjgjhgjhg jgjhgj jhg jhg hjg jgjhghjg jhg hjg jhgjg jgjhghjg jg jgjhgjgjg jhg jhgjh" +
                            '\r' +
                            '\n' +
                            "rarrr",
                         true);
  }
}
