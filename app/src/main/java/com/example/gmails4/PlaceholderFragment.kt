package com.example.gmails4


import android.app.ActivityManager
import android.content.Context.ACTIVITY_SERVICE
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.gmail.Gmail
import com.google.api.services.gmail.model.ListMessagesResponse


class PlaceholderFragment: Fragment(){
    private lateinit var pageViewModel:PageViewModel
    lateinit var root:View

    private val GOT_INIT_TITLES = 1
    private val GOT_TITLES=2
    private val IS_LAST = 3
    private val NO_DATA = 4
    private val GET_MESSAGES_NUM = 5
    private val GET_PAGE_TOKEN = 6

    var isLoading = false
    var isLast = false

    var PageToken:String = ""


    lateinit var MailTitles:MutableList<MailTitle>
    lateinit var Searched_MailTitles:MutableList<MailTitle>
    private var isInitialized = false

    private var MAX_RESULTS:Long = 50
    private var RESPONSE_COUNT:Long = 0
    private var  Labels_Position = 0

    lateinit var mMainActivity:MainActivity
    lateinit var  iRecyclerView:RecyclerView
    lateinit var mProgressBar: ProgressBar
    lateinit var service:Gmail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewModelProviders is factory to return ViewModel
        MailTitles = mutableListOf()
        Searched_MailTitles = mutableListOf()


        mMainActivity = activity as MainActivity
        service = buildService(mMainActivity.mCredential)
       // mMainActivity.mCredential
        Labels_Position = arguments!!.getInt(ARG_SECTION_NUMBER)

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState:Bundle? ): View?{

        root = inflater.inflate(R.layout.page_fragment,container,false)
        mProgressBar = root.findViewById(R.id.progressBar)
        mProgressBar.visibility = View.GONE

        if (isInitialized) {
            iRecyclerView = root.findViewById(R.id.mRecyclerview)
            iRecyclerView.apply{
                this.layoutManager = LinearLayoutManager(context)

                this.adapter=ListAdapter(MailTitles)
            }
            iRecyclerView.addOnScrollListener(FragmentOnScrollListener(mhandler))
        }
        else if(!isLast){
            mProgressBar.visibility = View.VISIBLE
            Thread(
                Get_Email_Titles_Runnable(mhandler,PageToken)
            ).start()

        }
        else{//NoDATA
            iRecyclerView = root.findViewById(R.id.mRecyclerview)
            iRecyclerView.apply{
                this.layoutManager = LinearLayoutManager(context)

                this.adapter=ListAdapter(MailTitles)
            }
        }

        return root

    }

    fun buildService(credential: GoogleAccountCredential): Gmail {
        val transport = AndroidHttp.newCompatibleTransport()
        return Gmail.Builder(transport, GsonFactory(), credential)
            .setApplicationName("Send Mail")
            .build()
    }

     private  var mhandler: Handler = object:Handler(){
         override fun handleMessage(msg:android.os.Message) {//because have another Message class
             val what = msg.what
             when (what) {
                 GOT_INIT_TITLES -> {
                     //Toast.makeText(context,mMainActivity.mCredential.token.toString(),Toast.LENGTH_SHORT).show()
                    // Log.e("tag","gettitle_init")

                     var data:Bundle = msg.data
                     var From = data.getString("From")


                     var arrow_pos = From.indexOf("<")
                     if (arrow_pos>0) From = From.substring(0,From.indexOf("<"))


                     var Subject = data.getString("Subject")
                     var mSecond = data.getLong("mSecond")
                     var id = data.getString("Id")

                     Searched_MailTitles.add(MailTitle(From,Subject,mSecond,id))

                     if (Searched_MailTitles.size  == RESPONSE_COUNT.toInt()&&!isLast){



                         MailTitles.addAll(Searched_MailTitles)
                         Searched_MailTitles = mutableListOf()
                         MailTitles.sortByDescending { it.date }
                         iRecyclerView = root.findViewById(R.id.mRecyclerview)
                         iRecyclerView.apply{
                             this.layoutManager = LinearLayoutManager(activity)

                             this.adapter=ListAdapter(MailTitles)
                         }


                         iRecyclerView.addOnScrollListener(FragmentOnScrollListener(this))

                         isLoading = false
                         isInitialized = true
                         mProgressBar.visibility = View.GONE

                     }



                 }
                 GOT_TITLES->{
                     //Toast.makeText(mMainActivity,mMainActivity.mCredential.token,Toast.LENGTH_SHORT).show()

                   // Log.e("tag","gettitle")

                     var data:Bundle = msg.data
                     var From = data.getString("From")

                     var arrow_pos = From.indexOf("<")
                     if (arrow_pos>0) From = From.substring(0,From.indexOf("<"))

                     var Subject = data.getString("Subject")
                     var mSecond = data.getLong("mSecond")
                     var id = data.getString("Id")


                     Searched_MailTitles.add(MailTitle(From,Subject,mSecond,id))

                     if (Searched_MailTitles.size  == RESPONSE_COUNT.toInt()){

                         MailTitles.addAll(Searched_MailTitles)

                         Searched_MailTitles = mutableListOf()

                         MailTitles.sortByDescending { it.date }
                         iRecyclerView = root.findViewById(R.id.mRecyclerview)
                         iRecyclerView.adapter!!.notifyDataSetChanged()


                         isLoading = false
                         mProgressBar.visibility = View.GONE
                     }

                 }
                 IS_LAST->{
                    isLast = true
                     mProgressBar.visibility = View.GONE

                 }
                 NO_DATA->{
                     mProgressBar.visibility = View.GONE
                     isLast = true
                     MailTitles.add(MailTitle("此標籤沒有信件","",0,""))
                     iRecyclerView = root.findViewById(R.id.mRecyclerview)
                     iRecyclerView.apply{
                         this.layoutManager = LinearLayoutManager(activity)
                         this.adapter=ListAdapter(MailTitles)
                     }


                 }
                 GET_MESSAGES_NUM->{
                     var bundle = msg.data
                     RESPONSE_COUNT = bundle.getInt("Total_Response").toLong()
                     Searched_MailTitles = mutableListOf()
                     if (RESPONSE_COUNT.toInt() == 0) {
                         isLast = true
                     }
                 }
                 GET_PAGE_TOKEN->{
                     var bundle = msg.data
                     PageToken = bundle.getString("PAGE_TOKEN")
                 }

             }
         }
     }
    inner class FragmentOnScrollListener:RecyclerView.OnScrollListener{
       // lateinit var mhandler:Handler
        constructor(mhandler:Handler){
          //  this.mhandler = mhandler
        }


        var visibleItemCount:Int = 0
        var totalItemCount:Int = 0
        var pastVisibleItems:Int = 0
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

        }
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)

            // var lastposition = (iRecyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()
            // Log.e("LastPosition",lastposition.toString())
            visibleItemCount = (iRecyclerView.layoutManager as LinearLayoutManager).childCount
            totalItemCount = (iRecyclerView.layoutManager as LinearLayoutManager).itemCount
            pastVisibleItems = (iRecyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
            if (pastVisibleItems + visibleItemCount>=(totalItemCount-10) && !isLoading&&!isLast){//preload 10
                //Log.e("last","past:"+pastVisibleItems.toString()+"visible:"+visibleItemCount.toString()+"="+totalItemCount.toString())


                mProgressBar.visibility = View.VISIBLE

                isLoading = true
                Thread(
                    Get_Next_Email_Titles_Runnable(mhandler,PageToken)
                ).start()

                Log.e("last","This is the fucking most bottom!!")
                Log.e("TitleCount",MailTitles.size.toString())

                var mi = ActivityManager.MemoryInfo()
                var activityManager:ActivityManager = mMainActivity.getSystemService(ACTIVITY_SERVICE) as ActivityManager
                activityManager.getMemoryInfo(mi)

                var availableMegs = mi.availMem / 0x100000L

                Log.e("memory:",availableMegs.toString())

            }

            /*when(newState){
                RecyclerView.SCROLL_STATE_IDLE->{
                   Log.e("hihi","SCROLL_STATE_IDLE")
                }
                RecyclerView.SCROLL_STATE_DRAGGING->{
                    Log.e("hihi","SCROLL_STATE_DRAGGING")Get_Email_Titles_Runnable

                }
                RecyclerView.SCROLL_STATE_SETTLING->{
                    Log.e("hihi","SCROLL_STATE_SETTLING")

                }
            }*/

        }
    }

    inner class Get_Email_Titles_Runnable:Runnable{
         var mHandler: Handler
         var mPageToken:String
        constructor(mHandler:Handler,mPageToken:String){
            this.mHandler = mHandler
            this.mPageToken = mPageToken
        }
        override fun run() {
            try {
                if (!isLast) {

                    var user = "me"
                    var search_label_id = listOf(mMainActivity.LabelsTitle.get(Labels_Position).id)
                    var listResponse: ListMessagesResponse =
                        service.users().messages().list(user).setMaxResults(MAX_RESULTS)
                            .setLabelIds(search_label_id).setPageToken(mPageToken).execute()
                    var messages = listResponse.messages
                    Log.e("list done","list fucking done")

                    if (listResponse.nextPageToken != null) {
                        var msg_token : android.os.Message = android.os.Message()
                        var bundle_token: Bundle = Bundle()

                        bundle_token.putString("PAGE_TOKEN", listResponse.nextPageToken)

                        msg_token.data = bundle_token
                        msg_token.what = GET_PAGE_TOKEN
                        mHandler.handleMessage(msg_token)

                    }
                    else {
                        mhandler.sendEmptyMessage(IS_LAST)
                    }


                    var msg_total: android.os.Message = android.os.Message()
                    var bundle_total: Bundle = Bundle()

                    msg_total.what = GET_MESSAGES_NUM

                    if (messages == null) {
                        messages = listOf()
                        mHandler.sendEmptyMessage(NO_DATA)
                    }

                    bundle_total.putInt("Total_Response", messages.size)
                    msg_total.data = bundle_total



                    mHandler.handleMessage(msg_total)
                    Log.e("complete",mMainActivity.mCredential.token)
                    Log.e("complete",messages.size.toString())

                    for (message in messages) {
                        Thread(
                            Runnable {
                                try {

                                    var Subject: String = ""
                                    var From: String = ""
                                    var mSecond: Long = 0
                                    var user_get = "me"
                                    var Msg: android.os.Message = android.os.Message()
                                    var bundle: Bundle = Bundle()
                                    Log.e("complete",message.id)
                                    var message = service.users().messages().get(user_get, message.id).execute()


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


                                    bundle.putString("From", From)
                                    bundle.putString("Subject", Subject)
                                    bundle.putLong("mSecond", mSecond)
                                    bundle.putString("Id", message.id)
                                    Msg.data = bundle
                                    Msg.what = GOT_INIT_TITLES
                                    mHandler.sendMessage(Msg)
                                }catch(e:Exception){
                                    Log.e("fuck error2",e.message)
                                }

                            }
                        ).start()
                    }
                }
            } catch (e:Exception ){
                Log.e("fuck error",e.message)
            }

        }
    }

    inner class Get_Next_Email_Titles_Runnable:Runnable{
        var mHandler: Handler
        var mPageToken:String
        constructor(mHandler:Handler,mPageToken:String){
            this.mHandler = mHandler
            this.mPageToken = mPageToken
        }
        override fun run() {
          //  Log.e("run","This is the fucking Runnable!!")

            try {
                if (!isLast) {

                    var user = "me"
                    var search_label = listOf(mMainActivity.LabelsTitle.get(Labels_Position).id)
                    var listResponse: ListMessagesResponse =
                        service.users().messages().list(user).setMaxResults(MAX_RESULTS)
                            .setLabelIds(search_label).setPageToken(PageToken).execute()
                    var messages = listResponse.messages
                    Log.e("list done","list fucking done")



                    if (listResponse.nextPageToken != null) {

                        var msg_token : android.os.Message = android.os.Message()
                        var bundle_token: Bundle = Bundle()

                        bundle_token.putString("PAGE_TOKEN", listResponse.nextPageToken)

                        msg_token.data = bundle_token
                        msg_token.what = GET_PAGE_TOKEN
                        mHandler.handleMessage(msg_token)
                    }
                    else {
                        mhandler.sendEmptyMessage(IS_LAST)
                    }


                    var msg_total: android.os.Message = android.os.Message()
                    var bundle_total: Bundle = Bundle()

                    msg_total.what = GET_MESSAGES_NUM
                    bundle_total.putInt("Total_Response", messages.size)
                    msg_total.data = bundle_total
                    mHandler.handleMessage(msg_total)
                    Log.e("complete",mMainActivity.mCredential.token)
                    Log.e("complete",messages.size.toString())



                    for (message in messages) {
                        Thread(
                            Runnable {
                                try {
                                    var Subject: String = ""
                                    var From: String = ""
                                    var mSecond: Long = 0
                                    var user_get = "me"
                                    var Msg: android.os.Message = android.os.Message()
                                    var bundle: Bundle = Bundle()
                                    Log.e("complete",message.id)
                                    var message = service.users().messages().get(user_get, message.id).execute()

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
                                    bundle.putString("From", From)
                                    bundle.putString("Subject", Subject)
                                    bundle.putLong("mSecond", mSecond)
                                    bundle.putString("Id", message.id)
                                    Msg.data = bundle
                                    Msg.what = GOT_TITLES
                                    mHandler.sendMessage(Msg)

                                } catch(e:Exception){
                                    Log.e("fuck error2",e.message)
                                }
                            }
                        ).start()
                    }
                }
            }catch(e:Exception){
                Log.e("fuck error",e.message)
            }

        }
    }
    companion object{
        private const val ARG_SECTION_NUMBER = "section_number"
        @JvmStatic
        fun newInstance(sectionNumber:Int):PlaceholderFragment{
            return PlaceholderFragment().apply{
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}