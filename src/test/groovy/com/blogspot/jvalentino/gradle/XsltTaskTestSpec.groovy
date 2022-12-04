package com.blogspot.jvalentino.gradle

import org.gradle.api.Project
import org.gradle.api.internal.plugins.ExtensionContainerInternal
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.api.AntBuilder

import spock.lang.Specification
import spock.lang.Subject

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
}
