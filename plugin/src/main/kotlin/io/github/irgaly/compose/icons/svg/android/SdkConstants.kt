/*
 * Forked from:
 * https://android.googlesource.com/platform/tools/base/+/5273982464012584e6f7f3e21a253bf667398cdd/common/src/main/java/com/android/SdkConstants.java
 *
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.irgaly.compose.icons.svg.android

import java.io.File

/**
 * Constant definition class.<br></br>
 * <br></br>
 * Most constants have a prefix defining the content.
 *
 *  * `OS_` OS path constant. These paths are different depending on the platform.
 *  * `FN_` File name constant.
 *  * `FD_` Folder name constant.
 *  * `TAG_` XML element tag name
 *  * `ATTR_` XML attribute name
 *  * `VALUE_` XML attribute value
 *  * `CLASS_` Class name
 *  * `DOT_` File name extension, including the dot
 *  * `EXT_` File name extension, without the dot
 *
 */
@Suppress("unused") // Not documenting all the fields here
internal object SdkConstants {
    const val PLATFORM_UNKNOWN: Int = 0
    const val PLATFORM_LINUX: Int = 1
    const val PLATFORM_WINDOWS: Int = 2
    const val PLATFORM_DARWIN: Int = 3

    /**
     * Returns current platform, one of [.PLATFORM_WINDOWS], [.PLATFORM_DARWIN],
     * [.PLATFORM_LINUX] or [.PLATFORM_UNKNOWN].
     */
    val CURRENT_PLATFORM: Int = currentPlatform()

    /** Environment variable that specifies the path of an Android SDK.  */
    const val ANDROID_HOME_ENV: String = "ANDROID_HOME"

    /** Property in local.properties file that specifies the path of the Android SDK.   */
    const val SDK_DIR_PROPERTY: String = "sdk.dir"

    /** Property in local.properties file that specifies the path of the Android NDK.   */
    const val NDK_DIR_PROPERTY: String = "ndk.dir"

    /** Property in gradle-wrapper.properties file that specifies the URL to the correct Gradle distribution.  */
    const val GRADLE_DISTRIBUTION_URL_PROPERTY: String = "distributionUrl" //$NON-NLS-1$

    /**
     * The encoding we strive to use for all files we write.
     *
     *
     * When possible, use the APIs which take a [java.nio.charset.Charset] and pass in
     * [com.google.common.base.Charsets.UTF_8] instead of using the String encoding
     * method.
     */
    const val UTF_8: String = "UTF-8" //$NON-NLS-1$

    /**
     * Charset for the ini file handled by the SDK.
     */
    const val INI_CHARSET: String = UTF_8

    /** Path separator used by Gradle  */
    const val GRADLE_PATH_SEPARATOR: String = ":" //$NON-NLS-1$

    /** An SDK Project's AndroidManifest.xml file  */
    const val FN_ANDROID_MANIFEST_XML: String = "AndroidManifest.xml" //$NON-NLS-1$

    /** pre-dex jar filename. i.e. "classes.jar"  */
    const val FN_CLASSES_JAR: String = "classes.jar" //$NON-NLS-1$

    /** Dex filename inside the APK. i.e. "classes.dex"  */
    const val FN_APK_CLASSES_DEX: String = "classes.dex" //$NON-NLS-1$

    /** Dex filename inside the APK. i.e. "classes.dex"  */
    const val FN_APK_CLASSES_N_DEX: String = "classes%d.dex" //$NON-NLS-1$

    /** An SDK Project's build.xml file  */
    const val FN_BUILD_XML: String = "build.xml" //$NON-NLS-1$

    /** An SDK Project's build.gradle file  */
    const val FN_BUILD_GRADLE: String = "build.gradle" //$NON-NLS-1$

    /** An SDK Project's settings.gradle file  */
    const val FN_SETTINGS_GRADLE: String = "settings.gradle" //$NON-NLS-1$

    /** An SDK Project's gradle.properties file  */
    const val FN_GRADLE_PROPERTIES: String = "gradle.properties" //$NON-NLS-1$

    /** An SDK Project's gradle daemon executable  */
    const val FN_GRADLE_UNIX: String = "gradle" //$NON-NLS-1$

    /** An SDK Project's gradle.bat daemon executable (gradle for windows)  */
    const val FN_GRADLE_WIN: String = FN_GRADLE_UNIX + ".bat" //$NON-NLS-1$

    /** An SDK Project's gradlew file  */
    const val FN_GRADLE_WRAPPER_UNIX: String = "gradlew" //$NON-NLS-1$

    /** An SDK Project's gradlew.bat file (gradlew for windows)  */
    const val FN_GRADLE_WRAPPER_WIN: String = FN_GRADLE_WRAPPER_UNIX + ".bat" //$NON-NLS-1$

    /** An SDK Project's gradle wrapper library  */
    const val FN_GRADLE_WRAPPER_JAR: String = "gradle-wrapper.jar" //$NON-NLS-1$

    /** Name of the framework library, i.e. "android.jar"  */
    const val FN_FRAMEWORK_LIBRARY: String = "android.jar" //$NON-NLS-1$

    /** Name of the framework library, i.e. "uiautomator.jar"  */
    const val FN_UI_AUTOMATOR_LIBRARY: String = "uiautomator.jar" //$NON-NLS-1$

    /** Name of the layout attributes, i.e. "attrs.xml"  */
    const val FN_ATTRS_XML: String = "attrs.xml" //$NON-NLS-1$

    /** Name of the layout attributes, i.e. "attrs_manifest.xml"  */
    const val FN_ATTRS_MANIFEST_XML: String = "attrs_manifest.xml" //$NON-NLS-1$

    /** framework aidl import file  */
    const val FN_FRAMEWORK_AIDL: String = "framework.aidl" //$NON-NLS-1$

    /** framework renderscript folder  */
    const val FN_FRAMEWORK_RENDERSCRIPT: String = "renderscript" //$NON-NLS-1$

    /** framework include folder  */
    const val FN_FRAMEWORK_INCLUDE: String = "include" //$NON-NLS-1$

    /** framework include (clang) folder  */
    const val FN_FRAMEWORK_INCLUDE_CLANG: String = "clang-include" //$NON-NLS-1$

    /** layoutlib.jar file  */
    const val FN_LAYOUTLIB_JAR: String = "layoutlib.jar" //$NON-NLS-1$

    /** widget list file  */
    const val FN_WIDGETS: String = "widgets.txt" //$NON-NLS-1$

    /** Intent activity actions list file  */
    const val FN_INTENT_ACTIONS_ACTIVITY: String = "activity_actions.txt" //$NON-NLS-1$

    /** Intent broadcast actions list file  */
    const val FN_INTENT_ACTIONS_BROADCAST: String = "broadcast_actions.txt" //$NON-NLS-1$

    /** Intent service actions list file  */
    const val FN_INTENT_ACTIONS_SERVICE: String = "service_actions.txt" //$NON-NLS-1$

    /** Intent category list file  */
    const val FN_INTENT_CATEGORIES: String = "categories.txt" //$NON-NLS-1$

    /** annotations support jar  */
    const val FN_ANNOTATIONS_JAR: String = "annotations.jar" //$NON-NLS-1$

    /** platform build property file  */
    const val FN_BUILD_PROP: String = "build.prop" //$NON-NLS-1$

    /** plugin properties file  */
    const val FN_PLUGIN_PROP: String = "plugin.prop" //$NON-NLS-1$

    /** add-on manifest file  */
    const val FN_MANIFEST_INI: String = "manifest.ini" //$NON-NLS-1$

    /** add-on layout device XML file.  */
    const val FN_DEVICES_XML: String = "devices.xml" //$NON-NLS-1$

    /** hardware properties definition file  */
    const val FN_HARDWARE_INI: String = "hardware-properties.ini" //$NON-NLS-1$

    /** project property file  */
    const val FN_PROJECT_PROPERTIES: String = "project.properties" //$NON-NLS-1$

    /** project local property file  */
    const val FN_LOCAL_PROPERTIES: String = "local.properties" //$NON-NLS-1$

    /** project ant property file  */
    const val FN_ANT_PROPERTIES: String = "ant.properties" //$NON-NLS-1$

    /** project local property file  */
    const val FN_GRADLE_WRAPPER_PROPERTIES: String = "gradle-wrapper.properties" //$NON-NLS-1$

    /** Skin layout file  */
    const val FN_SKIN_LAYOUT: String = "layout" //$NON-NLS-1$

    /** dx.jar file  */
    const val FN_DX_JAR: String = "dx.jar" //$NON-NLS-1$

    /** dx executable (with extension for the current OS)  */
    val FN_DX: String = "dx" + ext(".bat", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** aapt executable (with extension for the current OS)  */
    val FN_AAPT: String = "aapt" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** aidl executable (with extension for the current OS)  */
    val FN_AIDL: String = "aidl" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** renderscript executable (with extension for the current OS)  */
    val FN_RENDERSCRIPT: String =
        "llvm-rs-cc" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** renderscript support exe (with extension for the current OS)  */
    val FN_BCC_COMPAT: String =
        "bcc_compat" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** renderscript support linker for ARM (with extension for the current OS)  */
    val FN_LD_ARM: String =
        "arm-linux-androideabi-ld" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** renderscript support linker for X86 (with extension for the current OS)  */
    val FN_LD_X86: String =
        "i686-linux-android-ld" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** renderscript support linker for MIPS (with extension for the current OS)  */
    val FN_LD_MIPS: String =
        "mipsel-linux-android-ld" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** adb executable (with extension for the current OS)  */
    val FN_ADB: String = "adb" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** emulator executable for the current OS  */
    val FN_EMULATOR: String = "emulator" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** zipalign executable (with extension for the current OS)  */
    val FN_ZIPALIGN: String = "zipalign" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** dexdump executable (with extension for the current OS)  */
    val FN_DEXDUMP: String = "dexdump" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** proguard executable (with extension for the current OS)  */
    val FN_PROGUARD: String =
        "proguard" + ext(".bat", ".sh") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** find_lock for Windows (with extension for the current OS)  */
    val FN_FIND_LOCK: String =
        "find_lock" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** hprof-conv executable (with extension for the current OS)  */
    val FN_HPROF_CONV: String =
        "hprof-conv" + ext(".exe", "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    /** jack.jar  */
    const val FN_JACK: String = "jack.jar" //$NON-NLS-1$

    /** jill.jar  */
    const val FN_JILL: String = "jill.jar" //$NON-NLS-1$

    /** split-select  */
    val FN_SPLIT_SELECT: String = "split-select" + ext(".exe", "")

    /** properties file for SDK Updater packages  */
    const val FN_SOURCE_PROP: String = "source.properties" //$NON-NLS-1$

    /** properties file for content hash of installed packages  */
    const val FN_CONTENT_HASH_PROP: String = "content_hash.properties" //$NON-NLS-1$

    /** properties file for the SDK  */
    const val FN_SDK_PROP: String = "sdk.properties" //$NON-NLS-1$
    const val FN_RENDERSCRIPT_V8_JAR: String = "renderscript-v8.jar" //$NON-NLS-1$

    /**
     * filename for gdbserver.
     */
    const val FN_GDBSERVER: String = "gdbserver" //$NON-NLS-1$
    const val FN_GDB_SETUP: String = "gdb.setup" //$NON-NLS-1$

    /** global Android proguard config file  */
    const val FN_ANDROID_PROGUARD_FILE: String = "proguard-android.txt" //$NON-NLS-1$

    /** global Android proguard config file with optimization enabled  */
    const val FN_ANDROID_OPT_PROGUARD_FILE: String = "proguard-android-optimize.txt" //$NON-NLS-1$

    /** default proguard config file with new file extension (for project specific stuff)  */
    const val FN_PROJECT_PROGUARD_FILE: String = "proguard-project.txt" //$NON-NLS-1$
    /* Folder Names for Android Projects . */
    /** Resources folder name, i.e. "res".  */
    const val FD_RESOURCES: String = "res" //$NON-NLS-1$

    /** Assets folder name, i.e. "assets"  */
    const val FD_ASSETS: String = "assets" //$NON-NLS-1$

    /** Default source folder name in an SDK project, i.e. "src".
     *
     *
     * Note: this is not the same as [.FD_PKG_SOURCES]
     * which is an SDK sources folder for packages.  */
    const val FD_SOURCES: String = "src" //$NON-NLS-1$

    /** Default main source set folder name, i.e. "main"  */
    const val FD_MAIN: String = "main" //$NON-NLS-1$

    /** Default test source set folder name, i.e. "androidTest"  */
    const val FD_TEST: String = "androidTest" //$NON-NLS-1$

    /** Default java code folder name, i.e. "java"  */
    const val FD_JAVA: String = "java" //$NON-NLS-1$

    /** Default native code folder name, i.e. "jni"  */
    const val FD_JNI: String = "jni" //$NON-NLS-1$

    /** Default gradle folder name, i.e. "gradle"  */
    const val FD_GRADLE: String = "gradle" //$NON-NLS-1$

    /** Default gradle wrapper folder name, i.e. "gradle/wrapper"  */
    val FD_GRADLE_WRAPPER: String = FD_GRADLE + File.separator + "wrapper" //$NON-NLS-1$

    /** Default generated source folder name, i.e. "gen"  */
    const val FD_GEN_SOURCES: String = "gen" //$NON-NLS-1$

    /** Default native library folder name inside the project, i.e. "libs"
     * While the folder inside the .apk is "lib", we call that one libs because
     * that's what we use in ant for both .jar and .so and we need to make the 2 development ways
     * compatible.  */
    const val FD_NATIVE_LIBS: String = "libs" //$NON-NLS-1$

    /** Native lib folder inside the APK: "lib"  */
    const val FD_APK_NATIVE_LIBS: String = "lib" //$NON-NLS-1$

    /** Default output folder name, i.e. "bin"  */
    const val FD_OUTPUT: String = "bin" //$NON-NLS-1$

    /** Classes output folder name, i.e. "classes"  */
    const val FD_CLASSES_OUTPUT: String = "classes" //$NON-NLS-1$

    /** proguard output folder for mapping, etc.. files  */
    const val FD_PROGUARD: String = "proguard" //$NON-NLS-1$

    /** aidl output folder for copied aidl files  */
    const val FD_AIDL: String = "aidl" //$NON-NLS-1$

    /** rs Libs output folder for support mode  */
    const val FD_RS_LIBS: String = "rsLibs" //$NON-NLS-1$

    /** rs Libs output folder for support mode  */
    const val FD_RS_OBJ: String = "rsObj" //$NON-NLS-1$

    /** jars folder  */
    const val FD_JARS: String = "jars" //$NON-NLS-1$
    /* Folder Names for the Android SDK */
    /** Name of the SDK platforms folder.  */
    const val FD_PLATFORMS: String = "platforms" //$NON-NLS-1$

    /** Name of the SDK addons folder.  */
    const val FD_ADDONS: String = "add-ons" //$NON-NLS-1$

    /** Name of the SDK system-images folder.  */
    const val FD_SYSTEM_IMAGES: String = "system-images" //$NON-NLS-1$

    /** Name of the SDK sources folder where source packages are installed.
     *
     *
     * Note this is not the same as [.FD_SOURCES] which is the folder name where sources
     * are installed inside a project.  */
    const val FD_PKG_SOURCES: String = "sources" //$NON-NLS-1$

    /** Name of the SDK tools folder.  */
    const val FD_TOOLS: String = "tools" //$NON-NLS-1$

    /** Name of the SDK tools/support folder.  */
    const val FD_SUPPORT: String = "support" //$NON-NLS-1$

    /** Name of the SDK platform tools folder.  */
    const val FD_PLATFORM_TOOLS: String = "platform-tools" //$NON-NLS-1$

    /** Name of the SDK build tools folder.  */
    const val FD_BUILD_TOOLS: String = "build-tools" //$NON-NLS-1$

    /** Name of the SDK tools/lib folder.  */
    const val FD_LIB: String = "lib" //$NON-NLS-1$

    /** Name of the SDK docs folder.  */
    const val FD_DOCS: String = "docs" //$NON-NLS-1$

    /** Name of the doc folder containing API reference doc (javadoc)  */
    const val FD_DOCS_REFERENCE: String = "reference" //$NON-NLS-1$

    /** Name of the SDK images folder.  */
    const val FD_IMAGES: String = "images" //$NON-NLS-1$

    /** Name of the ABI to support.  */
    const val ABI_ARMEABI: String = "armeabi" //$NON-NLS-1$
    const val ABI_ARMEABI_V7A: String = "armeabi-v7a" //$NON-NLS-1$
    const val ABI_ARM64_V8A: String = "arm64-v8a" //$NON-NLS-1$
    const val ABI_INTEL_ATOM: String = "x86" //$NON-NLS-1$
    const val ABI_INTEL_ATOM64: String = "x86_64" //$NON-NLS-1$
    const val ABI_MIPS: String = "mips" //$NON-NLS-1$
    const val ABI_MIPS64: String = "mips64" //$NON-NLS-1$

    /** Name of the CPU arch to support.  */
    const val CPU_ARCH_ARM: String = "arm" //$NON-NLS-1$
    const val CPU_ARCH_ARM64: String = "arm64" //$NON-NLS-1$
    const val CPU_ARCH_INTEL_ATOM: String = "x86" //$NON-NLS-1$
    const val CPU_ARCH_INTEL_ATOM64: String = "x86_64" //$NON-NLS-1$
    const val CPU_ARCH_MIPS: String = "mips" //$NON-NLS-1$

    /** TODO double-check this is appropriate value for mips64  */
    const val CPU_ARCH_MIPS64: String = "mips64" //$NON-NLS-1$

    /** Name of the CPU model to support.  */
    const val CPU_MODEL_CORTEX_A8: String = "cortex-a8" //$NON-NLS-1$

    /** Name of the SDK skins folder.  */
    const val FD_SKINS: String = "skins" //$NON-NLS-1$

    /** Name of the SDK samples folder.  */
    const val FD_SAMPLES: String = "samples" //$NON-NLS-1$

    /** Name of the SDK extras folder.  */
    const val FD_EXTRAS: String = "extras" //$NON-NLS-1$
    const val FD_M2_REPOSITORY: String = "m2repository" //$NON-NLS-1$
    const val FD_NDK: String = "ndk-bundle" //$NON-NLS-1$

    /**
     * Name of an extra's sample folder.
     * Ideally extras should have one [.FD_SAMPLES] folder containing
     * one or more sub-folders (one per sample). However some older extras
     * might contain a single "sample" folder with directly the samples files
     * in it. When possible we should encourage extras' owners to move to the
     * multi-samples format.
     */
    const val FD_SAMPLE: String = "sample" //$NON-NLS-1$

    /** Name of the SDK templates folder, i.e. "templates"  */
    const val FD_TEMPLATES: String = "templates" //$NON-NLS-1$

    /** Name of the SDK Ant folder, i.e. "ant"  */
    const val FD_ANT: String = "ant" //$NON-NLS-1$

    /** Name of the SDK data folder, i.e. "data"  */
    const val FD_DATA: String = "data" //$NON-NLS-1$

    /** Name of the SDK renderscript folder, i.e. "rs"  */
    const val FD_RENDERSCRIPT: String = "rs" //$NON-NLS-1$

    /** Name of the Java resources folder, i.e. "resources"  */
    const val FD_JAVA_RES: String = "resources" //$NON-NLS-1$

    /** Name of the SDK resources folder, i.e. "res"  */
    const val FD_RES: String = "res" //$NON-NLS-1$

    /** Name of the SDK font folder, i.e. "fonts"  */
    const val FD_FONTS: String = "fonts" //$NON-NLS-1$

    /** Name of the android sources directory and the root of the SDK sources package folder.  */
    const val FD_ANDROID_SOURCES: String = "sources" //$NON-NLS-1$

    /** Name of the addon libs folder.  */
    const val FD_ADDON_LIBS: String = "libs" //$NON-NLS-1$

    /** Name of the cache folder in the $HOME/.android.  */
    const val FD_CACHE: String = "cache" //$NON-NLS-1$

    /** API codename of a release (non preview) system image or platform.  */
    const val CODENAME_RELEASE: String = "REL" //$NON-NLS-1$

    /** Namespace for the resource XML, i.e. "http://schemas.android.com/apk/res/android"  */
    const val NS_RESOURCES: String = "http://schemas.android.com/apk/res/android" //$NON-NLS-1$

    /**
     * Namespace pattern for the custom resource XML, i.e. "http://schemas.android.com/apk/res/%s"
     *
     *
     * This string contains a %s. It must be combined with the desired Java package, e.g.:
     * <pre>
     * String.format(SdkConstants.NS_CUSTOM_RESOURCES_S, "android");
     * String.format(SdkConstants.NS_CUSTOM_RESOURCES_S, "com.test.mycustomapp");
    </pre> *
     *
     * Note: if you need an URI specifically for the "android" namespace, consider using
     * [SdkConstants.NS_RESOURCES] instead.
     */
    const val NS_CUSTOM_RESOURCES_S: String =
        "http://schemas.android.com/apk/res/%1\$s" //$NON-NLS-1$

    /** The name of the uses-library that provides "android.test.runner"  */
    const val ANDROID_TEST_RUNNER_LIB: String = "android.test.runner" //$NON-NLS-1$
    /* Folder path relative to the SDK root */
    /** Path of the documentation directory relative to the sdk folder.
     * This is an OS path, ending with a separator.  */
    val OS_SDK_DOCS_FOLDER: String = FD_DOCS + File.separator

    /** Path of the tools directory relative to the sdk folder, or to a platform folder.
     * This is an OS path, ending with a separator.  */
    val OS_SDK_TOOLS_FOLDER: String = FD_TOOLS + File.separator

    /** Path of the lib directory relative to the sdk folder, or to a platform folder.
     * This is an OS path, ending with a separator.  */
    val OS_SDK_TOOLS_LIB_FOLDER: String = OS_SDK_TOOLS_FOLDER + FD_LIB + File.separator

    /**
     * Path of the lib directory relative to the sdk folder, or to a platform
     * folder. This is an OS path, ending with a separator.
     */
    val OS_SDK_TOOLS_LIB_EMULATOR_FOLDER: String = (OS_SDK_TOOLS_LIB_FOLDER
            + "emulator" + File.separator) //$NON-NLS-1$

    /** Path of the platform tools directory relative to the sdk folder.
     * This is an OS path, ending with a separator.  */
    val OS_SDK_PLATFORM_TOOLS_FOLDER: String = FD_PLATFORM_TOOLS + File.separator

    /** Path of the build tools directory relative to the sdk folder.
     * This is an OS path, ending with a separator.  */
    val OS_SDK_BUILD_TOOLS_FOLDER: String = FD_BUILD_TOOLS + File.separator

    /** Path of the Platform tools Lib directory relative to the sdk folder.
     * This is an OS path, ending with a separator.  */
    val OS_SDK_PLATFORM_TOOLS_LIB_FOLDER: String =
        OS_SDK_PLATFORM_TOOLS_FOLDER + FD_LIB + File.separator

    /** Path of the bin folder of proguard folder relative to the sdk folder.
     * This is an OS path, ending with a separator.  */
    val OS_SDK_TOOLS_PROGUARD_BIN_FOLDER: String = OS_SDK_TOOLS_FOLDER +
            "proguard" + File.separator +  //$NON-NLS-1$
            "bin" + File.separator //$NON-NLS-1$

    /** Path of the template gradle wrapper folder relative to the sdk folder.
     * This is an OS path, ending with a separator.  */
    val OS_SDK_TOOLS_TEMPLATES_GRADLE_WRAPPER_FOLDER: String =
        OS_SDK_TOOLS_FOLDER + FD_TEMPLATES + File.separator + FD_GRADLE_WRAPPER + File.separator
    /* Folder paths relative to a platform or add-on folder */
    /** Path of the images directory relative to a platform or addon folder.
     * This is an OS path, ending with a separator.  */
    val OS_IMAGES_FOLDER: String = FD_IMAGES + File.separator

    /** Path of the skin directory relative to a platform or addon folder.
     * This is an OS path, ending with a separator.  */
    val OS_SKINS_FOLDER: String = FD_SKINS + File.separator
    /* Folder paths relative to a Platform folder */
    /** Path of the data directory relative to a platform folder.
     * This is an OS path, ending with a separator.  */
    val OS_PLATFORM_DATA_FOLDER: String = FD_DATA + File.separator

    /** Path of the renderscript directory relative to a platform folder.
     * This is an OS path, ending with a separator.  */
    val OS_PLATFORM_RENDERSCRIPT_FOLDER: String = FD_RENDERSCRIPT + File.separator

    /** Path of the samples directory relative to a platform folder.
     * This is an OS path, ending with a separator.  */
    val OS_PLATFORM_SAMPLES_FOLDER: String = FD_SAMPLES + File.separator

    /** Path of the resources directory relative to a platform folder.
     * This is an OS path, ending with a separator.  */
    val OS_PLATFORM_RESOURCES_FOLDER: String = OS_PLATFORM_DATA_FOLDER + FD_RES + File.separator

    /** Path of the fonts directory relative to a platform folder.
     * This is an OS path, ending with a separator.  */
    val OS_PLATFORM_FONTS_FOLDER: String = OS_PLATFORM_DATA_FOLDER + FD_FONTS + File.separator

    /** Path of the android source directory relative to a platform folder.
     * This is an OS path, ending with a separator.  */
    val OS_PLATFORM_SOURCES_FOLDER: String = FD_ANDROID_SOURCES + File.separator

    /** Path of the android templates directory relative to a platform folder.
     * This is an OS path, ending with a separator.  */
    val OS_PLATFORM_TEMPLATES_FOLDER: String = FD_TEMPLATES + File.separator

    /** Path of the Ant build rules directory relative to a platform folder.
     * This is an OS path, ending with a separator.  */
    val OS_PLATFORM_ANT_FOLDER: String = FD_ANT + File.separator

    /** Path of the attrs.xml file relative to a platform folder.  */
    val OS_PLATFORM_ATTRS_XML: String =
        OS_PLATFORM_RESOURCES_FOLDER + FD_RES_VALUES + File.separator +
                FN_ATTRS_XML

    /** Path of the attrs_manifest.xml file relative to a platform folder.  */
    val OS_PLATFORM_ATTRS_MANIFEST_XML: String =
        OS_PLATFORM_RESOURCES_FOLDER + FD_RES_VALUES + File.separator +
                FN_ATTRS_MANIFEST_XML

    /** Path of the layoutlib.jar file relative to a platform folder.  */
    val OS_PLATFORM_LAYOUTLIB_JAR: String = OS_PLATFORM_DATA_FOLDER + FN_LAYOUTLIB_JAR

    /** Path of the renderscript include folder relative to a platform folder.  */
    val OS_FRAMEWORK_RS: String = FN_FRAMEWORK_RENDERSCRIPT + File.separator + FN_FRAMEWORK_INCLUDE

    /** Path of the renderscript (clang) include folder relative to a platform folder.  */
    val OS_FRAMEWORK_RS_CLANG: String =
        FN_FRAMEWORK_RENDERSCRIPT + File.separator + FN_FRAMEWORK_INCLUDE_CLANG
    /* Folder paths relative to a addon folder */
    /** Path of the images directory relative to a folder folder.
     * This is an OS path, ending with a separator.  */
    val OS_ADDON_LIBS_FOLDER: String = FD_ADDON_LIBS + File.separator

    /** Skin default  */
    const val SKIN_DEFAULT: String = "default" //$NON-NLS-1$

    /** SDK property: ant templates revision  */
    const val PROP_SDK_ANT_TEMPLATES_REVISION: String = "sdk.ant.templates.revision" //$NON-NLS-1$

    /** SDK property: default skin  */
    const val PROP_SDK_DEFAULT_SKIN: String = "sdk.skin.default" //$NON-NLS-1$

    /* Android Class Constants */
    const val CLASS_ACTIVITY: String = "android.app.Activity" //$NON-NLS-1$
    const val CLASS_APPLICATION: String = "android.app.Application" //$NON-NLS-1$
    const val CLASS_SERVICE: String = "android.app.Service" //$NON-NLS-1$
    const val CLASS_BROADCASTRECEIVER: String = "android.content.BroadcastReceiver" //$NON-NLS-1$
    const val CLASS_CONTENTPROVIDER: String = "android.content.ContentProvider" //$NON-NLS-1$
    const val CLASS_ATTRIBUTE_SET: String = "android.util.AttributeSet" //$NON-NLS-1$
    const val CLASS_INSTRUMENTATION: String = "android.app.Instrumentation" //$NON-NLS-1$
    const val CLASS_INSTRUMENTATION_RUNNER: String =
        "android.test.InstrumentationTestRunner" //$NON-NLS-1$
    const val CLASS_BUNDLE: String = "android.os.Bundle" //$NON-NLS-1$
    const val CLASS_R: String = "android.R" //$NON-NLS-1$
    const val CLASS_R_PREFIX: String = CLASS_R + "." //$NON-NLS-1$
    const val CLASS_MANIFEST_PERMISSION: String = "android.Manifest\$permission" //$NON-NLS-1$
    const val CLASS_INTENT: String = "android.content.Intent" //$NON-NLS-1$
    const val CLASS_CONTEXT: String = "android.content.Context" //$NON-NLS-1$
    const val CLASS_VIEW: String = "android.view.View" //$NON-NLS-1$
    const val CLASS_VIEWGROUP: String = "android.view.ViewGroup" //$NON-NLS-1$
    const val CLASS_NAME_LAYOUTPARAMS: String = "LayoutParams" //$NON-NLS-1$
    const val CLASS_VIEWGROUP_LAYOUTPARAMS: String =
        CLASS_VIEWGROUP + "$" + CLASS_NAME_LAYOUTPARAMS //$NON-NLS-1$
    const val CLASS_NAME_FRAMELAYOUT: String = "FrameLayout" //$NON-NLS-1$
    const val CLASS_FRAMELAYOUT: String = "android.widget." + CLASS_NAME_FRAMELAYOUT //$NON-NLS-1$
    const val CLASS_PREFERENCE: String = "android.preference.Preference" //$NON-NLS-1$
    const val CLASS_NAME_PREFERENCE_SCREEN: String = "PreferenceScreen" //$NON-NLS-1$
    const val CLASS_PREFERENCES: String =
        "android.preference." + CLASS_NAME_PREFERENCE_SCREEN //$NON-NLS-1$
    const val CLASS_PREFERENCEGROUP: String = "android.preference.PreferenceGroup" //$NON-NLS-1$
    const val CLASS_PARCELABLE: String = "android.os.Parcelable" //$NON-NLS-1$
    const val CLASS_PARCEL: String = "android.os.Parcel" //$NON-NLS-1$
    const val CLASS_FRAGMENT: String = "android.app.Fragment" //$NON-NLS-1$
    const val CLASS_V4_FRAGMENT: String = "android.support.v4.app.Fragment" //$NON-NLS-1$
    const val CLASS_ACTION_PROVIDER: String = "android.view.ActionProvider" //$NON-NLS-1$
    const val CLASS_BACKUP_AGENT: String = "android.app.backup.BackupAgent" //$NON-NLS-1$

    /** MockView is part of the layoutlib bridge and used to display classes that have
     * no rendering in the graphical layout editor.  */
    const val CLASS_MOCK_VIEW: String = "com.android.layoutlib.bridge.MockView" //$NON-NLS-1$
    const val CLASS_LAYOUT_INFLATER: String = "android.view.LayoutInflater" //$NON-NLS-1$

    /* Android Design Support Class Constants */
    const val CLASS_COORDINATOR_LAYOUT: String =
        "android.support.design.widget.CoordinatorLayout" //$NON-NLS-1$
    const val CLASS_APP_BAR_LAYOUT: String =
        "android.support.design.widget.AppBarLayout" //$NON-NLS-1$
    const val CLASS_FLOATING_ACTION_BUTTON: String =
        "android.support.design.widget.FloatingActionButton" //$NON-NLS-1$
    const val CLASS_COLLAPSING_TOOLBAR_LAYOUT: String =
        "android.support.design.widget.CollapsingToolbarLayout" //$NON-NLS-1$
    const val CLASS_NAVIGATION_VIEW: String =
        "android.support.design.widget.NavigationView" //$NON-NLS-1$
    const val CLASS_SNACKBAR: String = "android.support.design.widget.Snackbar" //$NON-NLS-1$
    const val CLASS_TAB_LAYOUT: String = "android.support.design.widget.TabLayout" //$NON-NLS-1$
    const val CLASS_TEXT_INPUT_LAYOUT: String =
        "android.support.design.widget.TextInputLayout" //$NON-NLS-1$
    const val CLASS_NESTED_SCROLL_VIEW: String =
        "android.support.v4.widget.NestedScrollView" //$NON-NLS-1$

    /** Returns the appropriate name for the 'android' command, which is 'android.exe' for
     * Windows and 'android' for all other platforms.  */
    fun androidCmdName(): String {
        val os = System.getProperty("os.name") //$NON-NLS-1$
        var cmd = "android" //$NON-NLS-1$
        if (os.startsWith("Windows")) {                     //$NON-NLS-1$
            cmd += ".bat" //$NON-NLS-1$
        }
        return cmd
    }

    /** Returns the appropriate name for the 'mksdcard' command, which is 'mksdcard.exe' for
     * Windows and 'mkdsdcard' for all other platforms.  */
    fun mkSdCardCmdName(): String {
        val os = System.getProperty("os.name") //$NON-NLS-1$
        var cmd = "mksdcard" //$NON-NLS-1$
        if (os.startsWith("Windows")) {                     //$NON-NLS-1$
            cmd += ".exe" //$NON-NLS-1$
        }
        return cmd
    }

    /**
     * Returns current platform
     *
     * @return one of [.PLATFORM_WINDOWS], [.PLATFORM_DARWIN],
     * [.PLATFORM_LINUX] or [.PLATFORM_UNKNOWN].
     */
    fun currentPlatform(): Int {
        val os = System.getProperty("os.name") //$NON-NLS-1$
        if (os.startsWith("Mac OS")) {                      //$NON-NLS-1$
            return PLATFORM_DARWIN
        } else if (os.startsWith("Windows")) {              //$NON-NLS-1$
            return PLATFORM_WINDOWS
        } else if (os.startsWith("Linux")) {                //$NON-NLS-1$
            return PLATFORM_LINUX
        }
        return PLATFORM_UNKNOWN
    }

    /**
     * Returns current platform's UI name
     *
     * @return one of "Windows", "Mac OS X", "Linux" or "other".
     */
    fun currentPlatformName(): String {
        val os = System.getProperty("os.name") //$NON-NLS-1$
        if (os.startsWith("Mac OS")) {                      //$NON-NLS-1$
            return "Mac OS X" //$NON-NLS-1$
        } else if (os.startsWith("Windows")) {              //$NON-NLS-1$
            return "Windows" //$NON-NLS-1$
        } else if (os.startsWith("Linux")) {                //$NON-NLS-1$
            return "Linux" //$NON-NLS-1$
        }
        return "Other"
    }

    private fun ext(windowsExtension: String, nonWindowsExtension: String): String {
        return if (CURRENT_PLATFORM == PLATFORM_WINDOWS) {
            windowsExtension
        } else {
            nonWindowsExtension
        }
    }

    /** Default anim resource folder name, i.e. "anim"  */
    const val FD_RES_ANIM: String = "anim" //$NON-NLS-1$

    /** Default animator resource folder name, i.e. "animator"  */
    const val FD_RES_ANIMATOR: String = "animator" //$NON-NLS-1$

    /** Default color resource folder name, i.e. "color"  */
    const val FD_RES_COLOR: String = "color" //$NON-NLS-1$

    /** Default drawable resource folder name, i.e. "drawable"  */
    const val FD_RES_DRAWABLE: String = "drawable" //$NON-NLS-1$

    /** Default interpolator resource folder name, i.e. "interpolator"  */
    const val FD_RES_INTERPOLATOR: String = "interpolator" //$NON-NLS-1$

    /** Default layout resource folder name, i.e. "layout"  */
    const val FD_RES_LAYOUT: String = "layout" //$NON-NLS-1$

    /** Default menu resource folder name, i.e. "menu"  */
    const val FD_RES_MENU: String = "menu" //$NON-NLS-1$

    /** Default menu resource folder name, i.e. "mipmap"  */
    const val FD_RES_MIPMAP: String = "mipmap" //$NON-NLS-1$

    /** Default values resource folder name, i.e. "values"  */
    const val FD_RES_VALUES: String = "values" //$NON-NLS-1$

    /** Default xml resource folder name, i.e. "xml"  */
    const val FD_RES_XML: String = "xml" //$NON-NLS-1$

    /** Default raw resource folder name, i.e. "raw"  */
    const val FD_RES_RAW: String = "raw" //$NON-NLS-1$

    /** Separator between the resource folder qualifier.  */
    const val RES_QUALIFIER_SEP: String = "-" //$NON-NLS-1$
    /** Namespace used in XML files for Android attributes  */ // ---- XML ----
    /** URI of the reserved "xmlns"  prefix  */
    const val XMLNS_URI: String = "http://www.w3.org/2000/xmlns/" //$NON-NLS-1$

    /** The "xmlns" attribute name  */
    const val XMLNS: String = "xmlns" //$NON-NLS-1$

    /** The default prefix used for the [.XMLNS_URI]  */
    const val XMLNS_PREFIX: String = "xmlns:" //$NON-NLS-1$

    /** Qualified name of the xmlns android declaration element  */
    const val XMLNS_ANDROID: String = "xmlns:android" //$NON-NLS-1$

    /** The default prefix used for the [.ANDROID_URI] name space  */
    const val ANDROID_NS_NAME: String = "android" //$NON-NLS-1$

    /** The default prefix used for the [.ANDROID_URI] name space including the colon   */
    const val ANDROID_NS_NAME_PREFIX: String = "android:" //$NON-NLS-1$
    const val ANDROID_NS_NAME_PREFIX_LEN: Int = ANDROID_NS_NAME_PREFIX.length

    /** The default prefix used for the app  */
    const val APP_PREFIX: String = "app" //$NON-NLS-1$

    /** The entity for the ampersand character  */
    const val AMP_ENTITY: String = "&amp;" //$NON-NLS-1$

    /** The entity for the quote character  */
    const val QUOT_ENTITY: String = "&quot;" //$NON-NLS-1$

    /** The entity for the apostrophe character  */
    const val APOS_ENTITY: String = "&apos;" //$NON-NLS-1$

    /** The entity for the less than character  */
    const val LT_ENTITY: String = "&lt;" //$NON-NLS-1$

    /** The entity for the greater than character  */
    const val GT_ENTITY: String = "&gt;" //$NON-NLS-1$
    // ---- Elements and Attributes ----
    /** Namespace prefix used for all resources  */
    const val URI_PREFIX: String = "http://schemas.android.com/apk/res/" //$NON-NLS-1$

    /** Namespace used in XML files for Android attributes  */
    const val ANDROID_URI: String = "http://schemas.android.com/apk/res/android" //$NON-NLS-1$

    /** Namespace used in XML files for Android Tooling attributes  */
    const val TOOLS_URI: String = "http://schemas.android.com/tools" //$NON-NLS-1$

    /** Namespace used for auto-adjusting namespaces  */
    const val AUTO_URI: String = "http://schemas.android.com/apk/res-auto" //$NON-NLS-1$

    /** Default prefix used for tools attributes  */
    const val TOOLS_PREFIX: String = "tools" //$NON-NLS-1$
    const val R_CLASS: String = "R" //$NON-NLS-1$
    const val ANDROID_PKG: String = "android" //$NON-NLS-1$

    // Tags: Manifest
    const val TAG_SERVICE: String = "service" //$NON-NLS-1$
    const val TAG_PERMISSION: String = "permission" //$NON-NLS-1$
    const val TAG_USES_FEATURE: String = "uses-feature" //$NON-NLS-1$
    const val TAG_USES_PERMISSION: String = "uses-permission" //$NON-NLS-1$
    const val TAG_USES_LIBRARY: String = "uses-library" //$NON-NLS-1$
    const val TAG_APPLICATION: String = "application" //$NON-NLS-1$
    const val TAG_INTENT_FILTER: String = "intent-filter" //$NON-NLS-1$
    const val TAG_USES_SDK: String = "uses-sdk" //$NON-NLS-1$
    const val TAG_ACTIVITY: String = "activity" //$NON-NLS-1$
    const val TAG_RECEIVER: String = "receiver" //$NON-NLS-1$
    const val TAG_PROVIDER: String = "provider" //$NON-NLS-1$
    const val TAG_GRANT_PERMISSION: String = "grant-uri-permission" //$NON-NLS-1$
    const val TAG_PATH_PERMISSION: String = "path-permission" //$NON-NLS-1$

    // Tags: Resources
    const val TAG_RESOURCES: String = "resources" //$NON-NLS-1$
    const val TAG_STRING: String = "string" //$NON-NLS-1$
    const val TAG_ARRAY: String = "array" //$NON-NLS-1$
    const val TAG_STYLE: String = "style" //$NON-NLS-1$
    const val TAG_ITEM: String = "item" //$NON-NLS-1$
    const val TAG_GROUP: String = "group" //$NON-NLS-1$
    const val TAG_STRING_ARRAY: String = "string-array" //$NON-NLS-1$
    const val TAG_PLURALS: String = "plurals" //$NON-NLS-1$
    const val TAG_INTEGER_ARRAY: String = "integer-array" //$NON-NLS-1$
    const val TAG_COLOR: String = "color" //$NON-NLS-1$
    const val TAG_DIMEN: String = "dimen" //$NON-NLS-1$
    const val TAG_DRAWABLE: String = "drawable" //$NON-NLS-1$
    const val TAG_MENU: String = "menu" //$NON-NLS-1$
    const val TAG_ENUM: String = "enum" //$NON-NLS-1$
    const val TAG_FLAG: String = "flag" //$NON-NLS-1$
    const val TAG_ATTR: String = "attr" //$NON-NLS-1$
    const val TAG_DECLARE_STYLEABLE: String = "declare-styleable" //$NON-NLS-1$
    const val TAG_EAT_COMMENT: String = "eat-comment" //$NON-NLS-1$
    const val TAG_SKIP: String = "skip" //$NON-NLS-1$
    const val TAG_SELECTOR: String = "selector" //$NON-NLS-1$

    // Tags: XML
    const val TAG_HEADER: String = "header" //$NON-NLS-1$
    const val TAG_APPWIDGET_PROVIDER: String = "appwidget-provider" //$NON-NLS-1$
    const val TAG_PREFERENCE_SCREEN: String = "PreferenceScreen" //$NON-NLS-1$

    // Tags: Layouts
    const val VIEW_TAG: String = "view" //$NON-NLS-1$
    const val VIEW_INCLUDE: String = "include" //$NON-NLS-1$
    const val VIEW_MERGE: String = "merge" //$NON-NLS-1$
    const val VIEW_FRAGMENT: String = "fragment" //$NON-NLS-1$
    const val REQUEST_FOCUS: String = "requestFocus" //$NON-NLS-1$
    const val TAG: String = "tag" //$NON-NLS-1$
    const val VIEW: String = "View" //$NON-NLS-1$
    const val VIEW_GROUP: String = "ViewGroup" //$NON-NLS-1$
    const val FRAME_LAYOUT: String = "FrameLayout" //$NON-NLS-1$
    const val LINEAR_LAYOUT: String = "LinearLayout" //$NON-NLS-1$
    const val RELATIVE_LAYOUT: String = "RelativeLayout" //$NON-NLS-1$
    const val GRID_LAYOUT: String = "GridLayout" //$NON-NLS-1$
    const val SCROLL_VIEW: String = "ScrollView" //$NON-NLS-1$
    const val BUTTON: String = "Button" //$NON-NLS-1$
    const val COMPOUND_BUTTON: String = "CompoundButton" //$NON-NLS-1$
    const val ADAPTER_VIEW: String = "AdapterView" //$NON-NLS-1$
    const val GALLERY: String = "Gallery" //$NON-NLS-1$
    const val GRID_VIEW: String = "GridView" //$NON-NLS-1$
    const val TAB_HOST: String = "TabHost" //$NON-NLS-1$
    const val RADIO_GROUP: String = "RadioGroup" //$NON-NLS-1$
    const val RADIO_BUTTON: String = "RadioButton" //$NON-NLS-1$
    const val SWITCH: String = "Switch" //$NON-NLS-1$
    const val EDIT_TEXT: String = "EditText" //$NON-NLS-1$
    const val LIST_VIEW: String = "ListView" //$NON-NLS-1$
    const val TEXT_VIEW: String = "TextView" //$NON-NLS-1$
    const val CHECKED_TEXT_VIEW: String = "CheckedTextView" //$NON-NLS-1$
    const val IMAGE_VIEW: String = "ImageView" //$NON-NLS-1$
    const val SURFACE_VIEW: String = "SurfaceView" //$NON-NLS-1$
    const val ABSOLUTE_LAYOUT: String = "AbsoluteLayout" //$NON-NLS-1$
    const val TABLE_LAYOUT: String = "TableLayout" //$NON-NLS-1$
    const val TABLE_ROW: String = "TableRow" //$NON-NLS-1$
    const val TAB_WIDGET: String = "TabWidget" //$NON-NLS-1$
    const val IMAGE_BUTTON: String = "ImageButton" //$NON-NLS-1$
    const val SEEK_BAR: String = "SeekBar" //$NON-NLS-1$
    const val VIEW_STUB: String = "ViewStub" //$NON-NLS-1$
    const val SPINNER: String = "Spinner" //$NON-NLS-1$
    const val WEB_VIEW: String = "WebView" //$NON-NLS-1$
    const val TOGGLE_BUTTON: String = "ToggleButton" //$NON-NLS-1$
    const val CHECK_BOX: String = "CheckBox" //$NON-NLS-1$
    const val ABS_LIST_VIEW: String = "AbsListView" //$NON-NLS-1$
    const val PROGRESS_BAR: String = "ProgressBar" //$NON-NLS-1$
    const val ABS_SPINNER: String = "AbsSpinner" //$NON-NLS-1$
    const val ABS_SEEK_BAR: String = "AbsSeekBar" //$NON-NLS-1$
    const val VIEW_ANIMATOR: String = "ViewAnimator" //$NON-NLS-1$
    const val VIEW_SWITCHER: String = "ViewSwitcher" //$NON-NLS-1$
    const val EXPANDABLE_LIST_VIEW: String = "ExpandableListView" //$NON-NLS-1$
    const val HORIZONTAL_SCROLL_VIEW: String = "HorizontalScrollView" //$NON-NLS-1$
    const val MULTI_AUTO_COMPLETE_TEXT_VIEW: String = "MultiAutoCompleteTextView" //$NON-NLS-1$
    const val AUTO_COMPLETE_TEXT_VIEW: String = "AutoCompleteTextView" //$NON-NLS-1$
    const val CHECKABLE: String = "Checkable" //$NON-NLS-1$
    const val TEXTURE_VIEW: String = "TextureView" //$NON-NLS-1$

    /* Android Design Support Tag Constants */
    const val COORDINATOR_LAYOUT: String = CLASS_COORDINATOR_LAYOUT
    const val APP_BAR_LAYOUT: String = CLASS_APP_BAR_LAYOUT
    const val FLOATING_ACTION_BUTTON: String = CLASS_FLOATING_ACTION_BUTTON
    const val COLLAPSING_TOOLBAR_LAYOUT: String = CLASS_COLLAPSING_TOOLBAR_LAYOUT
    const val NAVIGATION_VIEW: String = CLASS_NAVIGATION_VIEW
    const val SNACKBAR: String = CLASS_SNACKBAR
    const val TAB_LAYOUT: String = CLASS_TAB_LAYOUT
    const val TEXT_INPUT_LAYOUT: String = CLASS_TEXT_INPUT_LAYOUT

    // Tags: Drawables
    const val TAG_BITMAP: String = "bitmap" //$NON-NLS-1$

    // Tags: Data-Binding
    const val TAG_LAYOUT: String = "layout" //$NON-NLS-1$
    const val TAG_DATA: String = "data" //$NON-NLS-1$
    const val TAG_VARIABLE: String = "variable" //$NON-NLS-1$
    const val TAG_IMPORT: String = "import" //$NON-NLS-1$

    // Attributes: Manifest
    const val ATTR_EXPORTED: String = "exported" //$NON-NLS-1$
    const val ATTR_PERMISSION: String = "permission" //$NON-NLS-1$
    const val ATTR_MIN_SDK_VERSION: String = "minSdkVersion" //$NON-NLS-1$
    const val ATTR_TARGET_SDK_VERSION: String = "targetSdkVersion" //$NON-NLS-1$
    const val ATTR_ICON: String = "icon" //$NON-NLS-1$
    const val ATTR_PACKAGE: String = "package" //$NON-NLS-1$
    const val ATTR_CORE_APP: String = "coreApp" //$NON-NLS-1$
    const val ATTR_THEME: String = "theme" //$NON-NLS-1$
    const val ATTR_SCHEME: String = "scheme" //$NON_NLS-1$
    const val ATTR_HOST: String = "host" //$NON_NLS-1$
    const val ATTR_PATH: String = "path" //$NON-NLS-1$
    const val ATTR_PATH_PREFIX: String = "pathPrefix" //$NON-NLS-1$
    const val ATTR_PATH_PATTERN: String = "pathPattern" //$NON-NLS-1$
    const val ATTR_ALLOW_BACKUP: String = "allowBackup" //$NON_NLS-1$
    const val ATTR_DEBUGGABLE: String = "debuggable" //$NON-NLS-1$
    const val ATTR_READ_PERMISSION: String = "readPermission" //$NON_NLS-1$
    const val ATTR_WRITE_PERMISSION: String = "writePermission" //$NON_NLS-1$
    const val ATTR_VERSION_CODE: String = "versionCode" //$NON_NLS-1$
    const val ATTR_VERSION_NAME: String = "versionName" //$NON_NLS-1$

    // Attributes: Resources
    const val ATTR_NAME: String = "name" //$NON-NLS-1$
    const val ATTR_FRAGMENT: String = "fragment" //$NON-NLS-1$
    const val ATTR_TYPE: String = "type" //$NON-NLS-1$
    const val ATTR_PARENT: String = "parent" //$NON-NLS-1$
    const val ATTR_TRANSLATABLE: String = "translatable" //$NON-NLS-1$
    const val ATTR_COLOR: String = "color" //$NON-NLS-1$
    const val ATTR_DRAWABLE: String = "drawable" //$NON-NLS-1$
    const val ATTR_VALUE: String = "value" //$NON-NLS-1$
    const val ATTR_QUANTITY: String = "quantity" //$NON-NLS-1$
    const val ATTR_FORMAT: String = "format" //$NON-NLS-1$
    const val ATTR_PREPROCESSING: String = "preprocessing" //$NON-NLS-1$

    // Attributes: Data-Binding
    const val ATTR_ALIAS: String = "alias" //$NON-NLS-1$

    // Attributes: Layout
    const val ATTR_LAYOUT_RESOURCE_PREFIX: String = "layout_" //$NON-NLS-1$
    const val ATTR_CLASS: String = "class" //$NON-NLS-1$
    const val ATTR_STYLE: String = "style" //$NON-NLS-1$
    const val ATTR_CONTEXT: String = "context" //$NON-NLS-1$
    const val ATTR_ID: String = "id" //$NON-NLS-1$
    const val ATTR_TEXT: String = "text" //$NON-NLS-1$
    const val ATTR_TEXT_SIZE: String = "textSize" //$NON-NLS-1$
    const val ATTR_LABEL: String = "label" //$NON-NLS-1$
    const val ATTR_HINT: String = "hint" //$NON-NLS-1$
    const val ATTR_PROMPT: String = "prompt" //$NON-NLS-1$
    const val ATTR_ON_CLICK: String = "onClick" //$NON-NLS-1$
    const val ATTR_INPUT_TYPE: String = "inputType" //$NON-NLS-1$
    const val ATTR_INPUT_METHOD: String = "inputMethod" //$NON-NLS-1$
    const val ATTR_LAYOUT_GRAVITY: String = "layout_gravity" //$NON-NLS-1$
    const val ATTR_LAYOUT_WIDTH: String = "layout_width" //$NON-NLS-1$
    const val ATTR_LAYOUT_HEIGHT: String = "layout_height" //$NON-NLS-1$
    const val ATTR_LAYOUT_WEIGHT: String = "layout_weight" //$NON-NLS-1$
    const val ATTR_PADDING: String = "padding" //$NON-NLS-1$
    const val ATTR_PADDING_BOTTOM: String = "paddingBottom" //$NON-NLS-1$
    const val ATTR_PADDING_TOP: String = "paddingTop" //$NON-NLS-1$
    const val ATTR_PADDING_RIGHT: String = "paddingRight" //$NON-NLS-1$
    const val ATTR_PADDING_LEFT: String = "paddingLeft" //$NON-NLS-1$
    const val ATTR_PADDING_START: String = "paddingStart" //$NON-NLS-1$
    const val ATTR_PADDING_END: String = "paddingEnd" //$NON-NLS-1$
    const val ATTR_FOREGROUND: String = "foreground" //$NON-NLS-1$
    const val ATTR_BACKGROUND: String = "background" //$NON-NLS-1$
    const val ATTR_ORIENTATION: String = "orientation" //$NON-NLS-1$
    const val ATTR_LAYOUT: String = "layout" //$NON-NLS-1$
    const val ATTR_ROW_COUNT: String = "rowCount" //$NON-NLS-1$
    const val ATTR_COLUMN_COUNT: String = "columnCount" //$NON-NLS-1$
    const val ATTR_LABEL_FOR: String = "labelFor" //$NON-NLS-1$
    const val ATTR_BASELINE_ALIGNED: String = "baselineAligned" //$NON-NLS-1$
    const val ATTR_CONTENT_DESCRIPTION: String = "contentDescription" //$NON-NLS-1$
    const val ATTR_IME_ACTION_LABEL: String = "imeActionLabel" //$NON-NLS-1$
    const val ATTR_PRIVATE_IME_OPTIONS: String = "privateImeOptions" //$NON-NLS-1$
    const val VALUE_NONE: String = "none" //$NON-NLS-1$
    const val VALUE_NO: String = "no" //$NON-NLS-1$
    const val ATTR_NUMERIC: String = "numeric" //$NON-NLS-1$
    const val ATTR_IME_ACTION_ID: String = "imeActionId" //$NON-NLS-1$
    const val ATTR_IME_OPTIONS: String = "imeOptions" //$NON-NLS-1$
    const val ATTR_FREEZES_TEXT: String = "freezesText" //$NON-NLS-1$
    const val ATTR_EDITOR_EXTRAS: String = "editorExtras" //$NON-NLS-1$
    const val ATTR_EDITABLE: String = "editable" //$NON-NLS-1$
    const val ATTR_DIGITS: String = "digits" //$NON-NLS-1$
    const val ATTR_CURSOR_VISIBLE: String = "cursorVisible" //$NON-NLS-1$
    const val ATTR_CAPITALIZE: String = "capitalize" //$NON-NLS-1$
    const val ATTR_PHONE_NUMBER: String = "phoneNumber" //$NON-NLS-1$
    const val ATTR_PASSWORD: String = "password" //$NON-NLS-1$
    const val ATTR_BUFFER_TYPE: String = "bufferType" //$NON-NLS-1$
    const val ATTR_AUTO_TEXT: String = "autoText" //$NON-NLS-1$
    const val ATTR_ENABLED: String = "enabled" //$NON-NLS-1$
    const val ATTR_SINGLE_LINE: String = "singleLine" //$NON-NLS-1$
    const val ATTR_SCALE_TYPE: String = "scaleType" //$NON-NLS-1$
    const val ATTR_VISIBILITY: String = "visibility" //$NON-NLS-1$
    const val ATTR_TEXT_IS_SELECTABLE: String = "textIsSelectable" //$NON-NLS-1$
    const val ATTR_IMPORTANT_FOR_ACCESSIBILITY: String = "importantForAccessibility" //$NON-NLS-1$
    const val ATTR_LIST_PREFERRED_ITEM_PADDING_LEFT: String =
        "listPreferredItemPaddingLeft" //$NON-NLS-1$
    const val ATTR_LIST_PREFERRED_ITEM_PADDING_RIGHT: String =
        "listPreferredItemPaddingRight" //$NON-NLS-1$
    const val ATTR_LIST_PREFERRED_ITEM_PADDING_START: String =
        "listPreferredItemPaddingStart" //$NON-NLS-1$
    const val ATTR_LIST_PREFERRED_ITEM_PADDING_END: String =
        "listPreferredItemPaddingEnd" //$NON-NLS-1$
    const val ATTR_INDEX: String = "index" //$NON-NLS-1$

    // AbsoluteLayout layout params
    const val ATTR_LAYOUT_Y: String = "layout_y" //$NON-NLS-1$
    const val ATTR_LAYOUT_X: String = "layout_x" //$NON-NLS-1$

    // GridLayout layout params
    const val ATTR_LAYOUT_ROW: String = "layout_row" //$NON-NLS-1$
    const val ATTR_LAYOUT_ROW_SPAN: String = "layout_rowSpan" //$NON-NLS-1$
    const val ATTR_LAYOUT_COLUMN: String = "layout_column" //$NON-NLS-1$
    const val ATTR_LAYOUT_COLUMN_SPAN: String = "layout_columnSpan" //$NON-NLS-1$

    // TableRow
    const val ATTR_LAYOUT_SPAN: String = "layout_span" //$NON-NLS-1$

    // RelativeLayout layout params:
    const val ATTR_LAYOUT_ALIGN_LEFT: String = "layout_alignLeft" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_RIGHT: String = "layout_alignRight" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_START: String = "layout_alignStart" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_END: String = "layout_alignEnd" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_TOP: String = "layout_alignTop" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_BOTTOM: String = "layout_alignBottom" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_PARENT_LEFT: String = "layout_alignParentLeft" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_PARENT_RIGHT: String = "layout_alignParentRight" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_PARENT_START: String = "layout_alignParentStart" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_PARENT_END: String = "layout_alignParentEnd" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_PARENT_TOP: String = "layout_alignParentTop" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_PARENT_BOTTOM: String = "layout_alignParentBottom" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_WITH_PARENT_MISSING: String =
        "layout_alignWithParentIfMissing" //$NON-NLS-1$
    const val ATTR_LAYOUT_ALIGN_BASELINE: String = "layout_alignBaseline" //$NON-NLS-1$
    const val ATTR_LAYOUT_CENTER_IN_PARENT: String = "layout_centerInParent" //$NON-NLS-1$
    const val ATTR_LAYOUT_CENTER_VERTICAL: String = "layout_centerVertical" //$NON-NLS-1$
    const val ATTR_LAYOUT_CENTER_HORIZONTAL: String = "layout_centerHorizontal" //$NON-NLS-1$
    const val ATTR_LAYOUT_TO_RIGHT_OF: String = "layout_toRightOf" //$NON-NLS-1$
    const val ATTR_LAYOUT_TO_LEFT_OF: String = "layout_toLeftOf" //$NON-NLS-1$
    const val ATTR_LAYOUT_TO_START_OF: String = "layout_toStartOf" //$NON-NLS-1$
    const val ATTR_LAYOUT_TO_END_OF: String = "layout_toEndOf" //$NON-NLS-1$
    const val ATTR_LAYOUT_BELOW: String = "layout_below" //$NON-NLS-1$
    const val ATTR_LAYOUT_ABOVE: String = "layout_above" //$NON-NLS-1$

    // Margins
    const val ATTR_LAYOUT_MARGIN: String = "layout_margin" //$NON-NLS-1$
    const val ATTR_LAYOUT_MARGIN_LEFT: String = "layout_marginLeft" //$NON-NLS-1$
    const val ATTR_LAYOUT_MARGIN_RIGHT: String = "layout_marginRight" //$NON-NLS-1$
    const val ATTR_LAYOUT_MARGIN_START: String = "layout_marginStart" //$NON-NLS-1$
    const val ATTR_LAYOUT_MARGIN_END: String = "layout_marginEnd" //$NON-NLS-1$
    const val ATTR_LAYOUT_MARGIN_TOP: String = "layout_marginTop" //$NON-NLS-1$
    const val ATTR_LAYOUT_MARGIN_BOTTOM: String = "layout_marginBottom" //$NON-NLS-1$

    // Attributes: Drawables
    const val ATTR_TILE_MODE: String = "tileMode" //$NON-NLS-1$

    // Attributes: CoordinatorLayout
    const val ATTR_LAYOUT_ANCHOR: String = "layout_anchor" //$NON-NLS-1$
    const val ATTR_LAYOUT_ANCHOR_GRAVITY: String = "layout_anchorGravity" //$NON-NLS-1$
    const val ATTR_LAYOUT_BEHAVIOR: String = "layout_behavior" //$NON-NLS-1$
    const val ATTR_LAYOUT_KEYLINE: String = "layout_keyline" //$NON-NLS-1$

    // Values: Manifest
    const val VALUE_SPLIT_ACTION_BAR_WHEN_NARROW: String = "splitActionBarWhenNarrow" // NON-NLS-$1

    // Values: Layouts
    const val VALUE_FILL_PARENT: String = "fill_parent" //$NON-NLS-1$
    const val VALUE_MATCH_PARENT: String = "match_parent" //$NON-NLS-1$
    const val VALUE_VERTICAL: String = "vertical" //$NON-NLS-1$
    const val VALUE_TRUE: String = "true" //$NON-NLS-1$
    const val VALUE_EDITABLE: String = "editable" //$NON-NLS-1$
    const val VALUE_AUTO_FIT: String = "auto_fit" //$NON-NLS-1$
    const val VALUE_SELECTABLE_ITEM_BACKGROUND: String =
        "?android:attr/selectableItemBackground" //$NON-NLS-1$

    // Values: Resources
    const val VALUE_ID: String = "id" //$NON-NLS-1$

    // Values: Drawables
    const val VALUE_DISABLED: String = "disabled" //$NON-NLS-1$
    const val VALUE_CLAMP: String = "clamp" //$NON-NLS-1$

    // Menus
    const val ATTR_SHOW_AS_ACTION: String = "showAsAction" //$NON-NLS-1$
    const val ATTR_TITLE: String = "title" //$NON-NLS-1$
    const val ATTR_VISIBLE: String = "visible" //$NON-NLS-1$
    const val VALUE_IF_ROOM: String = "ifRoom" //$NON-NLS-1$
    const val VALUE_ALWAYS: String = "always" //$NON-NLS-1$

    // Units
    const val UNIT_DP: String = "dp" //$NON-NLS-1$
    const val UNIT_DIP: String = "dip" //$NON-NLS-1$
    const val UNIT_SP: String = "sp" //$NON-NLS-1$
    const val UNIT_PX: String = "px" //$NON-NLS-1$
    const val UNIT_IN: String = "in" //$NON-NLS-1$
    const val UNIT_MM: String = "mm" //$NON-NLS-1$
    const val UNIT_PT: String = "pt" //$NON-NLS-1$

    // Filenames and folder names
    const val ANDROID_MANIFEST_XML: String = "AndroidManifest.xml" //$NON-NLS-1$
    const val OLD_PROGUARD_FILE: String = "proguard.cfg" //$NON-NLS-1$
    val CLASS_FOLDER: String = "bin" + File.separator + "classes" //$NON-NLS-1$ //$NON-NLS-2$
    const val GEN_FOLDER: String = "gen" //$NON-NLS-1$
    const val SRC_FOLDER: String = "src" //$NON-NLS-1$
    const val LIBS_FOLDER: String = "libs" //$NON-NLS-1$
    const val BIN_FOLDER: String = "bin" //$NON-NLS-1$
    const val RES_FOLDER: String = "res" //$NON-NLS-1$
    const val DOT_XML: String = ".xml" //$NON-NLS-1$
    const val DOT_XSD: String = ".xsd" //$NON-NLS-1$
    const val DOT_GIF: String = ".gif" //$NON-NLS-1$
    const val DOT_JPG: String = ".jpg" //$NON-NLS-1$
    const val DOT_JPEG: String = ".jpeg" //$NON-NLS-1$
    const val DOT_WEBP: String = ".webp" //$NON-NLS-1$
    const val DOT_PNG: String = ".png" //$NON-NLS-1$
    const val DOT_9PNG: String = ".9.png" //$NON-NLS-1$
    const val DOT_JAVA: String = ".java" //$NON-NLS-1$
    const val DOT_CLASS: String = ".class" //$NON-NLS-1$
    const val DOT_JAR: String = ".jar" //$NON-NLS-1$
    const val DOT_GRADLE: String = ".gradle" //$NON-NLS-1$
    const val DOT_PROPERTIES: String = ".properties" //$NON-NLS-1$

    /** Extension of the Application package Files, i.e. "apk".  */
    const val EXT_ANDROID_PACKAGE: String = "apk" //$NON-NLS-1$

    /** Extension of java files, i.e. "java"  */
    const val EXT_JAVA: String = "java" //$NON-NLS-1$

    /** Extension of compiled java files, i.e. "class"  */
    const val EXT_CLASS: String = "class" //$NON-NLS-1$

    /** Extension of xml files, i.e. "xml"  */
    const val EXT_XML: String = "xml" //$NON-NLS-1$

    /** Extension of gradle files, i.e. "gradle"  */
    const val EXT_GRADLE: String = "gradle" //$NON-NLS-1$

    /** Extension of jar files, i.e. "jar"  */
    const val EXT_JAR: String = "jar" //$NON-NLS-1$

    /** Extension of ZIP files, i.e. "zip"  */
    const val EXT_ZIP: String = "zip" //$NON-NLS-1$

    /** Extension of aidl files, i.e. "aidl"  */
    const val EXT_AIDL: String = "aidl" //$NON-NLS-1$

    /** Extension of Renderscript files, i.e. "rs"  */
    const val EXT_RS: String = "rs" //$NON-NLS-1$

    /** Extension of Renderscript files, i.e. "rsh"  */
    const val EXT_RSH: String = "rsh" //$NON-NLS-1$

    /** Extension of FilterScript files, i.e. "fs"  */
    const val EXT_FS: String = "fs" //$NON-NLS-1$

    /** Extension of Renderscript bitcode files, i.e. "bc"  */
    const val EXT_BC: String = "bc" //$NON-NLS-1$

    /** Extension of dependency files, i.e. "d"  */
    const val EXT_DEP: String = "d" //$NON-NLS-1$

    /** Extension of native libraries, i.e. "so"  */
    const val EXT_NATIVE_LIB: String = "so" //$NON-NLS-1$

    /** Extension of dex files, i.e. "dex"  */
    const val EXT_DEX: String = "dex" //$NON-NLS-1$

    /** Extension for temporary resource files, ie "ap_  */
    const val EXT_RES: String = "ap_" //$NON-NLS-1$

    /** Extension for pre-processable images. Right now pngs  */
    const val EXT_PNG: String = "png" //$NON-NLS-1$

    /** Extension for Android archive files  */
    const val EXT_AAR: String = "aar" //$NON-NLS-1$

    /** Extension for Java heap dumps.  */
    const val EXT_HPROF: String = "hprof" //$NON-NLS-1$
    private const val DOT = "." //$NON-NLS-1$

    /** Dot-Extension of the Application package Files, i.e. ".apk".  */
    const val DOT_ANDROID_PACKAGE: String = DOT + EXT_ANDROID_PACKAGE

    /** Dot-Extension of aidl files, i.e. ".aidl"  */
    const val DOT_AIDL: String = DOT + EXT_AIDL

    /** Dot-Extension of renderscript files, i.e. ".rs"  */
    const val DOT_RS: String = DOT + EXT_RS

    /** Dot-Extension of renderscript header files, i.e. ".rsh"  */
    const val DOT_RSH: String = DOT + EXT_RSH

    /** Dot-Extension of FilterScript files, i.e. ".fs"  */
    const val DOT_FS: String = DOT + EXT_FS

    /** Dot-Extension of renderscript bitcode files, i.e. ".bc"  */
    const val DOT_BC: String = DOT + EXT_BC

    /** Dot-Extension of dependency files, i.e. ".d"  */
    const val DOT_DEP: String = DOT + EXT_DEP

    /** Dot-Extension of dex files, i.e. ".dex"  */
    const val DOT_DEX: String = DOT + EXT_DEX

    /** Dot-Extension for temporary resource files, ie "ap_  */
    const val DOT_RES: String = DOT + EXT_RES

    /** Dot-Extension for BMP files, i.e. ".bmp"  */
    const val DOT_BMP: String = ".bmp" //$NON-NLS-1$

    /** Dot-Extension for SVG files, i.e. ".svg"  */
    const val DOT_SVG: String = ".svg" //$NON-NLS-1$

    /** Dot-Extension for template files  */
    const val DOT_FTL: String = ".ftl" //$NON-NLS-1$

    /** Dot-Extension of text files, i.e. ".txt"  */
    const val DOT_TXT: String = ".txt" //$NON-NLS-1$

    /** Dot-Extension for Android archive files  */
    const val DOT_AAR: String = DOT + EXT_AAR //$NON-NLS-1$

    /** Dot-Extension for Java heap dumps.  */
    const val DOT_HPROF: String = DOT + EXT_HPROF //$NON-NLS-1$

    /** Resource base name for java files and classes  */
    const val FN_RESOURCE_BASE: String = "R" //$NON-NLS-1$

    /** Resource java class  filename, i.e. "R.java"  */
    const val FN_RESOURCE_CLASS: String = FN_RESOURCE_BASE + DOT_JAVA

    /** Resource class file  filename, i.e. "R.class"  */
    const val FN_COMPILED_RESOURCE_CLASS: String = FN_RESOURCE_BASE + DOT_CLASS

    /** Resource text filename, i.e. "R.txt"  */
    const val FN_RESOURCE_TEXT: String = FN_RESOURCE_BASE + DOT_TXT

    /** Filename for public resources in AAR archives  */
    const val FN_PUBLIC_TXT: String = "public.txt"

    /** Generated manifest class name  */
    const val FN_MANIFEST_BASE: String = "Manifest" //$NON-NLS-1$

    /** Generated BuildConfig class name  */
    const val FN_BUILD_CONFIG_BASE: String = "BuildConfig" //$NON-NLS-1$

    /** Manifest java class filename, i.e. "Manifest.java"  */
    const val FN_MANIFEST_CLASS: String = FN_MANIFEST_BASE + DOT_JAVA

    /** BuildConfig java class filename, i.e. "BuildConfig.java"  */
    const val FN_BUILD_CONFIG: String = FN_BUILD_CONFIG_BASE + DOT_JAVA
    const val DRAWABLE_FOLDER: String = "drawable" //$NON-NLS-1$
    const val DRAWABLE_XHDPI: String = "drawable-xhdpi" //$NON-NLS-1$
    const val DRAWABLE_XXHDPI: String = "drawable-xxhdpi" //$NON-NLS-1$
    const val DRAWABLE_XXXHDPI: String = "drawable-xxxhdpi" //$NON-NLS-1$
    const val DRAWABLE_HDPI: String = "drawable-hdpi" //$NON-NLS-1$
    const val DRAWABLE_MDPI: String = "drawable-mdpi" //$NON-NLS-1$
    const val DRAWABLE_LDPI: String = "drawable-ldpi" //$NON-NLS-1$

    // Resources
    const val PREFIX_RESOURCE_REF: String = "@" //$NON-NLS-1$
    const val PREFIX_THEME_REF: String = "?" //$NON-NLS-1$
    const val PREFIX_BINDING_EXPR: String = "@{" //$NON-NLS-1$
    const val ANDROID_PREFIX: String = "@android:" //$NON-NLS-1$
    const val ANDROID_THEME_PREFIX: String = "?android:" //$NON-NLS-1$
    const val LAYOUT_RESOURCE_PREFIX: String = "@layout/" //$NON-NLS-1$
    const val STYLE_RESOURCE_PREFIX: String = "@style/" //$NON-NLS-1$
    const val COLOR_RESOURCE_PREFIX: String = "@color/" //$NON-NLS-1$
    const val NEW_ID_PREFIX: String = "@+id/" //$NON-NLS-1$
    const val ID_PREFIX: String = "@id/" //$NON-NLS-1$
    const val DRAWABLE_PREFIX: String = "@drawable/" //$NON-NLS-1$
    const val STRING_PREFIX: String = "@string/" //$NON-NLS-1$
    const val DIMEN_PREFIX: String = "@dimen/" //$NON-NLS-1$
    const val MIPMAP_PREFIX: String = "@mipmap/" //$NON-NLS-1$
    const val ANDROID_LAYOUT_RESOURCE_PREFIX: String = "@android:layout/" //$NON-NLS-1$
    const val ANDROID_STYLE_RESOURCE_PREFIX: String = "@android:style/" //$NON-NLS-1$
    const val ANDROID_COLOR_RESOURCE_PREFIX: String = "@android:color/" //$NON-NLS-1$
    const val ANDROID_NEW_ID_PREFIX: String = "@android:+id/" //$NON-NLS-1$
    const val ANDROID_ID_PREFIX: String = "@android:id/" //$NON-NLS-1$
    const val ANDROID_DRAWABLE_PREFIX: String = "@android:drawable/" //$NON-NLS-1$
    const val ANDROID_STRING_PREFIX: String = "@android:string/" //$NON-NLS-1$
    const val RESOURCE_CLZ_ID: String = "id" //$NON-NLS-1$
    const val RESOURCE_CLZ_COLOR: String = "color" //$NON-NLS-1$
    const val RESOURCE_CLZ_ARRAY: String = "array" //$NON-NLS-1$
    const val RESOURCE_CLZ_ATTR: String = "attr" //$NON-NLS-1$
    const val RESOURCE_CLR_STYLEABLE: String = "styleable" //$NON-NLS-1$
    const val NULL_RESOURCE: String = "@null" //$NON-NLS-1$
    const val TRANSPARENT_COLOR: String = "@android:color/transparent" //$NON-NLS-1$
    const val REFERENCE_STYLE: String = "style/" //$NON-NLS-1$
    const val PREFIX_ANDROID: String = "android:" //$NON-NLS-1$

    // Resource Types
    const val DRAWABLE_TYPE: String = "drawable" //$NON-NLS-1$
    const val MENU_TYPE: String = "menu" //$NON-NLS-1$

    // Packages
    const val ANDROID_PKG_PREFIX: String = "android." //$NON-NLS-1$
    const val WIDGET_PKG_PREFIX: String = "android.widget." //$NON-NLS-1$
    const val VIEW_PKG_PREFIX: String = "android.view." //$NON-NLS-1$

    // Project properties
    const val ANDROID_LIBRARY: String = "android.library" //$NON-NLS-1$
    const val PROGUARD_CONFIG: String = "proguard.config" //$NON-NLS-1$
    const val ANDROID_LIBRARY_REFERENCE_FORMAT: String =
        "android.library.reference.%1\$d" //$NON-NLS-1$
    const val PROJECT_PROPERTIES: String = "project.properties" //$NON-NLS-1$

    // Java References
    const val ATTR_REF_PREFIX: String = "?attr/" //$NON-NLS-1$
    const val R_PREFIX: String = "R." //$NON-NLS-1$
    const val R_ID_PREFIX: String = "R.id." //$NON-NLS-1$
    const val R_LAYOUT_RESOURCE_PREFIX: String = "R.layout." //$NON-NLS-1$
    const val R_DRAWABLE_PREFIX: String = "R.drawable." //$NON-NLS-1$
    const val R_STYLEABLE_PREFIX: String = "R.styleable." //$NON-NLS-1$
    const val R_ATTR_PREFIX: String = "R.attr." //$NON-NLS-1$

    // Attributes related to tools
    const val ATTR_IGNORE: String = "ignore" //$NON-NLS-1$
    const val ATTR_LOCALE: String = "locale" //$NON-NLS-1$

    // SuppressLint
    const val SUPPRESS_ALL: String = "all" //$NON-NLS-1$
    const val SUPPRESS_LINT: String = "SuppressLint" //$NON-NLS-1$
    const val TARGET_API: String = "TargetApi" //$NON-NLS-1$
    const val ATTR_TARGET_API: String = "targetApi" //$NON-NLS-1$
    const val FQCN_SUPPRESS_LINT: String = "android.annotation." + SUPPRESS_LINT //$NON-NLS-1$
    const val FQCN_TARGET_API: String = "android.annotation." + TARGET_API //$NON-NLS-1$

    // Class Names
    const val CONSTRUCTOR_NAME: String = "<init>" //$NON-NLS-1$
    const val CLASS_CONSTRUCTOR: String = "<clinit>" //$NON-NLS-1$
    const val FRAGMENT: String = "android/app/Fragment" //$NON-NLS-1$
    const val FRAGMENT_V4: String = "android/support/v4/app/Fragment" //$NON-NLS-1$
    const val ANDROID_APP_ACTIVITY: String = "android/app/Activity" //$NON-NLS-1$
    const val ANDROID_APP_SERVICE: String = "android/app/Service" //$NON-NLS-1$
    const val ANDROID_CONTENT_CONTENT_PROVIDER: String =
        "android/content/ContentProvider" //$NON-NLS-1$
    const val ANDROID_CONTENT_BROADCAST_RECEIVER: String =
        "android/content/BroadcastReceiver" //$NON-NLS-1$
    const val ANDROID_VIEW_VIEW: String = "android/view/View" //$NON-NLS-1$

    // Method Names
    const val FORMAT_METHOD: String = "format" //$NON-NLS-1$
    const val GET_STRING_METHOD: String = "getString" //$NON-NLS-1$
    const val ATTR_TAG: String = "tag" //$NON-NLS-1$
    const val ATTR_NUM_COLUMNS: String = "numColumns" //$NON-NLS-1$

    // Some common layout element names
    const val CALENDAR_VIEW: String = "CalendarView" //$NON-NLS-1$
    const val SPACE: String = "Space" //$NON-NLS-1$
    const val GESTURE_OVERLAY_VIEW: String = "GestureOverlayView" //$NON-NLS-1$
    const val ATTR_HANDLE: String = "handle" //$NON-NLS-1$
    const val ATTR_CONTENT: String = "content" //$NON-NLS-1$
    const val ATTR_CHECKED: String = "checked" //$NON-NLS-1$

    // TextView
    const val ATTR_DRAWABLE_RIGHT: String = "drawableRight" //$NON-NLS-1$
    const val ATTR_DRAWABLE_LEFT: String = "drawableLeft" //$NON-NLS-1$
    const val ATTR_DRAWABLE_START: String = "drawableStart" //$NON-NLS-1$
    const val ATTR_DRAWABLE_END: String = "drawableEnd" //$NON-NLS-1$
    const val ATTR_DRAWABLE_BOTTOM: String = "drawableBottom" //$NON-NLS-1$
    const val ATTR_DRAWABLE_TOP: String = "drawableTop" //$NON-NLS-1$
    const val ATTR_DRAWABLE_PADDING: String = "drawablePadding" //$NON-NLS-1$
    const val ATTR_USE_DEFAULT_MARGINS: String = "useDefaultMargins" //$NON-NLS-1$
    const val ATTR_MARGINS_INCLUDED_IN_ALIGNMENT: String =
        "marginsIncludedInAlignment" //$NON-NLS-1$
    const val VALUE_WRAP_CONTENT: String = "wrap_content" //$NON-NLS-1$
    const val VALUE_FALSE: String = "false" //$NON-NLS-1$
    const val VALUE_N_DP: String = "%ddp" //$NON-NLS-1$
    const val VALUE_ZERO_DP: String = "0dp" //$NON-NLS-1$
    const val VALUE_ONE_DP: String = "1dp" //$NON-NLS-1$
    const val VALUE_TOP: String = "top" //$NON-NLS-1$
    const val VALUE_BOTTOM: String = "bottom" //$NON-NLS-1$
    const val VALUE_CENTER_VERTICAL: String = "center_vertical" //$NON-NLS-1$
    const val VALUE_CENTER_HORIZONTAL: String = "center_horizontal" //$NON-NLS-1$
    const val VALUE_FILL_HORIZONTAL: String = "fill_horizontal" //$NON-NLS-1$
    const val VALUE_FILL_VERTICAL: String = "fill_vertical" //$NON-NLS-1$
    const val VALUE_0: String = "0" //$NON-NLS-1$
    const val VALUE_1: String = "1" //$NON-NLS-1$

    // Gravity values. These have the GRAVITY_ prefix in front of value because we already
    // have VALUE_CENTER_HORIZONTAL defined for layouts, and its definition conflicts
    // (centerHorizontal versus center_horizontal)
    const val GRAVITY_VALUE_: String = "center" //$NON-NLS-1$
    const val GRAVITY_VALUE_CENTER: String = "center" //$NON-NLS-1$
    const val GRAVITY_VALUE_LEFT: String = "left" //$NON-NLS-1$
    const val GRAVITY_VALUE_RIGHT: String = "right" //$NON-NLS-1$
    const val GRAVITY_VALUE_START: String = "start" //$NON-NLS-1$
    const val GRAVITY_VALUE_END: String = "end" //$NON-NLS-1$
    const val GRAVITY_VALUE_BOTTOM: String = "bottom" //$NON-NLS-1$
    const val GRAVITY_VALUE_TOP: String = "top" //$NON-NLS-1$
    const val GRAVITY_VALUE_FILL_HORIZONTAL: String = "fill_horizontal" //$NON-NLS-1$
    const val GRAVITY_VALUE_FILL_VERTICAL: String = "fill_vertical" //$NON-NLS-1$
    const val GRAVITY_VALUE_CENTER_HORIZONTAL: String = "center_horizontal" //$NON-NLS-1$
    const val GRAVITY_VALUE_CENTER_VERTICAL: String = "center_vertical" //$NON-NLS-1$
    const val GRAVITY_VALUE_FILL: String = "fill" //$NON-NLS-1$

    /**
     * The top level android package as a prefix, "android.".
     */
    const val ANDROID_SUPPORT_PKG_PREFIX: String = ANDROID_PKG_PREFIX + "support." //$NON-NLS-1$

    /** The android.view. package prefix  */
    const val ANDROID_VIEW_PKG: String = ANDROID_PKG_PREFIX + "view." //$NON-NLS-1$

    /** The android.widget. package prefix  */
    const val ANDROID_WIDGET_PREFIX: String = ANDROID_PKG_PREFIX + "widget." //$NON-NLS-1$

    /** The android.webkit. package prefix  */
    const val ANDROID_WEBKIT_PKG: String = ANDROID_PKG_PREFIX + "webkit." //$NON-NLS-1$

    /** The LayoutParams inner-class name suffix, .LayoutParams  */
    const val DOT_LAYOUT_PARAMS: String = ".LayoutParams" //$NON-NLS-1$

    /** The fully qualified class name of an EditText view  */
    const val FQCN_EDIT_TEXT: String = "android.widget.EditText" //$NON-NLS-1$

    /** The fully qualified class name of a LinearLayout view  */
    const val FQCN_LINEAR_LAYOUT: String = "android.widget.LinearLayout" //$NON-NLS-1$

    /** The fully qualified class name of a RelativeLayout view  */
    const val FQCN_RELATIVE_LAYOUT: String = "android.widget.RelativeLayout" //$NON-NLS-1$

    /** The fully qualified class name of a RelativeLayout view  */
    const val FQCN_GRID_LAYOUT: String = "android.widget.GridLayout" //$NON-NLS-1$
    const val FQCN_GRID_LAYOUT_V7: String = "android.support.v7.widget.GridLayout" //$NON-NLS-1$

    /** The fully qualified class name of a FrameLayout view  */
    const val FQCN_FRAME_LAYOUT: String = "android.widget.FrameLayout" //$NON-NLS-1$

    /** The fully qualified class name of a TableRow view  */
    const val FQCN_TABLE_ROW: String = "android.widget.TableRow" //$NON-NLS-1$

    /** The fully qualified class name of a TableLayout view  */
    const val FQCN_TABLE_LAYOUT: String = "android.widget.TableLayout" //$NON-NLS-1$

    /** The fully qualified class name of a GridView view  */
    const val FQCN_GRID_VIEW: String = "android.widget.GridView" //$NON-NLS-1$

    /** The fully qualified class name of a TabWidget view  */
    const val FQCN_TAB_WIDGET: String = "android.widget.TabWidget" //$NON-NLS-1$

    /** The fully qualified class name of a Button view  */
    const val FQCN_BUTTON: String = "android.widget.Button" //$NON-NLS-1$

    /** The fully qualified class name of a RadioButton view  */
    const val FQCN_RADIO_BUTTON: String = "android.widget.RadioButton" //$NON-NLS-1$

    /** The fully qualified class name of a ToggleButton view  */
    const val FQCN_TOGGLE_BUTTON: String = "android.widget.ToggleButton" //$NON-NLS-1$

    /** The fully qualified class name of a Spinner view  */
    const val FQCN_SPINNER: String = "android.widget.Spinner" //$NON-NLS-1$

    /** The fully qualified class name of an AdapterView  */
    const val FQCN_ADAPTER_VIEW: String = "android.widget.AdapterView" //$NON-NLS-1$

    /** The fully qualified class name of a ListView  */
    const val FQCN_LIST_VIEW: String = "android.widget.ListView" //$NON-NLS-1$

    /** The fully qualified class name of an ExpandableListView  */
    const val FQCN_EXPANDABLE_LIST_VIEW: String = "android.widget.ExpandableListView" //$NON-NLS-1$

    /** The fully qualified class name of a GestureOverlayView  */
    const val FQCN_GESTURE_OVERLAY_VIEW: String = "android.gesture.GestureOverlayView" //$NON-NLS-1$

    /** The fully qualified class name of a DatePicker  */
    const val FQCN_DATE_PICKER: String = "android.widget.DatePicker" //$NON-NLS-1$

    /** The fully qualified class name of a TimePicker  */
    const val FQCN_TIME_PICKER: String = "android.widget.TimePicker" //$NON-NLS-1$

    /** The fully qualified class name of a RadioGroup  */
    const val FQCN_RADIO_GROUP: String = "android.widgets.RadioGroup" //$NON-NLS-1$

    /** The fully qualified class name of a Space  */
    const val FQCN_SPACE: String = "android.widget.Space" //$NON-NLS-1$
    const val FQCN_SPACE_V7: String = "android.support.v7.widget.Space" //$NON-NLS-1$

    /** The fully qualified class name of a TextView view  */
    const val FQCN_TEXT_VIEW: String = "android.widget.TextView" //$NON-NLS-1$

    /** The fully qualified class name of an ImageView view  */
    const val FQCN_IMAGE_VIEW: String = "android.widget.ImageView" //$NON-NLS-1$
    const val ATTR_SRC: String = "src" //$NON-NLS-1$
    const val ATTR_GRAVITY: String = "gravity" //$NON-NLS-1$
    const val ATTR_WEIGHT_SUM: String = "weightSum" //$NON-NLS-1$
    const val ATTR_EMS: String = "ems" //$NON-NLS-1$
    const val VALUE_HORIZONTAL: String = "horizontal" //$NON-NLS-1$
    const val GRADLE_PLUGIN_NAME: String = "com.android.tools.build:gradle:"
    const val GRADLE_MINIMUM_VERSION: String = "2.2.1"
    const val GRADLE_LATEST_VERSION: String = "2.4"
    const val GRADLE_PLUGIN_MINIMUM_VERSION: String = "1.0.0"
    const val GRADLE_PLUGIN_RECOMMENDED_VERSION: String = "1.2.3"
    const val GRADLE_PLUGIN_LATEST_VERSION: String = GRADLE_PLUGIN_RECOMMENDED_VERSION
    const val MIN_BUILD_TOOLS_VERSION: String = "19.1.0"
    const val SUPPORT_LIB_ARTIFACT: String = "com.android.support:support-v4"
    const val APPCOMPAT_LIB_ARTIFACT: String = "com.android.support:appcompat-v7"

    // Annotations
    const val SUPPORT_ANNOTATIONS_PREFIX: String = "android.support.annotation."
    const val INT_DEF_ANNOTATION: String = SUPPORT_ANNOTATIONS_PREFIX + "IntDef"
    const val STRING_DEF_ANNOTATION: String = SUPPORT_ANNOTATIONS_PREFIX + "StringDef"
    const val TYPE_DEF_VALUE_ATTRIBUTE: String = "value"
    const val TYPE_DEF_FLAG_ATTRIBUTE: String = "flag"
    const val FN_ANNOTATIONS_ZIP: String = "annotations.zip"

    // Data Binding MISC
    const val DATA_BINDING_LIB_ARTIFACT: String = "com.android.databinding:library"
    val TAGS_DATA_BINDING: Array<String> = arrayOf(
        TAG_VARIABLE,
        TAG_IMPORT, TAG_LAYOUT, TAG_DATA
    )
    val ATTRS_DATA_BINDING: Array<String> = arrayOf(
        ATTR_NAME,
        ATTR_TYPE, ATTR_CLASS, ATTR_ALIAS
    )
}
