/**
 * Copyright (C) 2014-2018 Philip Helger (www.helger.com)
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
package com.helger.commons.serialize.convert;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.imageio.ImageIO;

import com.helger.commons.annotation.IsSPIImplementation;
import com.helger.commons.charset.CharsetHelper;
import com.helger.commons.io.stream.StreamHelper;

/**
 * Implementation of {@link ISerializationConverterRegistrarSPI} for basic types
 * like Charset etc.
 *
 * @author Philip Helger
 */
@Immutable
@IsSPIImplementation
public final class BasicSerializationConverterRegistrar implements ISerializationConverterRegistrarSPI
{

  private static final class SerializationConverterBufferedImage implements ISerializationConverter <BufferedImage>
  {
    public void writeConvertedObject (@Nonnull final BufferedImage aSourceObject,
                                      @Nonnull final ObjectOutputStream aOOS) throws IOException
    {
      ImageIO.write (aSourceObject, "png", aOOS);
    }

    public BufferedImage readConvertedObject (@Nonnull final ObjectInputStream aOIS) throws IOException
    {
      return ImageIO.read (aOIS);
    }
  }

  private static final class SerializationConverterCharset implements ISerializationConverter <Charset>
  {
    public void writeConvertedObject (@Nonnull final Charset aSourceObject,
                                      @Nonnull final ObjectOutputStream aOOS) throws IOException
    {
      StreamHelper.writeSafeUTF (aOOS, aSourceObject.name ());
    }

    public Charset readConvertedObject (@Nonnull final ObjectInputStream aOIS) throws IOException
    {
      final String sCharsetName = StreamHelper.readSafeUTF (aOIS);
      return CharsetHelper.getCharsetFromName (sCharsetName);
    }
  }

  public void registerSerializationConverter (@Nonnull final ISerializationConverterRegistry aRegistry)
  {
    aRegistry.registerSerializationConverter (BufferedImage.class, new SerializationConverterBufferedImage ());
    aRegistry.registerSerializationConverter (Charset.class, new SerializationConverterCharset ());
  }
}
