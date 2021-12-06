package com.wanandroid.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class MKsp:SymbolProcessorProvider  {

    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {

        return MySymbolProcessor(
            environment.codeGenerator,
            environment.logger
        )
    }

}
