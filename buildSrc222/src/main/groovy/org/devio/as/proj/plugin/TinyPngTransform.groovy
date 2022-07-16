package org.devio.as.proj.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import javassist.CtClass
import javassist.bytecode.ClassFile
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

class  TinyPngTransform extends Transform{
    private ClassPool classPool = ClassPool.getDefault()
    TinyPngTransform(Project project){
        //为了能够查找到android 相关的类，需要把android.jar包的路径添加到classPool  类搜索路径
        classPool.appendClassPath(project.android.bootClasspath[0].toString())

        classPool.importPackage("android.os.Bundle")
        classPool.importPackage("android.widget.Toast")
        classPool.importPackage("android.app.Activity")

        classPool.importPackage("java.lang.Runnable")
        classPool.importPackage("android.widget.ImageView")
        classPool.importPackage("androidx.appcompat.widget.AppCompatImageView")
        classPool.importPackage("android.graphics.drawable.Drawable")
    }
    @Override
    String getName() {
        return "TinyPngTransform"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        def  outputProvider = transformInvocation.outputProvider
        transformInvocation.inputs.each { input->
            input.directoryInputs.each { dirInput->
                println("directoryInputs abs file path = ${dirInput.file.absolutePath}")
                handlerDirectory(dirInput.file)

                //把input-》dir->class 复制到dest下;dest作为下一个transform的输入数据
                def dest = outputProvider.getContentLocation(dirInput.name,dirInput.contentTypes,dirInput.scopes, Format.DIRECTORY)
                FileUtils.copyDirectory(dirInput.file,dest)
            }

            input.jarInputs.each { jarInputs->
                println("jarInputs abs file path = ${jarInputs.file.absolutePath}")
                handlerJar(jarInputs.file)

                //主要为了防止重名
                def jarName = jarInputs.name
                def  md5 = DigestUtils.md5Hex(jarInputs.file.absolutePath)
                if(jarName.endsWith(".jar")){
                    jarName = jarName.substring(0,jarName.length()-4)
                }
                //获取jar包 输出路径
                def dest = outputProvider.getContentLocation(md5+jarName,jarInputs.contentTypes,jarInputs.scopes, Format.JAR)
                FileUtils.copyFile(jarInputs.file,dest)
            }

        }

        classPool.clearImportedPackages()
    }

    void handlerDirectory(File dir){
          classPool.appendClassPath(dir.absolutePath)
        if(dir.isDirectory()){
            dir.eachFileRecurse { file ->
                def  filePath = file.absolutePath
                println("handlerDirectory file path = $filePath")
                if(shouldModifyClass(filePath)){
                       def  inputStream = new FileInputStream(file)
                    def  ctClass = modifyClass(inputStream)
                    ctClass.writeFile(dir.name)//将类写回到目录下
                    ctClass.detach()//从classpool中释放
                }

            }
        }
    }

    File handlerJar(File jarFile){
        classPool.appendClassPath(jarFile.absolutePath)
        def inputJarFile = new JarFile(jarFile)
        def enumeration = inputJarFile.entries()

        def outputJarfile = new File(jarFile.parentFile,"temp_"+jarFile.name)
        if(outputJarfile.exists()){
            outputJarfile.delete()
        }
        def jarOutputStream = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(outputJarfile)))
        while (enumeration.hasMoreElements()){
            def inputJarEntry = enumeration.nextElement()
            def inputJarEntryName = inputJarEntry.name

            def outputJarEntry = new JarEntry(inputJarEntryName)
            jarOutputStream.putNextEntry(outputJarEntry)
            println("inputJarEntryName: " + inputJarEntryName)

            def inputStream = inputJarFile.getInputStream(inputJarEntry)
            if(!shouldModifyClass(inputJarEntryName)){

                jarOutputStream.write(IOUtils.toByteArray(inputStream))
                inputStream.close()
                continue
            }

            def ctClass = modifyClass(inputStream)
            def  byteCode = ctClass.toBytecode()
            ctClass.detach()
            inputStream.close()

            jarOutputStream.write(byteCode)
            jarOutputStream.flush()
        }
        inputJarFile.close()
        jarOutputStream.closeEntry()
        jarOutputStream.flush()
        jarOutputStream.close()
        return outputJarfile
    }

    boolean shouldModifyClass(String filePath){

        return (filePath.contains("com/example/optimize")
                && filePath.endsWith("Activity.class")
                && !filePath.contains("R.class")
                && !filePath.contains('$')
                && !filePath.contains('R$')
                && !filePath.contains("BuildConfig.class")
        )
    }

    CtClass modifyClass(InputStream is){
        def  classFile = new ClassFile(new DataInputStream(new BufferedInputStream(is)))
        println("modifyClass file 全类名 = ${classFile.name}")
        def ctClass = classPool.get(classFile.name)
        if(ctClass.isFrozen()){//促使从磁盘中加载
            ctClass.defrost()
        }

        def bundle = classPool.get("android.os.Bundle")
        CtClass[] params = Arrays.asList(bundle).toArray()
        def method = ctClass.getDeclaredMethod("onCreate",params)

        def message = "这么吊${classFile.name}"
        method.insertAfter("Toast.makeText(this,"+"\""+message+"\""+",Toast.LENGTH_SHORT).show();")

        return ctClass
    }
}
