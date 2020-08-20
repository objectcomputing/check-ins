import React, { useContext, useEffect, useState } from "react";
import { AppContext } from "../../context/AppContext";
import {getCheckinByMemberId} from '../../api/checkins'

const Notes = () => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const { id } = userProfile;
  const [notes, setNotes] = useState([])
  const [checkin, setCheckin] = useState({})
  
  useEffect(() => {
    async function updateCheckin() {
      if (id) {
        let res = await getCheckinByMemberId(id);
        let data =
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          setCheckin(data);
        }
      }
    }
    updateCheckin();
  }, [id]);

  useEffect(() => {
    async function updateNotes() {
      if (checkin.id) {
        let res = await getCheckinByMemberId(id);
        let data =
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          setCheckin(data);
        }
      }
    }
    updateNotes();
  }, [checkin]);

  return <div>Notes</div>;
};

export default Notes;
