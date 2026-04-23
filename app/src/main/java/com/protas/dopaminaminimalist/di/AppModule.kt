package com.protas.dopaminaminimalist.di

import android.content.Context
import com.protas.dopaminaminimalist.data.ai.AddictionAnalyzer
import com.protas.dopaminaminimalist.data.dataStore.DefensePreferences
import com.protas.dopaminaminimalist.data.datasource.UsageProvider
import com.protas.dopaminaminimalist.data.repository.VicioRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // Esto hace que las dependencias duren tanto como la App activa
object AppModule {

    @Provides
    @Singleton
    fun provideAddictionAnalyzer(@ApplicationContext context: Context): AddictionAnalyzer {
        return AddictionAnalyzer(context)
    }

    @Provides
    @Singleton
    fun provideUsageProvider(@ApplicationContext context: Context): UsageProvider {
        return UsageProvider(context)
    }

    @Provides
    @Singleton
    fun provideDefensePreferences(@ApplicationContext context: Context): DefensePreferences {
        // Aunque no pasaste el código de DefensePreferences, el ViewModel indica que lo usa
        return DefensePreferences(context)
    }

    @Provides
    @Singleton
    fun provideVicioRepository(
        analyzer: AddictionAnalyzer,
        usageProvider: UsageProvider
    ): VicioRepository {
        return VicioRepository(analyzer, usageProvider)
    }
}