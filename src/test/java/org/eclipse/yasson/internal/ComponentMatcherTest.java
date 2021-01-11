package org.eclipse.yasson.internal;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComponentMatcherTest {
  @Test
  public void testGetInterfaces(){
    assertEquals(Collections.emptyList(), ComponentMatcher.getInterfaces(TestClass.class));

    assertEquals(List.of(TestInterfaceA.class), ComponentMatcher.getInterfaces(TestClassA.class));
    assertEquals(List.of(TestInterfaceB.class, TestInterfaceA.class), ComponentMatcher.getInterfaces(TestClassB.class));
    assertEquals(List.of(TestInterfaceC.class, TestInterfaceB.class, TestInterfaceA.class), ComponentMatcher.getInterfaces(TestClassC.class));
    assertEquals(List.of(TestInterfaceB.class, TestInterfaceD.class, TestInterfaceA.class), ComponentMatcher.getInterfaces(TestClassBD.class));
  }

  @Test
  public void testGetSuperclasses(){
    assertEquals(List.of(Object.class), ComponentMatcher.getSuperclasses(TestClass.class));
    assertEquals(List.of(TestClass.class, Object.class), ComponentMatcher.getSuperclasses(TestSubclass.class));
    assertEquals(List.of(TestSubclass.class, TestClass.class, Object.class), ComponentMatcher.getSuperclasses(TestSubSubclass.class));
  }

  interface TestInterfaceA {}
  interface TestInterfaceB extends TestInterfaceA {}
  interface TestInterfaceC extends TestInterfaceB {}
  interface TestInterfaceD {}

  class TestClass {}
  class TestSubclass extends TestClass {}
  class TestSubSubclass extends TestSubclass {}
  class TestClassA implements TestInterfaceA {}
  class TestClassB implements TestInterfaceB {}
  class TestClassC implements TestInterfaceC {}
  class TestClassBD implements TestInterfaceB, TestInterfaceD {}
}
