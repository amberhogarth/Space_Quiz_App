package au.edu.jcu.spacequizapp.main

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "questions",
    foreignKeys = [ForeignKey(
        entity = Quiz::class,
        parentColumns = ["quizId"],
        childColumns = ["quizId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Question(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val quizId: Int, // This is the foreign key
    val questionText: String,
    val correctAnswer: String,
    val optionTwo: String,
    val optionThree: String
)
