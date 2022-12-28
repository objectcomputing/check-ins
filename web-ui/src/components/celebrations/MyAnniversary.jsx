import React, { useState } from "react";

import "./MyAnniversary.css";

const MyAnniversary = (props) => {
  const { hideMyAnniversary, myAnniversary } = props;
  const fullName = myAnniversary[0]?.name;
  const firstName = fullName?.substring(0, fullName.indexOf(" "));
  const yearsOfService = myAnniversary[0]?.yearsOfService;
  const [open, setOpen] = useState(false);

  const text = (
    <p>
      Thank you for <span>{yearsOfService.toFixed(0)}</span> years!!!
    </p>
  );

  return (
    <div
      className={
        open ? "my-anniversary-container open" : "my-anniversary-container"
      }
    >
      <div className="hide-my-anniversary" onClick={hideMyAnniversary}>
        X
      </div>
      <div
        className={open ? "my-anniversary-gift box" : "my-anniversary-gift"}
        onClick={() => setOpen(!open)}
      >
        <div
          className={
            open ? "my-anniversary-gift-top boxOpen" : "my-anniversary-gift-top"
          }
        ></div>
        <div
          className={
            open ? "my-anniversary-gift-text open" : "my-anniversary-gift-text"
          }
        >
          {open ? text : ""}
        </div>
        <div
          className={
            open ? "my-anniversary-gift-box boxDown" : "my-anniversary-gift-box"
          }
        ></div>
      </div>
      <div className="my-anniversary">
        <h1>Happy Anniversary {firstName}!!!</h1>
      </div>
    </div>
  );
};

export default MyAnniversary;
