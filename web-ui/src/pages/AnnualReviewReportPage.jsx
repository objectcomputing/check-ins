import React, { useContext, useState, useCallback, useEffect } from "react";

import { AppContext } from "../context/AppContext";
import AnnualReviewReport from "../components/annual-review-report/AnnualReviewReport";
import {
  selectCurrentMembers,
  selectCurrentUserId,
} from "../context/selectors";
import { TextField } from "@mui/material";
import Autocomplete from '@mui/material/Autocomplete';

import "./AnnualReviewReportPage.css";

const AnnualReviewReportPage = () => {
  const { state } = useContext(AppContext);
  const users = selectCurrentMembers(state);
  const currentUserId = selectCurrentUserId(state);
  const [filteredUsers, setFilteredUsers] = useState([]);
  const [selectedUser, setSelectedUser] = useState(null);
  const [peer, setPeer] = useState(false);

  useEffect(() => {
    if(users && users.length > 0) {
      const filtered = users.filter((user) => user.id !== currentUserId);
      setFilteredUsers(filtered);
    } else {
      setFilteredUsers([]);
    }
  }, [users, currentUserId]);

  const onUserChange = useCallback((event, newValue) => {
    setSelectedUser(newValue);
  }, [setSelectedUser]);

  const handlePeer = useCallback(() => {
    setPeer(!peer);
  }, [setPeer, peer]);

  return (
    <div>
      <div className="filter-users">
        <Autocomplete
          id="userSelect"
          disablePortal
          fullWidth
          options={filteredUsers}
          onChange={onUserChange}
          getOptionLabel={(option) => option.name}
          renderInput={(params) => (
            <TextField
              {...params}
              label="Select User..."
              placeholder="Member Name"
            />
          )}
        />
      </div>
      <div className="checkbox-row">
        <label htmlFor="peer">Include peer feedback</label>
        <input id="peer" onClick={handlePeer} value={peer} type="checkbox" />
      </div>
      { selectedUser && (<AnnualReviewReport userId={selectedUser?.id} includePeer={peer} />) }
    </div>
  );
};

export default AnnualReviewReportPage;
