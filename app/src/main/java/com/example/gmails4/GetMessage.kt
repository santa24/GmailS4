package com.example.gmails4

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.View
import android.widget.Toast
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.client.util.Base64
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.model.Message
import com.google.api.services.gmail.model.MessagePartHeader

class GetMessage(
    private val  credential: GoogleAccountCredential,
    private val service: Gmail,
    private val messageid:String,
    private val contextt:Context


): AsyncTask<Unit, Unit, MailTitle>() {

    lateinit var onAuthError: (Intent) -> Unit


    //static method
    /*companion object {
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
    }*/

    /*  constructor(
          credential: GoogleAccountCredential,
          service: Gmail,
          onAuthError: (Intent) -> Unit // new a onAuthError object
      ) {

          var ii = 12

      }*/
    /* init{

        // var onAuthError: (Intent) -> Unit

     }*/



    override fun onPostExecute(result: MailTitle) {//
        //Toast.makeText(contextt,labels.toString(), Toast.LENGTH_SHORT).show()

       /* if (message.isEmpty()) {
            println("No labels found.")
        } else {
            println("Labels:")
            var Subject:String = ""
            for ( header in message.payload.headers){
                if (header.name == "Subject"){
                    Subject = header.value
                    break
                }
            }

              //  Toast.makeText(contextt,Subject, Toast.LENGTH_SHORT).show()


        }*/
        Toast.makeText(contextt,result.sender, Toast.LENGTH_SHORT).show()
        //result?.also(onAuthError)
    }

    override fun doInBackground(vararg params: Unit?): MailTitle {
      /*  return try {
            //  buildService(credential).send(message)
            //service.send(message)
            getlabels()
            null
        } catch (e: UserRecoverableAuthIOException) {
            e.intent
        }*/

        var user = "me"
        message = service.users().messages().get(user,messageid).execute()
        var Subject:String = ""
        var From:String = ""

        for ( header in message.payload.headers){
            if (header.name == "Subject"){
                Subject = header.value

            }
            if (header.name=="From"){
                From = header.value
            }
        }


        return MailTitle(From,Subject,123,"")//message.payload.headers


    }
    lateinit var message:Message
    fun getlabels(){
        var user = "me"
         message = service.users().messages().get(user,messageid).execute()
        //Base64.decodeBase64(message.snippet)


       // messages = listResponse.messages


        /*if (labels.isEmpty()) {
            println("No labels found.")
        } else {
            println("Labels:")
            for (label in labels) {
                // System.out.printf("- %s\n", label.getName())
                Toast.makeText(contextt,label.getName(), Toast.LENGTH_SHORT).show()
            }
        }*/


    }

    /* private fun buildService(credential: GoogleAccountCredential): Gmail {
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
     }*/
}