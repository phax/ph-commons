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
package com.helger.commons.gfx;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.helger.commons.io.resource.ClassPathResource;

/**
 * Test class for class {@link ImageDataManager}.
 *
 * @author Philip Helger
 */
public final class ImageDataManagerTest
{
  @Test
  public void testGetImageSize ()
  {
    assertNull (ImageDataManager.getInstance ().getImageSize (null));

    // Valid file
    ClassPathResource aRes = new ClassPathResource ("gfx/bullet_green.gif");
    assertNotNull (ImageDataManager.getInstance ().getImageSize (aRes));
    assertNotNull (ImageDataManager.getInstance ().getImageSize (aRes));

    // Not an image
    aRes = new ClassPathResource ("xml/buildinfo.xml");
    assertNull (ImageDataManager.getInstance ().getImageSize (aRes));
    assertNull (ImageDataManager.getInstance ().getImageSize (aRes));

    // Non existing file
    aRes = new ClassPathResource ("gfx/non_existing_file");
    assertNull (ImageDataManager.getInstance ().getImageSize (aRes));
    assertNull (ImageDataManager.getInstance ().getImageSize (aRes));
  }
}
