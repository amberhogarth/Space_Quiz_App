package au.edu.jcu.spacequizapp.main

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scores")
data class Score(
    @PrimaryKey(autoGenerate = true) val statisticId: Int = 0,
    val quizId: Int,
    val maxScore: Int
)