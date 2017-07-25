package com.helger.commons.traits;

public interface IPrimitiveConverterTo <DSTTYPE>
{
  DSTTYPE convert (boolean value);

  DSTTYPE convert (byte value);

  DSTTYPE convert (char value);

  DSTTYPE convert (double value);

  DSTTYPE convert (float value);

  DSTTYPE convert (int value);

  DSTTYPE convert (long value);

  DSTTYPE convert (short value);

  DSTTYPE convert (Object value);
}