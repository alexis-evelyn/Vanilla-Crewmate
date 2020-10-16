package me.alexisevelyn.crewmate.vanillaredirector.region;

import android.content.Context;
import android.os.Environment;
import android.os.NetworkOnMainThreadException;
import android.widget.Toast;
import me.alexisevelyn.crewmate.vanillaredirector.MainActivity;
import me.alexisevelyn.crewmate.vanillaredirector.R;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class RegionFileGenerator {
    public static void createRegionFile(String ipAddressRaw, int port, String displayName) {
        String masterServerName = "CrewMate-Master-1";

        // TODO: Don't Hardcode This
        File outFile = new File(Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.innersloth.spacemafia/files/regionInfo.dat");

        try {
            InetAddress ipAddress = InetAddress.getByName(ipAddressRaw);

            byte[] regionFileData = createRegionFileBytes(ipAddress, port, displayName, masterServerName);

            saveRegionFile(regionFileData, outFile);
        } catch (UnknownHostException e) {
            android.util.Log.e(MainActivity.LOG_TAG, "Unknown Host - Failed To Create Region File", e);

            Context context = MainActivity.mainActivity.getApplicationContext();
            CharSequence text = "Unknown Host - Failed To Create Region File: " + outFile.getAbsolutePath();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } catch (IOException e) {
            android.util.Log.e(MainActivity.LOG_TAG, "IOException - Failed To Create Region File", e);

            Context context = MainActivity.mainActivity.getApplicationContext();
            CharSequence text = "IOException - Failed To Create Region File: " + outFile.getAbsolutePath() + "\n" + e.getMessage();
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } catch (NetworkOnMainThreadException ignored) {
            // TODO: Deal With This!!!
            // https://stackoverflow.com/a/6343299/6828099

            // InetAddress.getByName(ipAddressRaw); performs a lookup if it assumes it's a non-ip host
            // That's why an invalid ip causes this exception

            Context context = MainActivity.mainActivity.getApplicationContext();
            CharSequence text = context.getText(R.string.invalid_ip_address);
            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    private static byte[] createRegionFileBytes(InetAddress ipAddress, int port, String displayName, String masterServerName) {
        // TODO: Add Support For Multiple Masters

        // 00000000: 0000 0000 0843 7265 776d 6174 6509 3132  .....Crewmate.12
        // 00000010: 372e 302e 302e 3101 0000 0011 4372 6577  7.0.0.1.....Crew
        // 00000020: 6d61 7465 2d4d 6173 7465 722d 317f 0000  mate-Master-1...
        // 00000030: 0107 5600 0000 00                        ..V....

        // Int-32 0
        byte[] header = {0x00, 0x00, 0x00, 0x00};

        // Display Name
        byte[] displayNameBytes = displayName.getBytes();
        byte[] displayNameLength = {(byte) displayNameBytes.length};

        // IP Address
        byte[] ipAddressBytes = ipAddress.getHostAddress().getBytes();
        byte[] ipAddressLength = {(byte) ipAddressBytes.length};

        // Number of Masters
        byte[] numberOfMasters = {(byte) 1};

        // Message Length and Flag (TODO: Verify)
        byte[] messageLength = {0x00, 0x00};
        byte[] messageFlag = {0x00};

        // Master Name
        byte[] masterNameBytes = masterServerName.getBytes();
        byte[] masterNameLength = {(byte) masterNameBytes.length};

        // IP Address Byte Form
        byte[] ipAddressByteForm = ipAddress.getAddress();

        // Port - Converts Integer port to bytes Int-16 LE
        byte[] portBytes = convertShortToLE((short) port);
        // System.out.println("Port Bytes: " + Arrays.toString(convertShortToLE((short) port)));

        // Footer
        byte[] footerBytes = {0x00, 0x00, 0x00, 0x00};

        return mergeBytes(header,
                displayNameLength,
                displayNameBytes,
                ipAddressLength,
                ipAddressBytes,
                numberOfMasters,
                messageLength,
                messageFlag,
                masterNameLength,
                masterNameBytes,
                ipAddressByteForm,
                portBytes,
                footerBytes);
    }

    private static void saveRegionFile(byte[] bytes, File outFile) throws IOException {
        if (outFile.exists() && !outFile.delete())
            throw new IOException("Failed to delete existing file!!!");

        if (!outFile.createNewFile())
            throw new IOException("Failed to create new file!!!");

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(outFile);
            fileOutputStream.write(bytes);

            fileOutputStream.flush();
            // fileOutputStream.close(); // Apparently this is redundant
        } catch (FileNotFoundException ignored) {
            // Ignored For Now!!!
        }

        Context context = MainActivity.mainActivity.getApplicationContext();
        CharSequence text = context.getText(R.string.created_file);
        int duration = Toast.LENGTH_LONG;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private static byte[] mergeBytes(byte[] ...bytes) {
        // Don't Bother Merging If Nothing To Merge
        if (bytes == null)
            return new byte[0];

        ArrayList<Byte> byteList = new ArrayList<>();

        for (byte[] byteArray : bytes) {
            // You'd think people would know better than to not pass in null values, but just in case, skip this byte[]
            if (byteArray == null)
                continue;

            for (byte bite : byteArray) {
                byteList.add(bite);
            }
        }

        Byte[] mergedBytes = byteList.toArray(new Byte[0]);
        byte[] mergedBytesPrimitive = new byte[mergedBytes.length];

        for (int i = 0; i < mergedBytesPrimitive.length; i++) {
            mergedBytesPrimitive[i] = mergedBytes[i]; // Apparently Unboxing Is Automatic
        }

        return mergedBytesPrimitive;
    }

    private static byte[] convertShortToLE(short value) {
        return new byte[] {(byte)(value & 0xff), (byte)((value >> 8) & 0xff)};
    }
}