package com.example.javalib.b;

import com.example.processor.MethodProcessor;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

//@AutoService(Processor.class) 这个有木有？这是一个注解处理器，是Google开发的，
//用来生成META-INF/services/javax.annotation.processing.Processor文件的。
@AutoService(Processor.class)
public class MethodAbstractProcessorImp extends AbstractProcessor {

    private Types mTypeUtils;
    private Elements mElementUtils;
    private Filer mFiler;
    private Messager mMessager;
    /**
     * 指定Java版本
     * 可用注解SupportedAnnotationTypes代替
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        //把我们自己定义的注解添加进去
        Set<String> annotations = new LinkedHashSet<>();
        annotations.add(MethodProcessor.class.getCanonicalName());
        return annotations;
    }

    /**
     * 指定Java版本
     * 可用注解SupportedSourceVersion代替
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();//或者指定 版本，比如 SourceVersion.RELEASE_8
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mTypeUtils = processingEnv.getTypeUtils();
        mElementUtils = processingEnv.getElementUtils();
        mFiler = processingEnv.getFiler();
        mMessager = processingEnv.getMessager();

    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
       for(Element annotatedElement : roundEnv.getElementsAnnotatedWith(MethodProcessor.class)){

           if(annotatedElement.getKind() != ElementKind.CLASS){
               error(annotatedElement, "Only classes can be annotated with @%s", MethodProcessor.class.getSimpleName());
           return true;
           }

           analysisAnnotated(annotatedElement);

       }


        return false;
    }

    private void error(Element e,String msg,Object... arg){
        mMessager.printMessage(Diagnostic.Kind.ERROR,String.format(msg,arg),e);
    }

    private static final String SUFFIX = "Test";
    private static final String packageName = "com.example.javalib";
    private static final String retStr = "这是应该返回的内容，哈哈哈！";
    private void analysisAnnotated(Element classElement){
        MethodProcessor annotation = classElement.getAnnotation(MethodProcessor.class);
        String name = annotation.name();
        String newClassName=name+SUFFIX;
        StringBuilder builder = new StringBuilder();
        builder.append("package "+packageName+";\n\n")
               .append("public class ")
                .append(newClassName)
                .append(" {\n\n")
                .append("\tpublic String getMessage() {\n")
                .append("\t\treturn \"")
                .append(retStr);
        builder.append("\";\n") // end return
                .append("\t}\n") // close method
                .append("}\n"); // close class

        try {//write the file
            JavaFileObject source = mFiler.createSourceFile(packageName+"."+newClassName);
            Writer writer = source.openWriter();
            writer.write(builder.toString());
            writer.flush();
            writer.close();
        }catch (IOException e){

        }

    }

}
