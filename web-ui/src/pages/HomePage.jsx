import React, { useContext, useEffect, useState } from 'react';

import { getTodaysCelebrations } from '../api/birthdayanniversary';
import Anniversaries from '../components/celebrations/Anniversaries';
import Birthdays from '../components/celebrations/Birthdays';
import PublicKudos from '../components/kudos/PublicKudos';
import MyAnniversary from '../components/celebrations/MyAnniversary';
import MyBirthday from '../components/celebrations/MyBirthday';
import { AppContext } from '../context/AppContext';
import { selectCsrfToken, selectCurrentUser } from '../context/selectors';
import { sortAnniversaries, sortBirthdays } from '../context/util';
import { Button } from '@mui/material';

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

  const hideMyAnniversary = () => {
    setMyAnniversary(false);
  };
  const hideMyBirthday = () => {
    setMyBirthday(false);
  };

  const checkForImpersonation = () => {
    return document.cookie.indexOf("OJWT=") != -1;
  }

  return (
    <div className="home-page">
      <div className="celebrations">
        { myBirthday  ? (
          <MyBirthday me={me} hideMyBirthday={hideMyBirthday} />
        ) : myAnniversary ? (
          <MyAnniversary
            hideMyAnniversary={hideMyAnniversary}
            myAnniversary={myAnniversaryData}
          />
        ) : (
          <>
            { anniversaries.length > 0  && (<Anniversaries anniversaries={anniversaries} />) }
            { birthdays.length > 0 && (<Birthdays birthdays={birthdays} />) }
            <PublicKudos />
          </>
        )}
      </div>
      {checkForImpersonation() &&
        <a class="bottom-right" href="/impersonation/end"><Button variant="contained">Original User</Button></a>}
    </div>
  );
}
