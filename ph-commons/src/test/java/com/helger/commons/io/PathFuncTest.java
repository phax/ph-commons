package com.helger.commons.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Test;

import com.helger.commons.system.EOperatingSystem;

/**
 * Playground for {@link Path} testing
 *
 * @author Philip Helger
 */
public final class PathFuncTest
{
  private static final char SEP = File.separatorChar;
  private static final boolean WIN = EOperatingSystem.getCurrentOS ().isWindowsBased ();

  @Test
  public void testSimple () throws IOException
  {
    final Path p = Paths.get ("pom.xml");
    assertFalse (p.isAbsolute ());
    assertTrue (Files.exists (p));
    assertTrue (Files.exists (p.normalize ()));
    assertFalse (Files.isDirectory (p));
    if (!WIN)
      assertFalse (Files.isExecutable (p));
    assertFalse (Files.isHidden (p));
    assertTrue (Files.isReadable (p));
    assertTrue (Files.isRegularFile (p));
    assertTrue (Files.isSameFile (p, p));
    assertFalse (Files.isSymbolicLink (p));
    assertTrue (Files.isWritable (p));
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
    final Path p = Paths.get ("../ph-commons/pom.xml");
    assertFalse (p.isAbsolute ());
    assertTrue (Files.exists (p));
    assertTrue (Files.exists (p.normalize ()));
    assertFalse (Files.isDirectory (p));
    if (!WIN)
      assertFalse (Files.isExecutable (p));
    assertFalse (Files.isHidden (p));
    assertTrue (Files.isReadable (p));
    assertTrue (Files.isRegularFile (p));
    assertTrue (Files.isSameFile (p, p));
    assertFalse (Files.isSymbolicLink (p));
    assertTrue (Files.isWritable (p));
    assertEquals (".." + SEP + "ph-commons" + SEP + "pom.xml", p.toString ());
    assertEquals (".." + SEP + "ph-commons" + SEP + "pom.xml", p.normalize ().toString ());

    // C:\Users\xxx\git\ph-commons\ph-commons\pom.xml
    assertTrue ("Is <" +
                p.toRealPath ().toString () +
                ">",
                p.toRealPath ().toString ().endsWith (SEP + "ph-commons" + SEP + "pom.xml"));

    // C:\Users\xxx\git\ph-commons\ph-commons\..\ph-commons\pom.xml
    assertTrue ("Is <" +
                p.toAbsolutePath ().toString () +
                ">",
                p.toAbsolutePath ()
                 .toString ()
                 .endsWith (SEP + "ph-commons" + SEP + ".." + SEP + "ph-commons" + SEP + "pom.xml"));
  }

  @Test
  public void testTraverse2 () throws IOException
  {
    final Path p = Paths.get ("cde/../pom.xml");
    assertFalse (p.isAbsolute ());
    if (WIN)
    {
      assertTrue (Files.exists (p));
    }
    else
    {
      assertFalse (Files.exists (p));
    }
    assertTrue (Files.exists (p.normalize ()));
    assertFalse (Files.isDirectory (p));
    if (!WIN)
      assertFalse (Files.isExecutable (p));
    assertFalse (Files.isHidden (p));
    if (WIN)
    {
      assertTrue (Files.isReadable (p));
      assertTrue (Files.isReadable (p.normalize ()));
      assertTrue (Files.isRegularFile (p));
      assertTrue (Files.isRegularFile (p.normalize ()));
    }
    else
    {
      assertFalse (Files.isReadable (p));
      assertTrue (Files.isReadable (p.normalize ()));
      assertFalse (Files.isRegularFile (p));
      assertTrue (Files.isRegularFile (p.normalize ()));
    }
    assertTrue (Files.isSameFile (p, p));
    assertFalse (Files.isSymbolicLink (p));
    if (WIN)
    {
      assertTrue (Files.isWritable (p));
      assertTrue (Files.isWritable (p.normalize ()));
    }
    else
    {
      assertFalse (Files.isWritable (p));
      assertTrue (Files.isWritable (p.normalize ()));
    }
    assertEquals ("cde" + SEP + ".." + SEP + "pom.xml", p.toString ());
    assertEquals ("pom.xml", p.normalize ().toString ());

    // C:\Users\xxx\git\ph-commons\ph-commons\pom.xml
    assertTrue ("Is <" +
                p.toRealPath ().toString () +
                ">",
                p.toRealPath ().toString ().endsWith (SEP + "ph-commons" + SEP + "pom.xml"));

    // C:\Users\xxx\git\ph-commons\ph-commons\pom.xml
    assertTrue ("Is <" +
                p.toAbsolutePath ().toString () +
                ">",
                p.toAbsolutePath ()
                 .toString ()
                 .endsWith (SEP + "ph-commons" + SEP + "cde" + SEP + ".." + SEP + "pom.xml"));
  }
}
