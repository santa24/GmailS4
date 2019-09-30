package com.example.gmails4

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ListAdapter(private val list:List<MailTitle>):
    RecyclerView.Adapter<MailTitleViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MailTitleViewHolder {
        val inflater:LayoutInflater = LayoutInflater.from(parent.context)
        return MailTitleViewHolder(inflater,parent,list)
    }
    override fun getItemCount():Int = list.size
    /*override fun getItemCount(): Int {
        return list.size
    }*/

    // get data object to bind  viewholder
    override fun onBindViewHolder(holder: MailTitleViewHolder, position: Int) {
        val mailtitle: MailTitle = list[position]

        holder.itemView.tag = position
        holder.bind(mailtitle)
        // user tag save position

    }

}

class MailTitleViewHolder(inflater: LayoutInflater, parent:ViewGroup,list:List<MailTitle>):
    RecyclerView.ViewHolder(inflater.inflate(R.layout.mail_item,parent,false)){
    private var mSenderView: TextView? = null
    private var mSubjectView:TextView? = null
    private var mDateView:TextView? = null

    init{
        mSenderView = itemView.findViewById(R.id.sender_tv)
        mSubjectView = itemView.findViewById(R.id.subject_tv)
        mDateView = itemView.findViewById(R.id.date_tv)


        itemView.setOnClickListener{

            var intent: Intent = Intent(parent.context,ShowEmailActivity::class.java)


            if ((list.get(itemView.tag as Int)).id != "" ) {
                intent.putExtra("MessageId", (list.get(itemView.tag as Int)).id)
                parent.context.startActivity(intent)
            }
        }

    }
    //this is original fun to bind view and data
    fun bind(mailtitle: MailTitle){
        //var mDate = Date(mailtitle.date)
        var mDate:String
        if (mailtitle.date.toInt() == 0)
            mDate = ""
        else
            mDate =  SimpleDateFormat("MM/dd").format(Date(mailtitle.date))

        mSenderView?.text = mailtitle.sender
        mSubjectView?.text = mailtitle.subject


        mDateView?.text = mDate
    }
}