package me.alexisevelyn.crewmate.vanillaredirector;

import android.Manifest;
import android.app.Instrumentation.ActivityResult;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import me.alexisevelyn.crewmate.vanillaredirector.region.RegionFileGenerator;

import java.util.logging.Logger;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.create_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // NavHostFragment.findNavController(FirstFragment.this).navigate(R.id.action_FirstFragment_to_SecondFragment);

                // TODO: Create File Here
                EditText displayNameEditText = MainActivity.mainActivity.findViewById(R.id.displayName);
                EditText ipAddressEditText = MainActivity.mainActivity.findViewById(R.id.ipAddress);
                EditText portEditText = MainActivity.mainActivity.findViewById(R.id.port);

                if (ipAddressEditText == null || portEditText == null || displayNameEditText == null) {
                    Log.e(MainActivity.LOG_TAG, "IP and/or Port and/or displayName EditText is null!!!!!!!!!!!!!!!!");
                    return;
                }

                Editable displayNameText = displayNameEditText.getText();
                Editable ipAddressText = ipAddressEditText.getText();
                Editable portText = portEditText.getText();

                if (ipAddressText == null || portText == null || displayNameText == null) {
                    Log.e(MainActivity.LOG_TAG, "IP and/or Port and/or displayName is null");
                    return;
                }

                String displayName = MainActivity.mainActivity.getApplicationContext().getText(R.string.defaultDisplayName).toString();
                String ipAddress = ipAddressText.toString();
                int port = 22023;

                if (!displayNameText.toString().isEmpty()) {
                    displayName = displayNameText.toString();
                }

                if (ipAddress.isEmpty()) {
                    Context context = MainActivity.mainActivity.getApplicationContext();
                    CharSequence text = context.getText(R.string.empty_ip_address);
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    return;
                }

                try {
                    port = Integer.parseInt(portText.toString());
                } catch (NumberFormatException ignored) {
                    // Ignored For Now!!!
                }

                Log.d(MainActivity.LOG_TAG, "IP Address: " + ipAddress + ":" + port);

                int canWriteToSD = ContextCompat.checkSelfPermission(MainActivity.mainActivity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

                if (canWriteToSD == PackageManager.PERMISSION_GRANTED)
                    RegionFileGenerator.createRegionFile(ipAddress, port, displayName);
                else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Request Permission Here
                    // https://developer.android.com/training/permissions/requesting
                    // https://developer.android.com/reference/androidx/core/app/ActivityCompat#requestPermissions(android.app.Activity,%20java.lang.String%5B%5D,%20int)
                    ActivityCompat.requestPermissions(MainActivity.mainActivity, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1337);

//                    Context context = MainActivity.mainActivity.getApplicationContext();
//                    CharSequence text = "Ask Permission";
//                    int duration = Toast.LENGTH_LONG;
//
//                    Toast toast = Toast.makeText(context, text, duration);
//                    toast.show();
                } else {
                    Context context = MainActivity.mainActivity.getApplicationContext();
                    CharSequence text = context.getText(R.string.need_sd_card_permission);
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }
        });
    }
}