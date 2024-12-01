package au.edu.jcu.spacequizapp.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.math.Position
import io.github.sceneview.ar.node.PlacementMode
import au.edu.jcu.spacequizapp.R

class ARActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    private val modelNodes = mutableMapOf<String, ArModelNode>()
    private lateinit var buttonContainer: LinearLayout
    private val planets = listOf(
        Triple("Earth", "models/earth.glb", 0),
        Triple("Moon", "models/moon.glb", 10),
        Triple("Sun", "models/sun.glb", 20),
        Triple("Saturn", "models/saturn.glb", 30)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        // Set up the toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Initialize the AR SceneView
        sceneView = findViewById(R.id.sceneView)

        // Get the user's overall score from the Intent
        val overallScore = intent.getIntExtra("overallScore", 0)

        // Initialize button container
        buttonContainer = findViewById(R.id.buttonContainer)

        // Load and show all unlocked models
        for ((name, modelPath, scoreThreshold) in planets) {
            if (overallScore >= scoreThreshold) {
                loadModel(name, modelPath)
                addPlaceButton(name)
            }
        }
    }

    private fun loadModel(name: String, modelPath: String) {
        val modelNode = ArModelNode(sceneView.engine, PlacementMode.INSTANT).apply {
            loadModelGlbAsync(modelPath, scaleToUnits = 1f, centerOrigin = Position(-0.5f))
        }
        modelNodes[name] = modelNode
        sceneView.addChild(modelNode)
    }

    private fun addPlaceButton(name: String) {
        val button = ExtendedFloatingActionButton(this).apply {
            text = "Place $name"
            setOnClickListener {
                modelNodes[name]?.anchor() // Anchor the model in place
                this.isEnabled = false  // Disable button after placing the model
            }
        }
        buttonContainer.addView(button)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_ar -> return true
            R.id.action_quizzes -> {
                val intent = Intent(this, QuizActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
