package com.example.gmails4

import android.content.Intent
import android.os.AsyncTask
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import java.io.ByteArrayOutputStream
import java.util.*
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class SendMailUsingGmailService(//this is primary constructor
    private val credential: GoogleAccountCredential,
    private val message: MimeMessage,
    private val service:Gmail,
    private val onAuthError: (Intent) -> Unit
) : AsyncTask<Unit, Unit, Intent?>() {
    //static method
    companion object {
        private fun createMimeMessage(
            fromAddress: String,
            toAddress: String,
            subject: String,
            text: String
        ): MimeMessage {
            val session = Session.getDefaultInstance(Properties())
            return MimeMessage(session).also {
                it.setFrom(InternetAddress(fromAddress))
                it.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(toAddress))
                it.subject = subject
                it.setText(text)
            }
        }
    }

    constructor(
        credential: GoogleAccountCredential,
        service:Gmail,
        toAddress: String,
        subject: String,
        text: String,
        onAuthError: (Intent) -> Unit // new a onAuthError object
    ) : this( //call primary constructor
        credential,//setting this object credential
        createMimeMessage(credential.selectedAccountName, toAddress, subject, text), //all is string
        service,
        onAuthError // set onAuthError object
    )

    override fun onPostExecute(result: Intent?) {
        result?.also(onAuthError)
    }

    override fun doInBackground(vararg params: Unit?): Intent? {
        return try {
          //  buildService(credential).send(message)
            service.send(message)
            null
        } catch (e: UserRecoverableAuthIOException) {
            e.intent
        }
    }

    private fun buildService(credential: GoogleAccountCredential): Gmail {
        val transport = AndroidHttp.newCompatibleTransport()
        return Gmail.Builder(transport, GsonFactory(), credential)
            .setApplicationName("Send Mail")
            .build()
    }

    private fun Gmail.send(mimeMessage: MimeMessage) {// add Gmail method
        users()
            .messages()
            .send(credential.selectedAccountName, createMessage(mimeMessage))
            .execute()
    }

    private fun createMessage(mimeMessage: MimeMessage): Message {
        return Message().apply {
            val bytes = ByteArrayOutputStream()
                .also(mimeMessage::writeTo)
                .toByteArray()
            raw = Base64.encodeBase64URLSafeString(bytes)
        }
    }
}