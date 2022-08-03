package com.example.javalib.a

import com.example.javalib.a.DemoProcessor.DEMO_ANNOTATION
import java.io.IOException
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.Modifier
import javax.lang.model.element.PackageElement
import javax.lang.model.element.TypeElement
import javax.tools.JavaFileObject
class DemoAnnotationProcessor {
    fun process(annotations: Set<TypeElement>,
                roundEnv:RoundEnvironment,
                processingEnv:ProcessingEnvironment):Boolean{

        println("start process")
        if(roundEnv.rootElements.size==0){
            return  false
        }

        for (annotation in annotations){
            //好到需要处理的注解
            if(!annotation.qualifiedName.toString().contains(DEMO_ANNOTATION)){
                println("hyc>>> qualifiedName = ${annotation.qualifiedName.toString()}")
                continue
            }

            val elements = roundEnv.getElementsAnnotatedWith(annotation)
            for (element in elements){
                // 去掉非方法的注解
                if(element !is ExecutableElement){
                  continue
                }
                // 检查被注解的方法是否符合要求
                if(!checkHasNoErrors(element)){
                    continue
                }

                // 获取类
                val classElement = element.enclosingElement as TypeElement
                //获取包
                val packageElement = getPackageElement(classElement)
                createServiceClass(
                    packageElement!!.qualifiedName.toString(),
                    classElement.simpleName.toString(),
                    classElement.simpleName.toString(),
                    processingEnv
                )
            }
        }

        return true
    }

    private fun createServiceClass(
        pkName: String,
        simpleClazzName: String,
        serviceName: String,
        processingEnv: ProcessingEnvironment
    ) {
        println("hyc>>> createServiceClass")
        println("hyc>>> pkName = $pkName")
        println("hyc>>> simpleClazzName = $simpleClazzName")
        println("hyc>>> serviceName = $serviceName")
        val className = "$pkName.$simpleClazzName"
        val builder=StringBuilder()
            .append("package com.example.javalib.generated;\n\n")
            .append("import $className;\n\n")
            .append("public class GeneratedClass$serviceName {\n\n")
            .append("\tpublic $simpleClazzName getInstance(){\n")
            .append("\t\treturn ")
        builder.append("new $simpleClazzName()")
        builder.append(";\n")
        builder.append("\t}\n")
        builder.append("}\n")
        try{

            var source:JavaFileObject = processingEnv.filer
                .createSourceFile("com.example.javalib.generated.GeneratedClass"+serviceName)
            val writer = source.openWriter()
            writer.write(builder.toString())
            writer.flush()
            writer.close()
        }catch (e:IOException){
            println("hyc>>> createServiceClass error  = ${e.message}")
        }

    }

    private fun getPackageElement(subscriberClass:TypeElement):PackageElement?{
       var candidate = subscriberClass.enclosingElement
        while (candidate !is PackageElement){
            candidate = candidate.enclosingElement
        }
        return candidate
    }

    private fun checkHasNoErrors(initMethod:ExecutableElement):Boolean{
        if(!initMethod.modifiers.contains(Modifier.PUBLIC)){
            return  false
        }
        return initMethod.parameters.size<=0
    }
}