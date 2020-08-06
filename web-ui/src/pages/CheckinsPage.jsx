import React from "react";
import CheckinsHistory from "../components/checkin/CheckinHistory";
import Personnel from "../components/personnel/Personnel";

import "./CheckinsPage.css"

const CheckinsPage = () => {
  return (
    <div className="container">
      <div className="contents">
        < CheckinsHistory />
      </div>
      <div className="right-sidebar">
        < Personnel />
      </div>
    </div>
  );
};

export default CheckinsPage;
