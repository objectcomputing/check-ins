import React, { useEffect, useContext, useState } from "react";

import GuildSummaryCard from "./GuildSummaryCard";
import { AppContext } from "../../context/AppContext";
import { UPDATE_GUILDS } from "../../context/actions";
import GuildsActions from "./GuildsActions";
import { getAllGuilds } from "../../api/guild";
import PropTypes from "prop-types";
import { TextField } from "@material-ui/core";
import { makeStyles } from "@material-ui/core/styles";
import "./GuildResults.css";

const useStyles = makeStyles((theme) => ({
  searchInput: {
    width: "20em",
  }
}));

const propTypes = {
  guilds: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.string,
      name: PropTypes.string,
      description: PropTypes.string,
    })
  ),
};

const displayName = "GuildResults";

const GuildResults = () => {
  const { state, dispatch } = useContext(AppContext);
  const { csrf, guilds } = state;
  const [searchText, setSearchText] = useState("");

  const classes = useStyles();

  useEffect(() => {
    async function getGuilds() {
      let res = await getAllGuilds(csrf);
      let data =
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data) {
        dispatch({ type: UPDATE_GUILDS, payload: data });
      }
    }
    if (csrf) {
      getGuilds();
    }
  }, [csrf, dispatch]);

  return (
    <div>
      <div className="guild-search">
        <TextField
          className={classes.searchInput}
          label="Search Guilds"
          placeholder="Guild Name"
          value={searchText}
          onChange={(e) => {
            setSearchText(e.target.value);
          }}
        />
        <GuildsActions />
      </div>
      <div className="guilds">
        {guilds.map((guild, index) =>
          guild.name.toLowerCase().includes(searchText.toLowerCase()) ? (
            <GuildSummaryCard
              key={`guild-summary-${guild.id}`}
              index={index}
              guild={guild}
            />
          ) : null
        )}
      </div>
    </div>
  );
};

GuildResults.propTypes = propTypes;
GuildResults.displayName = displayName;

export default GuildResults;