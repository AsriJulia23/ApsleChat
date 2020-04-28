package com.example.chatrealtime;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class FindFriendActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView findFriendRv;
    private DatabaseReference UsersRef;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_friend);

        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        findFriendRv = (RecyclerView)findViewById(R.id.find_friend_rv);
        findFriendRv.setLayoutManager(new LinearLayoutManager(this));


        toolbar = (androidx.appcompat.widget.Toolbar)findViewById(R.id.find_friend_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Find Friend");

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Contacts> options =
                new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(UsersRef, Contacts.class)
                .build();

        FirebaseRecyclerAdapter<Contacts, FindFriendViewholder> adapter =
                new FirebaseRecyclerAdapter<Contacts, FindFriendViewholder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull FindFriendViewholder holder, final int position, @NonNull Contacts model) {
                        holder.username.setText(model.getName());
                        holder.status.setText(model.getStatus());
                        Picasso.get().load(model.getImage()).placeholder(R.drawable.user).into(holder.profileImage);


                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String visitUserId = getRef(position).getKey();

                                Intent profileIntent = new Intent(FindFriendActivity.this,ProfileActivity.class);
                                profileIntent.putExtra("visit_user_id",visitUserId);
                                startActivity(profileIntent);
                            }
                        });

                    }

                    @NonNull
                    @Override
                    public FindFriendViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout,parent,false);
                        FindFriendViewholder viewholder = new FindFriendViewholder(view);
                        return viewholder;


                    }
                };

        findFriendRv.setAdapter(adapter);

        adapter.startListening();

    }

    public static class FindFriendViewholder extends RecyclerView.ViewHolder{
        TextView username, status;
        CircleImageView profileImage;


        public FindFriendViewholder(@NonNull View itemView) {

            super(itemView);

            username = itemView.findViewById(R.id.user_profile_name);
            status = itemView.findViewById(R.id.user_status);
            profileImage = itemView.findViewById(R.id.users_profile_image);
        }
    }
}
