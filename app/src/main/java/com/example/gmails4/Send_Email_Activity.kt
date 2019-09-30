package com.example.gmails4

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.Message
import java.io.ByteArrayOutputStream
import java.util.*
import javax.mail.Session
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class Send_Email_Activity : AppCompatActivity() {
    lateinit var service:Gmail
    lateinit var credential: GoogleAccountCredential
    lateinit var to_et:EditText
    lateinit var subject_et:EditText
    lateinit var content_et:EditText
    lateinit var send_mail_BTN:Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_send__email_)





        var commonModelClass = CommonModelClass.getSingletonObject()

        var mMainActivity:MainActivity = commonModelClass.getbaseActivity() as MainActivity
        service = mMainActivity.service
        credential = mMainActivity.mCredential

        to_et = findViewById(R.id.to_et)
        subject_et = findViewById(R.id.subject_et)
        content_et = findViewById(R.id.content_et)
      /*  send_mail_BTN = findViewById(R.id.send_mail_BTN)
        send_mail_BTN.setOnClickListener {
            Thread(Runnable{
                val session = Session.getDefaultInstance(Properties())
                var to = to_et.text.toString()
                var subject = subject_et.text.toString()
                var content = content_et.text.toString()

                var mMimeMessage =  MimeMessage(session).also {
                                                        it.setFrom(InternetAddress(credential.selectedAccountName))//credential
                                                        it.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
                                                        it.subject = subject
                                                        it.setText(content)
                                                    }

                service.users().messages()
                    .send(credential.selectedAccountName, createMessage(mMimeMessage))
                    .execute()


            }).start()
            Toast.makeText(this,"已發送",Toast.LENGTH_LONG).show()
            finish()

        }*/

    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.send_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.send_mail_menu_icon -> {
                val session = Session.getDefaultInstance(Properties())
                var to = to_et.text.toString()
                var subject = subject_et.text.toString()
                var content = content_et.text.toString()
                if (isEmailValid(to)) {
                    if (subject != "" && content != "") {
                        Thread(Runnable {
                            var mMimeMessage = MimeMessage(session).also {
                                it.setFrom(InternetAddress(credential.selectedAccountName))//credential
                                it.addRecipient(javax.mail.Message.RecipientType.TO, InternetAddress(to))
                                it.subject = subject
                                it.setText(content)
                            }

                            service.users().messages()
                                .send(credential.selectedAccountName, createMessage(mMimeMessage))
                                .execute()


                        }).start()


                        Toast.makeText(this, "已發送", Toast.LENGTH_LONG).show()
                        finish()
                    }
                    else {
                        Toast.makeText(this, "有欄位空白", Toast.LENGTH_SHORT).show()
                    }
                }
                else{
                    Toast.makeText(this, "Email地址錯誤", Toast.LENGTH_SHORT).show()
                }

                //Toast.makeText(this,"This is the fucking menu",Toast.LENGTH_SHORT).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }




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

    fun isEmailValid(address:String):Boolean{
        return android.util.Patterns.EMAIL_ADDRESS.matcher(address).matches()
    }
}
