package com.example.instagram.Adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.instagram.CommentsActivity;
import com.example.instagram.FollwersActivity;
import com.example.instagram.Fragment.PostDetailsFragment;
import com.example.instagram.Fragment.ProfileFragment;
import com.example.instagram.Model.Post;
import com.example.instagram.Model.User;
import com.example.instagram.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    public Context context;
    private List<Post> mPost;
    private FirebaseUser firebaseUser;

    public PostAdapter(Context context, List<Post> post) {
        this.context = context;
        mPost = post;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.post_item,parent,false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
            firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
            final Post post=mPost.get(position);

            Glide.with(context).load(post.getPostimage()).into(holder.post_image);

            if (post.getDescription().equals("")){
                holder.description.setVisibility(View.GONE);

            }else {
                holder.description.setVisibility(View.VISIBLE);
                holder.description.setText(post.getDescription());
            }
            publisherInfo(holder.image_profile,holder.username,holder.publisher,post.getPublisher());

            isLiked(post.getPostid(),holder.like);
            no_Likes(holder.likes,post.getPostid());



            getComments(post.getPostid(),holder.comments);

            isSaved(post.getPostid(),holder.save);


            holder.comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(context, CommentsActivity.class);
                    intent.putExtra("postid",post.getPostid());
                    intent.putExtra("publisherid",post.getPublisher());
                    context.startActivity(intent);
                }
            });

        holder.comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, CommentsActivity.class);
                intent.putExtra("postid",post.getPostid());
                intent.putExtra("publisherid",post.getPublisher());
                context.startActivity(intent);
            }
        });

        holder.save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.save.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid())
                            .child(post.getPostid()).removeValue();
                }
            }
        });

        holder.likes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context, FollwersActivity.class);
                intent.putExtra("id",post.getPostid());
                intent.putExtra("title","likes");
                context.startActivity(intent);
            }
        });



            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.like.getTag().equals("like")){
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).setValue(true);
                        addNotifications(post.getPublisher(),post.getPostid());


                    }else {
                        FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostid())
                                .child(firebaseUser.getUid()).removeValue();
                    }
                }
            });

            holder.image_profile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileid",post.getPublisher());
                    editor.apply();
                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                }
            });

        holder.publisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });
        holder.post_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postid",post.getPostid());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PostDetailsFragment()).commit();
            }
        });

        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileid",post.getPublisher());
                editor.apply();
                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mPost.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image_profile,post_image,like,comment,save;
        public TextView username,likes,comments,publisher,description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image_profile=itemView.findViewById(R.id.image_profile);
            post_image=itemView.findViewById(R.id.post_image);
            like=itemView.findViewById(R.id.like);
            likes=itemView.findViewById(R.id.likes);
            comment=itemView.findViewById(R.id.comment);
            comments=itemView.findViewById(R.id.comments);
            save=itemView.findViewById(R.id.save);
            publisher=itemView.findViewById(R.id.publisher);
            description=itemView.findViewById(R.id.description);
            username=itemView.findViewById(R.id.username);
        }
    }
    private void getComments(String postid,final TextView comments){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                comments.setText("View all "+dataSnapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void isLiked(String postId, final ImageView imageView){
            final FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

            DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(firebaseUser.getUid()).exists()){
                        imageView.setImageResource(R.drawable.ic_liked);
                        imageView.setTag("liked");
                    }else {
                        imageView.setImageResource(R.drawable.ic_like);
                        imageView.setTag("like");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
    }

    private void no_Likes(final TextView likes,String postId){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                likes.setText(dataSnapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void publisherInfo(final ImageView image_profile, final TextView username, final TextView publisher, final String userid){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users").child(userid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                publisher.setText(user.getUsername());            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void isSaved(final String postId, final ImageView imageView){
        FirebaseUser firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("Saves").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(postId).exists()){
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("Saved");
                }else {
                    imageView.setImageResource(R.drawable.ic_save);
                    imageView.setTag("save");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void addNotifications(String userid,String postid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","liked your post");
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);

        reference.push().setValue(hashMap);


    }
}
