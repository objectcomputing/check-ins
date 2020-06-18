import React from "react";
import CheckinCycle from "../components/checkin/CheckinCycle";
import "./HomePage.css";

const HomePage = () => (
  <div className="home">
    <header>
      <h3>Professional Development @ OCI</h3>
      <div>Stuff 1 Stuff 2 Stuff 3</div>
    </header>
    <CheckinCycle style={{ margin: "0px 20px" }} />
  </div>
);

export default HomePage;
