package com.example.watchtest;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothManager {
    private static final String TAG = "BluetoothManager";
    private static final UUID MY_UUID = UUID.fromString("8CE255C0-200A-11E0-AC64-0800200C9A66");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothServerSocket serverSocket;
    private BluetoothSocket clientSocket;
    private ConnectedThread connectedThread;
    public Handler uiHandler;
    private Context context;

    public static final int MESSAGE_RECEIVED = 1;
    private static final int REQUEST_BLUETOOTH_SCAN_PERMISSION = 1;

    private BluetoothReceiver bluetoothReceiver;  //BluetoothReceiver 멤버 변수

    //블루투스 서버 기기 이름
    private static final String SERVER_DEVICE_NAME = "VPetWatch";

    public BluetoothManager(Context context, Handler handler) {
        this.context = context;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        uiHandler = handler;

        // 블루투스 스캔 권한 요청
        requestBluetoothScanPermission();

        //BluetoothReceiver 초기화
        bluetoothReceiver = new BluetoothReceiver(SERVER_DEVICE_NAME);
        // Bluetooth 기기 검색 결과를 수신하기 위한 BroadcastReceiver 등록
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(bluetoothReceiver, filter);
    }

    //권한 체크
    private void requestBluetoothScanPermission() {
        // Android 버전이 6.0 (마시멜로) 이상인 경우 권한을 동적으로 요청
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN)
                    != PackageManager.PERMISSION_GRANTED) {
                // 권한이 없는 경우 권한 요청
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.BLUETOOTH_SCAN},
                        REQUEST_BLUETOOTH_SCAN_PERMISSION);
            } else {
                // 이미 권한이 있는 경우 원하는 작업 수행
                // 여기서는 블루투스 스캔을 시작하거나 기타 작업을 수행할 수 있습니다.
            }
        } else {
            // Android 버전이 6.0 미만인 경우에는 권한이 필요하지 않으므로 원하는 작업 수행
            // 여기서는 블루투스 스캔을 시작하거나 기타 작업을 수행할 수 있습니다.
        }
    }

    // 블루투스 기기 검색 시작
    public boolean startDiscovery(String serverDeviceName) {
        boolean isStartDiscovery = false;
        if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
            // 서버 기기 이름을 찾아 연결 시도
            for (BluetoothDevice device : bluetoothAdapter.getBondedDevices()) {
                if (device.getName() != null && device.getName().equals(serverDeviceName)) {
                    connectToDevice(device);
                    break;
                }
            }
            // 페어링된 기기에 서버 기기가 없는 경우 스캔 시작
            isStartDiscovery = bluetoothAdapter.startDiscovery();
        }
        return isStartDiscovery;
    }

    public Handler getHandler() {
        return uiHandler;
    }

    // 블루투스 기기 검색 결과를 처리하는 BroadcastReceiver
    private class BluetoothReceiver extends BroadcastReceiver {
        private final String serverDeviceName;

        public BluetoothReceiver(String serverDeviceName) {
            this.serverDeviceName = serverDeviceName;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("bluetoothManager", "onReceive");
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // 여기에서 찾은 기기(device)에 대한 처리를 수행할 수 있습니다.
                if (device.getName() != null && device.getName().equals(serverDeviceName)) {
                    connectToDevice(device);
                }
            }
        }
    }

    public void enableBluetooth() {
        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth가 비활성화 상태인 경우 활성화 요청
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            // 위의 주석 처리된 부분은 액티비티에서 블루투스 활성화 결과를 받을 때 사용하는 코드입니다.
            // 여기서는 그냥 활성화 요청만 하는 부분이므로 해당 부분은 주석 처리합니다.
            context.startActivity(enableBtIntent);
        }
    }

    // 클라이언트 기기로 연결을 대기하는 메서드
    public void startServer(String serverDeviceName) {
        try {
            serverSocket = bluetoothAdapter.listenUsingRfcommWithServiceRecord(serverDeviceName, MY_UUID);
            new Thread(new AcceptThread()).start();
        } catch (IOException e) {
            Log.e(TAG, "Error creating server socket", e);
        }
    }

    public void connectToDevice(BluetoothDevice device) {
        Log.d("bluetoothManager", "connectToDevice : "+ device.getName());
        new ConnectThread(device).start();
    }

    public void write(String message) {
        if (connectedThread != null) {
            connectedThread.write(message);
        }
    }

    public void cancel() {
        if (connectedThread != null) {
            connectedThread.cancel();
        }
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing server socket", e);
            }
        }
        if (context != null && bluetoothReceiver != null) {
            context.unregisterReceiver(bluetoothReceiver);
        }
    }

    private class AcceptThread extends Thread {

        public void run() {
            Log.d("bluetoothManager", "AcceptThread");
            try {
                clientSocket = serverSocket.accept();
                connectedThread = new ConnectedThread(clientSocket);
                connectedThread.start();
            } catch (IOException e) {
                Log.e(TAG, "Error accepting connection", e);
            }
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothDevice device;
        private BluetoothSocket socket;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
        }

        public void run() {
            try {
                Log.d("bluetoothManager", "ConnectThread");
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
                connectedThread = new ConnectedThread(socket);
                connectedThread.start();
            } catch (IOException e) {
                Log.e(TAG, "Error connecting to device", e);
            }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket) {
            this.socket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;

            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error creating socket streams", e);
            }

            inputStream = tempIn;
            outputStream = tempOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = inputStream.read(buffer);
                    String receivedMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, "Received: " + receivedMessage);

                    // UI 스레드로 메시지 전달
                    Message message = uiHandler.obtainMessage(MESSAGE_RECEIVED, receivedMessage);
                    uiHandler.sendMessage(message);
                } catch (IOException e) {
                    Log.e(TAG, "Error reading from input stream", e);
                    break;
                }
            }
        }

        public void write(String message) {
            byte[] bytes = message.getBytes();
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Error writing to output stream", e);
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error closing socket", e);
            }
        }

    }
}
