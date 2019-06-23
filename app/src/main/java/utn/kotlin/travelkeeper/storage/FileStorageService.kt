package utn.kotlin.travelkeeper.storage

import android.net.Uri
import android.os.Environment
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File
import java.net.URI


class FileStorageService {
    private val storageReference = FirebaseStorage.getInstance().reference
    private val basePath = "${Environment.getExternalStorageDirectory().absolutePath}/TravelKeeper"

    fun uploadFile(uri: Uri, tripId: String, fileName: String): UploadTask {

        val fileReference = storageReference.child("$tripId/$fileName")
        return fileReference.putFile(uri)
    }


    fun getFile(tripId: String, fileName: String): FileDownloadTask? {
        val destinationFolder = File("$basePath/$tripId")
        val destinationFile = File(destinationFolder, fileName)
        if (!destinationFile.exists()) {
            if (!destinationFolder.exists()) destinationFolder.mkdir()
            val fileReference = storageReference.child("$tripId/$fileName")
            return fileReference.getFile(destinationFile)
        }
        return null
    }
}