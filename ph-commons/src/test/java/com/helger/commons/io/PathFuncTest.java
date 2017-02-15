package com.helger.commons.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

/**
 * Playground for {@link Path} testing
 *
 * @author Philip Helger
 */
public final class PathFuncTest
{
  private static final char SEP = File.separatorChar;

  @Test
  public void testSimple () throws IOException
  {
    final Path p = Paths.get ("pom.xml");
    assertEquals ("pom.xml", p.toString ());
    assertEquals ("pom.xml", p.normalize ().toString ());

    // C:\Users\xxx\git\ph-commons\ph-commons\pom.xml
    assertTrue (p.toRealPath ().toString ().endsWith (SEP + "ph-commons" + SEP + "pom.xml"));

    // C:\Users\xxx\git\ph-commons\ph-commons\pom.xml
    assertTrue (p.toAbsolutePath ().toString ().endsWith (SEP + "ph-commons" + SEP + "pom.xml"));
  }

  @Test
  public void testTraverse1 () throws IOException
  {
    final Path p = Paths.get ("cde/../pom.xml");
    assertEquals ("cde" + SEP + ".." + SEP + "pom.xml", p.toString ());
    assertEquals ("pom.xml", p.normalize ().toString ());

    // C:\Users\xxx\git\ph-commons\ph-commons\pom.xml
    assertTrue (p.toRealPath ().toString ().endsWith (SEP + "ph-commons" + SEP + "pom.xml"));

    // C:\Users\xxx\git\ph-commons\ph-commons\pom.xml
    assertTrue (p.toAbsolutePath ().toString ().endsWith (SEP + "ph-commons" + SEP + "cde" + SEP + ".." + SEP + "pom.xml"));
  }
}
