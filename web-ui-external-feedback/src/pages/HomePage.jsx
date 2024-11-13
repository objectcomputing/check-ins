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
import { Button, Grid } from '@mui/material';

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

  // This width matches the birthdays-card and anniversaries-card style.
  // However, we do not want to set this width on the PublicKudos css as it is
  // used elsewhere and does not need to have it's width restricted.  This only
  // applies if if we have birthdays or anniversaries to display on this page.
  const kudosStyle = birthdays.length == 0 &&
                     anniversaries.length == 0 ? {} : { width: '450px' };

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
          <Grid container spacing={2} style={{ padding: '0 20px 0 20px' }}>
            { anniversaries.length > 0  && (
              <Grid item>
                <Anniversaries anniversaries={anniversaries} />
              </Grid>) }
            { birthdays.length > 0 && (
              <Grid item>
                <Birthdays birthdays={birthdays} />
              </Grid>) }
            <Grid item style={kudosStyle}>
              <PublicKudos />
            </Grid>
          </Grid>
        )}
      </div>
      {checkForImpersonation() &&
        <a class="bottom-right" href="/impersonation/end"><Button variant="contained">Original User</Button></a>}
    </div>
  );
}
