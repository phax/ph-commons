/**
 * Copyright (C) 2014 Philip Helger (www.helger.com)
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
package com.helger.commons.bmx;

import javax.annotation.concurrent.Immutable;

import com.helger.commons.annotations.PresentForCodeCoverage;

@Immutable
public final class CBMXIO
{
  /** Version number of format v1 - must be 4 bytes, all ASCII! */
  public static final byte [] VERSION1 = new byte [] { 'B', 'M', 'X', '1' };

  public static final int NODETYPE_CDATA = 0x01;
  public static final int NODETYPE_COMMENT = 0x02;
  public static final int NODETYPE_CONTAINER = 0x03;
  public static final int NODETYPE_DOCUMENT = 0x04;
  public static final int NODETYPE_DOCUMENT_TYPE = 0x05;
  public static final int NODETYPE_ELEMENT = 0x06;
  public static final int NODETYPE_ENTITY_REFERENCE = 0x07;
  public static final int NODETYPE_PROCESSING_INSTRUCTION = 0x08;
  public static final int NODETYPE_TEXT = 0x09;
  public static final int NODETYPE_STRING = 0x0a;
  public static final int SPECIAL_CHILDREN_START = 0x7b;
  public static final int SPECIAL_CHILDREN_END = 0x7d;
  public static final int NODETYPE_EOF = 0xff;

  @SuppressWarnings ("unused")
  @PresentForCodeCoverage
  private static final CBMXIO s_aInstance = new CBMXIO ();

  /** The string table index for null strings */
  public static final int INDEX_NULL_STRING = 0;

  private CBMXIO ()
  {}
}
