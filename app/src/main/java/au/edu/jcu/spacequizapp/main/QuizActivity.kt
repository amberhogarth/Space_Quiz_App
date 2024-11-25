package au.edu.jcu.spacequizapp.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import au.edu.jcu.spacequizapp.R
import au.edu.jcu.spacequizapp.databinding.ActivityQuizBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class QuizActivity : AppCompatActivity(), QuizFragment.QuizCompletionListener {
    private lateinit var binding: ActivityQuizBinding
    private lateinit var quizDao: QuizDao
    private var overallScore: Int = 0 // Track overall score

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the overall score from SharedPreferences (if available)
        overallScore = getSharedPreferences("quiz_prefs", MODE_PRIVATE)
            .getInt("overallScore", 0)

        // Display the initial score
        binding.userScoreTextView.text = "Overall Score: $overallScore"

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
        val quizTitles = quizzes.map { it.title }
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
                binding.quizCompletionNote.visibility = View.GONE // Hide the note
                val selectedQuiz = quizzes[position]
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
        binding.userScoreTextView.text = "Overall Score: $overallScore"
    }

    override fun onQuizCompleted(score: Int) {
        addToOverallScore(score)
        // Show the note to encourage selecting a new quiz
        binding.quizCompletionNote.visibility = View.VISIBLE

        // Reload quizzes to refresh spinner options
        CoroutineScope(Dispatchers.IO).launch {
            val quizzes = quizDao.getAllQuizzes()
            withContext(Dispatchers.Main) {
                setupQuizSpinner(quizzes)
    }}}

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
