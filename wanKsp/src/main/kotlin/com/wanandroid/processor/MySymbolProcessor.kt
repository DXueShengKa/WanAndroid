package com.wanandroid.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFile
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import kotlin.io.path.absolutePathString

class MySymbolProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
) : SymbolProcessor {

    private var f = true

    override fun process(resolver: Resolver): List<KSAnnotated> {

        if (f) {
            resolver.getAllFiles().forEach {
                it.accept(EV(codeGenerator), Unit)
            }
            f = false
        }

        logger.info("KSPLogger MySymbolProcessor process")
        return emptyList()
    }

    class EV(
        private val codeGenerator: CodeGenerator
    ) : KSVisitorVoid() {

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
//            classDeclaration.getDeclaredFunctions().map { it.accept(this, Unit) }
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            printlnLog("visitFunctionDeclaration -> " + function.simpleName.asString())
        }

        override fun visitFile(file: KSFile, data: Unit) {
//            printlnLog(file.filePath + "\n")

            if (file.fileName == "Routes.kt") {
                val packageName = file.packageName.asString()

                createJava(codeGenerator,packageName)
                createKotlin(codeGenerator,packageName)
            }
        }
    }
}

fun createKotlin(codeGenerator: CodeGenerator,packageName:String){
    val writer = codeGenerator.createNewFile(Dependencies.ALL_FILES,packageName,"FN")
        .writer()

    val fs = FileSpec.builder(packageName,"FN")
        .addFunction(FunSpec.builder("s4")
            .returns(Long::class)
            .addStatement("return System.currentTimeMillis()")
            .build())
        .build()

    fs.writeTo(writer)

    writer.flush()
}


fun createJava(codeGenerator: CodeGenerator,packageName:String){

    codeGenerator.createNewFile(
        Dependencies.ALL_FILES,
        packageName,
        "JFN",
        "java"
    ).write(
        """
        package ${packageName};
        public final class JFN {

            public static long s4(){
                return System.currentTimeMillis();
            }
       
        }
        """.trimIndent().toByteArray()
    )
}

private val LogPath = "D:\\CodeProject\\as\\WanAndroid\\wanKsp\\build\\tmp"

fun printlnLog(string: String) {
    println(string)
    Files.write(
        Paths.get("$LogPath\\s.txt"),
        "$string \n".toByteArray(),
        StandardOpenOption.APPEND, StandardOpenOption.CREATE
    )
}

fun List<String>.printlnLog() {
    Files.write(
        Paths.get("$LogPath\\s.txt"),
        this,
        StandardOpenOption.APPEND, StandardOpenOption.CREATE
    )
}