import React, { useCallback, useContext, useEffect, useState } from "react";

import { UPDATE_TOAST } from "../context/actions";
import { AppContext } from "../context/AppContext";
import { selectCsrfToken } from "../context/selectors";
import { sortKudos } from "../context/util";

import { getAllKudos } from "../api/kudos";

import KudosCard from "../components/kudos_card/KudosCard";
import KudosDialog from "../components/kudos_dialog/KudosDialog";
import SkeletonLoader from "../components/skeleton_loader/SkeletonLoader";

import StarIcon from "@mui/icons-material/Star";
import { Button, Grid, Typography } from "@mui/material";
import { styled } from "@mui/material/styles";

import "./KudosPage.css";

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

const KudosHomePage = () => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [kudos, setKudos] = useState(null);
  const [kudosDialogOpen, setKudosDialogOpen] = useState(false);
  const [kudosLoading, setKudosLoading] = useState(false);

  let lastMonth = new Date();
  lastMonth.setMonth(lastMonth.getMonth() - 1);

  const loadKudos = useCallback(async () => {
    setKudosLoading(true);
    const res = await getAllKudos(csrf, true);
    console.log('KudosHomePage.jsx loadKudos: res =', res);
    if (res.error) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to retrieve kudos",
        },
      });
      return;
    }

    const kudos = res.payload.data;
    console.log('KudosHomePage.jsx loadKudos: kudos =', kudos);
    setKudos(sortKudos(kudos));
    setKudosLoading(false);
  }, [csrf, dispatch]);

  useEffect(() => {
    loadKudos();
  }, [csrf, dispatch]);

  return (
    <Root className="kudos-page">
      <div className="kudos-title">
        <div>
          <h1>Kudos</h1>
          <KudosDialog
            open={kudosDialogOpen}
            onClose={() => setKudosDialogOpen(false)}
          />
          <Button
            className="kudos-dialog-open"
            startIcon={<StarIcon />}
            onClick={() => setKudosDialogOpen(true)}
          >
            Give Kudos
          </Button>
        </div>
      </div>
      <Grid container columns={6} spacing={3}>
        <Grid item className={classes.members}>
          {kudosLoading ? (
            <div className="kudos-list">
              {Array.from({ length: 5 }).map((_, index) => (
                <SkeletonLoader key={index} type="kudos" />
              ))}
            </div>
          ) : !kudosLoading && kudos?.length > 0 ? (
            <div className="kudos-list">
              {kudos.map((k) => {
                if (new Date(k.dateApproved) > lastMonth) {
                  return <KudosCard key={k.id} kudos={k} />;
                }
              })}
            </div>
          ) : (
            <div className="no-kudos-message">
              <Typography variant="body2">
                There are currently no kudos
              </Typography>
            </div>
          )}
        </Grid>
      </Grid>
    </Root>
  );
};

export default KudosHomePage;
