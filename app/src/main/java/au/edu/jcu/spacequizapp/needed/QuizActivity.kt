package au.edu.jcu.spacequizapp.needed

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import au.edu.jcu.spacequizapp.R
import au.edu.jcu.spacequizapp.databinding.ActivityQuizBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity() {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var quizDao: QuizDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getDatabase(this)
        quizDao = db.quizDao()

        CoroutineScope(Dispatchers.IO).launch {
            val quizzes = quizDao.getAllQuizzes()
            withContext(Dispatchers.Main) {
                setupQuizButtons(quizzes)
            }
        }
    }

    private fun setupQuizButtons(quizzes: List<Quiz>) {
        Log.d("QuizActivity", "Quizzes loaded: ${quizzes.size}")
        for (quiz in quizzes) {
            val button = Button(this).apply {
                text = quiz.title
                setOnClickListener {
                    val fragment = QuizFragment.newInstance(quiz.quizId)
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.quizListContainer, fragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
            binding.quizListContainer.addView(button)
        }
    }
}
