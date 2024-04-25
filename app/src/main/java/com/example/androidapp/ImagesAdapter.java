package com.example.androidapp;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.widget.EditText;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Button;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androidapp.models.Picture;

import java.util.List;
import java.io.InputStream;
import java.io.IOException;
import android.net.Uri;
import java.io.FileNotFoundException;
import android.util.Log;
import com.example.androidapp.models.CurrentUser;
import com.example.androidapp.UserUtility;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder> {

    private List<Picture> images;
    private Context context;

    public ImagesAdapter(Context context, List<Picture> images) {
        this.context = context;
        this.images = images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Picture picture = images.get(position);
        new LoadImage(holder.imageViewPicture, context).execute(picture.getImagePath());
        holder.textViewImageName.setText(new java.io.File(picture.getImagePath()).getName());
        holder.textViewCaption.setText(picture.getCaption());
        holder.textViewDate.setText(picture.getCaptureDate());

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the item from the dataset
                images.remove(position);
                // Notify the adapter of the item removed
                UserUtility.saveUser(context, CurrentUser.getInstance().getUser(), "me.ser");
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, images.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (images != null) {
            return images.size();
        } else {
            return 0;
        }

    }

    private void showEditCaptionDialog(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Edit Caption");

        // Set up the input
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(images.get(position).getCaption());  // Pre-fill with current caption
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newCaption = input.getText().toString();
                Picture picture = images.get(position);
                picture.setCaption(newCaption);  // Update the caption
                UserUtility.saveUser(context, CurrentUser.getInstance().getUser(), "me.ser");
                notifyItemChanged(position);  // Notify to refresh the item
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewPicture;
        public TextView textViewImageName, textViewCaption, textViewDate;
        public Button deleteButton;

        public Button manageTagsButton;



        public ViewHolder(View itemView) {
            super(itemView);
            imageViewPicture = itemView.findViewById(R.id.imageViewPicture);
            textViewImageName = itemView.findViewById(R.id.textViewImageName);
            textViewCaption = itemView.findViewById(R.id.textViewCaption);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            deleteButton = itemView.findViewById(R.id.deleteButton);  // Add this line
            manageTagsButton = itemView.findViewById(R.id.manageTagsButton);
            manageTagsButton.setOnClickListener(v -> showAddTagDialog());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Define what happens when the itemView is clicked
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        showEditCaptionDialog(position);
                    }
                }
            });
        }

        private void showAddTagDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            builder.setTitle("Add Tag");

            // Retrieve the current picture based on adapter position
            int position = getAdapterPosition();
            Picture picture = images.get(position);

            // Set up the input fields
            final EditText inputPerson = new EditText(itemView.getContext());
            inputPerson.setHint("Person Tag");
            final EditText inputLocation = new EditText(itemView.getContext());
            inputLocation.setHint("Location Tag");

            // Pre-set existing tag values if they exist
            if (picture.getTagPersonValue() != null && !picture.getTagPersonValue().isEmpty()) {
                inputPerson.setText(picture.getTagPersonValue());
            }
            if (picture.getTagLocationValue() != null && !picture.getTagLocationValue().isEmpty()) {
                inputLocation.setText(picture.getTagLocationValue());
            }

            LinearLayout layout = new LinearLayout(itemView.getContext());
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(inputPerson);
            layout.addView(inputLocation);
            builder.setView(layout);

            // Set up the buttons
            builder.setPositiveButton("Add", (dialog, which) -> {
                String personTag = inputPerson.getText().toString();
                String locationTag = inputLocation.getText().toString();
                addTag(personTag, locationTag, position);  // Include position in the method call
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

            builder.show();
        }


        private void addTag(String personTag, String locationTag, int position) {
            Picture picture = images.get(position);

            picture.setTagPersonValue(personTag);
            picture.setTagLocationValue(locationTag);

            UserUtility.saveUser(context, CurrentUser.getInstance().getUser(), "me.ser");
            notifyItemChanged(position);  // Notify to refresh the item

            System.out.println("TAGS SET: " + "PERSON: " + personTag + " AND LOCATION: " + locationTag);
        }

    }

    // AsyncTask to load images in the background
    private static class LoadImage extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        private Context context;

        public LoadImage(ImageView imageView, Context context) {
            this.imageView = imageView;
            this.context = context;
        }
        @Override
        protected Bitmap doInBackground(String... strings) {
            String imagePath = strings[0];
            InputStream inputStream = null;
            try {
                // Use ContentResolver to get the InputStream
                inputStream = context.getContentResolver().openInputStream(Uri.parse(imagePath));
                return BitmapFactory.decodeStream(inputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
