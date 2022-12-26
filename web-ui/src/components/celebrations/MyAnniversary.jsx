import React, { useState } from "react";

import "./MyAnniversary.css";

const MyAnniversary = ({ myAnniversary }) => {
  const fullName = myAnniversary[0].name;
  const firstName = fullName.substring(0, fullName.indexOf(" "));
  const yearsOfService = myAnniversary[0].yearsOfService;
  const [open, setOpen] = useState(false);

  return (
    <div
      className={
        open ? "my-anniversary-container open" : "my-anniversary-container"
      }
    >
      <div
        className={open ? "gift dukdik" : "gift"}
        onClick={() => setOpen(!open)}
      >
        <div className={open ? "gift-top boxOpen" : "gift-top"}></div>
        <div className={open ? "gift-text open" : "gift-text"}>
          {open ? `Thank you for ${yearsOfService} years of service` : ""}
        </div>
        <div className={open ? "gift-box boxDown" : "gift-box"}></div>
      </div>
      <div className="my-anniversary">
        <h1>Happy Anniversary {firstName}!!!</h1>
      </div>
    </div>
  );
};

export default MyAnniversary;
