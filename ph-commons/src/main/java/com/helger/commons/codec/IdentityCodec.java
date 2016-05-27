package com.helger.commons.codec;

import javax.annotation.Nullable;

/**
 * Special implementation of {@link ICodec} that does nothing. This is a
 * separate class to be able to identify it from non-identity codecs.
 *
 * @author Philip Helger
 * @param <DATATYPE>
 *        Codec data type
 */
public class IdentityCodec <DATATYPE> implements ICodec <DATATYPE>
{
  @Nullable
  public DATATYPE getEncoded (@Nullable final DATATYPE aInput)
  {
    return aInput;
  }

  @Nullable
  public DATATYPE getDecoded (@Nullable final DATATYPE aInput)
  {
    return aInput;
  }
}
