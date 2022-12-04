package com.blogspot.jvalentino.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.TaskAction

/**
 * <p>Uses XSLT</p>
 * @author jvalentino2
 */
@SuppressWarnings(['Println'])
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
