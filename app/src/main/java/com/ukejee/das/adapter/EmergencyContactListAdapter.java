package com.ukejee.das.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ukejee.das.R;
import com.ukejee.das.models.EmergencyContact;
import com.ukejee.das.web.AppRepository;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * @author .: Ukeje Emeka
 * @email ..: ukejee3@gmail.com
 * @created : 6/9/20
 */
public class EmergencyContactListAdapter extends BaseAdapter<EmergencyContactListAdapter.MyViewHolder> {

    private List<EmergencyContact> contacts;
    private Context context;
    private AppRepository repository;
    private String token;

    public EmergencyContactListAdapter(List<EmergencyContact> contacts, Context context,
                                       AppRepository repository, String token){
        super(context);
        this.contacts = contacts;
        this.context = context;
        this.repository = repository;
        this.token = token;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_emergency_contact_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        final EmergencyContact contact = contacts.get(position);
        String nameInitial = contact.getName().substring(0,1);
        holder.name.setText(contact.getName());
        holder.phoneNumber.setText(contact.getPhoneNumber());
        holder.nameInitials.setText(nameInitial);
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(context, holder.menu);
                popupMenu.inflate(R.menu.emergency_contact_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.delete_item) {
                            showAlertDialog(contact.getId());
                            return true;
                        }
                        else {
                            return true;
                        }
                    }
                });
                popupMenu.show();
            }
        });

    }

    private void showAlertDialog(final int id){
        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Are you sure?");
            builder.setMessage("You are about to delete an emergency contact");
            builder.setNegativeButton("NO", null);
            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    deleteEmergencyContact(id);
                }
            });
            builder.show();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void deleteEmergencyContact(int id){
        showProgressDialog();
        repository.getDatabaseApiService().deleteEmergencyContact(token, id)
                .enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        cancelProgressDialog();
                        if(response.isSuccessful()){
                            showMessage("Success",
                                    "Emergency Contact has been deleted.");
                        }
                        else{
                            showMessage("Error", "Something went wrong");
                        }
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        showMessage("Error", "Check Internet Connection");
                    }
                });
    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView nameInitials;
        private TextView name;
        private TextView phoneNumber;
        private ImageView menu;

        public View getView() {
            return view;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;

            nameInitials = view.findViewById(R.id.first_name_letter);
            name = view.findViewById(R.id.emergency_contact_name);
            phoneNumber = view.findViewById(R.id.emergency_contact_phone_number);
            menu = view.findViewById(R.id.menu);
        }
    }
}
