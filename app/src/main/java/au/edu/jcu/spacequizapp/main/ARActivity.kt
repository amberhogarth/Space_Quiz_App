package au.edu.jcu.spacequizapp.main


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.math.Position
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import au.edu.jcu.spacequizapp.R
import io.github.sceneview.ar.node.PlacementMode

class ARActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    private lateinit var placeButton: ExtendedFloatingActionButton
    private lateinit var modelNode: ArModelNode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        // Link the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Access sceneView and set lightEstimationMode
        sceneView = findViewById(R.id.sceneView)
        sceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED

        // Initialize the place button
        placeButton = findViewById(R.id.place)
        placeButton.setOnClickListener {
            placeModel()
        }

        // Get the user's score from the Intent
        val overallScore = intent.getIntExtra("overallScore", 0)

        // Choose the planet based on the score
        val planetModel = when {
            overallScore >= 30 -> "models/saturn.glb" // Load Moon for score >= 30
            overallScore >= 20 -> "models/sun.glb" // Load Venus for score >= 20
            overallScore >= 10 -> "models/moon.glb"   // Load Sun for score >= 10
            else -> "models/earth.glb"             // Default to Earth if no other conditions are met
        }

        // Setup the AR model node with the selected planet model
        modelNode = ArModelNode(sceneView.engine, PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = planetModel, // Load the appropriate model based on the score
                scaleToUnits = 1f,
                centerOrigin = Position(-0.5f)
            ) {
                sceneView.planeRenderer.isVisible = true
            }
            onAnchorChanged = {
                placeButton.isGone = it != null
            }
        }

        // Add model to the scene
        sceneView.addChild(modelNode)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_ar -> {
                return true
            }
            R.id.action_quizzes -> {
                val intent = Intent(this, QuizActivity::class.java)
                startActivity(intent)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Function to place the AR model
    private fun placeModel() {
        modelNode.anchor()
        sceneView.planeRenderer.isVisible = false
    }
}
