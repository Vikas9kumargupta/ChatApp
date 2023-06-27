package com.example.vikaschatapp

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vikaschatapp.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.UUID

class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList : ArrayList<Message>
    private lateinit var storageReference: StorageReference
    private lateinit var mDbRef : DatabaseReference
    private lateinit var onlineStatusMap: HashMap<String, Boolean>

    private val REQUEST_IMAGE_PICKER = 1


    private var receiverRoom : String? = null
    private var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mDbRef = FirebaseDatabase.getInstance().reference
        storageReference = FirebaseStorage.getInstance().reference

        val name = intent.getStringExtra("name")
        val uid = intent.getStringExtra("uid")

        supportActionBar!!.title = name

        binding.sendImage.setOnClickListener {
            openImagePicker()
        }

        onlineStatusMap = HashMap()

        val receiverUid = intent.getStringExtra("uid")
        val senderUid = FirebaseAuth.getInstance().currentUser!!.uid

        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)

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
                    Toast.makeText(this@ChatActivity,error.message, Toast.LENGTH_SHORT).show()
                }
            })

        binding.sent.setOnClickListener {
            //logic to send the message to dataBase
            val message = binding.messageBox.text.toString()
            val messageObject = Message(message,senderUid,imageUrl = null)

            mDbRef.child("chats").child(senderRoom!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                        .setValue(messageObject)
                }
            binding.messageBox.text?.clear()
        }
    }
    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICKER)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICKER && resultCode == RESULT_OK) {
            val selectedImageUri: Uri? = data?.data
            if (selectedImageUri != null) {
                // Generate a unique filename for the image
                val filename = UUID.randomUUID().toString()

                // Get a reference to the Firebase Storage location
                val storageRef = FirebaseStorage.getInstance().reference.child("images/$filename")

                // Upload the image file to Firebase Storage
                val uploadTask = storageRef.putFile(selectedImageUri)

                // Monitor the upload progress and handle success/failure
                uploadTask.addOnSuccessListener {
                    // Image uploaded successfully, retrieve the download URL
                    storageRef.downloadUrl.addOnSuccessListener {
                        // Create a new message object with the image URL
                        val message = Message(message = "", imageUrl = selectedImageUri.toString(), senderId = "")

                        // Add the message to the sender and receiver rooms
                        mDbRef.child("chats").child(senderRoom!!).child("messages").push().setValue(message)
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").push().setValue(message)

                        // Notify the user or update the UI
                        Toast.makeText(this@ChatActivity, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        // Failed to retrieve the download URL
                        Toast.makeText(this@ChatActivity, "Failed to retrieve image URL", Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener {
                    // Image upload failed
                    Toast.makeText(this@ChatActivity, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}