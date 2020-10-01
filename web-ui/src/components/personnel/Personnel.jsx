import React, { useContext, useEffect, useState } from "react";
import { getMembersByPDL } from "../../api/member";
import { getMemberCheckinsByPDL } from "../../api/checkins";
import { AppContext, UPDATE_SELECTED_PROFILE } from "../../context/AppContext";

import "./Personnel.css";

const Personnel = () => {
  const { state, dispatch } = useContext(AppContext);
  const { userProfile } = state;
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
        let res = await getMembersByPDL(id);
        let data =
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          let personnelData = { pdlId: id, data: data };
          setPersonnel(personnelData);
        }
      }
    }
    updatePersonnel();
  }, [id]);

  // Get checkins per personnel
  useEffect(() => {
    async function updateCheckins() {
      if (personnel && personnel.pdlId && personnel.data) {
        const tmpCheckins = [];
        for (const person of personnel.data) {
          let res = await getMemberCheckinsByPDL(person.id, personnel.pdlId);
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
    updateCheckins();
  }, [personnel]);

  // Create entry of member and their last checkin
  function createEntry(person, checkins, keyInput) {
    let key = keyInput ? keyInput : undefined;
    let name = "Team Member";
    let lastCheckInDate = "Unknown";
    let infoClassName = "personnel-info-hidden";
    if (checkins && checkins.length) {
      const lastCheckin = checkins[checkins.length - 1];
      lastCheckInDate = new Date(...lastCheckin.checkInDate);
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
        createEntry(person, person.checkIn, null)
      );
    } else if (personnel && personnel.data && personnel.data.length > 0) {
      return personnel.data.map((person) => createEntry(person, null, null));
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
