/**
 * Copyright (C) 2006-2014 phloc systems (www.phloc.com)
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
    assertNull (ImageDataManager.getImageSize (null));

    // Valid file
    ClassPathResource aRes = new ClassPathResource ("gfx/bullet_green.gif");
    assertNotNull (ImageDataManager.getImageSize (aRes));
    assertNotNull (ImageDataManager.getImageSize (aRes));

    // Not an image
    aRes = new ClassPathResource ("xml/buildinfo.xml");
    assertNull (ImageDataManager.getImageSize (aRes));
    assertNull (ImageDataManager.getImageSize (aRes));

    // Non existing file
    aRes = new ClassPathResource ("gfx/non_existing_file");
    assertNull (ImageDataManager.getImageSize (aRes));
    assertNull (ImageDataManager.getImageSize (aRes));
  }
}
