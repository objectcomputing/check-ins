import React, {useContext, useEffect, useState} from "react";
import {styled} from "@mui/material/styles";
import {Collapse, Divider, IconButton, Typography} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import {AppContext} from "../context/AppContext";
import {selectCsrfToken, selectCurrentUser} from "../context/selectors";
import {getReceivedKudos, getSentKudos} from "../api/kudos";
import {UPDATE_TOAST} from "../context/actions";
import KudosCard from "../components/kudos_card/KudosCard";

import "./KudosPage.css";

const PREFIX = "KudosPage";
const classes = {
  expandOpen: `${PREFIX}-expandOpen`,
  expandClose: `${PREFIX}-expandClose`
};

const Root = styled("div")({
  [`& .${classes.expandOpen}`]: {
    transform: "rotate(180deg)",
    transition: "transform 0.1s linear",
    marginLeft: "auto"
  },
  [`& .${classes.expandClose}`]: {
    transform: "rotate(0deg)",
    transition: "transform 0.1s linear",
    marginLeft: "auto"
  }
});

const KudosPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const currentUser = selectCurrentUser(state);

  const [receivedKudos, setReceivedKudos] = useState([]);
  const [sentKudos, setSentKudos] = useState([]);
  const [receivedKudosExpanded, setReceivedKudosExpanded] = useState(true);
  const [sentKudosExpanded, setSentKudosExpanded] = useState(true);

  useEffect(() => {
    const loadReceivedKudos = async () => {
      const res = await getReceivedKudos(currentUser.id, csrf);
      if (res?.payload?.data && !res.error) {
        return res.payload.data;
      } else {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to retrieve your received kudos"
          }
        });
      }
    };

    const loadSentKudos = async () => {
      const res = await getSentKudos(currentUser.id, csrf);
      if (res?.payload?.data && !res.error) {
        return res.payload.data;
      } else {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to retrieve your sent kudos"
          }
        });
      }
    }

    if (csrf && currentUser && currentUser.id) {
      loadReceivedKudos().then(data => {
        if (data) {
          setReceivedKudos(data);
        }
      });

      loadSentKudos().then(data => {
        if (data) {
          setSentKudos(data);
        }
      });
    }
  }, [csrf, currentUser, dispatch]);

  return (
    <Root className="kudos-page">
      <div className="kudos-page-header">
        <Typography fontWeight="bold" variant="h4">Kudos</Typography>
      </div>
      <div className="received-kudos-header">
        <Typography variant="h5">Received Kudos</Typography>
        <IconButton
          className={receivedKudosExpanded ? classes.expandOpen : classes.expandClose}
          onClick={() => setReceivedKudosExpanded(!receivedKudosExpanded)}>
          <ExpandMoreIcon/>
        </IconButton>
      </div>
      <Divider/>
      <Collapse in={receivedKudosExpanded}>
        <div className="received-kudos-list">
          {receivedKudos.length > 0
            ? receivedKudos.map(kudos =>
              <KudosCard key={kudos.id} kudos={kudos} type="RECEIVED"/>
            )
            : <div className="empty-kudos-container">
              <Typography variant="body2">You have not received any kudos</Typography>
            </div>
          }
        </div>
      </Collapse>
      <div className="sent-kudos-header">
        <Typography variant="h5">Sent Kudos</Typography>
        <IconButton
          className={sentKudosExpanded ? classes.expandOpen : classes.expandClose}
          onClick={() => setSentKudosExpanded(!sentKudosExpanded)}>
          <ExpandMoreIcon/>
        </IconButton>
      </div>
      <Divider/>
      <Collapse in={sentKudosExpanded}>
        <div className="sent-kudos-list">
          {sentKudos.length > 0
            ? sentKudos.map(kudos =>
              <KudosCard key={kudos.id} kudos={kudos} type="SENT"/>
            )
            : <div className="empty-kudos-container">
              <Typography variant="body2">
                You have not given any kudos. Visit a member's profile page to send them some kudos!
              </Typography>
            </div>
          }
        </div>
      </Collapse>
    </Root>
  );
};

export default KudosPage;