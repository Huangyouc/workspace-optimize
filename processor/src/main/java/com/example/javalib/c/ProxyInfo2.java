package com.example.javalib.c;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * 生成注解逻辑代码的辅助类
 */
public class ProxyInfo2 {
   public static final String PROXY = "_ViewBinding";
   //注解变量集合
    public Map<Integer,VariableElement> viewVariableElement = new HashMap<>();
    //生成代理类的名字
    public String proxyClassName;
    //生成代理类的包名
    public String packageName;
    private TypeElement typeElement;
    public ProxyInfo2(TypeElement element,String pkgName){
        this.typeElement = element;
        this.packageName = pkgName;
        String className1=getClassName(typeElement,packageName);
        this.proxyClassName = className1+PROXY;
    }

    /**
     * 获取生成的代理类的类名
     * 之所以用字符串截取、替换而没用clas.getSimpleName()的原因是为了处理内部类注解的情况，比如adapter.ViewHolder
     * 内部类反射之后的类名：例如MyAdapter$ContentViewHolder，中间是$，而不是.
     *
     * @param element
     * @param pkgName
     * @return
     */
    private String getClassName(TypeElement element,String pkgName){
        int packageLength = pkgName.length()+1;
        return  element.getQualifiedName().toString().substring(packageLength)
                .replace(".","$");
    }

    /**
     * 通过javapoet API生成代理类
     * @return
     */
    public TypeSpec generateProxyClass(){
        //代理类实现的接口
        ClassName viewInjector = ClassName.get("com.example.inject","IViewInjector");
        //类
        ClassName className = ClassName.get(typeElement);
        //泛型接口
        ParameterizedTypeName parameterizedTypeName = ParameterizedTypeName.get(viewInjector,className);
        //生成构造方法
        MethodSpec.Builder constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(className,"target")
                .addStatement("this.target = target");

        //生成接口的实现方法inject()
        MethodSpec.Builder bindBuilder = MethodSpec.methodBuilder("inject")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)//添加方法注解
                .addParameter(className,"target")
                .addParameter(Object.class,"source");

        for(int id : viewVariableElement.keySet()){
            VariableElement element = viewVariableElement.get(id);
            String fieldName = element.getSimpleName().toString();
            bindBuilder.addStatement(" if (source instanceof androidx.appcompat.app.AppCompatActivity){target.$L = ((androidx.appcompat.app.AppCompatActivity)source).findViewById($L);}" +
                    "else{target.$L = ((android.view.View)source).findViewById($L);}",fieldName,id,fieldName,id);
        }
        MethodSpec bindMethodSpec = bindBuilder.build();
        //创建类
        TypeSpec typeSpec = TypeSpec.classBuilder(proxyClassName)
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(parameterizedTypeName)//实现接口
//                .addField(className,"target")
//                .addMethod(constructorBuilder.build())//添加构造方法
                .addMethod(bindMethodSpec)//添加类中的方法
                .build();

        return typeSpec;
    }
}
