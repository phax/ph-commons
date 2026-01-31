/*
 * Copyright (C) 2014-2026 Philip Helger (www.helger.com)
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
package com.helger.base.rt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.base.io.nonblocking.NonBlockingByteArrayOutputStream;
import com.helger.base.io.nonblocking.NonBlockingStringReader;
import com.helger.base.io.nonblocking.NonBlockingStringWriter;

/**
 * Test class for class {@link NonBlockingProperties}.
 *
 * @author Philip Helger
 */
public final class NonBlockingPropertiesTest
{
  @Test
  public void testConstructorDefault ()
  {
    final NonBlockingProperties props = new NonBlockingProperties ();
    assertTrue (props.isEmpty ());
    assertNull (props.getDefaults ());
  }

  @Test
  public void testConstructorWithDefaults ()
  {
    final NonBlockingProperties defaults = new NonBlockingProperties ();
    defaults.setProperty ("default.key", "default.value");

    final NonBlockingProperties props = new NonBlockingProperties (defaults);
    assertTrue (props.isEmpty ());
    assertEquals (defaults, props.getDefaults ());
  }

  @Test
  public void testSetPropertyAndGetProperty ()
  {
    final NonBlockingProperties props = new NonBlockingProperties ();

    // Test setting and getting a property
    assertNull (props.setProperty ("key1", "value1"));
    assertEquals ("value1", props.getProperty ("key1"));

    // Test overwriting a property
    assertEquals ("value1", props.setProperty ("key1", "newvalue1"));
    assertEquals ("newvalue1", props.getProperty ("key1"));

    // Test getting non-existent property
    assertNull (props.getProperty ("nonexistent"));
  }

  @Test
  public void testGetPropertyWithDefaultValue ()
  {
    final NonBlockingProperties props = new NonBlockingProperties ();
    props.setProperty ("existing", "value");

    // Test getting existing property with default
    assertEquals ("value", props.getProperty ("existing", "default"));

    // Test getting non-existent property with default
    assertEquals ("default", props.getProperty ("nonexistent", "default"));

    // Test getting non-existent property with null default
    assertNull (props.getProperty ("nonexistent", null));
  }

  @Test
  public void testGetPropertyWithDefaults ()
  {
    final NonBlockingProperties defaults = new NonBlockingProperties ();
    defaults.setProperty ("default.key", "default.value");
    defaults.setProperty ("override.key", "default.override");

    final NonBlockingProperties props = new NonBlockingProperties (defaults);
    props.setProperty ("local.key", "local.value");
    props.setProperty ("override.key", "local.override");

    // Test getting property from local properties
    assertEquals ("local.value", props.getProperty ("local.key"));

    // Test getting property from defaults
    assertEquals ("default.value", props.getProperty ("default.key"));

    // Test getting overridden property (local takes precedence)
    assertEquals ("local.override", props.getProperty ("override.key"));

    // Test getting non-existent property
    assertNull (props.getProperty ("nonexistent"));
  }

  @Test
  public void testGetPropertyWithDefaultValueAndDefaults ()
  {
    final NonBlockingProperties defaults = new NonBlockingProperties ();
    defaults.setProperty ("default.key", "default.value");

    final NonBlockingProperties props = new NonBlockingProperties (defaults);

    // Test getting property from defaults with explicit default value
    assertEquals ("default.value", props.getProperty ("default.key", "fallback"));

    // Test getting non-existent property with explicit default value
    assertEquals ("fallback", props.getProperty ("nonexistent", "fallback"));
  }

  @Test
  public void testLoadFromReader () throws IOException
  {
    final String propertiesText = "key1=value1\n" +
                                  "key2 = value2\n" +
                                  "key3:value3\n" +
                                  "key4 : value4\n" +
                                  "# This is a comment\n" +
                                  "! This is also a comment\n" +
                                  "key5=value5\n" +
                                  "key_with_spaces = value with spaces\n" +
                                  "empty.key=\n" +
                                  "key.with.equals=value=with=equals\n";

    final NonBlockingProperties props = new NonBlockingProperties ();
    props.load (new NonBlockingStringReader (propertiesText));

    assertEquals ("value1", props.getProperty ("key1"));
    assertEquals ("value2", props.getProperty ("key2"));
    assertEquals ("value3", props.getProperty ("key3"));
    assertEquals ("value4", props.getProperty ("key4"));
    assertEquals ("value5", props.getProperty ("key5"));
    assertEquals ("value with spaces", props.getProperty ("key_with_spaces"));
    assertEquals ("", props.getProperty ("empty.key"));
    assertEquals ("value=with=equals", props.getProperty ("key.with.equals"));
  }

  @Test
  public void testLoadFromInputStream () throws IOException
  {
    final String propertiesText = "key1=value1\nkey2=value2\n";
    try (final NonBlockingByteArrayInputStream bais = new NonBlockingByteArrayInputStream (propertiesText.getBytes (StandardCharsets.ISO_8859_1)))
    {
      final NonBlockingProperties props = new NonBlockingProperties ();
      props.load (bais);

      assertEquals ("value1", props.getProperty ("key1"));
      assertEquals ("value2", props.getProperty ("key2"));
    }
  }

  @Test
  public void testLoadWithEscapedCharacters () throws IOException
  {
    final String propertiesText = "escaped.key\\=with\\:equals=escaped\\nvalue\\twith\\ttabs\n" +
                                  "unicode.key=\\u0048\\u0065\\u006C\\u006C\\u006F\n" +
                                  "multiline.key=line1\\\n" +
                                  "  line2\\\n" +
                                  "  line3\n";

    final NonBlockingProperties props = new NonBlockingProperties ();
    props.load (new NonBlockingStringReader (propertiesText));

    assertEquals ("escaped\nvalue\twith\ttabs", props.getProperty ("escaped.key=with:equals"));
    assertEquals ("Hello", props.getProperty ("unicode.key"));
    assertEquals ("line1line2line3", props.getProperty ("multiline.key"));
  }

  @Test
  public void testLoadWithWhitespaceAndComments () throws IOException
  {
    final String propertiesText = "   \n" +
                                  "# Comment line\n" +
                                  "   # Indented comment\n" +
                                  "! Another comment\n" +
                                  "   \n" +
                                  "key1=value1\n" +
                                  "   key2   =   value2   \n" +
                                  "\n" +
                                  "key3=value3\n";

    final NonBlockingProperties props = new NonBlockingProperties ();
    props.load (new NonBlockingStringReader (propertiesText));

    assertEquals (3, props.size ());
    assertEquals ("value1", props.getProperty ("key1"));
    // Trailing whitespace is preserved
    assertEquals ("value2   ", props.getProperty ("key2"));
    assertEquals ("value3", props.getProperty ("key3"));
  }

  @Test
  public void testLoadEmptyProperties () throws IOException
  {
    final String propertiesText = "# Only comments\n" + "! And more comments\n" + "   \n" + "\n";

    final NonBlockingProperties props = new NonBlockingProperties ();
    props.load (new NonBlockingStringReader (propertiesText));

    assertTrue (props.isEmpty ());
  }

  @Test
  public void testStoreToWriter () throws IOException
  {
    final NonBlockingProperties props = new NonBlockingProperties ();
    props.setProperty ("key1", "value1");
    props.setProperty ("key2", "value2");
    props.setProperty ("special.chars", "value with spaces");

    try (final NonBlockingStringWriter writer = new NonBlockingStringWriter ())
    {
      props.store (writer, "Test comment");

      final String result = writer.getAsString ();
      // No space after # in NonBlockingProperties
      assertTrue (result.contains ("#Test comment"));
      assertTrue (result.contains ("key1=value1"));
      assertTrue (result.contains ("key2=value2"));
      // Spaces are not escaped in values
      assertTrue (result.contains ("special.chars=value with spaces"));
      // Should contain timestamp
      assertTrue (result.contains ("#20"));
    }
  }

  @Test
  public void testStoreToOutputStream () throws IOException
  {
    final NonBlockingProperties props = new NonBlockingProperties ();
    props.setProperty ("key1", "value1");
    props.setProperty ("key2", "value2");

    try (final NonBlockingByteArrayOutputStream baos = new NonBlockingByteArrayOutputStream ())
    {
      props.store (baos, "Test comment");

      final String result = baos.getAsString (StandardCharsets.ISO_8859_1);
      // No space after # in NonBlockingProperties
      assertTrue (result.contains ("#Test comment"));
      assertTrue (result.contains ("key1=value1"));
      assertTrue (result.contains ("key2=value2"));
    }
  }

  @Test
  public void testStoreWithNullComment () throws IOException
  {
    final NonBlockingProperties props = new NonBlockingProperties ();
    props.setProperty ("key1", "value1");

    try (final NonBlockingStringWriter writer = new NonBlockingStringWriter ())
    {
      props.store (writer, null);

      final String result = writer.getAsString ();
      assertTrue (result.contains ("key1=value1"));
      // Should contain timestamp
      assertTrue (result.contains ("#20"));
      // Should not contain any comment line other than timestamp
    }
  }

  @Test
  public void testStoreWithSpecialCharacters () throws IOException
  {
    final NonBlockingProperties props = new NonBlockingProperties ();
    props.setProperty ("key with spaces", "value with spaces");
    props.setProperty ("key=with:equals", "value=with:equals");
    props.setProperty ("unicode", "Hello\u00A9World");

    try (final NonBlockingStringWriter writer = new NonBlockingStringWriter ())
    {
      props.store (writer, null);

      final String result = writer.getAsString ();
      // Keys escaped, values not
      assertTrue (result.contains ("key\\ with\\ spaces=value with spaces"));
      // Special chars escaped in values too
      assertTrue (result.contains ("key\\=with\\:equals=value\\=with\\:equals"));
      // Unicode should be preserved as-is
    }
  }

  @Test
  public void testStoreAndLoadRoundTrip () throws IOException
  {
    final NonBlockingProperties original = new NonBlockingProperties ();
    original.setProperty ("key1", "value1");
    original.setProperty ("key2", "value with spaces");
    original.setProperty ("key3", "");
    original.setProperty ("special=key", "special:value");

    // Store to string
    try (final NonBlockingStringWriter writer = new NonBlockingStringWriter ())
    {
      original.store (writer, "Round trip test");

      // Load from string
      final NonBlockingProperties loaded = new NonBlockingProperties ();
      loaded.load (new NonBlockingStringReader (writer.getAsString ()));

      // Verify all properties are preserved
      assertEquals (original.size (), loaded.size ());
      for (final Map.Entry <String, String> entry : original.entrySet ())
      {
        assertEquals (entry.getValue (), loaded.getProperty (entry.getKey ()));
      }
    }
  }

  @Test
  public void testEqualsAndHashCode ()
  {
    final NonBlockingProperties props1 = new NonBlockingProperties ();
    props1.setProperty ("key1", "value1");
    props1.setProperty ("key2", "value2");

    final NonBlockingProperties props2 = new NonBlockingProperties ();
    props2.setProperty ("key1", "value1");
    props2.setProperty ("key2", "value2");

    final NonBlockingProperties props3 = new NonBlockingProperties ();
    props3.setProperty ("key1", "value1");
    props3.setProperty ("key2", "different");

    // Test equality
    assertEquals (props1, props2);
    assertNotEquals (props1, props3);

    // Test hash code consistency
    assertEquals (props1.hashCode (), props2.hashCode ());

    // Test reflexivity
    assertEquals (props1, props1);

    // Test null inequality
    assertNotEquals (props1, null);

    // Test different class inequality
    assertNotEquals (props1, "string");
  }

  @Test
  public void testCreateFromMap ()
  {
    final Map <String, String> map = new HashMap <> ();
    map.put ("key1", "value1");
    map.put ("key2", "value2");

    final NonBlockingProperties props = NonBlockingProperties.create (map);

    assertEquals (2, props.size ());
    assertEquals ("value1", props.getProperty ("key1"));
    assertEquals ("value2", props.getProperty ("key2"));
  }

  @Test
  public void testCreateFromNullMap ()
  {
    final NonBlockingProperties props = NonBlockingProperties.create (null);
    assertTrue (props.isEmpty ());
  }

  @Test
  public void testCreateFromProperties ()
  {
    final Properties javaProps = new Properties ();
    javaProps.setProperty ("key1", "value1");
    javaProps.setProperty ("key2", "value2");

    final NonBlockingProperties props = NonBlockingProperties.create (javaProps);

    assertEquals (2, props.size ());
    assertEquals ("value1", props.getProperty ("key1"));
    assertEquals ("value2", props.getProperty ("key2"));
  }

  @Test
  public void testMapOperations ()
  {
    final NonBlockingProperties props = new NonBlockingProperties ();

    // Test put/get operations
    props.put ("key1", "value1");
    assertEquals ("value1", props.get ("key1"));

    // Test size operations
    assertEquals (1, props.size ());
    assertFalse (props.isEmpty ());

    // Test containsKey
    assertTrue (props.containsKey ("key1"));
    assertFalse (props.containsKey ("nonexistent"));

    // Test remove
    assertEquals ("value1", props.remove ("key1"));
    assertTrue (props.isEmpty ());
  }

  @Test
  public void testInheritedMapBehavior ()
  {
    final NonBlockingProperties props = new NonBlockingProperties ();

    // Test putAll
    final Map <String, String> additional = new HashMap <> ();
    additional.put ("key1", "value1");
    additional.put ("key2", "value2");
    props.putAll (additional);

    assertEquals (2, props.size ());
    assertEquals ("value1", props.getProperty ("key1"));
    assertEquals ("value2", props.getProperty ("key2"));

    // Test clear
    props.clear ();
    assertTrue (props.isEmpty ());
  }

  @Test
  public void testNestedDefaults ()
  {
    // Create a chain of defaults
    final NonBlockingProperties level3 = new NonBlockingProperties ();
    level3.setProperty ("level3.key", "level3.value");
    level3.setProperty ("override.key", "level3.override");

    final NonBlockingProperties level2 = new NonBlockingProperties (level3);
    level2.setProperty ("level2.key", "level2.value");
    level2.setProperty ("override.key", "level2.override");

    final NonBlockingProperties level1 = new NonBlockingProperties (level2);
    level1.setProperty ("level1.key", "level1.value");

    // Test property resolution through the chain
    assertEquals ("level1.value", level1.getProperty ("level1.key"));
    assertEquals ("level2.value", level1.getProperty ("level2.key"));
    assertEquals ("level3.value", level1.getProperty ("level3.key"));

    // Test that closer levels override deeper ones
    assertEquals ("level2.override", level1.getProperty ("override.key"));

    // Test non-existent property
    assertNull (level1.getProperty ("nonexistent"));
  }

  @Test
  public void testLargeProperties () throws IOException
  {
    final NonBlockingProperties props = new NonBlockingProperties ();

    // Add many properties
    for (int i = 0; i < 1000; i++)
    {
      props.setProperty ("key" + i, "value" + i);
    }

    assertEquals (1000, props.size ());

    // Test store and load with large number of properties
    try (final NonBlockingStringWriter writer = new NonBlockingStringWriter ())
    {
      props.store (writer, "Large properties test");

      final NonBlockingProperties loaded = new NonBlockingProperties ();
      loaded.load (new NonBlockingStringReader (writer.getAsString ()));

      assertEquals (1000, loaded.size ());
      for (int i = 0; i < 1000; i++)
      {
        assertEquals ("value" + i, loaded.getProperty ("key" + i));
      }
    }
  }

  @Test
  public void testEmptyValues () throws IOException
  {
    final NonBlockingProperties props = new NonBlockingProperties ();
    props.setProperty ("empty", "");
    props.setProperty ("null.like", null);

    // Test getProperty behavior with empty/null values
    assertEquals ("", props.getProperty ("empty"));
    assertNull (props.getProperty ("null.like"));

    // Test store/load behavior with empty values
    try (final NonBlockingStringWriter writer = new NonBlockingStringWriter ())
    {
      props.store (writer, null);

      final NonBlockingProperties loaded = new NonBlockingProperties ();
      loaded.load (new NonBlockingStringReader (writer.getAsString ()));

      assertEquals ("", loaded.getProperty ("empty"));
      // Note: null values are stored as "null" strings or may be handled differently
    }
  }

  @Test
  public void testLoadFromEmptyStream () throws IOException
  {
    final NonBlockingProperties props = new NonBlockingProperties ();
    props.load (new NonBlockingStringReader (""));

    assertTrue (props.isEmpty ());
  }

  @Test
  public void testStorePreservesOrder () throws IOException
  {
    final NonBlockingProperties props = new NonBlockingProperties ();

    // Add properties in specific order
    props.setProperty ("zebra", "z");
    props.setProperty ("alpha", "a");
    props.setProperty ("beta", "b");

    try (final NonBlockingStringWriter writer = new NonBlockingStringWriter ())
    {
      props.store (writer, null);

      final String result = writer.getAsString ();

      // LinkedHashMap should preserve insertion order
      final int zebraPos = result.indexOf ("zebra=z");
      final int alphaPos = result.indexOf ("alpha=a");
      final int betaPos = result.indexOf ("beta=b");

      assertTrue (zebraPos < alphaPos);
      assertTrue (alphaPos < betaPos);
    }
  }
}
