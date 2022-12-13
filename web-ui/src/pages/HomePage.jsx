import React, { useContext, useState } from "react";

import "./HomePage.css";
import Anniversaries from "../components/celebrations/Anniversaries";

export default function HomePage() {
  const [anniversaries, setAnniversaries] = useState([]);
  const [birthdays, setBirthdays] = useState([]);

  return (
    <div className="home-page">
      {/* {anniversaries.length ? ( */}
      {anniversaries ? (
        <Anniversaries />
      ) : birthdays.length ? (
        <h2>Bdays</h2>
      ) : (
        <h1>No events currently available...</h1>
      )}
    </div>
  );
}
