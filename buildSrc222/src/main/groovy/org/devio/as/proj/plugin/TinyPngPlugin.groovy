package org.devio.as.proj.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

class TinyPngPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        if(!project.plugins.hasPlugin("com.android.application")){
            throw  new ProjectConfigurationException("plugin:com.android.application must apply",null)
        }

        project.android.registerTransform(new TinyPngTransform(project))
//        def  android = project.extensions.findByType(AppExtension.class)
//        android.registerTransform(new TinyPngTransform(project))
    }
}
