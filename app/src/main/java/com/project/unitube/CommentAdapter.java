package com.project.unitube;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context context;
    private List<Comment> comments;
    private CommentAdapterListener listener;

    public interface CommentAdapterListener {
        void onCommentDeleted(int newCommentCount);
    }

    public CommentAdapter(Context context, List<Comment> comments, CommentAdapterListener listener) {
        this.context = context;
        this.comments = comments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.commentUserName.setText(comment.getUserName());
        holder.commentContent.setText(comment.getCommentText());

        // Load user profile image
        Log.d(TAG, "commentAdapter : username = " + comment.getUserName());
        Log.d(TAG, "commentAdapter : user ProfilePictureUri = " + comment.getProfilePicture());
        int profileImageResourceId = context.getResources().getIdentifier(comment.getProfilePicture().getLastPathSegment(), "drawable", context.getPackageName());
        if (profileImageResourceId != 0) {
            holder.commentUserProfileImage.setImageResource(profileImageResourceId);
        } else {
            holder.commentUserProfileImage.setImageResource(R.drawable.ic_profile_placeholder); // Fallback profile image
        }

        // Set up more options button logic
        holder.moreOptionsButton.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context, holder.moreOptionsButton);
            popupMenu.inflate(R.menu.comment_options_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (RegisterScreen.currentUser == null) {
                        // User is not logged in, show notification
                        Toast.makeText(context, "You must be logged in to perform this action.", Toast.LENGTH_SHORT).show();
                        return true;
                    } else {
                        int adapterPosition = holder.getAdapterPosition(); // Use getAdapterPosition()
                        if (adapterPosition == RecyclerView.NO_POSITION) {
                            return false;
                        }
                        if (item.getItemId() == R.id.action_edit) {
                            // Handle edit action
                            showEditDialog(adapterPosition, comment);
                            return true;
                        } else if (item.getItemId() == R.id.action_delete) {
                            // Show confirmation dialog
                            new AlertDialog.Builder(context)
                                    .setTitle("Delete Comment")
                                    .setMessage("Are you sure you want to delete this comment?")
                                    .setPositiveButton("Yes", (dialog, which) -> {
                                        // Delete the comment
                                        comments.remove(adapterPosition);
                                        notifyItemRemoved(adapterPosition);
                                        notifyItemRangeChanged(adapterPosition, comments.size());
                                        // Notify listener about the deletion
                                        if (listener != null) {
                                            listener.onCommentDeleted(comments.size());
                                        }
                                    })
                                    .setNegativeButton("No", null)
                                    .show();
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
            });
            popupMenu.show();
        });
    }

    private void showEditDialog(int position, Comment comment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Comment");

        // Set up the input
        final EditText input = new EditText(context);
        input.setText(comment.getCommentText());
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", (dialog, which) -> {
            String newCommentText = input.getText().toString().trim();
            if (!newCommentText.isEmpty()) {
                comment.setCommentText(newCommentText);
                notifyItemChanged(position);
            } else {
                Toast.makeText(context, "Comment cannot be empty.", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView commentUserProfileImage;
        TextView commentUserName;
        TextView commentContent;
        ImageButton moreOptionsButton;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            commentUserProfileImage = itemView.findViewById(R.id.comment_user_profile_image); // ID from round_comment_profile_image.xml
            commentUserName = itemView.findViewById(R.id.comment_user_name);
            commentContent = itemView.findViewById(R.id.comment_content);
            moreOptionsButton = itemView.findViewById(R.id.moreOptionsButton);
        }
    }
}
