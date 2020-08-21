import React, { useEffect, useState } from "react";
import { getNoteByCheckinId, updateCheckinNote } from "../../api/checkins";
import useDebounce from "../../hooks/useDebounce";

const Notes = (props) => {
  const { checkin } = props;
  const { checkinid, createdbyid, id, description } = checkin;
  const [note, setNote] = useState(description);

  useEffect(() => {
    async function getNotes() {
      if (description) {
        let res = await getNoteByCheckinId(checkinid);
        let data =
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          setNote(data[0].description);
        }
      }
    }
    getNotes();
  }, [description, checkinid]);

  let debouncedNote = useDebounce(note, 2000);

  useEffect(() => {
    async function updateNotes() {
      if (id && checkinid) {
        let res = await updateCheckinNote({
          ...checkin,
          description: debouncedNote,
        });
        if (res.error) {
          console.error(res.error);
        }
      }
    }
    updateNotes();
  }, [debouncedNote]);

  const handleChange = (e) => {
    setNote(e.target.value);
  };

  return (
    <div>
      <h1>Notes</h1>
      <div style={{ display: "flex", justifyContent: "center" }}>
        <textarea onChange={handleChange} value={note}></textarea>
      </div>
    </div>
  );
};

export default Notes;
