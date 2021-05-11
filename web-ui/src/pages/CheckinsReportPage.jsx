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
  const [planned, setPlanned] = useState(false);
  const [closed, setClosed] = useState(false);

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

  const handleClosed = () => {
    setClosed(!closed);
  };

  const handlePlanned = () => {
    setPlanned(!planned);
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
      <div className="checkbox-row">
        <label htmlFor="closed">Show Closed</label>
        <input id="closed" onClick={handleClosed} type="checkbox" />
        <label htmlFor="planned">Show Planned</label>
        <input id="planned" onClick={handlePlanned} type="checkbox" />
      </div>
      {selectedPdls.length
        ? selectedPdls.map((pdl) => (
            <CheckinReport
              closed={closed}
              key={pdl.id}
              pdl={pdl}
              planned={planned}
            />
          ))
        : filteredPdls.map((pdl) => (
            <CheckinReport
              closed={closed}
              key={pdl.id}
              pdl={pdl}
              planned={planned}
            />
          ))}
    </div>
  );
};

export default CheckinsReportPage;
