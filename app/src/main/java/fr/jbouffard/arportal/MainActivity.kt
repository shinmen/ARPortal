package fr.jbouffard.arportal

import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.math.Quaternion
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.ux.ArFragment

class MainActivity : AppCompatActivity() {

    private var invisibleContainerRenderable: ModelRenderable? = null
    private var roomRenderable: ModelRenderable? = null
    private var roomNode: Node? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment

        ModelRenderable.builder().setSource(this@MainActivity, Uri.parse("ar-portal-container.sfb")).build()
            .thenAccept{ renderable -> run {
                    invisibleContainerRenderable = renderable
                invisibleContainerRenderable?.apply {
                        renderPriority = Renderable.RENDER_PRIORITY_LAST
                        isShadowCaster = true
                        isShadowReceiver = true
                    }
                }
            }
        ModelRenderable.builder().setSource(this@MainActivity, Uri.parse("ar-portal-room.sfb")).build()
            .thenAccept{ renderable -> run {
                    roomRenderable = renderable
                roomRenderable?.apply {
                        renderPriority = Renderable.RENDER_PRIORITY_LAST
                        isShadowCaster = true
                        isShadowReceiver = true
                    }
                }
            }

        arFragment.setOnTapArPlaneListener { hitResult: HitResult, plane: Plane, motionEvent: MotionEvent ->
            roomNode?.run {
                arFragment.arSceneView.planeRenderer.isEnabled = false
                return@setOnTapArPlaneListener
            }

            // Create the Anchor.
            val anchor = hitResult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment.arSceneView.scene)

            val invisibleNode = Node().apply {
                setParent(anchorNode)
                renderable = invisibleContainerRenderable
                localPosition = Vector3(0f, -2f, -7f)
                localRotation = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 180f)
            }

            roomNode = Node().apply {
                setParent(invisibleNode)
                renderable = roomRenderable
                localScale = Vector3(
                    invisibleNode.localScale.x - 0.01f,
                    invisibleNode.localScale.y - 0.01f,
                    invisibleNode.localScale.y - 0.01f
                )
                //localRotation = Quaternion.axisAngle(Vector3(0f, 1f, 0f), 180f)
            }
        }

    }
}
