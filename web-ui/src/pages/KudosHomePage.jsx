import React, { useContext, useEffect, useState } from "react";

import { AppContext } from "../context/AppContext";
import { selectCsrfToken } from "../context/selectors";
import { UPDATE_TOAST } from "../context/actions";

import { getAllKudos } from "../api/kudos";

import KudosCard from "../components/kudos_card/KudosCard";
import SkeletonLoader from "../components/skeleton_loader/SkeletonLoader";

import { styled } from "@mui/material/styles";
import { TabPanel } from "@mui/lab";
import { Typography } from "@mui/material";

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
  const [kudsoLoading, setKudosLoading] = useState(false);

  useEffect(async () => {
    setKudosLoading(true);
    const res = await getAllKudos(csrf, false);
    if (res?.payload?.data && !res.error) {
      setKudos(res.payload.data);
      setKudosLoading(false);
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "Failed to retrieve kudos",
        },
      });
    }
  }, [csrf, dispatch]);

  return (
    <Root className="kudos-page">
      <h1>Kudos</h1>
      {
        // kudsoLoading ? (
        //   Array.from({ length: 5 }).map((_, index) => (
        //     <SkeletonLoader key={index} type="kudos" />
        //   ))
        // ) :
        kudos?.length > 0 ? (
          <div className="kudos-list">
            {kudos.map((k) => (
              <KudosCard
                key={k.id}
                kudos={k}
                // onKudosAction={() => {
                //   const updatedKudos = kudos.filter((pk) => pk.id !== k.id);
                //   setkudos(updatedKudos);
                // }}
              />
            ))}
          </div>
        ) : (
          <div className="no-kudos-message">
            <Typography variant="body2">
              There are currently no kudos
            </Typography>
          </div>
        )
      }
    </Root>
  );
};

export default KudosHomePage;
