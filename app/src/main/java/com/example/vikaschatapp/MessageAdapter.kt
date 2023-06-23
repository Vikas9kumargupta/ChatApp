package com.example.vikaschatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class MessageAdapter(val context: Context, val messageList : ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1;
    val ITEM_SENT = 2;

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if(viewType == ITEM_RECEIVE){
            //inflate receive
            val view: View = LayoutInflater.from(context).inflate(R.layout.layout_receiver_message,parent,false)
            return ReceiverViewHolder(view)
        }else{
            //inflate sent
            val view: View = LayoutInflater.from(context).inflate(R.layout.layout_sender_message,parent,false)
            return SentViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if(FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)){
            return ITEM_SENT
        }else{
            return ITEM_RECEIVE
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]

        if (holder is SentViewHolder) {
            val viewHolder = holder as SentViewHolder
            viewHolder.sentMessage.text = currentMessage.message
            viewHolder.sentImage.visibility = View.GONE

            if (!currentMessage.imageUrl.isNullOrEmpty()) {
                viewHolder.sentImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(currentMessage.imageUrl)
                    .into(viewHolder.sentImage)
            }
        } else if (holder is ReceiverViewHolder) {
            val viewHolder = holder as ReceiverViewHolder
            viewHolder.receiveMessage.text = currentMessage.message
            viewHolder.receiveImage.visibility = View.GONE

            if (!currentMessage.imageUrl.isNullOrEmpty()) {
                viewHolder.receiveImage.visibility = View.VISIBLE
                Glide.with(context)
                    .load(currentMessage.imageUrl)
                    .into(viewHolder.receiveImage)
            }
        }
    }

    }

    class SentViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val sentImage = itemView.findViewById<ImageView>(R.id.img_sent_image)
    }

    class ReceiverViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val receiveImage = itemView.findViewById<ImageView>(R.id.img_receive_image)
    }
