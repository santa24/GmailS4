package com.example.gmails4

import android.accounts.AccountManager
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.viewpager.widget.ViewPager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.ExponentialBackOff
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.GmailScopes
import com.google.api.services.gmail.model.Label
import com.google.api.services.gmail.model.ListLabelsResponse

class MainActivity : AppCompatActivity() {
    val GET_LABLES = 1
    val COMPLETE_LABELS = 2
    val GET_TOTAL_LABLES_NUM = 3
    var total_labels_num = 0


    lateinit var fab:FloatingActionButton
    lateinit var LabelsTitle:MutableList<LabelData>
    companion object {
        private const val REQUEST_ACCOUNT_CHOOSER = 1
    }


     public lateinit var mCredential: GoogleAccountCredential
     public lateinit var service:Gmail

    fun isNetworkAvailable():Boolean{
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return  activeNetworkInfo != null && activeNetworkInfo.isConnected
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)

        if (!isNetworkAvailable()){
            Toast.makeText(this,"請開啟網路",Toast.LENGTH_SHORT).show()
            finish()
        }




        //default labels
        LabelsTitle = mutableListOf(
            LabelData("INBOX","INBOX"),
            LabelData("SENT","SENT"),
            LabelData("UNREAD","UNREAD")
        )


        fab = findViewById(R.id.fab)
        fab.setOnClickListener {
            //Toast.makeText(this,"Fab",Toast.LENGTH_SHORT).show()
           // var intent:Intent = Intent()
            var intent: Intent = Intent(this,Send_Email_Activity::class.java)
            startActivity(intent)
        }

        initialize_drawerlayout()
        get_permission()


        // starting get auth

        mCredential = GoogleAccountCredential.usingOAuth2(applicationContext, listOf(
           GmailScopes.MAIL_GOOGLE_COM
        )).setBackOff(ExponentialBackOff())
        startActivityForResult(mCredential.newChooseAccountIntent(), REQUEST_ACCOUNT_CHOOSER)


    }
    fun initialize_drawerlayout(){
        //initialize drawerLayout
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }
    fun get_permission(){
        //get android system permission dynamically
        val list = listOf<String>(
            android.Manifest.permission.GET_ACCOUNTS,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.READ_CONTACTS,
            android.Manifest.permission.WRITE_CONTACTS
        )
        ActivityCompat.requestPermissions(this,list.toTypedArray(),123)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQUEST_ACCOUNT_CHOOSER -> {
                if (resultCode == Activity.RESULT_OK && data != null) {

                    service = buildService(mCredential)
                   // mCredential.
                    fab.show()
                   // Toast.makeText(this,mCredential.token,Toast.LENGTH_SHORT).show()



                    var commonModelClass = CommonModelClass.getSingletonObject()
                    commonModelClass.setbaseActivity(this)

                    val accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                    Log.e(localClassName, "Account Name: $accountName")

                    if (accountName != null) {
                        mCredential.selectedAccountName = accountName
                    }
                    var current_email_tv:TextView = findViewById(R.id.current_email)
                    current_email_tv.text = mCredential.selectedAccountName

                    Thread(
                        Runnable{
                            var user = "me"
                            var listResponse:ListLabelsResponse =  service.users().labels().list(user).execute()
                            var labels:List<Label> = listResponse.labels



                            var bundle_num:Bundle = Bundle()
                            var msg_num:Message = Message()
                            bundle_num.putInt("TotalNum",labels.size)
                            msg_num.data = bundle_num
                            msg_num.what = GET_TOTAL_LABLES_NUM
                            mhandler.sendMessage(msg_num)


                            for (lable in labels){
                                var bundle:Bundle = Bundle()
                                var msg:Message = Message()
                                bundle.putString("LabelName",lable.name)
                                bundle.putString("LabelId",lable.id)


                                msg.data = bundle
                                msg.what = GET_LABLES
                                mhandler.sendMessage(msg)

                            }
                        }
                    ).start()

                }else if (data == null) {
                    Toast.makeText(this, "未選取任何帳號", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
    private var mhandler: Handler = object: Handler(){
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            when(msg!!.what){
                GET_LABLES ->{
                    var bundle:Bundle = msg.data
                    var label_name = bundle.getString("LabelName")
                    var label_id = bundle.getString("LabelId")

                    if (LabelsTitle.count{it.id==label_id}==0){// meaning the mutablelist don't have the label

                            var mLabelData = LabelData(label_id, label_name)
                            LabelsTitle.add(mLabelData)


                    }
                    if (total_labels_num ==LabelsTitle.size ){
                        sendEmptyMessage(COMPLETE_LABELS)
                    }

                }
                GET_TOTAL_LABLES_NUM->{
                    var bundle:Bundle = msg.data
                    total_labels_num = bundle.getInt("TotalNum")

                }
                COMPLETE_LABELS ->{
                    val sectionPagerAdapter = SectionsPagerAdapter(supportFragmentManager,LabelsTitle)
                    val viewPager: ViewPager = findViewById(R.id.view_pager)
                    viewPager.adapter = sectionPagerAdapter

                    val tabs: TabLayout = findViewById(R.id.tabs)
                    tabs.setupWithViewPager(viewPager)
                }

            }
        }
    }
     fun buildService(credential: GoogleAccountCredential): Gmail {
        val transport = AndroidHttp.newCompatibleTransport()
        return Gmail.Builder(transport, GsonFactory(), credential)
            .setApplicationName("Send Mail")
            .build()
    }
}
