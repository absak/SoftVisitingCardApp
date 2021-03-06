package com.example.android.softvisitingcardapp.people;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.softvisitingcardapp.R;
import com.example.android.softvisitingcardapp.api.APIService;
import com.example.android.softvisitingcardapp.api.APIUrl;
import com.example.android.softvisitingcardapp.models.MessageResponse;
import com.example.android.softvisitingcardapp.models.User;


import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.android.softvisitingcardapp.R.id.editTextMessage;
import static com.example.android.softvisitingcardapp.R.id.editTextTitle;

/**
 * Created by Belal on 14/04/17.
 */

public class OtherUserAdapter extends RecyclerView.Adapter<OtherUserAdapter.ViewHolder> {

    private List<User> users;
    private Context mCtx;

    public OtherUserAdapter(List<User> users, Context mCtx) {
        this.users = users;
        this.mCtx = mCtx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_other_users, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(OtherUserAdapter.ViewHolder holder, int position) {
        final User user = users.get(position);
        holder.textViewName.setText(user.getName());
        holder.textViewUserName.setText(user.getEmail());

        holder.imageButtonMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater li = LayoutInflater.from(mCtx);
                View promptsView = li.inflate(R.layout.dialog_send_card, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mCtx);
                alertDialogBuilder.setView(promptsView);

                //final EditText editTextTitle = (EditText) promptsView.findViewById(R.id.editTextTitle);
                //final EditText editTextMessage = (EditText) promptsView.findViewById(R.id.editTextMessage);

                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("Send",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {

                                        //getting the values
                                        //String title = editTextTitle.getText().toString().trim();
                                        //String message = editTextMessage.getText().toString().trim();

                                        //sending the message
                                        sendCard(user.getEmail());
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

            }
        });
    }

    //method to send message to the user
    private void sendCard(String receiverEmail) {

        final ProgressDialog progressDialog = new ProgressDialog(mCtx);
        progressDialog.setMessage("Sending Message...");
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);


        Call<MessageResponse> call = service.sendCard(
                OtherUsersFragment.userEmail,
                receiverEmail,
                OtherUsersFragment.cardId
        );

        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                progressDialog.dismiss();
                Toast.makeText(mCtx, response.body().getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(mCtx, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName, textViewUserName;
        public ImageButton imageButtonMessage;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            textViewUserName = (TextView) itemView.findViewById(R.id.textViewUserName);
            imageButtonMessage = (ImageButton) itemView.findViewById(R.id.imageButtonMessage);
        }
    }
}