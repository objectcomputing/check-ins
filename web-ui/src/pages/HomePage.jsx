import React, { useContext, useEffect, useState } from 'react';

import { getTodaysCelebrations } from '../api/birthdayanniversary';
import Anniversaries from '../components/celebrations/Anniversaries';
import Birthdays from '../components/celebrations/Birthdays';
import DoubleCelebration from '../components/celebrations/DoubleCelebration';
import KudosHomePage from './KudosHomePage';
import MyAnniversary from '../components/celebrations/MyAnniversary';
import MyBirthday from '../components/celebrations/MyBirthday';
import { AppContext } from '../context/AppContext';
import { selectCsrfToken, selectCurrentUser } from '../context/selectors';
import { sortAnniversaries, sortBirthdays } from '../context/util';

import './HomePage.css';

export default function HomePage() {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  let me = selectCurrentUser(state);
  let currentYear = new Date().getFullYear();

  const [anniversaries, setAnniversaries] = useState([]);
  const [birthdays, setBirthdays] = useState([]);
  const [myAnniversary, setMyAnniversary] = useState(false);
  const [myAnniversaryData, setMyAnniversaryData] = useState([]);
  const [myBirthday, setMyBirthday] = useState(false);
  const [showMyAnniversary, setShowMyAnniversary] = useState(false);
  const [showMyBirthday, setShowMyBirthday] = useState(false);

  let doubleCelebration;

  useEffect(() => {
    doubleCelebration =
      myBirthday && me && myAnniversary && showMyBirthday && showMyAnniversary;
  }, [
    myBirthday && me && myAnniversary && showMyBirthday && showMyAnniversary,
  ]);

  useEffect(() => {
    if (csrf) {
      const getCelebrations = async () => {
        let res = await getTodaysCelebrations(csrf);
        let data =
          res.payload && res.payload.data && !res.error
            ? res.payload.data
            : null;
        if (data) {
          if (data.anniversaries) {
            let filteredAnniversaries = data.anniversaries.filter(anniv => {
              let annivYear = new Date(anniv.anniversary).getFullYear();
              return annivYear !== currentYear;
            });
            setAnniversaries(sortAnniversaries(filteredAnniversaries));
          }
          if (data.birthdays) {
            setBirthdays(sortBirthdays(data.birthdays));
          }
        }
      };
      getCelebrations();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [csrf]);

  useEffect(() => {
    if (birthdays) {
      setMyBirthday(birthdays.some(bday => bday.userId === me.id));
    }
    if (anniversaries) {
      setMyAnniversary(anniversaries.some(anniv => anniv.userId === me.id));
      setMyAnniversaryData(
        anniversaries.filter(anniv => anniv.userId === me.id)
      );
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [anniversaries, birthdays, me.id]);

  useEffect(() => {
    myBirthday ? setShowMyBirthday(true) : setShowMyBirthday(false);
    myAnniversary ? setShowMyAnniversary(true) : setShowMyAnniversary(false);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [myAnniversary, myBirthday]);

  const hideMyAnniversary = () => {
    setShowMyAnniversary(false);
  };
  const hideMyBirthday = () => {
    setShowMyBirthday(false);
  };

  return (
    <div className="home-page">
      <div
        className={
          myBirthday &&
          me &&
          myAnniversary &&
          showMyBirthday &&
          showMyAnniversary
            ? 'double-celebrations'
            : 'celebrations'
        }
      >
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
          <h1>No Celebrations today &#129300;</h1>
        )}
      </div>
      <div className="kudos">
        <KudosHomePage />
      </div>
    </div>
  );
}
