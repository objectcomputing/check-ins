import React, { useContext, useEffect, useState } from "react";

import { AppContext } from "../context/AppContext";
import CheckinReport from "../components/Reports/CheckinReport";
import {
  selectCheckinPDLS,
  selectTeamMembersWithCheckinPDL,
} from "../context/selectors";

import { TextField } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";

import "./CheckinsReportPage.css";

const CheckinsReportPage = () => {
  const { state } = useContext(AppContext);
  const [searchText, setSearchText] = useState("");
  const pdls = selectCheckinPDLS(state).sort((a, b) => {
    const aPieces = a.name.split(" ").slice(-1);
    const bPieces = b.name.split(" ").slice(-1);
    return aPieces.toString().localeCompare(bPieces);
  });
  const [filteredPdls, setFilteredPdls] = useState(pdls);
  const [selectedPdls, setSelectedPdls] = useState([]);
  const [pdlOptions, setPdlOptions] = useState([]);

  useEffect(() => {
    if (!pdls) return;
    pdls.map(
      (pdl) => (pdl.members = selectTeamMembersWithCheckinPDL(state, pdl.id))
    );
    setFilteredPdls(pdls);
    if (!filteredPdls) return;
    let options = filteredPdls.map((pdl) => pdl.name);
    setPdlOptions(options);
  }, [pdls, filteredPdls]);

  console.log({ pdls, filteredPdls, selectedPdls });

  const onLeadsChange = (event, newValue) => {
    setFilteredPdls([...filteredPdls, newValue]);
    console.log({ event, newValue });
  };

  return (
    <div>
      <div>
        <Autocomplete
          id="pdlSelect"
          multiple
          options={pdlOptions}
          value={filteredPdls ? filteredPdls : []}
          onChange={onLeadsChange}
          getOptionLabel={(option) => option.name}
          renderInput={(params) => (
            <TextField {...params} label="PDLs" placeholder="Select PDLs" />
          )}
        />
      </div>
      {filteredPdls && filteredPdls.map((pdl) => <CheckinReport pdl={pdl} />)}
    </div>
  );
};

export default CheckinsReportPage;
