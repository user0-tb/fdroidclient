package org.fdroid.repo

import android.net.Uri
import org.fdroid.database.Repository

internal object RepoUriGetter {

    fun getUri(url: String): NormalizedUri {
        val uri = Uri.parse(url)
        val fingerprint = uri.getQueryParameter("fingerprint")?.lowercase()

        val pathSegments = uri.pathSegments
        val normalizedUri = uri.buildUpon().apply {
            clearQuery() // removes fingerprint and other query params
            fragment("") // remove # hash fragment
            if (pathSegments.size >= 2 &&
                pathSegments[pathSegments.lastIndex - 1] == "fdroid" &&
                pathSegments.last() == "repo"
            ) {
                // path already is /fdroid/repo, use as is
            } else if (pathSegments.lastOrNull() == "repo") {
                // path already ends in /repo, use as is
            } else if (pathSegments.size >= 1 && pathSegments.last() == "fdroid") {
                // path is /fdroid with missing /repo, so add that
                appendPath("repo")
            } else {
                // path is missing /fdroid/repo, so add it
                appendPath("fdroid")
                appendPath("repo")
            }
        }.build().let { newUri ->
            // hacky way to remove trailing slash
            val path = newUri.path
            if (path != null && path.endsWith('/')) {
                newUri.buildUpon().path(path.trimEnd('/')).build()
            } else {
                newUri
            }
        }
        return NormalizedUri(normalizedUri, fingerprint)
    }

    /**
     * A class for normalizing the [Repository] URI and holding an optional fingerprint.
     */
    data class NormalizedUri(val uri: Uri, val fingerprint: String?)

}
