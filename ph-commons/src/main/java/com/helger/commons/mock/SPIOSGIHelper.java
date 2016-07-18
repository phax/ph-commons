/**
 * Copyright (C) 2014-2016 Philip Helger (www.helger.com)
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
package com.helger.commons.mock;

import javax.annotation.Nonnull;

import com.helger.commons.collection.ext.CommonsArrayList;
import com.helger.commons.collection.ext.ICommonsList;
import com.helger.commons.collection.multimap.MultiTreeMapTreeSetBased;

/**
 * Special helper for SPI in OSGI - see
 * http://blog.osgi.org/2013/02/javautilserviceloader-in-osgi.html
 *
 * @author Philip Helger
 */
public final class SPIOSGIHelper
{
  private SPIOSGIHelper ()
  {}

  @Nonnull
  public static String getAllSPIImplementationsForOSGIString () throws Exception
  {
    final ICommonsList <String> aRequireC = new CommonsArrayList<> ();
    final ICommonsList <String> aProvideC = new CommonsArrayList<> ();

    // ServiceLoader provider
    final MultiTreeMapTreeSetBased <String, String> aImpls = SPITestHelper.testIfAllMainSPIImplementationsAreValid (false);
    if (aImpls.isNotEmpty ())
    {
      aRequireC.add ("osgi.extender; filter:=\"(osgi.extender=osgi.serviceloader.registrar)\"");
      aImpls.forEachSingleValue (x -> aProvideC.add ("osgi.serviceloader; osgi.serviceloader=" + x));
    }

    // Scan over all classes and check if they have the @IsSPIInterface
    // annotation

    // The consumer may contain code similar to:
    // ServiceLoader<MySPI> ldr = ServiceLoader.load(MySPI.class);
    // for (MySPI spiObject : ldr) {
    // spiObject.doit(); // invoke the SPI object
    // }
    //
    // In the META-INF/MANIFEST.MF, the consumer bundle has:
    // Require-Capability: osgi.extender;
    // filter:="(osgi.extender=osgi.serviceloader.processor)",
    // osgi.serviceloader;
    // filter:="(osgi.serviceloader=foo.bar.MySPI)"; cardinality:=multiple

    // Build felix bundle string
    final StringBuilder ret = new StringBuilder ();
    if (aRequireC.isNotEmpty ())
    {
      ret.append ("            <Require-Capability>\n");
      aRequireC.forEach ( (x, idx) -> {
        if (idx > 0)
          ret.append (",\n");
        ret.append ("              ").append (x);
      });
      ret.append ("\n            </Require-Capability>\n");
    }
    if (aProvideC.isNotEmpty ())
    {
      ret.append ("            <Provide-Capability>\n");
      aProvideC.forEach ( (x, idx) -> {
        if (idx > 0)
          ret.append (",\n");
        ret.append ("              ").append (x);
      });
      ret.append ("\n            </Provide-Capability>\n");
    }
    return ret.toString ();
  }
}
