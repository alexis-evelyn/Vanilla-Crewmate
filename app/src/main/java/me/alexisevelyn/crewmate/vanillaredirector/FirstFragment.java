package me.alexisevelyn.crewmate.vanillaredirector;

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
                EditText ipAddressEditText = MainActivity.mainActivity.findViewById(R.id.ip_address);
                EditText portEditText = MainActivity.mainActivity.findViewById(R.id.port);
                // EditText displayName = view.findViewById(R.id.display_name);

                if (ipAddressEditText == null || portEditText == null) {
                    Log.e(MainActivity.LOG_TAG, "IP and/or Port EditText is null!!!!!!!!!!!!!!!!");
                    return;
                }

                Editable ipAddressText = ipAddressEditText.getText();
                Editable portText = portEditText.getText();

                if (ipAddressText == null || portText == null) {
                    Log.e(MainActivity.LOG_TAG, "IP and/or Port is null");
                    return;
                }

                String displayName = "Custom";
                String ipAddress = ipAddressText.toString();
                int port = 22023;

                try {
                    port = Integer.parseInt(portText.toString());
                } catch (NumberFormatException ignored) {
                    // Ignored For Now!!!
                }

                if (ipAddress.isEmpty()) {
                    Context context = MainActivity.mainActivity.getApplicationContext();
                    CharSequence text = context.getText(R.string.empty_ip_address);
                    int duration = Toast.LENGTH_LONG;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    return;
                }

                Log.d(MainActivity.LOG_TAG, "IP Address: " + ipAddress + ":" + port);

                int canWriteToSD = ContextCompat.checkSelfPermission(MainActivity.mainActivity.getApplicationContext(), "android.permission.WRITE_EXTERNAL_STORAGE");

                if (canWriteToSD == PackageManager.PERMISSION_GRANTED)
                    RegionFileGenerator.createRegionFile(ipAddress, port, displayName);
                else if (shouldShowRequestPermissionRationale("android.permission.WRITE_EXTERNAL_STORAGE")) {
                    // TODO: Request Permission Here
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