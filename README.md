# Weaving JDK classes with AspectJ

This demo project shows how to weave aspects into JDK/JRE classes with a Maven build. The basic steps are:

1. Put `rt.jar` in the _inpath_ for the AspectJ compiler _ajc_. This results in binary weaving, i.e. _ajc_
   creates new class files, many of which will be unchanged JRE files and some will contain woven aspect code.
   The aspects themselves will also be compiled into the target directory, of course.
2. Package all class files (JRE + aspect files) into a new JAR which will from now on serve as a replacement
   for the original `rt.jar`.
3. Start the JVM with the modified `rt.jar` and `aspectjrt.jar` on the boot classpath (normal classpath is
   not enough!), either by replacing the original boot classpath altogether with `-Xbootclasspath:<path>`
   or by just prepending the two JARs to the usual boot classpath with `-Xbootclasspath/p:<path>`.

What you can do to the JRE/JDK with AspectJ basically encompasses the full feature set. In some cases you
just need to be careful not to create bootstrapping or recursion problems in situations when aspects use JRE
code which in turn triggers aspect code etc.

Now you could do steps 1-3 manually from your command line or IDE, but I think it is much nicer to have a
standard Maven build for it. So I prepared a little sample project with a main module and two sub-modules.
One submodules takes care of weaving aspects into `rt.jar`, the other one is just a demo showing how to use
the woven JRE from
* unit tests via _Maven Surefire_ (you can use it the same way for integration tests via _Maven Failsafe_),
* applications (via _Exec Maven_ plugin).

## How to use

Just call `mvn install`. First Maven will build the woven JAR, then run unit tests and subsequently run the
demo application, both of which will only work if JRE weaving was successful.

## Where to look

If you want to learn how to use this approach for your own purposes, you may want to look at:
* [StringExtenderAspect](jdk-weaver/src/main/java/de/scrum_master/aspect/StringExtenderAspect.aj): adds a
  new method to JDK class `String`
* [ClassLoaderAspect](jdk-weaver/src/main/java/de/scrum_master/aspect/ClassLoaderAspect.aj): adds some log
  output to `ClassLoader.loadClass(..)` and overriding methods
* [POM for JDK weaver module](jdk-weaver/pom.xml): weaves aspects into `rt.jar` and creates a new, woven JAR
* [POM for application module](application/pom.xml): uses woven JAR for running tests and the demo application
* [Parent POM](pom.xml): preconfigures _AspectJ Maven_, _MavenDependency_ and other plugins. E.g. in the
  _Maven Dependency_ configuration you can see how to use `<goal>properties</goal>` in order to expose
  dependency paths as Maven properties. The latter are referred to from the application POM in order to build
  the boot classpath for tests and application.
* [Sample unit test](application/src/test/java/de/scrum_master/app/WovenJDKTest.java): checks if JRE weaving
  was indeed successful
* [Sample application](application/src/main/java/de/scrum_master/app/Application.java): uses method
  `String.repeat(int)` introduced by aspect.

## Possible improvements

Instead of zipping up all class files into a woven _rt.jar_ version, it would be better and more efficient
to just package the JRE files which have actually been modified by the AspectJ compiler. Unfortunately there
is no compiler option to avoid writing unchanged binaries to the target directory. Andy Clement, if you are
reading this, please consider adding this feature for an upcoming AspectJ release. Because if we had that
feature, we could just create small JARs containing modified class files + aspects and **prepend** them to
the boot classpath. This is like the normal JRE wearing glasses: Changed class files would be found in the
small JAR, original JRE files would be found in the original _rt.jar_. Of course, you can do this manually
by looking at the weave info on the console during the build and then zip up just the minimal number of
class files into the woven _rt.jar_ version. I have tried and it works beautifully. I just have not found an
elegant way of automating this in a Maven build via resource filtering or whatever.
