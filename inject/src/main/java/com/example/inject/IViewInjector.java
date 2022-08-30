package com.example.inject;

public interface IViewInjector<T> {
    /**
     * 通过source.findViewById()
     *
     * @param target 泛型参数，调用类 activity、fragment等
     * @param source Activity、View
     */
    void inject(T target, Object source);
}
