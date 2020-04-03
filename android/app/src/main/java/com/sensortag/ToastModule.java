package com.sensortag;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.ParcelUuid;
import android.se.omapi.Session;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.annotation.Nonnull;

public class ToastModule extends ReactContextBaseJavaModule {
    private static ReactApplicationContext reactContext;

    private static final String DURATION_SHORT_KEY = "SHORT";
    private static final String DURATION_LONG_KEY = "LONG";
    private ArrayList<DeviceItem> deviceItemList;

    ToastModule(ReactApplicationContext context) {
        super(context);
        reactContext = context;
    }

    /**
     * @return the name of this module. This will be the name used to {@code require()} this module
     * from javascript.
     */
    @Nonnull
    @Override
    public String getName() {
        return "ToastExample";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
        constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
        return constants;
    }

    private void onConnected(Session session) {

        WritableMap payload = Arguments.createMap();
        payload.putString("sessionId", "DummyId");
        this.reactContext
                .getJSModule(
                        DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit("onSessionConnect", payload);
    }

    @ReactMethod
    public void showBlueTooths() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Toast.makeText(getReactApplicationContext(), "Device Name", Toast.LENGTH_LONG).show();
        if (bluetoothAdapter == null) {
            Toast.makeText(getReactApplicationContext(), "Device Adapter None", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getReactApplicationContext(), "Device Adapter not null", Toast.LENGTH_LONG).show();
            // Do whatever you want to do with your bluetoothAdapter
            Method getNameMethod = BluetoothAdapter.class.getDeclaredMethod("getName", null);
            String names = (String) getNameMethod.invoke(bluetoothAdapter, null);
            Toast.makeText(getReactApplicationContext(), "Device Name :" + names, Toast.LENGTH_LONG).show();
            Method getAddressMethod = BluetoothAdapter.class.getDeclaredMethod("getAddress", null);
            String address = (String) getAddressMethod.invoke(bluetoothAdapter, null);
            Toast.makeText(getReactApplicationContext(), "Device Address :" + address, Toast.LENGTH_LONG).show();
            deviceItemList = new ArrayList<DeviceItem>();

            Set<BluetoothDevice> pairedDevices = bTAdapter.getBondedDevices();
        }
    }

    @ReactMethod
    public void show(String message, int duration) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        Method getUuidsMethod = BluetoothAdapter.class.getDeclaredMethod("getUuids", null);

        ParcelUuid[] uuids = (ParcelUuid[]) getUuidsMethod.invoke(adapter, null);

        for (ParcelUuid uuid : uuids) {
            Toast.makeText(getReactApplicationContext(), uuid.getUuid().toString(), duration).show();
        }
        Toast.makeText(getReactApplicationContext(), message, duration).show();
    }
}