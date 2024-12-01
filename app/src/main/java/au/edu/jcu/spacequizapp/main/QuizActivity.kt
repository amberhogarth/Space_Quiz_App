package au.edu.jcu.spacequizapp.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import au.edu.jcu.spacequizapp.R
import au.edu.jcu.spacequizapp.databinding.ActivityQuizBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity(), QuizFragment.QuizCompletionListener {
    lateinit var binding: ActivityQuizBinding
    private lateinit var quizDao: QuizDao
    private var overallScore: Int = 0 // Track overall score
    private var selectedQuizId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the overall score from SharedPreferences (if available)
        overallScore = getSharedPreferences("quiz_prefs", MODE_PRIVATE)
            .getInt("overallScore", 0)

        // Display the initial score
        "Overall Score: $overallScore".also { binding.userScoreTextView.text = it }

        val db = AppDatabase.getDatabase(this)
        quizDao = db.quizDao()

        // Load quizzes
        CoroutineScope(Dispatchers.IO).launch {
            val quizzes = quizDao.getAllQuizzes()
            withContext(Dispatchers.Main) {
                setupQuizSpinner(quizzes)
            }
        }

        // Set up the Toolbar
        setSupportActionBar(binding.toolbar)
    }

    private fun setupQuizSpinner(quizzes: List<Quiz>) {
        val quizTitles = quizzes.map {
            if (it.completed) "${it.title} (Completed)" else it.title
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, quizTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.quizSpinner.adapter = adapter

        binding.quizSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                val selectedQuiz = quizzes[position]
                selectedQuizId = selectedQuiz.quizId // Track selected quiz ID
                displayQuizQuestions(selectedQuiz)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }
    }



    private fun displayQuizQuestions(quiz: Quiz) {
        val fragment = QuizFragment.newInstance(quiz.quizId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.quizListContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun addToOverallScore(score: Int) {
        overallScore += score

        // Save updated score in SharedPreferences
        getSharedPreferences("quiz_prefs", MODE_PRIVATE)
            .edit()
            .putInt("overallScore", overallScore)
            .apply()

        // Dynamically update the UI
        "Overall Score: $overallScore".also { binding.userScoreTextView.text = it }
    }

    override fun onQuizCompleted(score: Int) {
        addToOverallScore(score)

        CoroutineScope(Dispatchers.IO).launch {
            // Mark the selected quiz as completed if the score is 10/10
            selectedQuizId?.let { quizId ->
                if (score == 10) {
                    quizDao.updateQuizCompletionStatus(quizId, true)
                }
            }

            // Reload quizzes to update the spinner
            val updatedQuizzes = quizDao.getAllQuizzes()
            withContext(Dispatchers.Main) {
                setupQuizSpinner(updatedQuizzes)

            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_ar -> {
                val arIntent = Intent(this, ARActivity::class.java)
                arIntent.putExtra("overallScore", overallScore)
                startActivity(arIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
