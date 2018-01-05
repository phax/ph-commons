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
package com.helger.commons.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helger.commons.equals.EqualsHelper;
import com.helger.commons.io.file.PathHelper;
import com.helger.commons.system.EOperatingSystem;

/**
 * Playground for {@link Path} testing
 *
 * @author Philip Helger
 */
public final class PathFuncTest
{
  private static final Logger s_aLogger = LoggerFactory.getLogger (PathFuncTest.class);
  private static final char SEP = File.separatorChar;
  private static final boolean WIN = EOperatingSystem.getCurrentOS ().isWindowsBased ();

  @Test
  public void testSimple () throws IOException
  {
    final Path p = Paths.get ("pom.xml");
    assertTrue (EqualsHelper.equals (p, Paths.get ("pom.xml")));
    assertTrue (EqualsHelper.equals (p, Paths.get ("../ph-commons/pom.xml")));
    assertTrue (EqualsHelper.equals (p.toRealPath (), Paths.get ("../ph-commons/pom.xml").toRealPath ()));

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

    assertEquals (3, p.getNameCount ());
    assertEquals ("..", p.getName (0).toString ());
    assertEquals ("ph-commons", p.getName (1).toString ());
    assertEquals ("pom.xml", p.getName (2).toString ());
    assertEquals ("pom.xml", p.getFileName ().toString ());
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

    if (WIN)
    {
      // C:\Users\xxx\git\ph-commons\ph-commons\pom.xml
      assertTrue ("Is <" +
                  p.toRealPath ().toString () +
                  ">",
                  p.toRealPath ().toString ().endsWith (SEP + "ph-commons" + SEP + "pom.xml"));
    }
    else
    {
      // Gives "cde/../pom.xml" on Linux
      try
      {
        p.toRealPath ();
        fail ();
      }
      catch (final NoSuchFileException ex)
      {
        // expected
      }
    }

    // C:\Users\xxx\git\ph-commons\ph-commons\pom.xml
    assertTrue ("Is <" +
                p.toAbsolutePath ().toString () +
                ">",
                p.toAbsolutePath ()
                 .toString ()
                 .endsWith (SEP + "ph-commons" + SEP + "cde" + SEP + ".." + SEP + "pom.xml"));
  }

  @Test
  public void testIterate1 () throws IOException
  {
    final Path p = Paths.get ("pom.xml").toRealPath ();
    final Path aStartPath = p.getParent ();
    assertNotNull (aStartPath);
    final int nNames = aStartPath.getNameCount ();
    Files.walkFileTree (aStartPath, EnumSet.noneOf (FileVisitOption.class), 2, new SimpleFileVisitor <Path> ()
    {
      @Override
      public FileVisitResult visitFile (final Path aCurFile, final BasicFileAttributes attrs) throws IOException
      {
        s_aLogger.info (aCurFile.subpath (nNames, aCurFile.getNameCount ()).toString ());
        return FileVisitResult.CONTINUE;
      }
    });
    s_aLogger.info (PathHelper.getDirectoryContent (aStartPath).toString ());
  }
}
