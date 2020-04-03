// @flow

import React from 'react';
import {Provider} from 'react-redux';
import {store} from './Store';
// import SensorTag from './SensorTag';
import DemoBe from './DemoBe';

export default function App() {
  return (
    <Provider store={store}>
      <DemoBe />
    </Provider>
  );
}
