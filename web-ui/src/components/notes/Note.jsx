import React, { useContext, useEffect, useState } from "react";
import {
  getNoteByCheckinId,
  createCheckinNote,
  updateCheckinNote,
} from "../../api/checkins";
import { debounce } from "lodash/function";
import NotesIcon from "@material-ui/icons/Notes";
import LockIcon from "@material-ui/icons/Lock";
import { AppContext } from "../../context/AppContext";
import Snackbar from "@material-ui/core/Snackbar";
import MuiAlert from "@material-ui/lab/Alert";

import "./Note.css";

async function realUpdate(note) {
  let res = await updateCheckinNote(note);
  if (res.error) {
    console.error(res.error);
  }
}

const updateNote = debounce(realUpdate, 1000);

function Alert(props) {
  return <MuiAlert elevation={6} variant="filled" {...props} />;
}

const Notes = (props) => {
  const { state } = useContext(AppContext);
  const { userProfile } = state;
  const canViewPrivateNote =
    userProfile.memberProfile &&
    userProfile.memberProfile.role &&
    (userProfile.memberProfile.role.includes("PDL") ||
      userProfile.memberProfile.role.includes("ADMIN"));
  const { checkin, memberName } = props;
  const [note, setNote] = useState({});
  const [isLoading, setIsLoading] = useState(true);
  const [open, setOpen] = useState(false);
  const [toast, setToast] = useState("");
  // TODO: get private note and determine if user is PDL
  const [privateNote, setPrivateNote] = useState("Private note");

  useEffect(() => {
    const id = checkin.id;
    const createdby = checkin.teamMemberId;
    async function getNotes() {
      if (checkin) {
        setIsLoading(true);
        let res = await getNoteByCheckinId(id);
        if (res.payload && res.payload.data && !res.error) {
          if (res.payload.data.length === 0) {
            res = await createCheckinNote({
              checkinid: id,
              createdbyid: createdby,
              description: "",
            });
            setNote(res.payload.data);
          } else {
            setNote(res.payload.data[0]);
          }
        } else {
          setToast("There was a problem loading Notes");
          setOpen(true);
          setNote(null);
        }
        setIsLoading(false);
      }
    }
    getNotes();
  }, [checkin]);

  const handleClose = (event, reason) => {
    if (reason === "clickaway") {
      return;
    }
    setOpen(false);
  };

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
            <div className="is-loading"></div>
          ) : (
            <textarea
              onChange={handleNoteChange}
              value={note && note.description ? note.description : ""}
            ></textarea>
          )}
        </div>
        <Snackbar
          autoHideDuration={2500}
          open={open}
          onClose={handleClose}
          style={{ left: "40%", bottom: "20%" }}
        >
          <Alert onClose={handleClose} severity="error">
            {toast}
          </Alert>
        </Snackbar>
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
