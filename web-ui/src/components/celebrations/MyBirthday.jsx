import React, { useContext } from "react";

import { AppContext } from "../../context/AppContext";
import { selectCurrentUser } from "../../context/selectors";
import { randomConfetti } from "../../context/util";

import "./MyBirthday.css";
import "./Birthdays.css";

const MyBirthday = () => {
  const { state } = useContext(AppContext);

  let me = selectCurrentUser(state);

  return (
    <div className="my-birthday-container">
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
