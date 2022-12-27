import React, { useRef } from "react";

import { randomConfetti } from "../../context/util";

import "./MyBirthday.css";
import "./Birthdays.css";

const MyBirthday = (props) => {
  const { me, hideMyBirthday } = props;

  return (
    <div className="my-birthday-container">
      <div className="hide-my-birthday" onClick={hideMyBirthday}>
        X
      </div>
      <div className="my-balloons" onClick={() => randomConfetti(0.6, 0.5)}>
        <div>
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
        <div>
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
        <div>
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
        <div>
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
        <div>
          <p>Happy</p>
          <p>Birthday!</p>
        </div>
      </div>
      <div className="my-birthday">
        <h1>Happy Birthday {me.firstName}!!!</h1>
      </div>
    </div>
  );
};

export default MyBirthday;
