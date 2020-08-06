import React, { useContext, useState } from "react";
import { getMembersByPDL } from "../../api/member";
import { getMemberCheckinsByPDL } from "../../api/checkins";
import { AppContext } from "../../context/AppContext";

import "./Personnel.css";

const Personnel = () => {
  const { state } = useContext(AppContext);
  const user = state.user;
  const [ personnel, setPersonnel ] = useState();
  const [ checkins, setCheckins ] = useState();

  // Get personnel 
  React.useEffect(() => {
    async function updatePersonnel() {
      if(user.uuid) {
        let res = await getMembersByPDL(user.uuid);
        let data = res && res.payload && res.payload.status === 200 ? res.payload.data : null
        if(data && !res.error) {
          let personnelData = {pdlId: user.uuid, data: data}
          setPersonnel(personnelData);
        }
      }
    };
    updatePersonnel();
  }, [user.uuid]);
 
  // Get checkins per personnel
  React.useEffect(() => {
    async function updateCheckins() {
      if(personnel && personnel.pdlId && personnel.data) {
        const tmpCheckins = []
        for(const person of personnel.data) {
          let res = await getMemberCheckinsByPDL(person.uuid, personnel.pdlId)
          let checkIn = undefined
          let data = res && res.payload && res.payload.status === 200 ? res.payload.data : null
          if(data && data.length > 0 && !res.error) {
            data.sort((a, b) => (a.checkInDate < b.checkInDate) ? 1 : -1)
            checkIn = data[0]
          }
          let personWithCheckin = Object.assign({}, person)
          personWithCheckin.checkIn = checkIn
          tmpCheckins.push(personWithCheckin)
        };
        setCheckins(tmpCheckins);
      }
    };
    updateCheckins();
  },[personnel]);

  // Create entry of member and their last checkin
  function createEntry(person, checkIn, key) {
    key = key ? key : undefined;
    let name = "Team Member"
    let lastCheckIn = "Unknown"
    let infoClassName = "personnel-info-hidden"

    if(checkIn && checkIn.checkInDate && checkIn.checkInDate.length === 3 && checkIn.id) {
        let checkInDate = checkIn.checkInDate;
        checkInDate =  new Date(checkInDate[0], checkInDate[1]-1, checkInDate[2]).toLocaleDateString();
        lastCheckIn = <a href={`/checkin/${checkIn.id}`}>{checkInDate}</a>
    }

    if(person) {
      let id = person.id ? person.id : null
      name = person.name ? person.name : id ? id : name
      key = id && !key ? `${id}Personnel` : key
      infoClassName = "personnel-info"
    }

    return (
      <div key={key} className="image-div">
        <img className="member-image" alt="personnel pic" src={require("../../images/default_profile.jpg")}/>
        <div className="info-div">
          <p className={infoClassName}>{name}</p>
          <p className={infoClassName}>Last Check-in: {lastCheckIn}</p>
        </div>
      </div>
    )
  }

  // Create the entries for the personnel container
  const createPersonnelEntries = () => {
    if(checkins && checkins.length > 0) {
      return checkins.map(person => createEntry(person, person.checkIn, null))
    }  else if(personnel && personnel.data && personnel.data.length > 0) {
      return personnel.data.map(person => createEntry(person, null, null))
    } else {
      let fake = Array(3);
      for(let i = 0; i < fake.length; i++) {
        fake[i] = createEntry(null, null, `${i+1}Personnel`)
      }
      return fake
    }
  }
  
  return (
    <fieldset className="personnel-container">
      <legend>My Personnel</legend>
    { createPersonnelEntries() }
    </fieldset>
  )
};

export default Personnel;