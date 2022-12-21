import React, {useCallback, useContext, useEffect, useState} from "react";
import {styled} from "@mui/material/styles";
import {Button, Collapse, Divider, IconButton, Tab, Typography} from "@mui/material";
import {TabContext, TabList, TabPanel} from '@mui/lab';
import {AppContext} from "../context/AppContext";
import {selectCsrfToken, selectCurrentUser} from "../context/selectors";
import {getReceivedKudos, getSentKudos} from "../api/kudos";
import {UPDATE_TOAST} from "../context/actions";
import KudosCard from "../components/kudos_card/KudosCard";

import "./KudosPage.css";
import KudosDialog from "../components/kudos_dialog/KudosDialog";
import StarIcon from "@mui/icons-material/Star";

import ArchiveIcon from "@mui/icons-material/Archive";
import UnarchiveIcon from "@mui/icons-material/Unarchive";

import SkeletonLoader from "../components/skeleton_loader/SkeletonLoader";

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

  const [kudosDialogOpen, setKudosDialogOpen] = useState(false);
  const [kudosTab, setKudosTab] = useState("RECEIVED");
  const [receivedKudos, setReceivedKudos] = useState([]);
  const [receivedKudosLoading, setReceivedKudosLoading] = useState(true)
  const [sentKudos, setSentKudos] = useState([]);
  const [sentKudosLoading, setSentKudosLoading] = useState(true);

  const loadReceivedKudos = useCallback(async () => {
    setReceivedKudosLoading(true);
    const res = await getReceivedKudos(currentUser.id, csrf);
    if (res?.payload?.data && !res.error) {
      setReceivedKudosLoading(false);
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
  }, [csrf, dispatch]);

  const loadSentKudos = useCallback(async () => {
    setSentKudosLoading(true);
    const res = await getSentKudos(currentUser.id, csrf);
    if (res?.payload?.data && !res.error) {
      setSentKudosLoading(false);
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
  }, [csrf, dispatch]);

  useEffect(() => {
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

  const handleTabChange = useCallback((event, newTab) => {
    switch (newTab) {
      case "RECEIVED":
        loadReceivedKudos().then(data => {
          if (data) {
            setReceivedKudos(data);
          }
        });
        break;
      case "SENT":
        loadSentKudos().then(data => {
          if (data) {
            setSentKudos(data);
          }
        });
        break;
      default:
        console.warn(`Invalid tab: ${newTab}`);
    }

    setKudosTab(newTab);
  }, [loadReceivedKudos, loadSentKudos]);

  return (
    <Root className="kudos-page">
      <div className="kudos-page-header">
        <Typography fontWeight="bold" variant="h4">Kudos</Typography>
        <KudosDialog
          open={kudosDialogOpen}
          onClose={() => setKudosDialogOpen(false)}
        />
        <Button
          className="kudos-dialog-open"
          variant="outlined"
          startIcon={<StarIcon/>}
          onClick={() => setKudosDialogOpen(true)}
        >
          Give Kudos
        </Button>
      </div>
      {/* Tabs */}
      <TabContext value={kudosTab}>
        <div className="kudos-tab-container">
          <TabList onChange={handleTabChange}>
            <Tab label="Received" value="RECEIVED" icon={<ArchiveIcon/>} iconPosition="start"/>
            <Tab label="Sent" value="SENT" icon={<UnarchiveIcon/>} iconPosition="start"/>
          </TabList>
        </div>
        <TabPanel value="RECEIVED" style={{ padding: "1rem 0" }}>
          {receivedKudosLoading
            ? Array.from({length: 5}).map((_, index) => <SkeletonLoader key={index} type="kudos"/>)
            : (
              receivedKudos.length > 0
              ? <div className="received-kudos-list">
                  {receivedKudos.map(k =>
                    <KudosCard
                      key={k.id}
                      kudos={k}
                      onKudosAction={() => {
                        const updatedKudos = receivedKudos.filter(pk => pk.id !== k.id);
                        setReceivedKudos(updatedKudos);
                      }}
                    />
                  )}
              </div>
              : <div className="no-pending-kudos-message">
                  <Typography variant="body2">There are currently no pending kudos</Typography>
              </div>
            )
          }
        </TabPanel>
        <TabPanel value="SENT" style={{ padding: "1rem 0" }}>
        {sentKudosLoading
            ? Array.from({length: 5}).map((_, index) => <SkeletonLoader key={index} type="kudos"/>)
            : (
              <div>
                {sentKudos.map(k =>
                  <KudosCard key={k.id} kudos={k}/>
                )}
              </div>
            )
          }
        </TabPanel>
      </TabContext>

      {/* Collapse */}
      {/* <div className="received-kudos-header">
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
              <KudosCard key={kudos.id} kudos={kudos}/>
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
              <KudosCard key={kudos.id} kudos={kudos}/>
            )
            : <div className="empty-kudos-container">
              <Typography variant="body2">
                You have not given any kudos. Visit a member's profile page to send them some kudos!
              </Typography>
            </div>
          }
        </div>
      </Collapse> */}
    </Root>
  );
};

export default KudosPage;