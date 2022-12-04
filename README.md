## 4.8 Ant

 Gradle comes integrated with Ant, which allows Ant to be directly invokes as a part of any Gradle build or plugin. The difficult with Ant integration though, it how one goes about dealing with it in a unit test. The purpose of this plugin is to demonstrate how to make calls to Ant within a Gradle plugin, and how to handle that Ant integration in a unit test. This particular plugin uses Ant’s XSLT function to transform an XML document into HTML. 

 

#### Ant XSLT

```xml
<xslt 
    in="doc.xml" 
    out="build/doc/output.xml"
    style="style/apache.xsl">
</xslt>

```



The basic usage of XSLT in Ant is to specify the input document, output document, and XSL specification in an XML node.

#### src/main/groovy/com/blogspot/jvalenitno/gradle/XsltTask.groovy

```groovy
class XsltTask extends DefaultTask {

    protected XsltTask instance = this

    @TaskAction
    void perform() {
        Project p = instance.project

        XsltExtension ex =
                p.extensions.findByType(XsltExtension)

        p.ant.echo("Using Ant to turn ${ex.input} and " +
                "${ex.style} into ${ex.output}")
        p.ant.xslt(in:ex.input, out:ex.output, style:ex.style)
    }
}

```

**Line 18: The project**

The technique of accessing the **project** through the instance member variable is used, so that we can later replace the **project** instance with a mock.

 

**Lines 20-21: The extension**

The standard mechanism for looking up the extension is used, but through the earlier setup **project** variable. This is so when testing the extension container can be mocked to return an extension instance configured within the test case.

 

**Lines 23-24: echo**

It is recommended to access Ant via the **project** variable, as it can and has already been setup to that it can be mocked in a test. The **echo** function within Ant is used to write directly to the command-line, to notate what this task is doing.

 

**Lines 25: XSLT**

Calls Ant’s XSLT function, passing in the “in”, “out”, and “style” parameters form the extension.

 

#### src/main/groovy/com/blogspot/jvalenitno/gradle/XsltExtension.groovy

```groovy
class XsltExtension {
    String input
    String output
    String style
}

```

The extension is a means for passing in the XML input, the HTML output, and the XSD document within the using build.gradle.

 

#### plugin-tests/local/input.xml

```xml
<root>
    <why>I need something do to in Ant</why>
    <what>A demonstration of Gradle and Ant</what>
    <when>Now</when>
    <how>Using Gradle</how>
</root>

```

For testing purposes, a simple XML docment is used with the intention of writing an XSL to handle transforming that XML into HTML.

#### plugin-tests/local/input.xsl

```xml
<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<html>
			<body>
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>
	<xsl:template match="why">
		Why:
		<xsl:value-of select="." />
		<br />
	</xsl:template>
	<xsl:template match="what">
		What:
		<xsl:value-of select="." />
		<br />
	</xsl:template>
	<xsl:template match="when">
		When:
		<xsl:value-of select="." />
		<br />
	</xsl:template>
	<xsl:template match="how">
		How:
		<xsl:value-of select="." />
		<br />
	</xsl:template>

</xsl:stylesheet>

```

The XSL document uses a series of statements for looking up nodes within the applying XML document, and writing their outputs in HTML format.

 

#### plugin-tests/local/build.gradle

```groovy
buildscript {
  repositories {
	jcenter()
  }
  dependencies {
    classpath 'com.blogspot.jvalentino.gradle:ant-demo:1.0.0'
  }
}

apply plugin: 'ant-demo'

xslt {
    input = 'input.xml'
    output = 'build/output.html'
    style = 'input.xsl'
}


```

**Lines 1-10: Standard plugin usage**

Considering that the **settings.gradle** file references the plugin project, so that it is already on the classpath, the plugin can be directly applied.

 

**Lines 12-16: The extension**

This is where our input XML, output HTML, and style XSL are specified to the plugin.

 

#### Manual Testing

```bash
plugin-tests/local$ gradlew xslt

> Task :xslt 
[ant:echo] Using Ant to turn input.xml and input.xsl into build/output.html


BUILD SUCCESSFUL

```

When executing the **xslt** task of the plugin, Ant’s **echo** function is used to print to the command-line what happened.

 

#### plugin-tests/local/build/output.html

```html
<html>
<body>
    
		Why:
		I need something do to in Ant<br>
    
		What:
		A demonstration of Gradle and Ant<br>
    
		When:
		Now<br>
    
		How:
		Using Gradle<br>

</body>
</html>


```

The result of the XSLT transformation is this HTML document, which displays the **why**, **what**, **when**, and **how** nodes of the original XML document.

 

#### src/test/groovy/com/blogspot/jvalenitno/gradle/MockTestAntBuilder.groovy

```groovy
abstract class MockTestAntBuilder extends AntBuilder {

    void echo(String value) {
        
    }
    
    void xslt(def closure) {
        
    }
}

```

From the compile-time perspective, Gradle’s uses an Ant object where the various functions are not checked; they are dynamic. At runtime the Ant object within Gradle is enhanced, to contain the needed Ant methods. Inside the debugger at runtime this is seen as **AntBuilderEnhanced**, when inspecting **project.ant**. 

 

The enhancement of the **AntBuilder** class at runtime means:

·   **AntBuilder** cannot be mocked using Mock or GroovyMock, and doing so will result in a **ClassCastException**

·   Creating a class that extends **AntBuilder** isn’t enough, as the Ant methods we are calling do not exist

 

The strategy for mocking Ant is to declare an abstract class that extends AntBuilder, which defines empty methods representing the calls that are being made. The use of an abstract class is so that we don’t have to declare the dozens of required methods. We then only have to define the methods that are being uses, which for our custom task is **echo** and **xlst**.

 

#### src/test/groovy/com/blogspot/jvalenitno/gradle/XsltTaskTestSpec.groovy

```groovy
class XsltTaskTestSpec extends Specification {

    @Subject
    XsltTask task
    Project project
    ExtensionContainer extensions
    
    def setup() {
        Project p = ProjectBuilder.builder().build()
        task = p.task('xslt', type:XsltTask)
        task.instance = Mock(XsltTask)
        project = Mock(ProjectInternal)
        extensions = Mock(ExtensionContainerInternal)
    }

```

**Lines 15-25: Standard setup**

The task that is the subject of the test with the project and extension container are kept as members, as they will be used in most every test case. The standard instantiation strategy in then used to get the task instance, and to replace self-reference with a mock.

```groovy
    void "test perform"() {
        given:
        XsltExtension ex = new XsltExtension(
            input:'foo.xml', output:'bar.html', style:'style.xsl')
        AntBuilder ant = Mock(MockTestAntBuilder)
        
        when:
        task.perform()
        
        then:
        1 * task.instance.project >> project
        1 * project.extensions >> extensions
        1 * extensions.findByType(XsltExtension) >> ex
        
        and:
        2 * project.ant >> ant
        1 * ant.echo("Using Ant to turn ${ex.input} " + 
            "and ${ex.style} into ${ex.output}")
        1 * ant.xslt(_) >> { def closure ->
             assert closure.toString() == 
                 '[[in:foo.xml, out:bar.html, style:style.xsl]]'
        }
    }

```

**Lines 30-31: Extension**

Since the interaction with Ant and its usage of XSLT are via a mock, there is not any interaction with the file system in terms of input and output. For this reason, the values given to the extension don’t matter, except in their usage in **ant.echo**.

 

**Line 32: AntBuilder**

A mock is created using our **MockTestAntBuilder** class, so that we are able to handle the later method calls to **ant.echo** and **ant.xslt**.

 

**Lines 34-35: Perform**

Handles executing the task under test.

 

**Lines 38-40: Extension mocking**

The standard strategy is used for using the mocked project to return the mocked extension container, to then return an instance of the custom extension.

 

**Line 43: Returning ant**

Since the method under test calls both **ant.echo** and **ant.xslt,** there are two calls to ant. This is why there are to mock returns expected, which both return the previously mocked version of **AntBuilder**.

 

**Line 44: Ant echo**

The assertion is that a single call is made to **ant.echo**, with the test showing the inputs and output.

 

**Lines 46-48: Ant xstl**

The input to **ant.xslt** can be represented as a generic closure. As a part of the mock return, if a closure is used that closure can be used to capture the parameters given to the original method call. It is this captures input closure that is used to assert the parameters names and values that were used to call **ant.xslt**.



