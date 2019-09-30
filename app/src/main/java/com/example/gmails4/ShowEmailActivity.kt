package com.example.gmails4

import android.accessibilityservice.GestureDescription
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Base64
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.webkit.WebView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.navigation.NavigationView
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.MessagePart
import com.google.common.base.Utf8
import android.view.animation.ScaleAnimation
import android.widget.LinearLayout
import android.widget.ScrollView


class ShowEmailActivity : AppCompatActivity() {
    lateinit var service: Gmail
    lateinit var message_id:String
    private val GOT_MESSAGE:Int = 6969
    private var mScale:Float = 1f

    lateinit var mMainActivity:MainActivity


    lateinit var gestureDectector:GestureDetector
    private lateinit var  mScaleGestureDetector:ScaleGestureDetector

    lateinit var  navView: NavigationView


    override fun onResume() {
        super.onResume()

        var hView = navView.getHeaderView(0)
        var current_email_tv:TextView = hView.findViewById(R.id.current_email) as TextView
        current_email_tv.text = mMainActivity.mCredential.selectedAccountName

    }
  //  override fun on





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_show_email)


       /* gestureDectector = GestureDetector(this,GestureListener())

        mScaleGestureDetector = ScaleGestureDetector(this,object:ScaleGestureDetector.SimpleOnScaleGestureListener(){
            override fun onScale(detector: ScaleGestureDetector?): Boolean {
                val scale = 1 - detector!!.scaleFactor
                val prevScale = mScale
                mScale += scale

                if (mScale > 10f)
                    mScale = 10f

                val scaleAnimation = ScaleAnimation(
                    1f / prevScale,
                    1f / mScale,
                    1f / prevScale,
                    1f / mScale,
                    detector.focusX,
                    detector.focusY
                )
                scaleAnimation.duration = 0
                scaleAnimation.fillAfter = true
                var layout: ScrollView = findViewById(R.id.show_scroll_view)
                layout.startAnimation(scaleAnimation)
                return true






               // return super.onScale(detector)
            }
        })*/







        /*



        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
         */





        val drawerLayout: DrawerLayout = findViewById(R.id.show_drawer_layout)
        navView  = findViewById(R.id.nav_view)
        val toolbar: Toolbar = findViewById(R.id.toolbar_show)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


       // val parentView = navView.getHeaderView(0)
       // navView.getHeaderView(2)








        //MessageId
         message_id = intent.getStringExtra("MessageId")
        //service = (parent as MainActivity).service

        var commonModelClass = CommonModelClass.getSingletonObject()

         mMainActivity = commonModelClass.getbaseActivity() as MainActivity
        service = mMainActivity.service





       // Toast.makeText(this,service.toString(),Toast.LENGTH_SHORT).show()
        Thread(
            Runnable{
                var user="me"
                var Subject: String = ""
                var From: String = ""
                var mSecond: Long = 0
                var Msg: android.os.Message = android.os.Message()
                var bundle: Bundle = Bundle()
                var message = service.users().messages().get(user, message_id).execute()
                var MailBody:String = ""
                //message = service.users().messages().get(user,messageid).execute()
                mSecond = message.internalDate
                for (header in message.payload.headers) {
                    if (header.name == "Subject") {
                        Subject = header.value

                    }
                    if (header.name == "From") {
                        From = header.value
                    }
                }

                if (message.payload.parts ==null){
                    MailBody = message.payload.body.data
                }else {


                    for (part in message.payload.parts) {
                        if (part.mimeType == "text/html") {
                            MailBody = part.body.data
                        }
                    }
                }




                //Msg.data
                //ObjectWrapperForBinder()
                bundle.putString("From", From)
                bundle.putString("Subject", Subject)
                bundle.putLong("mSecond", mSecond)
                bundle.putString("Id",message.id)
                bundle.putString("MailBody",MailBody)
                Msg.data = bundle
                Msg.what = GOT_MESSAGE
                mhandler.sendMessage(Msg)

            }
        ).start()

       // Toast.makeText(this,message_id,Toast.LENGTH_SHORT).show()
    }

   /* override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        super.dispatchTouchEvent(ev)
        mScaleGestureDetector.onTouchEvent(ev)
        gestureDectector.onTouchEvent(ev)


        return super.dispatchTouchEvent(ev)
    }*/






    fun getHTMLPart(parts:List<MessagePart>){

    }



    private  var mhandler: Handler = object: Handler(){
        override fun handleMessage(msg:android.os.Message) {//because have another Message class
            val what = msg.what
            when (what) {
                GOT_MESSAGE-> {
                    //var subject_tv:TextView = findViewById(R.id.Mail_Subject_tv)
                   // var from_tv:TextView = findViewById(R.id.Mail_From_tv)
                    var mMail_wv:WebView = findViewById(R.id.Mail_wv)

                    var data:Bundle = msg.data
                    var From = data.getString("From")//.substring(0,)
                    /*var arrow_pos = From.indexOf("<")
                    if (arrow_pos>0) From = From.substring(0,From.indexOf("<"))*/

                    var Subject = data.getString("Subject")
                    var mSecond = data.getLong("mSecond")
                    var id = data.getString("Id")
                    var encodeBody = data.getString("MailBody").replace("-","+").replace("_","/")//.replace("\\s","")
                    // MailTitles.add(MailTitle(From,Subject,mSecond))
                    //MailBody = Base64.deco
                   var decodeMail =  Base64.decode(encodeBody,Base64.DEFAULT)

                    var MailBody = String(decodeMail,Charsets.UTF_8)

                   // Base64.de
                   /*subject_tv.text = Subject
                    from_tv.text = From*/
                   // subject_tv.text = ""
                    //from_tv.text = ""

                    //Toast.makeText(applicationContext,MailBody,Toast.LENGTH_SHORT).show()

                   /* if(MailBody.indexOf("viewport")==-1){
                        MailBody.indexOf("<head>")
                        var viewport:String = "<meta name=\"viewport\" content=\"width=device-width, user-scalable=yes\" >"
                        MailBody = MailBody.substring(0, MailBody.indexOf("<head>")+5) +
                                viewport +
                         MailBody.substring( MailBody.indexOf("<head>")+6)
                    }

                    mMail_wv.settings.minimumFontSize = 100*/
                    //mMail_wv.settings.javaScriptEnabled = true
                    MailBody.indexOf("<body>")
                    var division = "<div style='font-size:20px;'>"+Subject+"</div><div style='font-size:15px;'>"+From+"</div>"

                    MailBody = MailBody.substring(0, MailBody.indexOf("<head>")+5) +
                            division +
                            MailBody.substring( MailBody.indexOf("<head>")+6)




                    mMail_wv.setInitialScale(1)
                    mMail_wv.settings.loadWithOverviewMode = true
                    mMail_wv.settings.useWideViewPort = true
                    mMail_wv.settings.builtInZoomControls = true
                    mMail_wv.settings.displayZoomControls = false


                   // subject_tv.text = MailBody

                   // mMail_wv.settings.useWideViewPort = true

                    mMail_wv.loadDataWithBaseURL("",MailBody,"text/html","UTF-8","")






                    }


                }




            }
        }
    class GestureListener:GestureDetector.SimpleOnGestureListener(){
         override fun onDown(e: MotionEvent?): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent?): Boolean {
            return true
        }

    }


}






