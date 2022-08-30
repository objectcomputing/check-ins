import React, {useContext, useEffect, useState} from "react";
import {styled} from "@mui/material/styles";
import {Typography} from "@mui/material";
import {getAllKudos} from "../api/kudos";
import {AppContext} from "../context/AppContext";
import {selectCsrfToken} from "../context/selectors";
import {UPDATE_TOAST} from "../context/actions";
import KudosCard from "../components/kudos_card/KudosCard";

import "./ManageKudosPage.css";

const Root = styled("div")({});

const ManageKudosPage = () => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [kudos, setKudos] = useState([]);

  useEffect(() => {
    const loadKudos = async () => {
      const res = await getAllKudos(csrf);
      if (res?.payload?.data && !res.error) {
        return res.payload.data;
      } else {
        dispatch({
          type: UPDATE_TOAST,
          payload: {
            severity: "error",
            toast: "Failed to retrieve kudos"
          }
        });
      }
    };

    loadKudos().then(data => {
      if (data) {
        setKudos(data);
      }
    });
  }, [csrf, dispatch]);

  return (
    <Root className="manage-kudos-page">
      <div className="manage-kudos-page-header">
        <Typography fontWeight="bold" variant="h4">Manage Kudos</Typography>
      </div>
      <div className="manage-kudos-list">
        {kudos.map(k =>
          <KudosCard key={k.id} kudos={k} type="MANAGE"/>
        )}
      </div>
    </Root>
  );

};

export default ManageKudosPage;