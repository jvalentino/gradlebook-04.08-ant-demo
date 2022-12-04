package com.blogspot.jvalentino.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <p>A basic gradle plugin.</p>
 * @author jvalentino2
 */
class AntDemoPlugin implements Plugin<Project> {
    void apply(Project project) {
        String xslt = 'xslt'
        project.extensions.create xslt, XsltExtension
        project.task(xslt, type:XsltTask)
    }
}
