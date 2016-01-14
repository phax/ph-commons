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
package com.helger.commons.lang;

/**
 * This class needs to be in the same package as {@link CloneHelper} so that the
 * test works!<br>
 * Should fail because the copy constructor is private
 *
 * @author Philip Helger
 */
public final class MockWithPrivateCopyCtor
{
  public MockWithPrivateCopyCtor ()
  {
    // just a dummy call so that the copy constructor is not omitted from the
    // generated code
    new MockWithPrivateCopyCtor (this);
  }

  private MockWithPrivateCopyCtor (@SuppressWarnings ("unused") final MockWithPrivateCopyCtor ex)
  {}
}
