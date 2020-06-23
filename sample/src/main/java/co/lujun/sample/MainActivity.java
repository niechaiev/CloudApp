package co.lujun.sample;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;

public class MainActivity extends AppCompatActivity {

    public TagContainerLayout mTagContainerLayout1, mTagContainerLayout2,
            mTagContainerLayout3, mTagContainerLayout4, mTagcontainerLayout5;
    public AlertDialog.Builder builder;
    private AlertDialog.Builder builderLarger;
    private AlertDialog.Builder builderSmaller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final List<String> list1 = new ArrayList<String>();


        List<String> list2 = new ArrayList<String>();
        list2.add("China");
        list2.add("USA");
        list2.add("Austria");
        list2.add("Japan");
        list2.add("Sudan");
        list2.add("Spain");
        list2.add("UK");
        list2.add("Germany");
        list2.add("Niger");
        list2.add("Poland");
        list2.add("Norway");
        list2.add("Uruguay");
        list2.add("Brazil");

        String[] list3 = new String[]{"Persian", "波斯语", "فارسی", "Hello", "你好", "سلام"};
        String[] list4 = new String[]{"Adele", "Whitney Houston"};

        List<String> list5 = new ArrayList<String>();
        list5.add("Custom Red Color");
        list5.add("Custom Blue Color");


        mTagContainerLayout1 = (TagContainerLayout) findViewById(R.id.tagcontainerLayout1);
        mTagContainerLayout2 = (TagContainerLayout) findViewById(R.id.tagcontainerLayout2);
        mTagContainerLayout3 = (TagContainerLayout) findViewById(R.id.tagcontainerLayout3);
        mTagContainerLayout4 = (TagContainerLayout) findViewById(R.id.tagcontainerLayout4);
        mTagcontainerLayout5 = (TagContainerLayout) findViewById(R.id.tagcontainerLayout5);

        mTagContainerLayout1.setDefaultImageDrawableID(R.drawable.yellow_avatar);

        // Set custom click listener
        mTagContainerLayout1.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                Toast.makeText(MainActivity.this, "click-position:" + position + ", text:" + text,
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTagLongClick(final int position, String text) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Delete tag?")
                        .setMessage("You will delete this tag")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (position < mTagContainerLayout1.getChildCount()) {
                                    mTagContainerLayout1.removeTag(position);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog.show();
            }

            @Override
            public void onSelectedTagDrag(int position, String text) {}

            @Override
            public void onTagCrossClick(int position) {
//                mTagContainerLayout1.removeTag(position);
                //Toast.makeText(MainActivity.this, "Click TagView cross! position = " + position,
                        //Toast.LENGTH_SHORT).show();
            }
        });

        mTagContainerLayout3.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                List<Integer> selectedPositions = mTagContainerLayout3.getSelectedTagViewPositions();
                //deselect all tags when click on an unselected tag. Otherwise show toast.
                if (selectedPositions.isEmpty() || selectedPositions.contains(position)) {
                    Toast.makeText(MainActivity.this, "click-position:" + position + ", text:" + text,
                            Toast.LENGTH_SHORT).show();
                } else {
                    //deselect all tags
                    for (int i : selectedPositions) {
                        mTagContainerLayout3.deselectTagView(i);
                    }
                }

            }



            @Override
            public void onTagLongClick(final int position, String text) {
                mTagContainerLayout3.toggleSelectTagView(position);

                List<Integer> selectedPositions = mTagContainerLayout3.getSelectedTagViewPositions();
                Toast.makeText(MainActivity.this, "selected-positions:" + selectedPositions.toString(),
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSelectedTagDrag(int position, String text) {
                ClipData clip = ClipData.newPlainText("Text", text);
                View view = mTagContainerLayout3.getTagView(position);
                View.DragShadowBuilder shadow = new View.DragShadowBuilder(view);
                view.startDrag(clip, shadow, Boolean.TRUE, 0);
            }

            @Override
            public void onTagCrossClick(int position) {
            }
        });

        // Custom settings
//        mTagContainerLayout1.setTagMaxLength(4);

        // Set the custom theme
//        mTagContainerLayout1.setTheme(ColorFactory.PURE_CYAN);

        // If you want to use your colors for TagView, remember set the theme with ColorFactory.NONE
//        mTagContainerLayout1.setTheme(ColorFactory.NONE);
//        mTagContainerLayout1.setTagBackgroundColor(Color.TRANSPARENT);
//        mTagContainerLayout1.setTagTextDirection(View.TEXT_DIRECTION_RTL);

        // support typeface
//        Typeface typeface = Typeface.createFromAsset(getAssets(), "iran_sans.ttf");
//        mTagContainerLayout.setTagTypeface(typeface);

        // adjust distance baseline and descent
//        mTagContainerLayout.setTagBdDistance(4.6f);

        // After you set your own attributes for TagView, then set tag(s) or add tag(s)
        mTagContainerLayout1.setTags(list1);
        //oadImages(list1);
        mTagContainerLayout2.setTags(list2);
        mTagContainerLayout3.setTags(list3);
        mTagContainerLayout4.setTags(list4);

        List<int[]> colors = new ArrayList<int[]>();
        //int[]color = {backgroundColor, tagBorderColor, tagTextColor, tagSelectedBackgroundColor}
        int[] col1 = {Color.parseColor("#ff0000"), Color.parseColor("#000000"), Color.parseColor("#ffffff"), Color.parseColor("#999999")};
        int[] col2 = {Color.parseColor("#0000ff"), Color.parseColor("#000000"), Color.parseColor("#ffffff"), Color.parseColor("#999999")};

        colors.add(col1);
        colors.add(col2);

        mTagcontainerLayout5.setTags(list5, colors);
        final EditText text = (EditText) findViewById(R.id.text_tag);
        final Button btnAddTag = (Button) findViewById(R.id.btn_add_tag);
        btnAddTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerForContextMenu(btnAddTag);
                openContextMenu(btnAddTag);
                //mTagContainerLayout1.addTag(text.getText().toString());
                // Add tag in the specified position
//                mTagContainerLayout1.addTag(text.getText().toString(), 4);
            }
        });



        builder = new AlertDialog.Builder(this);
        builder.setTitle("Input your keyword:");
// Set up the input
        final EditText input = new EditText(this);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTagContainerLayout1.addTag("Text: "+input.getText().toString());
                input.setText("");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        builderLarger = new AlertDialog.Builder(this);
        builderLarger.setTitle("Input lower bound of Size (MB)");
// Set up the input
        final EditText input1 = new EditText(this);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input1.setInputType(InputType.TYPE_CLASS_NUMBER);
        builderLarger.setView(input1);

// Set up the buttons
        builderLarger.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTagContainerLayout1.addTag("Size: >"+input1.getText().toString()+"MB");
                input1.setText("");
            }
        });
        builderLarger.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        builderSmaller = new AlertDialog.Builder(this);
        builderSmaller.setTitle("Input upper bound of Size (MB)");
// Set up the input
        final EditText input2 = new EditText(this);

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input2.setInputType(InputType.TYPE_CLASS_NUMBER);
        builderSmaller.setView(input2);

// Set up the buttons
        builderSmaller.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTagContainerLayout1.addTag("Size: <"+input2.getText().toString()+"MB");
                input2.setText("");
            }
        });
        builderSmaller.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        if(getIntent().hasExtra("drive")) {
            text.setText(getIntent().getStringExtra("drive"));
            if (String.valueOf(text.getText()).equals("2another.one.bach@gmail.com")) {
                mTagContainerLayout1.addTag("Extension: jpg");
                mTagContainerLayout1.addTag("Type: archive");
            } else {
                mTagContainerLayout1.addTag("Type: video");
            }
        }




//        mTagContainerLayout1.setMaxLines(1);


        // test in RecyclerView
//        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
//        recyclerView.setVisibility(View.VISIBLE);
//        TagRecyclerViewAdapter adapter = new TagRecyclerViewAdapter(this, list3);
//        adapter.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "Click on TagContainerLayout", Toast.LENGTH_SHORT).show();
//            }
//        });
//        recyclerView.setAdapter(adapter);
    }

    private void loadImages(List<String> list) {
        String[] avatars = new String[]{"https://forums.oneplus.com/data/avatars/m/231/231279.jpg",
                "https://d1marr3m5x4iac.cloudfront.net/images/block/movies/17214/17214_aa.jpg",
                "https://lh3.googleusercontent.com/-KSI1bJ1aVS4/AAAAAAAAAAI/AAAAAAAAB9c/Vrgt6WyS5OU/il/photo.jpg"};

        for (int i=0; i<list.size(); i++) {
            final int index = i;
            Glide.with(mTagContainerLayout1.getContext())
                    .asBitmap()
                    .load(avatars[i % avatars.length])
                    .apply(new RequestOptions().override(85))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            mTagContainerLayout1.getTagView(index).setImage(resource);
                        }
                    });
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
            }
        }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.item4:
                builder.show();
                return true;
            case R.id.subitem1:
                builderLarger.show();
                return true;
            case R.id.subitem2:
                builderSmaller.show();
                return true;
            case R.id.png:
                mTagContainerLayout1.addTag("Extension: png");
                return true;
            case R.id.mp3:
                mTagContainerLayout1.addTag("Extension: mp3");
                return true;
            case R.id.document:
                mTagContainerLayout1.addTag("Type: document");
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public class TagRecyclerViewAdapter
            extends RecyclerView.Adapter<TagRecyclerViewAdapter.TagViewHolder> {

        private Context mContext;
        private String[] mData;
        private View.OnClickListener mOnClickListener;

        public TagRecyclerViewAdapter(Context context, String[] data) {
            this.mContext = context;
            this.mData = data;
        }

        @Override
        public int getItemCount() {
            return 10;
        }

        @Override
        public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new TagViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.view_recyclerview_item, parent, false), mOnClickListener);
        }

        @Override
        public void onBindViewHolder(TagViewHolder holder, int position) {
            holder.tagContainerLayout.setTags(mData);
            holder.button.setOnClickListener(mOnClickListener);
        }

        public void setOnClickListener(View.OnClickListener listener) {
            this.mOnClickListener = listener;
        }

        class TagViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TagContainerLayout tagContainerLayout;
            View.OnClickListener clickListener;
            Button button;

            public TagViewHolder(View v, View.OnClickListener listener) {
                super(v);
                this.clickListener = listener;
                tagContainerLayout = (TagContainerLayout) v.findViewById(R.id.tagcontainerLayout);
                button = (Button) v.findViewById(R.id.button);
//                v.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (clickListener != null) {
                    clickListener.onClick(v);
                }
            }
        }
    }
}
