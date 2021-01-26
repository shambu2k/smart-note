package com.example.smartnote.db

import android.content.Context
import com.example.smartnote.helpers.FileSystemHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@InstallIn(ApplicationComponent::class)
@Module
object DbModule {
  @Provides
  fun provideBookDao(@ApplicationContext appContext: Context): BookDao {
    return BookDatabase.getInstance(appContext).bookDao
  }

  @Provides
  fun provideSubjectDao(@ApplicationContext appContext: Context): SubjectDao {
    return BookDatabase.getInstance(appContext).subjectDao
  }

  @Provides
  fun provideUnitDao(@ApplicationContext appContext: Context): UnitDao {
    return BookDatabase.getInstance(appContext).unitDao
  }

  @Provides
  fun providePdfDao(@ApplicationContext appContext: Context): PdfDao{
    return BookDatabase.getInstance(appContext).pdfDao
  }

  @Provides
  fun provideBookRepository(bookDao: BookDao, subjectDao: SubjectDao, unitDao: UnitDao, pdfDao: PdfDao) = BookRepository(bookDao, subjectDao, unitDao,pdfDao)

  @Provides
  fun provideFileSystemHelper(@ApplicationContext appContext: Context) = FileSystemHelper(appContext)
}
