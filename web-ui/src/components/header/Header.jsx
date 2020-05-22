import React from "react";
import "./Header.css";

const Header = ({ title }) => {
  return (
    <div class="header">
      <div className="checkin">Check-in!</div>
      <h1>{title}</h1>
    </div>
  );
};

export default Header;
