package com.simplifiedschooling.app.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.simplifiedschooling.app.R;
import com.simplifiedschooling.app.helper.HomeworkData;
import com.simplifiedschooling.app.util.AppConfig;
import com.simplifiedschooling.app.util.AppController;
import com.simplifiedschooling.app.util.FileOpen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class CustomHomeworkAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<HomeworkData> homeworkItems;
   // Context context;
    String assment_path;
    private ProgressDialog pDialog;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomHomeworkAdapter(Activity activity,
                                 List<HomeworkData> homeworkItems) {
        this.activity = activity;
        this.homeworkItems = homeworkItems;
    }

    @Override
    public int getCount() {
        return homeworkItems.size();
    }

    @Override
    public Object getItem(int location) {
        return homeworkItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.homework_row, null);

        TextView studentName = (TextView) convertView.findViewById(R.id.textStudentName);
        TextView subject = (TextView) convertView.findViewById(R.id.textSubject);
        TextView givenon = (TextView) convertView.findViewById(R.id.textGivenon);
        TextView submissionDate = (TextView) convertView.findViewById(R.id.textSubmissionDate);
        TextView homeworkDescription = (TextView) convertView.findViewById(R.id.textHomeworkDescription);
        TextView attachment = (TextView) convertView.findViewById(R.id.textAttachment);
        // getting movie data for the row
        HomeworkData m = homeworkItems.get(position);

        // title
        studentName.setText(m.getStudentName());
        subject.setText(m.getSubject());
        givenon.setText(m.getGivenon());
        submissionDate.setText(m.getSubmissionDate());
        homeworkDescription.setText(m.getHomeworkDecription());
        if (m.getAttachementPath() != null) {
            attachment.setVisibility(View.VISIBLE);
            attachment.setOnClickListener(new attachmentListner(m.getAttachementPath()));
        } else {
            attachment.setVisibility(View.INVISIBLE);
        }


        return convertView;


    }


    private class attachmentListner implements View.OnClickListener {
        String path;

        public attachmentListner(String attPath) {
            // TODO Auto-generated constructor stub
            path = attPath;

        }
        @Override
        public void onClick(View v) {
            new DownloadTask(activity).execute(AppConfig.CLIENT_URL + "../" + "uploads/" + path);
            Log.e("AttachmentPath::", AppConfig.CLIENT_URL + "../" + "uploads/" + path);
        }
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;

            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);

                String path[] = sUrl[0].split("/");

                assment_path = path[path.length - 1];

                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP "
                            + connection.getResponseCode() + " "
                            + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream("/mnt/sdcard/" + assment_path);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            pDialog.setIndeterminate(false);
            pDialog.setMax(100);
            pDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {

            pDialog.dismiss();
            if (result != null)
                /*Toast.makeText(context,"Download error: ",
                Toast.LENGTH_LONG).show();*/
                Log.e("Error", AppConfig.CLIENT_URL + "../" + "uploads/" + assment_path + result);
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT)
                        .show();
            File myFile = new File("/mnt/sdcard/" + assment_path);
            try {
                FileOpen.openFile(context, myFile);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ActivityNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                Toast.makeText(
                        context,
                        "There is no app install to view" + " " + assment_path
                                + " file.", Toast.LENGTH_LONG).show();
            }
        }

    }

    }