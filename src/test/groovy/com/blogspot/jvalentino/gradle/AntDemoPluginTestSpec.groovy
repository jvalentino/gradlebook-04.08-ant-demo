package com.blogspot.jvalentino.gradle

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder

import spock.lang.Specification
import spock.lang.Subject

class AntDemoPluginTestSpec extends Specification {

    Project project
    @Subject
    AntDemoPlugin plugin

    def setup() {
        project = ProjectBuilder.builder().build()
        plugin = new AntDemoPlugin()
    }

    void "test plugin"() {
        when:
        plugin.apply(project)

        then:
        project.tasks.getAt(0).toString() == "task ':xslt'"
        project.getExtensions().findByType(XsltExtension) != null
    }
}
