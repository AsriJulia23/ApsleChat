package com.example.chatrealtime.Fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatrealtime.ChatRoom;
import com.example.chatrealtime.Contacts;
import com.example.chatrealtime.Notifications.Token;
import com.example.chatrealtime.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private View pirvateChatView;
    private RecyclerView chatList;
    private DatabaseReference ChatRef,usersRef;
    private FirebaseAuth mAuth;
    private String CurrentUserId;
    private String retImage = "default_image";
    private FirebaseUser fuser;



    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        pirvateChatView= inflater.inflate(R.layout.fragment_chat, container, false);
        mAuth = FirebaseAuth.getInstance();
        CurrentUserId = mAuth.getCurrentUser().getUid();
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        ChatRef = FirebaseDatabase.getInstance().getReference().child("Contacts").child(CurrentUserId);
        usersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        chatList = (RecyclerView)pirvateChatView.findViewById(R.id.chat_list);
        chatList.setLayoutManager(new LinearLayoutManager(getContext()));

        updateToken(FirebaseInstanceId.getInstance().getToken());

        return pirvateChatView;
    }

    private void updateToken(String token){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token1= new Token(token);
        reference.child(fuser.getUid()).setValue(token1);

    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts,ChatsViewHolder>adapter=
                new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final ChatsViewHolder holder, int position, @NonNull Contacts model) {
                        final String usersIds=getRef(position).getKey();
                        final String[] retImage = {"default_image"};

                        usersRef.child(usersIds).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if (dataSnapshot.exists()){
                                    if (dataSnapshot.hasChild("image")){
                                        retImage[0] = dataSnapshot.child("image").getValue().toString();
                                        Picasso.get().load(retImage[0]).into(holder.profileImage);
                                    }

                                    final String retName=dataSnapshot.child("name").getValue().toString();
                                    final String retStatus=dataSnapshot.child("status").getValue().toString();

                                    holder.Username.setText(retName);

                                    if(dataSnapshot.child("userState").hasChild("state")){

                                        String state = dataSnapshot.child("userState").child("state").getValue().toString();
                                        String date = dataSnapshot.child("userState").child("date").getValue().toString();
                                        String time = dataSnapshot.child("userState").child("time").getValue().toString();

                                        if(state.equals("online")){
                                            holder.userStatus.setText("online");
                                        }else if(state.equals("offline")){
                                            holder.userStatus.setText("Last Seen: "  + date + " " + time);


                                        }
                                    }else {
                                        holder.userStatus.setText("offline");
                                    }




                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            Intent chatIntent = new Intent(getContext(),ChatRoom.class);
                                            chatIntent.putExtra("visit_user_id",usersIds);
                                            chatIntent.putExtra("visit_user_name",retName);
                                            chatIntent.putExtra("visit_image", retImage[0]);
                                            startActivity(chatIntent);
                                        }
                                    });
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    }

                    @NonNull
                    @Override
                    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                       View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                       return new ChatsViewHolder(view);
                    }
                };

        chatList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class ChatsViewHolder extends RecyclerView.ViewHolder{

        CircleImageView profileImage ;
        TextView userStatus, Username;

        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage=itemView.findViewById(R.id.users_profile_image);
            userStatus=itemView.findViewById(R.id.user_status);
            Username=itemView.findViewById(R.id.user_profile_name);
        }
    }
}
