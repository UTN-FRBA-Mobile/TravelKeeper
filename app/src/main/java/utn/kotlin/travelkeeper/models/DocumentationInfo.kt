package utn.kotlin.travelkeeper.models;

import com.google.firebase.firestore.DocumentSnapshot

data class DocumentationInfo(val fileName: String, val type: String) {

    companion object {
        fun createObjectFromSnapshot(
            snapshot: MutableMap<String, Any>
        ): DocumentationInfo {
            return DocumentationInfo(
                snapshot["fileName"].toString(),
                snapshot["type"].toString()
            )
        }
    }

}