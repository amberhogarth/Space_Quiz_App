package au.edu.jcu.spacequizapp.main

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
                setupQuizSpinner(quizzes)
                updateScoreDisplay()
            }
        }

        // Link the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


    }

    private fun setupQuizSpinner(quizzes: List<Quiz>) {
        val spinner = binding.quizSpinner

        // Create an adapter for the spinner
        val quizTitles = quizzes.map { it.title }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, quizTitles)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // Set an item selected listener for the spinner
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                val selectedQuiz = quizzes[position]
                displayQuizQuestions(selectedQuiz)
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
    }

    private fun updateScoreDisplay() {
        val overallScore = intent.getIntExtra("overallScore", 0)
        binding.userScoreTextView.text = "Score: $overallScore"
    }


    private fun displayQuizQuestions(quiz: Quiz) {
        val fragment = QuizFragment.newInstance(quiz.quizId)
        supportFragmentManager.beginTransaction()
            .replace(R.id.quizListContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_quizzes -> {
                // You are already in QuizActivity, so no action needed
                return true
            }
            R.id.action_ar -> {
                val arIntent = Intent(this, ARActivity::class.java)
                val overallScore = intent.getIntExtra("overallScore", 0)
                arIntent.putExtra("overallScore", overallScore)
                startActivity(arIntent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
