import React, { useContext, useEffect, useState } from "react";

import { getTodaysCelebrations } from "../api/birthdayanniversary";
import Anniversaries from "../components/celebrations/Anniversaries";
import { AppContext } from "../context/AppContext";
import { selectCsrfToken } from "../context/selectors";
import { sortAnniversaries } from "../context/util";

import "./HomePage.css";

export default function HomePage() {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [anniversaries, setAnniversaries] = useState([]);
  const [birthdays, setBirthdays] = useState([]);

  useEffect(async () => {
    if (csrf) {
      let res = await getTodaysCelebrations(csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
      console.warn({ data });
      if (data && data.anniversaries) {
        setAnniversaries(data.anniversaries);
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [csrf]);

  return (
    <div className="home-page">
      {/* {anniversaries.length ? ( */}
      {anniversaries ? (
        <div className="anniversaries">
          <Anniversaries anniversaries={anniversaries} />
        </div>
      ) : birthdays.length ? (
        <h2>Bdays</h2>
      ) : (
        <h1>No events currently available...</h1>
      )}
    </div>
  );
}
