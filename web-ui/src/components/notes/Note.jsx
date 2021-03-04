import React, { useContext, useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import {
  getNoteByCheckinId,
  createCheckinNote,
  updateCheckinNote,
} from "../../api/checkins";
import { AppContext } from "../../context/AppContext";
import { selectCsrfToken, selectCurrentUser, selectCheckin, selectProfile } from "../../context/selectors";
import { debounce } from "lodash/function";
import NotesIcon from "@material-ui/icons/Notes";
import Skeleton from "@material-ui/lab/Skeleton";
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';

import "./Note.css";

async function realUpdate(note, csrf) {
  await updateCheckinNote(note, csrf);
}

const updateNote = debounce(realUpdate, 1000);

const Notes = (props) => {
  const { state } = useContext(AppContext);
  const { checkinId, memberId } = useParams();
  const csrf = selectCsrfToken(state);
  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;
  const currentCheckin = selectCheckin(state, checkinId);
  const currentMember = selectProfile(state, memberId);
  const pdlId = currentMember?.pdlId;

  const noteRef = useRef([]);
  const [note, setNote] = useState();
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    async function getNotes() {
      setIsLoading(true);
      try {
        let res = await getNoteByCheckinId(checkinId, csrf);
        if (res.error) throw new Error(res.error);
        const currentNote =
          res.payload && res.payload.data && res.payload.data.length > 0
            ? res.payload.data[0]
            : null;
        if (currentNote) {
          setNote(currentNote);
        } else if (currentUserId === pdlId) {
          if (!noteRef.current.some((id) => id === checkinId)) {
            noteRef.current.push(checkinId);
            res = await createCheckinNote({
              checkinid: checkinId,
              createdbyid: currentUserId,
              description: "",
            }, csrf);
            noteRef.current = noteRef.current.filter(
              (id) => id !== checkinId
            );
            if (res.error) throw new Error(res.error);
            if (res && res.payload && res.payload.data) {
              setNote(res.payload.data);
            }
          }
        } else {
          res = await createCheckinNote({
            checkinid: checkinId,
            createdbyid: currentUserId,
            description: "",
          }, csrf);
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
  }, [csrf, checkinId, currentUserId, pdlId]);

  const handleNoteChange = (e) => {
    if (Object.keys(note).length === 0 || !csrf) {
      return;
    }
    const { value } = e.target;
    setNote((note) => {
      const newNote = { ...note, description: value };
      updateNote(newNote, csrf);
      return newNote;
    });
  };

  return (
      <Card className="note">
        <CardHeader avatar={<NotesIcon />} title={`Notes for ${currentMember?.name}`} titleTypographyProps={{variant: "h5", component: "h2"}} />
        <CardContent>
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
                  currentCheckin?.completed ||
                  note === undefined || Object.keys(note) === 0
                }
                onChange={handleNoteChange}
                value={note && note.description ? note.description : ""}
              />
            )}
          </div>
        </CardContent>
      </Card>
      );
};

export default Notes;
