import React, { useCallback, useContext, useEffect, useState } from "react";
import { styled } from "@mui/material/styles";
import { MenuItem, Tab, TextField, Typography } from "@mui/material";
import { TabContext, TabList, TabPanel } from "@mui/lab";
import { getAllKudos } from "../api/kudos";
import { AppContext } from "../context/AppContext";
import { selectCsrfToken } from "../context/selectors";
import { UPDATE_TOAST } from "../context/actions";
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
  ALL_TIME: "all",
};

const SortOption = {
  NEWEST: "newest",
  OLDEST: "oldest",
};

const PREFIX = "ManageKudosPage";
const classes = {
  kudos: `${PREFIX}-kudos`,
  search: `${PREFIX}-search`,
  searchInput: `${PREFIX}-searchInput`,
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
  },
});

const ManageKudosPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [pendingKudos, setPendingKudos] = useState([]);
  const [approvedKudos, setApprovedKudos] = useState([]);
  const [pendingKudosLoading, setPendingKudosLoading] = useState(true);
  const [approvedKudosLoading, setApprovedKudosLoading] = useState(true);
  const [kudosTab, setKudosTab] = useState("PENDING");
  const [timeRange, setTimeRange] = useState(DateRange.TWO_WEEKS);

  const loadPendingKudos = useCallback(async () => {
    setPendingKudosLoading(true);
    const res = await getAllKudos(csrf, true);
    console.log({ res });
    if (res?.payload?.data && !res.error) {
      setPendingKudosLoading(false);
      return res.payload.data;
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to retrieve pending kudos",
        },
      });
    }
  }, [csrf, dispatch]);

  const loadApprovedKudos = useCallback(async () => {
    setApprovedKudosLoading(true);
    const res = await getAllKudos(csrf, false);
    if (res?.payload?.data && !res.error) {
      setApprovedKudosLoading(false);
      return res.payload.data;
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to retrieve approved kudos",
        },
      });
    }
  }, [csrf, dispatch]);

  useEffect(() => {
    loadPendingKudos().then((data) => {
      if (data) {
        setPendingKudos(data);
      }
    });
  }, [csrf, dispatch, loadPendingKudos]);

  const handleTabChange = useCallback(
    (event, newTab) => {
      switch (newTab) {
        case "PENDING":
          loadPendingKudos().then((data) => {
            if (data) {
              setPendingKudos(data);
            }
          });
          break;
        case "APPROVED":
          loadApprovedKudos().then((data) => {
            if (data) {
              setApprovedKudos(data);
            }
          });
          break;
        default:
          console.warn(`Invalid tab: ${newTab}`);
      }

      setKudosTab(newTab);
    },
    [loadPendingKudos, loadApprovedKudos]
  );

  return (
    <Root className="manage-kudos-page">
      <div className="manage-kudos-page-header">
        <Typography fontWeight="bold" variant="h4">
          Manage Kudos
        </Typography>
      </div>
      <TabContext value={kudosTab}>
        <div className="kudos-tab-container">
          <TabList onChange={handleTabChange}>
            <Tab
              label="Pending"
              value="PENDING"
              icon={<PendingIcon />}
              iconPosition="start"
            />
            <Tab
              label="Approved"
              value="APPROVED"
              icon={<ThumbUpIcon />}
              iconPosition="start"
            />
          </TabList>
          {kudosTab === "APPROVED" && (
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
          )}
          {kudosTab === "PENDING" && (
            <TextField select label="Sort by" variant="outlined">
              <MenuItem value={SortOption.NEWEST}>Newest</MenuItem>
              <MenuItem value={SortOption.OLDEST}>Oldest</MenuItem>
            </TextField>
          )}
        </div>
        <TabPanel value="PENDING" style={{ padding: "1rem 0" }}>
          {pendingKudosLoading ? (
            Array.from({ length: 5 }).map((_, index) => (
              <SkeletonLoader key={index} type="kudos" />
            ))
          ) : pendingKudos.length > 0 ? (
            <div className="manage-kudos-list">
              {pendingKudos.map((k) => (
                <KudosCard
                  key={k.id}
                  kudos={k}
                  includeActions
                  onKudosAction={() => {
                    const updatedKudos = pendingKudos.filter(
                      (pk) => pk.id !== k.id
                    );
                    setPendingKudos(updatedKudos);
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
        <TabPanel value="APPROVED" style={{ padding: "1rem 0" }}>
          {approvedKudosLoading ? (
            Array.from({ length: 5 }).map((_, index) => (
              <SkeletonLoader key={index} type="kudos" />
            ))
          ) : (
            <div>
              {approvedKudos.map((k) => (
                <KudosCard key={k.id} kudos={k} />
              ))}
            </div>
          )}
        </TabPanel>
      </TabContext>
    </Root>
  );
};

export default ManageKudosPage;
