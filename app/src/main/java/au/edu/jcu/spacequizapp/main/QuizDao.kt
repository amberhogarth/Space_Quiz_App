package au.edu.jcu.spacequizapp.main

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes")
    suspend fun getAllQuizzes(): List<Quiz>

    @Query("SELECT * FROM quizzes WHERE quizId = :quizId")
    fun getQuizById(quizId: Int): Quiz

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(quiz: Quiz): Long

    @Query("UPDATE quizzes SET completed = :completed WHERE quizId = :quizId")
    fun updateQuizCompletionStatus(quizId: Int, completed: Boolean)
}

