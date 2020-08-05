import React, { useContext, useState } from "react";
import { getMembersByPDL } from "../../api/member";
import { getMemberCheckinsByPDL } from "../../api/checkins";
import "./Personnel.css";
import { AppContext } from "../../context/AppContext";

const PersonnelComponent = () => {
  const { state } = useContext(AppContext);
  const user = state.user;
  const [ personnel, setPersonnel ] = useState();
  const [ checkins, setCheckins ] = useState();

  // Get team members 
  React.useEffect(() => {
    async function updatePersonnel() {
      if(user.uuid) {
        let res = await getMembersByPDL(user.uuid);
        if(res.data && !res.error) {
          let data = {pdlId: user.uuid, data: res.data}
          setPersonnel(data);
        }
      }
    };
    updatePersonnel();
  }, [user.uuid]);

 
  React.useEffect(() => {
    async function updateCheckins() {
      if(personnel && personnel.pdlId && personnel.data) {
        const tmpCheckins = []
        for(const person of personnel.data) {
          let res = await getMemberCheckinsByPDL(person.uuid, personnel.pdlId)
          let checkIn = undefined
          if(res && res.data && res.data.length > 0 && !res.error) {
            res.data.sort((a, b) => (a.checkInDate < b.checkInDate) ? 1 : -1)
            checkIn = res.data[0]
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

  function createEntry(person, checkIn) {
    let key = undefined;
    let name = "Team Member"
    let lastCheckIn = "Unknown"
    let infoClassName = "personnel-info-hidden"

    if(checkIn && checkIn.checkInDate && checkIn.checkInDate.length === 3 && checkIn.id) {
        let checkInDate = checkIn.checkInDate;
        checkInDate =  new Date(checkInDate[0], checkInDate[1]-1, checkInDate[2]).toLocaleDateString();
        lastCheckIn = <a href={`/checkin/${checkIn.id}`}>{checkInDate}</a>
    }

    console.log(person)
    if(person) {
      let id = person.id ? person.id : null
      name = person.name ? person.name : id ? id : name
      key = id ? `${id}Personnel` : key
      infoClassName = "personnel-info"
    }
    console.log(key)

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

  const createPersonnelEntries = () => {
    if(checkins && checkins.length > 0) {
      return checkins.map(person => createEntry(person, person.checkIn))
    }  else if(personnel && personnel.data && personnel.data.length > 0) {
      return personnel.data.map(person => createEntry(person, null))
    } else {
      let fake = Array(3);
      for(let i = 0; i < fake.length; i++) {
        fake[i] = createEntry({id: i+1}, null)
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

export default PersonnelComponent;