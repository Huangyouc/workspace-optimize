package com.example.javalib.c;

import com.example.processor.BindView;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
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
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;

@AutoService(Processor.class)
public class ButterKnifeProcessor2 extends AbstractProcessor {

    //生成文件的工具类
    private Filer filer;
    //打印信息
    Messager messager;
    //元素相关
    Elements elementUtils;

    Types typesUtils;

    Map<String,ProxyInfo2> proxyInfo2Map = new HashMap<>();

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
        typesUtils = processingEnv.getTypeUtils();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_8;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> stringSet = new LinkedHashSet<>();
        stringSet.add(BindView.class.getCanonicalName());
        return stringSet;
    }

    //1.通过getElementsAnnotatedWith()获取要处理的注解的元素的集合，换句话说，找到所有Class中被@BindView注解标记的变量；
    //2.遍历第一步中的元素集合，由于这个注解可能会在多个类中使用，所以我们以类名为单元划分注解。
    //具体说，新建一个ProxyInfo对象去保存一个类里面的所有被注解的元素；用proxyInfoMap去保存所有的ProxyInfo；
    //大概是这个样子Map<String, ProxyInfo> proxyInfoMap = new HashMap<>();
    //3.在ProxyInfo中为每个使用了@BindView注解的类生成一个代理类；
    //4.遍历proxyInfoMap,通过ProxyInfo和JavaFile生成具体的代理类文件
    /**
     *Element代表程序的一个元素，可以是package, class, interface, method.只在编译期存在
     *具体来说，可以按如下分类：
     *PackageElement 一般代表Package
     *TypeElement 一般代表代表类
     *VariableElement 一般代表成员变量
     *ExecutableElement 一般代表类中的方法
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        messager.printMessage(Diagnostic.Kind.NOTE,"annotations size--->" + annotations.size());
        //1、获取要处理的注解的元素的集合
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(BindView.class);
        //process()方法会调用3次，只有第一次有效，第2，3次调用的话生成.java文件会发生异常
        if(elements == null || elements.isEmpty()){
            return true;
        }
        //2、按类来划分注解元素，因为每个使用注解的类都会生成相应的代理类
        for (Element element:elements){
            checkAnnotationValid(element,BindView.class);
            //获取被注解的成员变量
            //这里被注解的类型只能是变量，所以可以直接强转
            VariableElement variableElement = (VariableElement) element;
            //获取该元素的父元素，成员变量的父元素就是类
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            //获取全类名
            String className = typeElement.getQualifiedName().toString();
            //获取被注解元素的包名
            String packageName = elementUtils.getPackageOf(element).getQualifiedName().toString();
            //获取注解的参数
            int resourceId = element.getAnnotation(BindView.class).value();

            //生成ProxyInfo对象
            //一个类里面的注解都在一个ProxyInfo中处理
             ProxyInfo2 proxyInfo2 = proxyInfo2Map.get(className);
             if(proxyInfo2==null){
                 proxyInfo2 = new ProxyInfo2(typeElement,packageName);
                 proxyInfo2Map.put(className,proxyInfo2);
             }
            proxyInfo2.viewVariableElement.put(resourceId,variableElement);

        }

         //3.生成注解逻辑处理类
        for(String key:proxyInfo2Map.keySet()){

            ProxyInfo2 proxy_info = proxyInfo2Map.get(key);
            JavaFile javaFile = JavaFile.builder(proxy_info.packageName,proxy_info.generateProxyClass())
                    .addFileComment("auto generateProxyClass code,can not modify")
                    .build();
            try {
                javaFile.writeTo(filer);
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        return true;
    }

    /**
     * 检查注解是否可用
     */
    private boolean checkAnnotationValid(Element annotatedElement,Class clazz) {
        if(annotatedElement.getKind() != ElementKind.FIELD){
            messager.printMessage(Diagnostic.Kind.NOTE, "%s must be declared on field.", annotatedElement);
            return false;
        }
        if(annotatedElement.getModifiers().contains(Modifier.PRIVATE)){
            messager.printMessage(Diagnostic.Kind.NOTE, "%s() can not be private.", annotatedElement);
            return false;
        }
        if(!isView(annotatedElement.asType())){
            return false;
        }

        return true;
    }

    /**
     * 递归判断android.view.View是不是其父类
     *
     * @return
     */
    private boolean isView(TypeMirror type){
        //这个判断应该有个问题：如果当前 type就是 android.view.View ，其父类为空，就无法正确判断

        List<? extends TypeMirror> supers = typesUtils.directSupertypes(type);
        if(supers.size()==0){
            return false;
        }
        for (TypeMirror superType:supers){
            if(superType.toString().equals("android.view.view") || isView(superType)){
                return true;
            }
        }

        return false;
    }
}
