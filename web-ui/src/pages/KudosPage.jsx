import React, { useCallback, useContext, useEffect, useState } from "react";
import { useParams } from 'react-router-dom';
import { styled } from "@mui/material/styles";
import { Button, Tab, Typography } from "@mui/material";
import MenuItem from '@mui/material/MenuItem';
import FormControl from '@mui/material/FormControl';
import TextField from '@mui/material/TextField';
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { AppContext } from "../context/AppContext";
import {
  selectCsrfToken,
  selectCurrentUser,
  selectHasCreateKudosPermission,
} from "../context/selectors";
import { getReceivedKudos, getSentKudos, getAllKudos } from "../api/kudos";
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

const validTabName = (name) => {
  switch (name) {
    case "received":
    case "sent":
    case "public":
      break;
    default:
      name && console.warn(`Invalid tab: ${name}`);
      name = "received";
  }
  return name;
}

const DateRange = {
  THREE_MONTHS: '3mo',
  SIX_MONTHS: '6mo',
  ONE_YEAR: '1yr',
  ALL_TIME: 'all'
};

const KudosPage = () => {
  const { initialTab } = useParams();
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const currentUser = selectCurrentUser(state);

  const [kudosDialogOpen, setKudosDialogOpen] = useState(false);
  const [kudosTab, setKudosTab] = useState(validTabName(initialTab));
  const [receivedKudos, setReceivedKudos] = useState([]);
  const [receivedKudosLoading, setReceivedKudosLoading] = useState(true);
  const [sentKudos, setSentKudos] = useState([]);
  const [sentKudosLoading, setSentKudosLoading] = useState(true);
  const [publicKudos, setPublicKudos] = useState([]);
  const [publicKudosLoading, setPublicKudosLoading] = useState(true);
  const [dateRange, setDateRange] = useState(DateRange.THREE_MONTHS);

  const isInRange = (requestDate) => {
    const oldestDate = new Date();
    switch (dateRange) {
      case DateRange.SIX_MONTHS:
        oldestDate.setMonth(oldestDate.getMonth() - 6);
        break;
      case DateRange.ONE_YEAR:
        oldestDate.setFullYear(oldestDate.getFullYear() - 1);
        break;
      case DateRange.ALL_TIME:
        return true;
      case DateRange.THREE_MONTHS:
      default:
        oldestDate.setMonth(oldestDate.getMonth() - 3);
    }

    if (Array.isArray(requestDate)) {
      requestDate = new Date(requestDate.join('/'));
      // have to do for Safari
    }
    return requestDate >= oldestDate;
  };

  const loadReceivedKudos = useCallback(async () => {
    setReceivedKudosLoading(true);
    const res = await getReceivedKudos(currentUser.id, csrf);
    if (res?.payload?.data && !res.error) {
      setReceivedKudosLoading(false);
      return res.payload.data.filter((k) => isInRange(k.dateCreated));
    }
  }, [csrf, dispatch, currentUser.id, dateRange]);

  const loadSentKudos = useCallback(async () => {
    setSentKudosLoading(true);
    const res = await getSentKudos(currentUser.id, csrf);
    if (res?.payload?.data && !res.error) {
      setSentKudosLoading(false);
      return res.payload.data.filter((k) => isInRange(k.dateCreated));
    }
  }, [csrf, dispatch, currentUser.id, dateRange]);

  const loadPublicKudos = useCallback(async () => {
    setPublicKudosLoading(true);
    const res = await getAllKudos(csrf);
    if (res?.payload?.data && !res.error) {
      setPublicKudosLoading(false);
      return res.payload.data.filter((k) => isInRange(k.dateCreated));
    }
  }, [csrf, dispatch, currentUser.id, dateRange]);

  const loadAndSetReceivedKudos = () => {
    loadReceivedKudos().then((data) => {
      if (data) {
        const filtered = data.filter((kudo) =>
          kudo.recipientMembers.some((member) => member.id === currentUser.id)
        );
        setReceivedKudos(filtered);
      }
    });
  };

  const loadAndSetSentKudos = () => {
    loadSentKudos().then((data) => {
      if (data) {
        setSentKudos(data.filter((kudo) => kudo.senderId === currentUser.id));
      }
    });
  };

  const loadAndSetPublicKudos = () => {
    loadPublicKudos().then((data) => {
      if (data) {
        setPublicKudos(data);
      }
    });
  };

  useEffect(() => {
    if (csrf && currentUser && currentUser.id) {
      loadAndSetReceivedKudos();
      loadAndSetSentKudos();
      loadAndSetPublicKudos();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [csrf, currentUser, kudosTab, dateRange]);

  const handleTabChange = useCallback(
    (event, newTab) => {
      setKudosTab(validTabName(newTab));
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
        <FormControl className={classes.textField}>
          <TextField
            id="select-time"
            select
            fullWidth
            size="small"
            label="Show kudos within"
            onChange={e => setDateRange(e.target.value)}
            value={dateRange}
            variant="outlined"
          >
            <MenuItem value={DateRange.THREE_MONTHS}>Past 3 months</MenuItem>
            <MenuItem value={DateRange.SIX_MONTHS}>Past 6 months</MenuItem>
            <MenuItem value={DateRange.ONE_YEAR}>Past year</MenuItem>
            <MenuItem value={DateRange.ALL_TIME}>All time</MenuItem>
          </TextField>
        </FormControl>
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
              value="received"
              icon={<ArchiveIcon />}
              iconPosition="start"
            />
            <Tab
              label="Sent"
              value="sent"
              icon={<UnarchiveIcon />}
              iconPosition="start"
            />
            <Tab
              label="Public"
              value="public"
              icon={<StarIcon />}
              iconPosition="start"
            />
          </TabList>
        </div>
        <TabPanel value="received" style={{ padding: "1rem 0" }}>
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
                  onKudosAction={loadAndSetReceivedKudos}
                />
              ))}
            </div>
          ) : (
            <div className="no-pending-kudos-message">
              <Typography variant="body2">
                There are currently no received kudos
              </Typography>
            </div>
          )}
        </TabPanel>
        <TabPanel value="sent" style={{ padding: "1rem 0" }}>
          {sentKudosLoading ? (
            Array.from({ length: 5 }).map((_, index) => (
              <SkeletonLoader key={index} type="kudos" />
            ))
          ) : sentKudos.length > 0 ? (
            <div>
              {sentKudos.map((k) => (
                <KudosCard key={k.id} kudos={k} includeEdit
                  onKudosAction={loadAndSetSentKudos}/>
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
        <TabPanel value="public" style={{ padding: "1rem 0" }}>
          {publicKudosLoading ? (
            Array.from({ length: 5 }).map((_, index) => (
              <SkeletonLoader key={index} type="kudos" />
            ))
          ) : publicKudos.length > 0
            ? (
            <div className="received-kudos-list">
              {publicKudos.map((k) => (
                <KudosCard
                  key={k.id}
                  kudos={k}
                />
              ))}
            </div>
          ) : (
            <div className="no-pending-kudos-message">
              <Typography variant="body2">
                There are currently no public kudos
              </Typography>
            </div>
          )}
        </TabPanel>
      </TabContext>
    </Root>
  );
};

export default KudosPage;
