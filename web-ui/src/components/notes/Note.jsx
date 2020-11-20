import React, { useContext, useEffect, useRef, useState } from "react";

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

async function realUpdate(note, csrf) {
  await updateCheckinNote(note, csrf);
}

const updateNote = debounce(realUpdate, 1000);

const Notes = (props) => {
  const { state } = useContext(AppContext);
  const noteRef = useRef([]);
  const { csrf, userProfile, currentCheckin, selectedProfile } = state;
  const { memberProfile } = userProfile;
  const { id } = memberProfile;
  const { memberName } = props;
  const [note, setNote] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  // TODO: get private note
  const [privateNote, setPrivateNote] = useState("Private note");
  const selectedProfilePDLId = selectedProfile && selectedProfile.pdlId;
  const pdlId = memberProfile && memberProfile.pdlId;
  const pdlorAdmin =
    (memberProfile && userProfile.role && userProfile.role.includes("PDL")) ||
    userProfile.role.includes("ADMIN");

  const canViewPrivateNote =
    pdlorAdmin && memberProfile.id !== currentCheckin.teamMemberId;
  const currentCheckinId = currentCheckin && currentCheckin.id;

  useEffect(() => {
    async function getNotes() {
      if (!pdlId) {
        return;
      }
      setIsLoading(true);
      try {
        let res = await getNoteByCheckinId(currentCheckinId, csrf);
        if (res.error) throw new Error(res.error);
        const currentNote =
          res.payload && res.payload.data && res.payload.data.length > 0
            ? res.payload.data[0]
            : null;
        if (currentNote) {
          setNote(currentNote);
        } else if (id === selectedProfilePDLId) {
          if (!noteRef.current.some((id) => id === currentCheckinId)) {
            noteRef.current.push(currentCheckinId);
            res = await createCheckinNote({
              checkinid: currentCheckinId,
              createdbyid: id,
              description: "",
              csrf,
            });
            noteRef.current = noteRef.current.filter(
              (id) => id !== currentCheckinId
            );
            if (res.error) throw new Error(res.error);
            if (res && res.payload && res.payload.data) {
              setNote(res.payload.data);
            }
          }
        } else {
          res = await createCheckinNote({
            checkinid: currentCheckinId,
            createdbyid: pdlId,
            description: "",
            csrf,
          });
          if (res.error) throw new Error(res.error);
          if (res && res.payload && res.payload.data) {
            setNote(res.payload.data);
          }
        }
      } catch (e) {
        console.log(e);
      }
      setIsLoading(false);
    }
    if (csrf) {
      getNotes();
    }
  }, [csrf, currentCheckinId, pdlId, id, selectedProfilePDLId, pdlorAdmin]);

  const handleNoteChange = (e) => {
    if (Object.keys(note) === 0 || !csrf) {
      return;
    }
    const { value } = e.target;
    setNote((note) => {
      const newNote = { ...note, description: value };
      updateNote(newNote, csrf);
      return newNote;
    });
  };

  const handlePrivateNoteChange = (e) => {
    setPrivateNote(e.target.value);
  };

  return (
    <div className="notes">
      <div>
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
                disabled={
                  !pdlorAdmin ||
                  currentCheckin.completed === true ||
                  Object.keys(note) === 0
                }
                onChange={handleNoteChange}
                value={note && note.description ? note.description : ""}
              ></textarea>
            )}
          </div>
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
