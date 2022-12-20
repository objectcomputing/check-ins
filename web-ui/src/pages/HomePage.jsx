import React, { useContext, useEffect, useState } from "react";

import { getTodaysCelebrations } from "../api/birthdayanniversary";
import Anniversaries from "../components/celebrations/Anniversaries";
import Birthdays from "../components/celebrations/Birthdays";
import { AppContext } from "../context/AppContext";
import { selectCsrfToken } from "../context/selectors";
import { sortAnniversaries, sortBirthdays } from "../context/util";

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
      if (data) {
        if (data.anniversaries) {
          setAnniversaries(sortAnniversaries(data.anniversaries));
        }
        if (data.birthdays) {
          setBirthdays(sortBirthdays(data.birthdays));
        }
      }
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [csrf]);

  return (
    <div className="home-page">
      {anniversaries.length && birthdays.length ? (
        <div className="celebrations">
          <Anniversaries anniversaries={anniversaries} />
          <Birthdays birthdays={birthdays} />
        </div>
      ) : birthdays.length ? (
        <Birthdays birthdays={birthdays} />
      ) : anniversaries.length ? (
        <Anniversaries anniversaries={anniversaries} />
      ) : (
        <h1>No events currently available...</h1>
      )}
    </div>
  );
}
