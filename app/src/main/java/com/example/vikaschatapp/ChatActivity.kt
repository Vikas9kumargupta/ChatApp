package com.example.vikaschatapp

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vikaschatapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList : ArrayList<Message>
    private lateinit var mDbRef : DatabaseReference

    var receiverRoom : String? = null
    private var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDbRef = FirebaseDatabase.getInstance().getReference()

        val name = intent.getStringExtra("name")
        val uid = intent.getStringExtra("uid")

        supportActionBar!!.title = name


        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser!!.uid


        senderRoom = receiverUid + senderUid

        receiverRoom = senderUid + receiverUid



        messageList = ArrayList()
        messageAdapter = MessageAdapter(this,messageList)

        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        // logic for adding data to recycler View

        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener{

                @SuppressLint("NotifyDataSetChanged")
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear()

                    for(postSnapshot in snapshot.children){

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
                    //Toast.makeText(this@ChatActivity,error.message, Toast.LENGTH_SHORT).show()
                }

            })

        binding.sent.setOnClickListener {
            //logic to send the message to dataBase
            val message = binding.messageBox.text.toString()
            val messageObject = Message(message,senderUid)


            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            binding.messageBox.text?.clear()
        }

    }
}