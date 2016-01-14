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
package com.helger.commons.convert;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * An implementation of {@link IConverter} that extracts the value from a
 * Map.Entry.
 *
 * @author Philip Helger
 * @param <KEYTYPE>
 *        Map key type
 * @param <VALUETYPE>
 *        Map value type
 */
public class ConverterMapEntryValue <KEYTYPE, VALUETYPE>
                                    implements IConverter <Map.Entry <KEYTYPE, VALUETYPE>, VALUETYPE>
{
  @Nullable
  public VALUETYPE apply (@Nullable final Map.Entry <KEYTYPE, VALUETYPE> aEntry)
  {
    return aEntry == null ? null : aEntry.getValue ();
  }
}
