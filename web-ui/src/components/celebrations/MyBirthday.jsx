import React, { useContext } from "react";
import { AppContext } from "../../context/AppContext";

import { selectCurrentUser } from "../../context/selectors";

import "./MyBirthday.css";
import "./Birthdays.css";

const MyBirthday = () => {
  const { state } = useContext(AppContext);

  let me = selectCurrentUser(state);

  return (
    <div class="my-birthday-container">
      <div class="my-balloons">
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
