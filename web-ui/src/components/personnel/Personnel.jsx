import React, { useContext, useEffect, useState } from "react";
import { getMembersByPDL } from "../../api/member";
import { getCheckinByMemberId } from "../../api/checkins";
import { AppContext, UPDATE_SELECTED_PROFILE } from "../../context/AppContext";

import "./Personnel.css";

const Personnel = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, userProfile } = state;
  const id =
    userProfile && userProfile.memberProfile
      ? userProfile.memberProfile.id
      : undefined;
  const [personnel, setPersonnel] = useState();
  const [checkins, setCheckins] = useState([]);

  // Get personnel
  useEffect(() => {
    async function updatePersonnel() {
      if (id) {
        let res = await getMembersByPDL(id, csrf);
        let data =
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          setPersonnel(data);
        }
      }
    }
    if (csrf) {
      updatePersonnel();
    }
  }, [csrf, id]);

  // Get checkins per personnel
  useEffect(() => {
    async function updateCheckins() {
      if (personnel) {
        const tmpCheckins = [];
        for (const person of personnel) {
          let res = await getCheckinByMemberId(person.id, csrf);
          let newCheckins = [];
          let data =
            res && res.payload && res.payload.status === 200
              ? res.payload.data
              : null;
          if (data && data.length > 0 && !res.error) {
            data.sort((a, b) => (a.checkInDate < b.checkInDate ? 1 : -1));
            newCheckins = data;
          }
          let personWithCheckin = Object.assign({}, person);
          personWithCheckin.checkins = newCheckins;
          tmpCheckins.push(personWithCheckin);
        }
        setCheckins(tmpCheckins);
      }
    }
    if (csrf) {
      updateCheckins();
    }
  }, [csrf, personnel]);

  // Create entry of member and their last checkin
  function createEntry(person, checkins, keyInput) {
    let key = keyInput ? keyInput : undefined;
    let name = "Team Member";
    let lastCheckInDate = "Unknown";
    let infoClassName = "personnel-info-hidden";
    if (checkins && checkins.length) {
      const lastCheckin = checkins[checkins.length - 1];
      lastCheckInDate = new Date(
        ...lastCheckin.checkInDate
      ).toLocaleDateString();
    }

    if (person) {
      let id = person.id ? person.id : null;
      name = person.name ? person.name : id ? id : name;
      key = id && !key ? `${id}Personnel` : key;
      infoClassName = "personnel-info";
    }

    return (
      <div
        onClick={() => {
          dispatch({ type: UPDATE_SELECTED_PROFILE, payload: person });
        }}
        key={key}
        className="image-div"
      >
        <img
          className="member-image"
          alt="personnel pic"
          src={"/default_profile.jpg"}
        />
        <div className="info-div">
          <p className={infoClassName}>{name}</p>
          <p className={infoClassName}>Last Check-in: {lastCheckInDate}</p>
        </div>
      </div>
    );
  }

  // Create the entries for the personnel container
  const createPersonnelEntries = () => {
    if (checkins && checkins.length > 0) {
      return checkins.map((person) =>
        createEntry(person, person.checkins, null)
      );
    } else if (personnel && personnel.length > 0) {
      return personnel.map((person) => createEntry(person, null, null));
    } else {
      let fake = Array(3);
      for (let i = 0; i < fake.length; i++) {
        fake[i] = createEntry(null, null, `${i + 1}Personnel`);
      }
      return fake;
    }
  };

  return (
    <fieldset className="personnel-container">
      <legend>My Personnel</legend>
      {createPersonnelEntries()}
    </fieldset>
  );
};

export default Personnel;
