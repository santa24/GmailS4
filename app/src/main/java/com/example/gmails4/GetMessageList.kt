package com.example.gmails4

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.View

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.ListLabelsResponse
import com.google.api.services.gmail.model.ListMessagesResponse
import com.google.api.services.gmail.model.Message

class GetMessageList(
    private val  credential: GoogleAccountCredential,
    private val service: Gmail,
    private val contextt: Context,
    private val root:View


): AsyncTask<Unit, Unit, MutableList<MailTitle>>() {

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



    override fun onPostExecute(result:MutableList<MailTitle>) {//
        //Toast.makeText(contextt,labels.toString(), Toast.LENGTH_SHORT).show()

       /* if (messages.isEmpty()) {
            println("No labels found.")
        } else {
            println("Labels:")
            for (message in messages) {
                // System.out.printf("- %s\n", label.getName())
                //message.id
                Toast.makeText(contextt,message.toPrettyString(), Toast.LENGTH_SHORT).show()

            }
        }*/



       /* val result = listOf(
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28"),
            MailTitle("Sarah","Hou are you?","8/28")
        )*/
       // Toast.makeText(contextt,"zzzz",Toast.LENGTH_SHORT).show()
      /*  Toast.makeText(contextt,"PageChange",Toast.LENGTH_SHORT).show()
        var  MailTitles = mutableListOf<MailTitle>()
*/
       /* for (mailTitle in result) {
            // System.out.printf("- %s\n", label.getName())
            Toast.makeText(contextt,mailTitle.sender,Toast.LENGTH_SHORT).show()
            //GetMessage(credential,service,contextt,message.id).execute()
            //MailTitles.add(MailTitle(message.id,"Hou are you?","8/28"))

        }*/




        val iRecyclerView: RecyclerView = root.findViewById(R.id.mRecyclerview)
        iRecyclerView.apply{
            this.layoutManager = LinearLayoutManager(context)

            this.adapter=ListAdapter(result)





        }

    }
    override fun doInBackground(vararg params: Unit?):MutableList<MailTitle> {
        /*return try {
            //  buildService(credential).send(message)
            //service.send(message)

            var user = "me"
            var listResponse: ListMessagesResponse = service.users().messages().list(user).setMaxResults(10).execute()

            messages = listResponse.messages
            messages

              getlabels()
              null
        } catch (e: UserRecoverableAuthIOException) {
           // e.intent
            null
        }*/
        var user = "me"
        var listResponse: ListMessagesResponse = service.users().messages().list(user).setMaxResults(10).execute()

        messages = listResponse.messages

        var MesTitles:MutableList<MailTitle> = mutableListOf()
        for (message in messages) {
            // System.out.printf("- %s\n", label.getName())

            //GetMessage(credential,service,contextt,message.id).execute()
           // MailTitles.add(MailTitle(message.id,"Hou are you?","8/28"))

           // Toast.makeText(contextt,message.id,Toast.LENGTH_SHORT).show()
            //MesTitles.add( GetMessage(credential,service,message.id,contextt).execute().get())

            //GetMessage(credential,service,message.id,contextt).execute()
            var message = service.users().messages().get(user,message.id).execute()
            var Subject:String=""
            var From:String=""

            for ( header in message.payload.headers){
                if (header.name == "Subject"){
                    Subject = header.value

                }
                if (header.name=="From"){
                    From = header.value
                }
            }

            MesTitles.add(MailTitle(From,Subject,456,""))//message.payload.headers




        }



        /*
            private val  credential: GoogleAccountCredential,
    private val service: Gmail,
    private val contextt: Context,

         */


        return MesTitles

    }

    lateinit var messages:List<Message>

    fun getlabels(){
        var user = "me"
        var listResponse: ListMessagesResponse = service.users().messages().list(user).setMaxResults(10).execute()

        messages = listResponse.messages




      /*  for (message in messages) {
            // System.out.printf("- %s\n", label.getName())
            Toast.makeText(contextt,message.id,Toast.LENGTH_SHORT).show()
            //GetMessage(credential,service,contextt,message.id).execute()

        }*/


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