package au.edu.jcu.spacequizapp.main

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quizzes")
data class Quiz(
    @PrimaryKey(autoGenerate = true) val quizId: Int = 0,
    val title: String
)
