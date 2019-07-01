package utn.kotlin.travelkeeper.storage

import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FileDownloadTask
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.io.File


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

    fun deleteFile(tripId: String, fileName: String): Task<Void> {
        val fileReference = storageReference.child("$tripId/$fileName")
        return fileReference.delete()
    }

    fun getFileUri(tripId: String, fileName: String): Uri {
        return Uri.parse("file://$basePath/$tripId/$fileName")
    }
    fun deleteFileFromLocalStorage(tripId: String, fileName: String){
        val fileDelete = File(getFileUri(tripId, fileName).path)
        if(fileDelete.exists()) fileDelete.delete()
    }

    fun getPathFromContentUri(context: Context, uri: Uri): String {
            var path: String?
        val projection = Array(10) { MediaStore.Files.FileColumns.DATA }
            val cursor = context.getContentResolver().query(uri, projection, null, null, null)

            if(cursor == null){
                path = uri.getPath()
            }
            else{
                cursor.moveToFirst()
                val column_index = cursor.getColumnIndexOrThrow(projection[0])
                path = cursor.getString(column_index)
                cursor.close();
            }

        if(path == null || path.isEmpty()) return uri.path else return path

    }

    fun getFileExtension(fileUrl: String): String? {
        var url = fileUrl
        if (url.indexOf("?") > -1) {
            url = url.substring(0, url.indexOf("?"))
        }
        if (url.lastIndexOf(".") == -1) {
            return null
        } else {
            var ext = url.substring(url.lastIndexOf(".") + 1)
            if (ext.indexOf("%") > -1) {
                ext = ext.substring(0, ext.indexOf("%"))
            }
            if (ext.indexOf("/") > -1) {
                ext = ext.substring(0, ext.indexOf("/"))
            }
            return ext.toLowerCase()

        }
    }
}