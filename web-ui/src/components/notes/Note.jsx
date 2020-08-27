import React, { useContext, useEffect, useState } from "react";
import { getNoteByCheckinId, updateCheckinNote } from "../../api/checkins";
import useDebounce from "../../hooks/useDebounce";
import NotesIcon from "@material-ui/icons/Notes";
import LockIcon from "@material-ui/icons/Lock";
import { AppContext } from "../../context/AppContext";

import "./Note.css";

const Notes = (props) => {
  const { state } = useContext(AppContext);
  const { userData } = state;
  const canViewPrivateNote =
    userData.role === "PDL" || userData.role === "ADMIN";
  const { checkin, memberName } = props;
  const { id } = checkin;
  const [note, setNote] = useState({});
  // TODO: get private note and determine if user is PDL
  const [privateNote, setPrivateNote] = useState("Private note");

  useEffect(() => {
    async function getNotes() {
      if (id) {
        let res = await getNoteByCheckinId(id);
        let data =
          res.payload &&
          res.payload.data &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          setNote(data[0]);
        }
      }
    }
    getNotes();
  }, [id]);

  let debouncedDescription = useDebounce(note.description, 2000);

  useEffect(() => {
    async function updateNotes() {
      if (note.id) {
        let res = await updateCheckinNote({
          ...note,
          description: debouncedDescription,
        });
        if (res.error) {
          console.error(res.error);
        }
      }
    }
    updateNotes();
  }, [debouncedDescription, note.id]);

  const handleNoteChange = (e) => {
    setNote({ ...note, description: e.target.value });
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
          <textarea
            onChange={handleNoteChange}
            value={note.description}
          ></textarea>
        </div>
      </div>
      {canViewPrivateNote && (
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
