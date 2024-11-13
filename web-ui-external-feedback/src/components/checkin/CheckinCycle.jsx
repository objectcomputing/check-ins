import React from 'react';
import cycle from './checkin-cycle.png';

const CheckinCycle = ({ style = { height: '35vw', width: '35vw' } }) => (
  <div>
    <img
      style={style}
      alt="The Check-in Cycle including Expectations, Feedback, and Development"
      src={cycle}
    />
  </div>
);

export default CheckinCycle;
