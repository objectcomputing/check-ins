import React, { useContext, useEffect, useState } from "react";

import {
  getNoteByCheckinId,
  createCheckinNote,
  updateCheckinNote,
} from "../../api/checkins";
import { AppContext } from "../../context/AppContext";

import { debounce } from "lodash/function";
import NotesIcon from "@material-ui/icons/Notes";
import LockIcon from "@material-ui/icons/Lock";
import Skeleton from "@material-ui/lab/Skeleton";

import "./Note.css";

async function realUpdate(note) {
  await updateCheckinNote(note);
}

const updateNote = debounce(realUpdate, 1000);

const Notes = (props) => {
  const { state } = useContext(AppContext);
  const { userProfile, currentCheckin } = state;
  const { memberName } = props;
  const [note, setNote] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  // TODO: get private note
  const [privateNote, setPrivateNote] = useState("Private note");

  const canViewPrivateNote =
    userProfile.memberProfile &&
    userProfile.memberProfile.role &&
    (userProfile.memberProfile.role.includes("PDL") ||
      userProfile.memberProfile.role.includes("ADMIN")) &&
    userProfile.memberProfile.id !== currentCheckin.teamMemberId;

  useEffect(() => {
    const id = currentCheckin.id;
    const createdby = currentCheckin.teamMemberId;
    async function getNotes() {
      if (currentCheckin) {
        setIsLoading(true);
        let res = await getNoteByCheckinId(id);
        if (res.payload && res.payload.data && !res.error) {
          if (res.payload.data.length === 0) {
            res = await createCheckinNote({
              currentCheckin: id,
              createdbyid: createdby,
              description: "",
            });
            setNote(res.payload.data);
          } else {
            setNote(res.payload.data[0]);
          }
        }
        setIsLoading(false);
      }
    }
    getNotes();
  }, [currentCheckin]);

  const handleNoteChange = (e) => {
    const { value } = e.target;
    setNote((note) => {
      const newNote = { ...note, description: value };
      updateNote(newNote);
      return newNote;
    });
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
          {isLoading ? (
            <div className="skeleton">
              <Skeleton variant="text" height={"2rem"} />
              <Skeleton variant="text" height={"2rem"} />
              <Skeleton variant="text" height={"2rem"} />
              <Skeleton variant="text" height={"2rem"} />
            </div>
          ) : (
            <textarea
              onChange={handleNoteChange}
              value={note && note.description ? note.description : ""}
            ></textarea>
          )}
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
