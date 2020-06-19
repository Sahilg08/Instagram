package com.example.instagram.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.AddStoryActivity;
import com.example.instagram.Model.Story;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.example.instagram.StoryActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{
   private Context context;
    private List<Story> mStory;

    public StoryAdapter(Context context, List<Story> mStory) {
        this.context = context;
        this.mStory = mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i==0){
            View view= LayoutInflater.from(context).inflate(R.layout.add_story_item,viewGroup,false);
            return new StoryAdapter.ViewHolder(view);
        }else {
            View view= LayoutInflater.from(context).inflate(R.layout.story_item,viewGroup,false);
            return new StoryAdapter.ViewHolder(view);

        }

    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int i) {

        final Story story=mStory.get(i);
        userInfo(holder,story.getUserid(),i);
        if (holder.getAdapterPosition() !=0){
            seenStory(holder,story.getUserid());
        }
        if (holder.getAdapterPosition()==0){
            myStory(holder.addstory_text,holder.story_plus,false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition()==0){
                    myStory(holder.addstory_text,holder.story_plus,true);
                }else {
                    Intent intent=new Intent(context, StoryActivity.class);
                    intent.putExtra("userid",story.getUserid());
                    context.startActivity(intent);

                }
            }
        });




    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView story_photo,story_photo_seen,story_plus;
        public TextView addstory_text,story_username;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            story_photo=itemView.findViewById(R.id.story_photo);
            story_photo_seen=itemView.findViewById(R.id.story_photo_seen);
            story_plus=itemView.findViewById(R.id.story_plus);

            addstory_text=itemView.findViewById(R.id.addstory_text);
            story_username=itemView.findViewById(R.id.story_username);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
        return 0;
        }
        return 1;
    }
    private void userInfo(final ViewHolder viewHolder, final String userid, final int pos){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(viewHolder.story_photo);
                if (pos!=0){
                    Glide.with(context).load(user.getImageurl()).into(viewHolder.story_photo_seen);
                    viewHolder.story_username.setText(user.getUsername());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void myStory(final TextView textView, final ImageView imageView, final Boolean click){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int count=0;
                long timecurrent=System.currentTimeMillis();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Story story=snapshot.getValue(Story.class);
                    if (timecurrent>story.getTimestart()&&timecurrent< story.getTimeend()){
                        count++;
                    }
                }
                if (click){
                    if (count>0){
                        AlertDialog alertDialog=new AlertDialog.Builder(context).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent=new Intent(context, StoryActivity.class);
                                        intent.putExtra("userid",FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        context.startActivity(intent);
                                        dialogInterface.dismiss();


                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent=new Intent(context, AddStoryActivity.class);
                                        context.startActivity(intent);
                                        dialogInterface.dismiss();

                                    }
                                });
                        alertDialog.show();
                    }else {
                        Intent intent=new Intent(context, AddStoryActivity.class);
                        context.startActivity(intent);

                    }

                }else {
                    if (count>0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }else {
                        textView.setText("Add story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private  void seenStory(final ViewHolder viewHolder, String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    if (!snapshot.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).exists()
                            && System.currentTimeMillis()<snapshot.getValue(Story.class).getTimeend()){

                        i++;

                    }

                }
                if (i>0){
                    viewHolder.story_photo.setVisibility(View.VISIBLE);
                    viewHolder.story_photo_seen.setVisibility(View.GONE);
                }else {
                    viewHolder.story_photo.setVisibility(View.GONE);
                    viewHolder.story_photo_seen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
