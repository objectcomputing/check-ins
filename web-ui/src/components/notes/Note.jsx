import React, { useContext, useEffect, useRef, useState } from "react";
import { getNoteByCheckinId, updateCheckinNote } from "../../api/checkins";
import { debounce } from "lodash/function";
import NotesIcon from "@material-ui/icons/Notes";
import LockIcon from "@material-ui/icons/Lock";
import { AppContext } from "../../context/AppContext";

import "./Note.css";

async function realUpdate(note) {
  let res = await updateCheckinNote(note);
  if (res.error) {
    console.error(res.error);
  }
}

const updateNote = debounce(realUpdate, 1000);

const Notes = (props) => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const canViewPrivateNote =
    userProfile.role.includes("PDL") || userProfile.role.includes("ADMIN");
  const { checkin, memberName } = props;
  const [note, setNote] = useState({});
  // TODO: get private note and determine if user is PDL
  const [privateNote, setPrivateNote] = useState("Private note");

  const canvasRef = useRef();

  // to draw empty sections when loading
  useEffect(() => {
    const context = canvasRef.current.getContext("2d");
    context.fillStyle = "lightgrey";
    for (let i = 1; i < 5; i++) {
      context.fillRect(5, 30 * i, 500, 15);
    }
  }, []);

  useEffect(() => {
    async function getNotes() {
      if (checkin) {
        let res = await getNoteByCheckinId(checkin.id);
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
          if (canvas && canvas.parentElement && data[0].description) {
            // to remove canvas if there is data
            canvas.parentElement.removeChild(canvas);
          }
        }
      }
    }
    getNotes();
  }, [checkin]);

  const handleNoteChange = (e) => {
    setNote({ ...note, description: e.target.value });
    updateNote(note);
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
