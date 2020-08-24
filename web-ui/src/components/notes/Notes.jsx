import React, { useEffect, useState } from "react";
import { getNoteByCheckinId, updateCheckinNote } from "../../api/checkins";
import useDebounce from "../../hooks/useDebounce";
import NotesIcon from "@material-ui/icons/Notes";
import LockIcon from "@material-ui/icons/Lock";

import "./Notes.css";

const Notes = (props) => {
  const { checkin, memberName } = props;
  const { checkinid, description } = checkin;
  const [note, setNote] = useState(description);
  // TODO: get private note and determine if user is PDL
  const isPDL = true;
  const [privateNote, setPrivateNote] = useState("Private note");

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
      if (checkin) {
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
  }, [debouncedNote, checkin]);

  const handleNoteChange = (e) => {
    setNote(e.target.value);
  };

  const handlePrivateNoteChange = (e) => {
    setPrivateNote(e.target.value);
  };

  return (
    <div className="notes">
      <div>
        <h1>
          <NotesIcon style={{ marginRight: "10px" }} />
          Notes for {memberName}
        </h1>
        <div className="container">
          <textarea onChange={handleNoteChange} value={note}>
            <p></p>
          </textarea>
        </div>
      </div>
      {isPDL && (
        <div>
          <h1>
            <LockIcon style={{ marginRight: "10px" }} />
            Private Notes
          </h1>
          <div className="container">
            <textarea onChange={handlePrivateNoteChange} value={privateNote}>
              <p></p>
            </textarea>
          </div>
        </div>
      )}
    </div>
  );
};

export default Notes;
