import React, { useCallback, useContext, useEffect, useState } from "react";
import { styled } from "@mui/material/styles";
import { Button, Tab, Typography } from "@mui/material";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { AppContext } from "../context/AppContext";
import {
  selectCsrfToken,
  selectCurrentUser,
  selectHasCreateKudosPermission,
} from "../context/selectors";
import { getReceivedKudos, getSentKudos } from "../api/kudos";
import { UPDATE_TOAST } from "../context/actions";
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
  expandClose: `${PREFIX}-expandClose`,
};

const Root = styled("div")({
  [`& .${classes.expandOpen}`]: {
    transform: "rotate(180deg)",
    transition: "transform 0.1s linear",
    marginLeft: "auto",
  },
  [`& .${classes.expandClose}`]: {
    transform: "rotate(0deg)",
    transition: "transform 0.1s linear",
    marginLeft: "auto",
  },
});

const KudosPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const currentUser = selectCurrentUser(state);

  const [kudosDialogOpen, setKudosDialogOpen] = useState(false);
  const [kudosTab, setKudosTab] = useState("RECEIVED");
  const [receivedKudos, setReceivedKudos] = useState([]);
  const [receivedKudosLoading, setReceivedKudosLoading] = useState(true);
  const [sentKudos, setSentKudos] = useState([]);
  const [sentKudosLoading, setSentKudosLoading] = useState(true);

  const loadReceivedKudos = useCallback(async () => {
    setReceivedKudosLoading(true);
    const res = await getReceivedKudos(currentUser.id, csrf);
    if (res?.payload?.data && !res.error) {
      setReceivedKudosLoading(false);
      return res.payload.data;
    }
  }, [csrf, dispatch, currentUser.id]);

  const loadSentKudos = useCallback(async () => {
    setSentKudosLoading(true);
    const res = await getSentKudos(currentUser.id, csrf);
    if (res?.payload?.data && !res.error) {
      setSentKudosLoading(false);
      return res.payload.data;
    }
  }, [csrf, dispatch, currentUser.id]);

  useEffect(() => {
    if (csrf && currentUser && currentUser.id) {
      loadReceivedKudos().then((data) => {
        if (data) {
          let filtered = data.filter((kudo) =>
            kudo.recipientMembers.some((member) => member.id === currentUser.id)
          );
          setReceivedKudos(filtered);
        }
      });

      loadSentKudos().then((data) => {
        if (data) {
          let filtered = data.filter(
            (kudo) => kudo.senderId === currentUser.id
          );
          setSentKudos(filtered);
        }
      });
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [csrf, currentUser, kudosTab]);

  const handleTabChange = useCallback(
    (event, newTab) => {
      switch (newTab) {
        case "RECEIVED":
          setKudosTab("RECEIVED");
          break;
        case "SENT":
          setKudosTab("SENT");
          break;
        default:
          console.warn(`Invalid tab: ${newTab}`);
      }

      setKudosTab(newTab);
    },
    // eslint-disable-next-line react-hooks/exhaustive-deps
    [loadReceivedKudos, loadSentKudos]
  );

  return (
    <Root className="kudos-page">
      <div className="kudos-page-header">
        <KudosDialog
          open={kudosDialogOpen}
          onClose={() => setKudosDialogOpen(false)}
        />
        {selectHasCreateKudosPermission(state) && <Button
          className="kudos-dialog-open"
          variant="outlined"
          startIcon={<StarIcon />}
          onClick={() => setKudosDialogOpen(true)}
        >
          Give Kudos
        </Button>}
      </div>
      <TabContext value={kudosTab}>
        <div className="kudos-tab-container">
          <TabList onChange={handleTabChange}>
            <Tab
              label="Received"
              value="RECEIVED"
              icon={<ArchiveIcon />}
              iconPosition="start"
            />
            <Tab
              label="Sent"
              value="SENT"
              icon={<UnarchiveIcon />}
              iconPosition="start"
            />
          </TabList>
        </div>
        <TabPanel value="RECEIVED" style={{ padding: "1rem 0" }}>
          {receivedKudosLoading ? (
            Array.from({ length: 5 }).map((_, index) => (
              <SkeletonLoader key={index} type="kudos" />
            ))
          ) : receivedKudos.length > 0 &&
            receivedKudos.filter(
              (kudo, index) =>
                kudo.recipientMembers[index]?.id === currentUser.id
            ) ? (
            <div className="received-kudos-list">
              {receivedKudos.map((k) => (
                <KudosCard
                  key={k.id}
                  kudos={k}
                  onKudosAction={() => {
                    const updatedKudos = receivedKudos.filter(
                      (pk) => pk.id !== k.id
                    );
                    setReceivedKudos(updatedKudos);
                  }}
                />
              ))}
            </div>
          ) : (
            <div className="no-pending-kudos-message">
              <Typography variant="body2">
                There are currently no pending kudos
              </Typography>
            </div>
          )}
        </TabPanel>
        <TabPanel value="SENT" style={{ padding: "1rem 0" }}>
          {sentKudosLoading ? (
            Array.from({ length: 5 }).map((_, index) => (
              <SkeletonLoader key={index} type="kudos" />
            ))
          ) : sentKudos.length > 0 ? (
            <div>
              {sentKudos.map((k) => (
                <KudosCard key={k.id} kudos={k} includeEdit
                  onKudosAction={loadSentKudos}/>
              ))}
            </div>
          ) : (
            <div className="no-sent-kudos-message">
              <Typography variant="body2">
                There are currently no sent kudos
              </Typography>
            </div>
          )}
        </TabPanel>
      </TabContext>
    </Root>
  );
};

export default KudosPage;
