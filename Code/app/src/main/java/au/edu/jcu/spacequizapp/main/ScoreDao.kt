package au.edu.jcu.spacequizapp.main

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ScoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScore(score: Score)

    @Query("SELECT * FROM scores WHERE quizId = :quizId")
    fun getScoresForQuiz(quizId: Int): List<Score>
}
