package de.scrum_master.app;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WovenJDKTest {
  @Test
  public void testRepeatString() throws Exception {
    // Check if method String.repeat(int) created by StringExtenderAspect via ITD is in fact working
    assertEquals("xoxoxo", "xo".repeat(3));
  }

  @Test
  public void testClassLoading() throws Exception {
    // Load some classes which normally are not loaded automatically by JUnit or Maven Surefire/Failsafe.
    // This should yield some log output by ClassLoaderAspect woven into JRE, similar to:
    //   execution(Class sun.misc.Launcher.AppClassLoader.loadClass(String, boolean)) -> java.awt.MenuContainer
    //   execution(Class sun.misc.Launcher.AppClassLoader.loadClass(String, boolean)) -> java.sql.Driver
    assertNotNull(Class.forName("java.awt.MenuContainer"));
    assertNotNull(Class.forName("java.sql.Driver"));
  }
}
