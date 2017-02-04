package com.jins_meme.bridge;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

/**
 * Created by shun on 2017/02/04.
 */

public class MemeBTSPP {
  static final int MAX_UUID_NUM = 2;

  // for Android to PC/Mac
  private static final UUID[] APP_UUID = {UUID.fromString("2f96a155-0b98-4023-87e7-c6dd3b7a4e1d")};

  private BluetoothAdapter btAdapter;
  private BluetoothSocket btSocket;
  private InputStream btIn = null;
  private PushbackInputStream btPbIn = null;
  private OutputStream btOut = null;
  private BufferedOutputStream btDataOut = null;

  private String[] pairedMachineNames;
  private int uuidIndex = 0;
  private boolean isConnectedMachine = false;
  private String connectedDeviceName;

  MemeBTSPP() {
    btAdapter = BluetoothAdapter.getDefaultAdapter();

    if(btAdapter == null) {
      Log.d("DEBUG", "This device is not implement Bluetooth.");
      return;
    }

    if(!btAdapter.isEnabled()) {
      Log.d("DEBUG", "This device is disabled Bluetooth.");
      return;
    }

    Set<BluetoothDevice> paired_devices = btAdapter.getBondedDevices();
    final BluetoothDevice[] devices = paired_devices.toArray(new BluetoothDevice[0]);
    String[] items = new String[devices.length];

    for(int i = 0; i < devices.length; i++) {
      items[i] = devices[i].getName();
    }
    pairedMachineNames = items.clone();

    Log.d("DEBUG", "LIST: " + Arrays.toString(pairedMachineNames));
  }

  public String[] getPairedDeviceName() {
    return pairedMachineNames;
  }

  public String getConnectedDeviceName() {
    return connectedDeviceName;
  }

  public void connect(String connectDeviceName) {
    if(isConnectedMachine)
      return;

    Set<BluetoothDevice> paired_devices = btAdapter.getBondedDevices();
    final BluetoothDevice[] devices = paired_devices.toArray(new BluetoothDevice[0]);
    String[] items = new String[devices.length];

    for(int i = 0; i < devices.length; i++) {
      items[i] = devices[i].getName();

      if(connectDeviceName.equals(items[i])) {
        try {
          Log.d("DEBUG", "hoge0... " + uuidIndex + " " + APP_UUID[uuidIndex < (MAX_UUID_NUM + 1) ? uuidIndex : 0]);

          btSocket = devices[i].createRfcommSocketToServiceRecord(APP_UUID[uuidIndex]);

          try {
            //debug Log.d("BT CONNECTING...")
            //setDebugTextView("BT CONNECTING...");

            btSocket.connect();

            Log.d("DEBUG", "hoge1...");

            btIn = btSocket.getInputStream();
            btPbIn = new PushbackInputStream(btIn);

            btOut = btSocket.getOutputStream();
            btDataOut = new BufferedOutputStream(btOut);

            connectedDeviceName = connectDeviceName;

            isConnectedMachine = true;
          }
          catch(Throwable t) {
            //Log.d("DEBUG", "hoge2...");

            btSocket.close();
            btSocket = null;

            isConnectedMachine = false;
          }
        }
        catch(IOException e) {
          Log.d("DEBUG", e.toString());
        }
      }
    }
    Log.d("DEBUG", Arrays.toString(items));
  }

  public void disconnect() {
    try {
      if(btDataOut != null) {
        //debug Log.d("DEBUG", "bluetooth write...");

        //bt_data_out.writeInt(0xFE000000 | (device_id << 16));
        //bt_data_out.flush();

        btDataOut.flush();
      }
    }
    catch(IOException ioe) {
      ioe.printStackTrace();
    }

    try {
      if(btIn != null)
        btIn.close();

      if(btOut != null)
        btOut.close();

      if(btSocket != null)
        btSocket.close();

      isConnectedMachine = false;
    }
    catch(IOException ioe) {
      btIn = null;
      btOut = null;
      btSocket = null;

      ioe.printStackTrace();
    }
  }
}
