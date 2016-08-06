package com.purvik.devdroid.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.purvik.devdroid.R;
import com.purvik.devdroid.singleObjects.VideoAdapter;
import com.purvik.devdroid.singleObjects.VideoObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Purvik Rana on 04-08-2016.
 */
public class CardViewFragment extends Fragment {


    // CONNECTION_TIMEOUT and READ_TIMEOUT are in milliseconds
    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    private RecyclerView mVideoView;
    private VideoAdapter vAdapter;

    List<VideoObject> videoObjectList;

    View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.card_view_fragment,container, false);

        mVideoView = (RecyclerView) view.findViewById(R.id.recycler_view);
//        vAdapter = new VideoAdapter(getActivity(), videoObjectList );

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        mVideoView.setLayoutManager(mLayoutManager);
        mVideoView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(2),true));
        mVideoView.setItemAnimator(new DefaultItemAnimator());


        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AsyncFetch().execute();
            }
        });


        return view;
    }


    private class AsyncFetch extends AsyncTask<String, String, String> {


        //VIDEO  LIST NODE PARAMETERS
        private static final String TAG_ID = "id"; 			//VIDEO ID
        private static final String TAG_NAME = "name";		//VIDEO TITLE
        private static final String TAG_Y_LINK = "y_link";	//YOUTUBE VIDEO CODE
        private static final String TAG_DESC = "desc";			//Image URL
        private static final String TAG = "AsyncTask";//VIDEO DESCRIPTION
        private static final String TAG_IMG_URL = "imgUrl";

        //PROGRESS DIALOG DISPLAYED WHILE LOADING THE LIST
        private ProgressDialog pDialog;

        //LINK FOR JSON ARRAY WHERE PLAYLIST STORED
        private final String url = "http://purvik.com/DevDroid/videos.API";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            // DISPLAY PROGRESS DIALOG
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("loading");
            pDialog.setCancelable(false);
            pDialog.show();

            Log.w(TAG, "onPreExecute:Call\t " + url);
        }

        @Override
        protected String doInBackground(String... params) {

            // Prepare the Looper for Execution
//            Looper.prepare();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    if (Looper.myLooper() == null ) {

                        Looper.prepare();
                    }
                }
            });


            // RESULT - HOLDS FETCHED URL
            StringBuilder result = new StringBuilder();

            // OBJECT DECLARATION FOR VIDEO LIST
            videoObjectList= new ArrayList<VideoObject>();
            
            Log.w(TAG, ""+ url);

            URL newUrl;

            try {

                // CONVERT TO URL OBJECT
                newUrl = new URL(url);

                //Toast.makeText(getActivity(), "HTTP Call", Toast.LENGTH_SHORT).show();

                //INSTANCE OF HttpUrlConnectoin & OPEN CONNECTION
                HttpURLConnection conn = (HttpURLConnection) newUrl.openConnection();

                // GET INPUTSTREAM FROM CONNECTION AND BUILD INSTANCE
                InputStream in = new BufferedInputStream(conn.getInputStream());

                // BIND READER TO INPUTSTREAM
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line; // TEMPORARY STRING TO READ DATA

                // READ CONTENT
                while ((line = reader.readLine()) != null) {

                    Log.w(TAG, "doInBackground: "+ line);
                    result.append(line);	// APPEND EACH READLINE DATA
                }

                Log.d(TAG, "doInBackground: >> "+ result.toString());

            } catch (MalformedURLException e2) {
                Log.d(TAG, "doInBackground: >> URL MalFormed" );
                Toast.makeText(getActivity(),"Can't fetch list wright now. Try Again :)", Toast.LENGTH_LONG).show();
                e2.printStackTrace();
            } catch (IOException e) {
                Log.d(TAG, "doInBackground: >> IO Exception");
                Toast.makeText(getActivity(),"Can't fetch list wright now. Try Again :)", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }

            // CONVERT TO STRING
            String jsonStr = result.toString();
            Log.w(TAG, ""+ jsonStr);

            // JSON ARRAY TO STORE VIDEO LIST
            JSONArray videos = null;

            if (jsonStr != null) {		// IF GET THE URL DATA
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // GET JSON ARRAY NODE
                    videos = jsonObj.getJSONArray("android");
                    Log.w(TAG, ""+ videos.toString());

                    if(videos != null) {

                        // LOOP THROUGH ALL VIDEOS
                        for (int i = 0; i < videos.length(); i++) {

                            // TAKE INDIVIDUAL JSON OBJECTS
                            JSONObject jObj = videos.getJSONObject(i);

                            VideoObject vObject = new VideoObject();
                            //Log.w(TAG, "doInBackground: "+jObj.toString());

                            vObject.id = jObj.getString(TAG_ID);            // GET ID
                            vObject.title = jObj.getString(TAG_NAME);        // GET NAME
                            vObject.videoTag= jObj.getString(TAG_Y_LINK);    // GET YouTube LINK
                            vObject.desc = jObj.getString(TAG_DESC);        // GET VIDEO DECRIPTION

                          /*  Log.d("ID: ", "-- " + id);
                            Log.d("Name: ", "-- " + name);
                            Log.d("Y_link: ", "-- " + y_link);
                            Log.d("Description: ", "-- " + desc);
                            */

                            Log.d(TAG, i + ":" + vObject.toString());

                            videoObjectList.add(vObject);
                        }
                    }else {

                        //  DISPLAY ALERT MESSAGE
                        AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                        build.setTitle("DevDroid")
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage("Video List Can't be Loaded..")
                                .create();
                        build.show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {

                //  DISPLAY ALERT MESSAGE
                AlertDialog.Builder build = new AlertDialog.Builder(getActivity());
                build.setTitle("DevDroid")
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage("Couldn't be able to connect wright now..")
                        .create();
                build.show();

                // IF CAN'T GET THE DATA FROM THE URL
//                Toast.makeText(getContext(), "Couldn't get any data from the url", Toast.LENGTH_LONG).show();
            }

            if (videoObjectList.size() != 0) {
                return "success";
            }else {
                return null;

            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            Log.d("List", ""+ videoObjectList.size());
            Log.d("List", "" + videoObjectList.toString());



            // DISMISS THE PROGRESS DIALOG
            if (pDialog.isShowing())
                pDialog.dismiss();

            if (s.equalsIgnoreCase("success")) {
                    vAdapter = new VideoAdapter(getActivity(), videoObjectList );
                    vAdapter.updateVideoList(videoObjectList);
                    mVideoView.setAdapter(vAdapter);

            }else{


            }

        }
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
