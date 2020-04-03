import React, {useEffect, useRef, useState} from 'react';
import {View, Text} from 'react-native';
import ToastExample from './ToastExample';
import {NativeEventEmitter, NativeModules} from 'react-native';
import {BleManager} from 'react-native-ble-plx';
const onSessionConnect = event => {
  console.log('onSessionConnect', event);
};
const sensors = {
  0: 'Temperature',
  1: 'Accelerometer',
  2: 'Humidity',
  3: 'Magnetometer',
  4: 'Barometer',
  5: 'Gyroscope',
};
const prefixUUID = 'f000aa';
const suffixUUID = '-0451-4000-b000-000000000000';
export default props => {
  let manager = useRef();
  const [info, setInfo] = useState('');
  const [values, setValues] = useState({});
  useEffect(() => {
    manager = new BleManager();
    manager.onStateChange(state => {
      console.log('mystate :', state);
      if (state === 'PoweredOn') {
        //scanAndConnect();
      }
    });
    ToastExample.showBlueTooths();
    // ToastExample.show('Awesome', ToastExample.SHORT);
    const eventEmitter = new NativeEventEmitter(NativeModules.ToastExample);
    eventEmitter.addListener('onSessionConnect', onSessionConnect);
  }, []);
  const setupNotifications = async device => {
    for (const id in sensors) {
      const service = serviceUUID(id);
      const characteristicW = writeUUID(id);
      const characteristicN = notifyUUID(id);

      const characteristic = await device.writeCharacteristicWithResponseForService(
        service,
        characteristicW,
        'AQ==' /* 0x01 in hex */,
      );

      device.monitorCharacteristicForService(
        service,
        characteristicN,
        (error, characteristic) => {
          if (error) {
            errorInfo(error.message);
            return;
          }
          updateValue(characteristic.uuid, characteristic.value);
        },
      );
    }
  };
  const scanAndConnect = () => {
    manager.startDeviceScan(null, null, (err, device) => {
      setInfo('Scanning...');
      console.log(device);
      if (device.name) {
        console.log(
          'device : { id: ',
          device.id,
          ', name: ',
          device.name,
          ', rssi: ',
          device.rssi,
          ', mtu: ',
          device.mtu,
          ', overflowServiceUUIDs: ',
          device.overflowServiceUUIDs,
          ', overflowServiceUUIDs: ',
          device.solicitedServiceUUIDs,
          ' }',
        );
      }

      if (err) {
        errorInfo(err.message);
        return;
      }

      if (
        device.name === 'TI BLE Sensor Tag' ||
        device.name === 'SensorTag' ||
        device.name === "Kan's iPHone" ||
        device.name === 'KopQQ'
      ) {
        setInfo('Connecting to TI Sensor');
        manager.stopDeviceScan();
        device
          .connect()
          .then(devices => {
            setInfo('Discovering services and characteristics');
            return devices.discoverAllServicesAndCharacteristics();
          })
          .then(devices => {
            setInfo('Setting notifications');
            return setupNotifications(device);
          })
          .then(
            () => {
              setInfo('Listening...');
            },
            errconect => {
              errorInfo(errconect.message);
            },
          );
      }
    });
  };

  const serviceUUID = num => {
    return prefixUUID + num + '0' + suffixUUID;
  };

  const notifyUUID = num => {
    return prefixUUID + num + '1' + suffixUUID;
  };

  const writeUUID = num => {
    return prefixUUID + num + '2' + suffixUUID;
  };

  const errorInfo = message => {
    setInfo('Error : ' + message);
  };

  const updateValue = (key, value) => {
    setValues({...values, [key]: value});
  };
  return (
    <View>
      <Text>{info}</Text>
    </View>
  );
};
