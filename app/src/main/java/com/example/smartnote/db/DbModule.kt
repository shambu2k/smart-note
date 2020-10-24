package com.example.smartnote.db

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ApplicationComponent::class)
@Module
object DbModule {
    @Provides
    fun provideBookDao(@ApplicationContext appContext: Context) : BookDao {
        return BookDatabase.getInstance(appContext).bookDao
    }

    @Provides
    fun provideBookRepository(bookDao: BookDao) = BookRepository(bookDao)

}
