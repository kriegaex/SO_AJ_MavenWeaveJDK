package de.scrum_master.aspect;

public aspect ClassLoaderAspect {
  before(String className):
    execution(* ClassLoader+.loadClass(String, ..)) &&
      args(className, ..) &&      // Bind class name to advice parameter
      !within(ClassLoader) &&     // Avoid AspectJ errors when initialising ClassLoader
      !cflow(adviceexecution())   // Avoid recursion, e.g. when Surefire tries to capture output to System.out
    {
      System.out.println(thisJoinPoint + " -> " + className);
    }
}
