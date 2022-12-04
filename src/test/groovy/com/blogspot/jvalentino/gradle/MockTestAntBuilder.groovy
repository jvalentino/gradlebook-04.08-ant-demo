package com.blogspot.jvalentino.gradle

import org.gradle.api.AntBuilder
import org.gradle.api.Transformer

abstract class MockTestAntBuilder extends AntBuilder {

    void echo(String value) {
        
    }
    
    void xslt(def closure) {
        
    }
}
