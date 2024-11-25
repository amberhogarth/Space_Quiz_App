package au.edu.jcu.spacequizapp.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import au.edu.jcu.spacequizapp.R
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startQuizButton: Button = findViewById(R.id.start_quiz_button)
        startQuizButton.setOnClickListener {
            // Navigate to au.edu.jcu.spacequizapp.needed.QuizActivity
            val intent = Intent(this, QuizActivity::class.java)
            startActivity(intent)
        }
    }
}
