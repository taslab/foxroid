package com.parse.starter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by Coty Saxman
 * TAS Design Group
 * Foxroid Tips #55
 * Cloud-based Image Storage
 */

public class ParseStarterProjectActivity extends Activity {

    LayoutInflater mInflater;   //Generates item from XML
    ImageView imageBox; //the image display
    //Request constants
    static final int REQUEST_CAMERA = 9001;
    static final int SELECT_FILE = 9002;

	/** Called when the activity is first created. */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
        initParse();

		ParseAnalytics.trackAppOpened(getIntent());

        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);

        mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

    /**Adds upload and download dialogs to the layout. Removes initialize button.*/
    public void scene1(View v) {
        //remove the unnecessary button
        ((ViewGroup) v.getParent()).removeView(v);
        //change the info text
        ((TextView)this.findViewById(R.id.textBox)).setText(R.string.scene1_info);
        addUploadButton();
        addDownloadButton();
    }

    //connect to the parse server
    private void initParse() {
        Parse.initialize(this, "5FvwABVcHBm8Q4KxDUNtmlR0htqcPXZNZmaaZKZG", "RTyR65BwAsd3sLeepEdtrgXDkI0Vw6szqU6htrHB");
    }

    //create the upload button and file field, and insert them
    private void addUploadButton() {
        LinearLayout uploadLayout = (LinearLayout)this.findViewById(R.id.upload);
        mInflater.inflate(R.layout.upload, uploadLayout);
    }

    //create and add the download button
    private void addDownloadButton() {
        LinearLayout downloadLayout = (LinearLayout)this.findViewById(R.id.download);
        mInflater.inflate(R.layout.download, downloadLayout);
    }

    /**submit a file and associated object to the parse server*/
    public void uploadFile(View vNULL) {
        selectImage();
    }

    /**retrieve a file with a given key from the parse server*/
    public void downloadFile(View vNULL) {
        ParseQuery<ParseObject> dlQuery = ParseQuery.getQuery("UserPhoto");
        dlQuery.getInBackground(((EditText)this.findViewById(R.id.downloadTextBox)).getText().toString(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(parseObject == null) {
                    Log.d("ObjectID", "The getFirst request failed");
                } else {
                    Log.d("ObjectID", "Retrieved the object");
                    displayPhoto(parseObject);
                }
            }
        });
    }

    //display the retrieved photo
    private void displayPhoto(ParseObject dlObject) {
        addImage();
        ParseFile pf = (ParseFile)dlObject.get("image");
        Log.d("Object URL", pf.getUrl());
        DownloadObject dlo = new DownloadObject(pf.getUrl(), imageBox);
        new DownloadImageTask().execute(dlo);
    }

    //add the photo display object to the screen
    private void addImage() {
        LinearLayout imageLayout = (LinearLayout)this.findViewById(R.id.image);
        mInflater.inflate(R.layout.image, imageLayout);
        imageBox = ((ImageView) this.findViewById(R.id.imageBox));
    }

    //select an image
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ParseStarterProjectActivity.this);
        builder.setTitle("Add photo...");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //handle result of image selection
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            if(requestCode == REQUEST_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for(File temp : f.listFiles()) {
                    if(temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btMapOptions = new BitmapFactory.Options();

                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(), btMapOptions);

                    //ivImage.setImageBitmap(bm);

                    String name = "photo.jpg";
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream(bm.getWidth() * bm.getHeight());
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, buffer);
                    ParseFile parseFile = new ParseFile(name, buffer.toByteArray());
                    parseFile.saveInBackground();

                    final ParseObject userPhoto = new ParseObject("UserPhoto");
                    userPhoto.put("image", parseFile);
                    userPhoto.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            ((EditText)ParseStarterProjectActivity.this.findViewById(R.id.downloadTextBox)).setText(userPhoto.getObjectId());
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();

                String tempPath = getPath(selectedImageUri, ParseStarterProjectActivity.this);
                Bitmap bm;
                BitmapFactory.Options btMapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btMapOptions);

                String name = "photo.jpg";
                ByteArrayOutputStream buffer = new ByteArrayOutputStream(bm.getWidth() * bm.getHeight());
                bm.compress(Bitmap.CompressFormat.JPEG, 100, buffer);
                ParseFile parseFile = new ParseFile(name, buffer.toByteArray());
                parseFile.saveInBackground();

                final ParseObject userPhoto = new ParseObject("UserPhoto");
                userPhoto.put("image", parseFile);
                userPhoto.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        ((EditText) ParseStarterProjectActivity.this.findViewById(R.id.downloadTextBox)).setText(userPhoto.getObjectId());
                    }
                });
            }
        }
    }

    //get file path
    private String getPath(Uri uri, Activity activity) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = activity.getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
}
