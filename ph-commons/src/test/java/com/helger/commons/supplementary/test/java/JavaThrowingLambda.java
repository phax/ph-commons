package com.helger.commons.supplementary.test.java;

import java.io.EOFException;
import java.io.IOException;

public class JavaThrowingLambda
{
  @FunctionalInterface
  public static interface IDummy
  {
    void foo () throws IOException;
  }

  public static void main (final String [] args) throws IOException
  {
    final IDummy x = () -> {
      throw new EOFException ();
    };
    x.foo ();
  }
}
