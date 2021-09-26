package huji.postpc.find.pic.aword.game.data

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storageMetadata
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2
import java.io.ByteArrayOutputStream
import java.io.File

val UPLOAD_IMAGE_PROGRESS_TAG : String = "image_upload_tag"

class FirestoreDatabaseManager() {

    // val database = Firebase.firestore
    val storage : FirebaseStorage = FirebaseStorage.getInstance()
    var storageRef : StorageReference = storage.reference
    // var templateRef : StorageReference? = storageRef.child("template_images")
    var userImagesRef : StorageReference = storageRef.child("user_images")
    // var chairRef = templateRef.child("missing_chair.jpg")

    fun uploadImageFromFile(file : File) {

        // TODO: Need to find the correct path...
        var uri = Uri.fromFile(file)
        val inputImageRef = userImagesRef.child(uri.lastPathSegment.toString())

        // image metadata example
        var metadata = storageMetadata {
            contentType = "image/png"
        }

        var uploadTask = inputImageRef.putFile(uri)

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
            inputImageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // use task.result here
            } else {
                // Handle failures
                // ...
            }
        }
        // TODO: add sessionUri functionality to handle interrupts
    }

    fun uploadImageFromBitmap(bitmap : Bitmap, imageName : String) {
        val inputImageRef = userImagesRef.child(imageName)

        var metadata = storageMetadata {
            contentType = "image/png"
        }

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = inputImageRef.putBytes(data)

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

        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
        }
    }

    // TODO: maybe turn this class into an activity with onSaveInstanceState, etc.
    //  To support lifecucle changes

}