import React, {useCallback, useContext, useEffect, useState} from "react";
import {styled} from "@mui/material/styles";
import {MenuItem, Tab, TextField, Typography} from "@mui/material";
import {TabContext, TabList, TabPanel} from "@mui/lab";
import {getAllKudos} from "../api/kudos";
import {AppContext} from "../context/AppContext";
import {
  selectCsrfToken,
  selectHasAdministerKudosPermission,
  noPermission,
} from "../context/selectors";
import {UPDATE_TOAST} from "../context/actions";
import KudosCard from "../components/kudos_card/KudosCard";
import SkeletonLoader from "../components/skeleton_loader/SkeletonLoader";
import PendingIcon from "@mui/icons-material/PendingActions";
import ThumbUpIcon from "@mui/icons-material/ThumbUp";

import "./ManageKudosPage.css";

const DateRange = {
  ONE_WEEK: "1wk",
  TWO_WEEKS: "2wk",
  ONE_MONTH: "1mo",
  ONE_YEAR: "1yr",
  ALL_TIME: "all"
};

const SortOption = {
  NEWEST: "newest",
  OLDEST: "oldest"
};

const PREFIX = 'ManageKudosPage';
const classes = {
  kudos: `${PREFIX}-kudos`,
  search: `${PREFIX}-search`,
  searchInput: `${PREFIX}-searchInput`
};

const Root = styled("div")({
  [`& .${classes.search}`]: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
  },
  [`& .${classes.searchInput}`]: {
    width: "20em",
  },
  [`& .${classes.kudos}`]: {
    display: "flex",
    flexWrap: "wrap",
    justifyContent: "space-evenly",
    width: "100%",
  },});

const ManageKudosPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [pendingKudos, setPendingKudos] = useState([]);
  const [approvedKudos, setApprovedKudos] = useState([]);
  const [pendingKudosLoading, setPendingKudosLoading] = useState(true);
  const [approvedKudosLoading, setApprovedKudosLoading] = useState(true);
  const [kudosTab, setKudosTab] = useState("PENDING");
  const [timeRange, setTimeRange] = useState(DateRange.TWO_WEEKS);
  const [pendingSort, setPendingSort] = useState(SortOption.OLDEST);

  const sortPendingKudos = (pending) => {
    return pending.sort((a, b) => {
      const l = pendingSort === SortOption.NEWEST ? a : b;
      const r = pendingSort === SortOption.NEWEST ? b : a;
      for(let i = 0; i < l.dateCreated.length; i++) {
        if (l.dateCreated[i] != r.dateCreated[i]) {
          return r.dateCreated[i] - l.dateCreated[i];
        }
      }
      return 0;
    });
  };

  const loadPendingKudos = useCallback(async () => {
    if (selectHasAdministerKudosPermission(state)) {
      setPendingKudosLoading(true);
      const res = await getAllKudos(csrf, true);
      if (res?.payload?.data && !res.error) {
        setPendingKudosLoading(false);
        return res.payload.data;
      }
    }
  }, [csrf, dispatch]);

  const loadApprovedKudos = useCallback(async () => {
    if (selectHasAdministerKudosPermission(state)) {
      setApprovedKudosLoading(true);
      const res = await getAllKudos(csrf, false);
      if (res?.payload?.data && !res.error) {
        setApprovedKudosLoading(false);
        return res.payload.data;
      }
    }
  }, [csrf, dispatch]);

  useEffect(() => {
    loadPendingKudos().then(data => {
      if (data) {
        setPendingKudos(sortPendingKudos(data));
      }
    });
  }, [csrf, dispatch, loadPendingKudos]);

  useEffect(() => {
    setPendingKudos(sortPendingKudos([...pendingKudos]));
  }, [pendingSort]);

  const loadAndSetPendingKudos = () => {
    loadPendingKudos().then(data => {
      if (data) {
        setPendingKudos(sortPendingKudos(data));
      }
    });
  };

  const loadAndSetApprovedKudos = () => {
    loadApprovedKudos().then(data => {
      if (data) {
        setApprovedKudos(data);
      }
    });
  };

  const handleTabChange = useCallback((event, newTab) => {
    switch (newTab) {
      case "PENDING":
        loadAndSetPendingKudos();
        break;
      case "APPROVED":
        loadAndSetApprovedKudos();
        break;
      default:
        console.warn(`Invalid tab: ${newTab}`);
    }

    setKudosTab(newTab);
  }, [loadPendingKudos, loadApprovedKudos]);

  const filterApprovedKudos = (kudos) => {
    if (!kudos.dateApproved) {
      return false;
    }

    const now = new Date();
    const approved = new Date(kudos.dateApproved[0],
                              kudos.dateApproved[1] - 1,
                              kudos.dateApproved[2]).getTime();
    switch(timeRange) {
      case DateRange.ONE_WEEK:
        return approved >= (new Date(now.getFullYear(), now.getMonth(),
                                     now.getDate() - 7).getTime());
      case DateRange.TWO_WEEKS:
        return approved >= (new Date(now.getFullYear(), now.getMonth(),
                                     now.getDate() - 14).getTime());
      case DateRange.ONE_MONTH:
        return approved >= (new Date(now.getFullYear(), now.getMonth() - 1,
                                     now.getDate()).getTime());
      case DateRange.ONE_YEAR:
        return approved >= (new Date(now.getFullYear() - 1, now.getMonth(),
                                     now.getDate()).getTime());
      case DateRange.ALL_TIME:
        return true;
    }
  };

  return selectHasAdministerKudosPermission(state) ? (
    <Root className="manage-kudos-page">
      <TabContext value={kudosTab}>
        <div className="kudos-tab-container">
          <TabList onChange={handleTabChange}>
            <Tab label="Pending" value="PENDING" icon={<PendingIcon/>} iconPosition="start"/>
            <Tab label="Approved" value="APPROVED" icon={<ThumbUpIcon/>} iconPosition="start"/>
          </TabList>
          {kudosTab === "APPROVED" &&
            <TextField
              select
              label="Time period"
              variant="outlined"
              value={timeRange}
              onChange={(event) => setTimeRange(event.target.value)}
            >
              <MenuItem value={DateRange.ONE_WEEK}>Past week</MenuItem>
              <MenuItem value={DateRange.TWO_WEEKS}>Past two weeks</MenuItem>
              <MenuItem value={DateRange.ONE_MONTH}>Past month</MenuItem>
              <MenuItem value={DateRange.ONE_YEAR}>Past year</MenuItem>
              <MenuItem value={DateRange.ALL_TIME}>All time</MenuItem>
            </TextField>
          }
          {kudosTab === "PENDING" &&
            <TextField
              select
              label="Sort by"
              variant="outlined"
              value={pendingSort}
              onChange={(event) => setPendingSort(event.target.value)}
            >
              <MenuItem value={SortOption.NEWEST}>Newest</MenuItem>
              <MenuItem value={SortOption.OLDEST}>Oldest</MenuItem>
            </TextField>
          }
        </div>
        <TabPanel value="PENDING" style={{ padding: "1rem 0" }}>
          {pendingKudosLoading
            ? Array.from({length: 5}).map((_, index) => <SkeletonLoader key={index} type="kudos"/>)
            : (
              pendingKudos.length > 0
              ? <div className="manage-kudos-list">
                  {pendingKudos.map(k =>
                    <KudosCard
                      key={k.id}
                      kudos={k}
                      includeActions
                      includeEdit
                      onKudosAction={loadAndSetPendingKudos}
                    />
                  )}
              </div>
              : <div className="no-pending-kudos-message">
                  <Typography variant="body2">There are currently no pending kudos</Typography>
              </div>
            )
          }
        </TabPanel>
        <TabPanel value="APPROVED" style={{ padding: "1rem 0" }}>
          {approvedKudosLoading
            ? Array.from({length: 5}).map((_, index) => <SkeletonLoader key={index} type="kudos"/>)
            : (
              <div>
                {approvedKudos.filter(filterApprovedKudos).map(k =>
                  <KudosCard
                    key={k.id}
                    kudos={k}
                    includeEdit
                    onKudosAction={loadAndSetApprovedKudos}
                  />
                )}
              </div>
            )
          }

        </TabPanel>
      </TabContext>
    </Root>
  ) : (
    <h3>{noPermission}</h3>
  );
};

export default ManageKudosPage;
