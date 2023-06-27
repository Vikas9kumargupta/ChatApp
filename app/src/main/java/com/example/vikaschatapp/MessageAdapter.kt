package com.example.vikaschatapp

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(private val context: Context, private val messageList : ArrayList<Message>)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val ITEM_RECEIVE = 1
    private val ITEM_SENT = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == ITEM_RECEIVE){
            //inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.layout_receiver_message,parent,false)
            ReceiverViewHolder(view)
        }else{
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.layout_sender_message,parent,false)
            SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        return if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            ITEM_SENT
        }else{
            ITEM_RECEIVE
        }
    }

    val selectedItems: HashSet<Int> = HashSet()
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (holder is SentViewHolder) {
            val viewHolder = holder as SentViewHolder
            viewHolder.sentImage.visibility = View.GONE
            viewHolder.sentMessage.visibility = View.GONE

            if (!currentMessage.message.isNullOrEmpty()) {
                viewHolder.sentMessage.visibility = View.VISIBLE
                viewHolder.sentMessage.text = currentMessage.message
            } else if (!currentMessage.imageUrl.isNullOrEmpty()) {
                viewHolder.sentImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(currentMessage.imageUrl)
                    .into(viewHolder.sentImage)
            }
        } else if (holder is ReceiverViewHolder) {
            val viewHolder = holder as ReceiverViewHolder
            viewHolder.receiveImage.visibility = View.GONE
            viewHolder.receiveMessage.visibility = View.GONE

            if (!currentMessage.message.isNullOrEmpty()) {
                viewHolder.receiveMessage.visibility = View.VISIBLE
                viewHolder.receiveMessage.text = currentMessage.message
            } else if (!currentMessage.imageUrl.isNullOrEmpty()) {
                viewHolder.receiveImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(currentMessage.imageUrl)
                    .into(viewHolder.receiveImage)
            }
        }
    }
}


    class SentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sentMessage: TextView = itemView.findViewById(R.id.txt_sent_message)
        val sentImage: ImageView = itemView.findViewById(R.id.img_sent_image)
    }

    class ReceiverViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val receiveMessage: TextView = itemView.findViewById(R.id.txt_receive_message)
        val receiveImage: ImageView = itemView.findViewById(R.id.img_receive_image)
    }
