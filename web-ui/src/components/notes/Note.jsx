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
  const { memberProfile } = userProfile;
  const { memberName } = props;
  const [note, setNote] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  // TODO: get private note
  const [privateNote, setPrivateNote] = useState("Private note");
  const pdlId = memberProfile && memberProfile.pdlId;

  const canViewPrivateNote =
    memberProfile &&
    memberProfile.role &&
    (memberProfile.role.includes("PDL") ||
      memberProfile.role.includes("ADMIN")) &&
    memberProfile.id !== currentCheckin.teamMemberId;

  useEffect(() => {
    async function getNotes() {
      if (currentCheckin) {
        const id = currentCheckin.id;
        setIsLoading(true);
        let res = await getNoteByCheckinId(id);
        if (
          pdlId &&
          res.payload &&
          res.payload.data &&
          res.payload.data.length > 0 &&
          !res.error
        ) {
          setNote(res.payload.data[0]);
        } else {
          res = await createCheckinNote({
            checkinid: id,
            createdbyid: pdlId,
            description: "",
          });
          if (res && res.payload && res.payload.data) {
            setNote(res.payload.data);
          }
        }
        setIsLoading(false);
      }
    }
    getNotes();
  }, [currentCheckin, pdlId]);

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
              disabled={currentCheckin.completed === true}
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
