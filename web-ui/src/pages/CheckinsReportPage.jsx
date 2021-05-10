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
  const pdls = selectCheckinPDLS(state).sort((a, b) => {
    const aPieces = a.name.split(" ").slice(-1);
    const bPieces = b.name.split(" ").slice(-1);
    return aPieces.toString().localeCompare(bPieces);
  });
  const [filteredPdls, setFilteredPdls] = useState(pdls);
  const [selectedPdls, setSelectedPdls] = useState([]);

  useEffect(() => {
    if (!pdls) return;
    pdls.map(
      (pdl) => (pdl.members = selectTeamMembersWithCheckinPDL(state, pdl.id))
    );
    setFilteredPdls(pdls);
  }, [pdls, filteredPdls, state]);

  const onPdlChange = (event, newValue) => {
    let extantPdls = filteredPdls || [];
    newValue.forEach((val) => {
      extantPdls = extantPdls.filter((pdl) => pdl.id !== val.id);
    });
    extantPdls = [...new Set(extantPdls)];
    newValue = [...new Set(newValue)];
    setFilteredPdls([...newValue]);
    setSelectedPdls(newValue);
  };

  return (
    <div>
      <div>
        <Autocomplete
          id="pdlSelect"
          multiple
          options={pdls}
          value={selectedPdls || []}
          onChange={onPdlChange}
          getOptionLabel={(option) => option.name}
          renderInput={(params) => (
            <TextField
              {...params}
              label="Select PDLs"
              placeholder="Choose which PDLs to display"
            />
          )}
        />
      </div>
      {selectedPdls.length
        ? selectedPdls.map((pdl) => <CheckinReport key={pdl.id} pdl={pdl} />)
        : filteredPdls.map((pdl) => <CheckinReport key={pdl.id} pdl={pdl} />)}
    </div>
  );
};

export default CheckinsReportPage;
