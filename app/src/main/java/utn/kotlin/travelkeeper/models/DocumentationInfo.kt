package utn.kotlin.travelkeeper.models;

data class DocumentationInfo(val fileName: String, val type: String, val id: String) {

    companion object {
        fun createObjectFromSnapshot(
            snapshot: MutableMap<String, Any>,
            id: String
        ): DocumentationInfo {
            return DocumentationInfo(
                snapshot["fileName"].toString(),
                snapshot["type"].toString(),
                id
            )
        }
    }

}