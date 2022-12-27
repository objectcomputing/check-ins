import React, { useContext, useEffect, useState } from "react";

import { getTodaysCelebrations } from "../api/birthdayanniversary";
import Anniversaries from "../components/celebrations/Anniversaries";
import Birthdays from "../components/celebrations/Birthdays";
import DoubleCelebration from "../components/celebrations/DoubleCelebration";
import MyAnniversary from "../components/celebrations/MyAnniversary";
import MyBirthday from "../components/celebrations/MyBirthday";
import { AppContext } from "../context/AppContext";
import { selectCsrfToken, selectCurrentUser } from "../context/selectors";
import { sortAnniversaries, sortBirthdays } from "../context/util";

import "./HomePage.css";

export default function HomePage() {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  let me = selectCurrentUser(state);

  const [anniversaries, setAnniversaries] = useState([]);
  const [birthdays, setBirthdays] = useState([]);
  const [myAnniversary, setMyAnniversary] = useState(false);
  const [myAnniversaryData, setMyAnniversaryData] = useState([]);
  const [myBirthday, setMyBirthday] = useState(false);
  const [showMyAnniversary, setShowMyAnniversary] = useState(false);
  const [showMyBirthday, setShowMyBirthday] = useState(false);

  useEffect(async () => {
    if (csrf) {
      let res = await getTodaysCelebrations(csrf);
      let data =
        res.payload && res.payload.data && !res.error ? res.payload.data : null;
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

  useEffect(async () => {
    if (birthdays) {
      setMyBirthday(birthdays.some((bday) => bday.userId === me.id));
    }
    if (anniversaries) {
      setMyAnniversary(anniversaries.some((anniv) => anniv.userId === me.id));
      setMyAnniversaryData(
        anniversaries.filter((anniv) => anniv.userId === me.id)
      );
    }
  }, [anniversaries, birthdays]);

  useEffect(async () => {
    myBirthday ? setShowMyBirthday(true) : setShowMyBirthday(false);
    myAnniversary ? setShowMyAnniversary(true) : setShowMyAnniversary(false);
  }, [myAnniversary, myBirthday]);

  const hideMyAnniversary = () => {
    setShowMyAnniversary(false);
  };
  const hideMyBirthday = () => {
    setShowMyBirthday(false);
  };

  return (
    <div className="home-page">
      <div className="celebrations">
        {myBirthday &&
        me &&
        myAnniversary &&
        showMyBirthday &&
        showMyAnniversary ? (
          <DoubleCelebration
            me={me}
            hideMyBirthday={hideMyBirthday}
            hideMyAnniversary={hideMyAnniversary}
            myAnniversary={myAnniversaryData}
          />
        ) : myBirthday && me && showMyBirthday ? (
          <MyBirthday me={me} hideMyBirthday={hideMyBirthday} />
        ) : myAnniversary && me && showMyAnniversary ? (
          <MyAnniversary
            hideMyAnniversary={hideMyAnniversary}
            myAnniversary={myAnniversaryData}
          />
        ) : anniversaries.length && birthdays.length ? (
          <>
            <Anniversaries anniversaries={anniversaries} />
            <Birthdays birthdays={birthdays} />
          </>
        ) : birthdays.length ? (
          <Birthdays birthdays={birthdays} xPos={0.5} />
        ) : anniversaries.length ? (
          <Anniversaries anniversaries={anniversaries} />
        ) : (
          <h1>No events currently available...</h1>
        )}
      </div>
    </div>
  );
}
