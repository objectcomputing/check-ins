import React, { useContext, useEffect, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import {
  getPrivateNoteByCheckinId,
  createPrivateNote,
  updatePrivateNote,
} from "../../api/checkins";
import { AppContext } from "../../context/AppContext";
import { selectCsrfToken, selectCurrentUser, selectIsPDL, selectIsAdmin, selectCheckin, selectProfile } from "../../context/selectors";
import { debounce } from "lodash/function";
import LockIcon from "@material-ui/icons/Lock";
import Skeleton from "@material-ui/lab/Skeleton";
import Card from '@material-ui/core/Card';
import CardHeader from '@material-ui/core/CardHeader';
import CardContent from '@material-ui/core/CardContent';

import "./PrivateNote.css";
import MarkdownNote from "../markdown-note/MarkdownNote";

async function realUpdate(note, csrf) {
  await updatePrivateNote(note, csrf);
}

const updateNote = debounce(realUpdate, 1000);

const PrivateNote = () => {
  const { state } = useContext(AppContext);
  const { checkinId, memberId } = useParams();
  const csrf = selectCsrfToken(state);
  const memberProfile = selectCurrentUser(state);
  const currentUserId = memberProfile?.id;
  const currentCheckin = selectCheckin(state, checkinId);
  const currentMember = selectProfile(state, memberId);
  const pdlId = currentMember?.pdlId;
  const isAdmin = selectIsAdmin(state);

  const noteRef = useRef([]);
  const [note, setNote] = useState();
  const [isLoading, setIsLoading] = useState(true);

  const pdlorAdmin = selectIsPDL(state) || isAdmin;
  const canView = pdlorAdmin && currentUserId !== memberId;

  useEffect(() => {
    async function getPrivateNotes() {
      setIsLoading(true);
      try {
        let res = await getPrivateNoteByCheckinId(checkinId, csrf);
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
            res = await createPrivateNote({
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
          res = await createPrivateNote({
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
      getPrivateNotes();
    }
  }, [csrf, checkinId, currentUserId, pdlId]);

  const handleNoteChange = (content, delta, source, editor) => {
    if (Object.keys(note).length === 0 || !csrf) {
      return;
    }
    
    setNote((note) => {
      const newNote = { ...note, description: content };
      updateNote(newNote, csrf);
      return newNote;
    });
  }

  return canView && (
      <Card className="private-note">
        <CardHeader avatar={<LockIcon />} title="Private Notes" titleTypographyProps={{variant: "h5", component: "h2"}} />
        <CardContent>
            {isLoading ? (
              <div className="container">
                <div className="skeleton">
                  <Skeleton variant="text" height={"2rem"} />
                  <Skeleton variant="text" height={"2rem"} />
                  <Skeleton variant="text" height={"2rem"} />
                  <Skeleton variant="text" height={"2rem"} />
                </div>
              </div>
            ) : (
              <MarkdownNote 
                style={{height: "175px", marginBottom: "30px"}}
                value={note && note.description ? note.description : ""}
                onChange={handleNoteChange}
                readOnly={
                  currentCheckin?.completed ||
                  note === undefined || Object.keys(note) === 0
                }
              />
            )}
          
        </CardContent>
      </Card>
      );
};

export default PrivateNote;
