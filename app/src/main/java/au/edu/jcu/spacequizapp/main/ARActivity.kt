package au.edu.jcu.spacequizapp.main


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import com.google.ar.core.Config
import io.github.sceneview.ar.ArSceneView
import io.github.sceneview.ar.node.ArModelNode
import io.github.sceneview.node.VideoNode
import io.github.sceneview.math.Position
import android.media.MediaPlayer
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import au.edu.jcu.spacequizapp.R
import io.github.sceneview.ar.node.PlacementMode

class ARActivity : AppCompatActivity() {

    private lateinit var sceneView: ArSceneView
    private lateinit var placeButton: ExtendedFloatingActionButton
    private lateinit var modelNode: ArModelNode
//    private lateinit var videoNode: VideoNode
//    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ar)

        // Link the Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // Set as the ActionBar

        // Initialize AR scene view
        sceneView = findViewById<ArSceneView>(R.id.sceneView).apply {
            this.lightEstimationMode = Config.LightEstimationMode.DISABLED
        }

        // Initialize the place button
        placeButton = findViewById(R.id.place)
        placeButton.setOnClickListener {
            placeModel()
        }

//        // Setup the video node
//        videoNode = VideoNode(sceneView.engine, scaleToUnits = 0.7f, centerOrigin = Position(y = -4f), glbFileLocation = "assets/models/earth.glb", player = mediaPlayer, onLoaded = { _, _ ->
//            mediaPlayer.start()
//        })

        // Setup the AR model node
        modelNode = ArModelNode(sceneView.engine, PlacementMode.INSTANT).apply {
            loadModelGlbAsync(
                glbFileLocation = "models/earth.glb",
                scaleToUnits = 1f,
                centerOrigin = Position(-0.5f)
            ) {
                sceneView.planeRenderer.isVisible = true
                val materialInstance = it.materialInstances[0]
            }
            onAnchorChanged = {
                placeButton.isGone = it != null
            }
        }

        // Add model and video nodes to the scene
        sceneView.addChild(modelNode)
//        modelNode.addChild(videoNode)
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

//    override fun onPause() {
//        super.onPause()
//        mediaPlayer.stop()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        mediaPlayer.release()
//    }
}