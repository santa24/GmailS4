package com.example.gmails4

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.widget.Toast
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.ListLabelsResponse
import com.google.api.services.gmail.model.Message
import java.io.ByteArrayOutputStream
import java.util.*
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class ShowMailTagsService(
    private val  credential: GoogleAccountCredential,
    private val service: Gmail,
    private val contextt:Context

): AsyncTask<Unit, Unit, Intent?>() {

    lateinit var onAuthError: (Intent) -> Unit


    override fun onPostExecute(result: Intent?) {//
        //Toast.makeText(contextt,labels.toString(), Toast.LENGTH_SHORT).show()

        if (labels.isEmpty()) {
            println("No labels found.")
        } else {
            println("Labels:")
            for (label in labels) {
                // System.out.printf("- %s\n", label.getName())
                Toast.makeText(contextt,label.getName(), Toast.LENGTH_SHORT).show()

            }
        }
        result?.also(onAuthError)
    }

    override fun doInBackground(vararg params: Unit?): Intent? {
        return try {
            //  buildService(credential).send(message)
            //service.send(message)
            getlabels()
            null
        } catch (e: UserRecoverableAuthIOException) {
            e.intent
        }
    }
    lateinit var labels:List<Label>

    fun getlabels(){
        var user = "me"
        var listResponse: ListLabelsResponse = service.users().labels().list(user).execute()
        labels = listResponse.labels



    }


}