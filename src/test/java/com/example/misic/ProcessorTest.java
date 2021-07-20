package com.example.misic;

import com.example.misic.anno.ImportProcessor;
import com.google.testing.compile.Compilation;
import com.google.testing.compile.JavaFileObjects;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static com.google.testing.compile.CompilationSubject.assertThat;
import static com.google.testing.compile.Compiler.javac;

@Slf4j
public class ProcessorTest {

    @Test
    public void testCompile() {
        Compilation compilation = javac()
            .withProcessors(new ImportProcessor())
            .compile(JavaFileObjects.forResource("java/X.java"));
        assertThat(compilation).succeeded();
    }
}
