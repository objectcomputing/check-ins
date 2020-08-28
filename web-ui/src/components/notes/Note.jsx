import React, { useContext, useEffect, useRef, useState } from "react";
import { getNoteByCheckinId, updateCheckinNote } from "../../api/checkins";
import useDebounce from "../../hooks/useDebounce";
import NotesIcon from "@material-ui/icons/Notes";
import LockIcon from "@material-ui/icons/Lock";
import { AppContext } from "../../context/AppContext";

import "./Note.css";

const Notes = (props) => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const canViewPrivateNote =
    userProfile.role.includes("PDL") || userProfile.role.includes("ADMIN");
  const { checkin, memberName } = props;
  const { id } = checkin;
  const [note, setNote] = useState({});
  // TODO: get private note and determine if user is PDL
  const [privateNote, setPrivateNote] = useState("Private note");

  const canvasRef = useRef();

  // to draw empty sections when loading
  useEffect(() => {
    const context = canvasRef.current.getContext("2d");
    context.fillStyle = "lightgrey";
    for (let i = 1; i < 5; i++) {
      context.fillRect(5, 15 * i, 500, 10);
    }
  }, []);

  useEffect(() => {
    async function getNotes() {
      if (id) {
        let res = await getNoteByCheckinId(id);
        let data =
          res.payload &&
          res.payload.data &&
          res.payload.data.length > 0 &&
          res.payload.status === 200 &&
          !res.error
            ? res.payload.data
            : null;
        if (data) {
          setNote(data[0]);
          const canvas = canvasRef.current;
          if (canvas && data[0].description) {
            // to remove canvas if there is data
            canvas.parentElement.removeChild(canvas);
          }
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
  }, [debouncedDescription, note, note.id]);

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
          <canvas ref={canvasRef}></canvas>
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
