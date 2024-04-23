import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";

import { Button, TextField } from "@mui/material";
import Autocomplete from "@mui/material/Autocomplete";

import "./BirthdayAnniversaryReportPage.css";

import { getAnniversaries, getBirthdays } from "../api/birthdayanniversary";
import { UPDATE_TOAST } from "../context/actions";
import SearchBirthdayAnniversaryResults from "../components/search-results/SearchBirthdayAnniversaryResults";
import { sortAnniversaries, sortBirthdays } from "../context/util";

import { selectCsrfToken } from "../context/selectors";

const months = [
  { month: "January" },
  { month: "February" },
  { month: "March" },
  { month: "April" },
  { month: "May" },
  { month: "June" },
  { month: "July" },
  { month: "August" },
  { month: "September" },
  { month: "October" },
  { month: "November" },
  { month: "December" },
];

const BirthdayAnniversaryReportPage = () => {
  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [anniversary, setAnniversary] = useState(true);
  const [birthday, setBirthday] = useState(true);
  const [searchBirthdayResults, setSearchBirthdayResults] = useState([]);
  const [searchAnniversaryResults, setSearchAnniversaryResults] = useState([]);
  const currentMonth = new Date().getMonth();
  const [selectedMonths, setSelectedMonths] = useState([months[currentMonth]]);
  const [hasSearched, setHasSearched] = useState(false);

  const handleBirthday = () => {
    setBirthday(!birthday);
  };

  const handleAnniversary = () => {
    setAnniversary(!anniversary);
  };

  const handleSearch = async (monthsToSearch) => {
    let anniversaryResults;
    let birthdayResults;
    const months = monthsToSearch.map((m) => m.month);
    if (!birthday && !anniversary) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: "error",
          toast: "You must select one of the below check boxes",
        },
      });
      return;
    }
    if (!birthday) {
      anniversaryResults = await getAnniversaries(months, csrf);
      setSearchAnniversaryResults(sortAnniversaries(anniversaryResults));
      setSearchBirthdayResults([]);
    } else if (!anniversary) {
      birthdayResults = await getBirthdays(months, csrf);
      setSearchBirthdayResults(sortBirthdays(birthdayResults));
      setSearchAnniversaryResults([]);
    } else {
      anniversaryResults = await getAnniversaries(months, csrf);
      birthdayResults = await getBirthdays(months, csrf);
      setSearchBirthdayResults(sortBirthdays(birthdayResults));
      setSearchAnniversaryResults(sortAnniversaries(anniversaryResults));
    }
    setHasSearched(true);
  };

  function onMonthChange(event, newValue) {
    setSelectedMonths(newValue);
  }

  return (
    <div>
      <div className="select-month">
        <Autocomplete
          multiple
          id="monthSelect"
          options={months}
          defaultValue={[months[currentMonth].month]}
          value={selectedMonths}
          onChange={onMonthChange}
          isOptionEqualToValue={(option, value) => {
            return value ? value.month === option.month : false;
          }}
          getOptionLabel={(option) => option.month}
          renderInput={(params) => (
            <TextField
              {...params}
              className="fullWidth"
              label="Select a month"
              placeholder="Choose a month"
            />
          )}
        />
      </div>
      <div className="birthday-anniversary-search halfWidth">
        <Button
          onClick={() => {
            if (!selectedMonths) {
              window.snackDispatch({
                type: UPDATE_TOAST,
                payload: {
                  severity: "error",
                  toast: "Must select a month",
                },
              });
              return;
            }
            handleSearch(selectedMonths);
          }}
          color="primary"
        >
          Run Search
        </Button>
      </div>
      <div className="checkbox-row">
        <label htmlFor="birthday">Include Birthdays</label>
        <input
          id="birthday"
          checked={birthday}
          onChange={handleBirthday}
          type="checkbox"
        />
        <label htmlFor="anniversary">Include Anniversaries</label>
        <input
          id="anniversary"
          checked={anniversary}
          onChange={handleAnniversary}
          type="checkbox"
        />
      </div>
      <div>
        {
          <div className="search-results">
            <SearchBirthdayAnniversaryResults
              hasSearched={hasSearched}
              birthday={birthday}
              anniversary={anniversary}
              searchBirthdayResults={searchBirthdayResults}
              searchAnniversaryResults={searchAnniversaryResults}
            />
          </div>
        }
      </div>
    </div>
  );
};

export default BirthdayAnniversaryReportPage;
