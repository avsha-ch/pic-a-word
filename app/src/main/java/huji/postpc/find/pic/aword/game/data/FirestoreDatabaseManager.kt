package huji.postpc.find.pic.aword.game.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import java.io.ByteArrayOutputStream
import java.io.File

val UPLOAD_IMAGE_PROGRESS_TAG : String = "image_upload_tag"

class FirestoreDatabaseManager(context : Context) {

    val context = context
    val database = Firebase.firestore
    val storage = Firebase.storage("gs://picaword-51613.appspot.com")

    var storageRef = storage.reference
    var templateRef : StorageReference? = storageRef.child("template_images")
    var userImages : StorageReference? = storageRef.child("user_images")
    // var chairRef = templateRef.child("missing_chair.jpg")

    fun uploadImageFromImageView(imageView: ImageView, imageName: String){

        // Create Reference to imageName
        var imageRef = userImages?.child(imageName)

        // Get the data from an ImageView as bytes
        imageView.isDrawingCacheEnabled = true
        imageView.buildDrawingCache()
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = imageRef?.putBytes(data)
        uploadTask?.addOnFailureListener {
            // Handle unsuccessful uploads
        }?.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }

        // TODO: function needs to return download image uri and metada
    }

    fun uploadImageFromCamera() : Uri {

        // TODO: Need to find the correct path...
        var file = Uri.fromFile(File("path/to/camera/images/rivers.jpg"))
        val riversRef = storageRef.child("images/${file.lastPathSegment}")

        // image metadata example
        var metadata = storageMetadata {
            contentType = "image/jpg"
        }

        var uploadTask = riversRef.putFile(file)

        // Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener { (bytesTransferred, totalByteCount) ->
            val progress = (100.0 * bytesTransferred) / totalByteCount
            Log.d(UPLOAD_IMAGE_PROGRESS_TAG, "Upload is $progress% done")
        }.addOnPausedListener {
            Log.d(UPLOAD_IMAGE_PROGRESS_TAG, "Upload is paused")
        }.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener {
            // Handle successful uploads on complete
            // ...
        }


        lateinit var downloadUri : Uri
        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            riversRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                downloadUri = task.result
            } else {
                // Handle failures
                // ...
            }
        }
        // TODO: add sessionUri functionality to handle interrupts
        return downloadUri
    }

    fun downloadTemplateImage(imageView: ImageView, imageName : String) {
        // TODO: determine if imageName needs to include extension
        var imageRef = templateRef?.child(imageName)

        // get images of max size of 1MB
        val ONE_MEGABYTE: Long = 1024 * 1024
        imageRef?.getBytes(ONE_MEGABYTE)?.addOnSuccessListener {
            // Data for "images/island.jpg" is returned, use this as needed
        }?.addOnFailureListener {
            // Handle any errors
        }

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
        // TODO: maybe it's the wrong context to handle with this image, if this is the case,
        //  add context as a parameter for the function instead
        Glide.with(context /* context */)
            .load(imageRef)
            .into(imageView)

    }

    // TODO: maybe turn this class into an activity with onSaveInstanceState, etc.
    //  To support lifecucle changes

}