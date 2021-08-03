import React, { useContext, useState } from "react";

import { AppContext } from "../context/AppContext";

import { Button, TextField } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";

import "./BirthdayAnniversaryReportPage.css";

import { getAnniversary, getBirthday } from "../api/birthdayanniversary.js";
import { UPDATE_TOAST } from "../context/actions";
import SearchBirthdayAnniversaryResults from "../components/search-results/SearchBirthdayAnniversaryResults";

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
  const [selectedMonth, setSelectedMonth] = useState(months[currentMonth]);
  const [hasSearched, setHasSearched] = useState(false);

  const handleBirthday = () => {
    setBirthday(!birthday);
  };

  const handleAnniversary = () => {
    setAnniversary(!anniversary);
  };

  const handleSearch = async (monthToSearch) => {
    let anniversaryResults;
    let birthdayResults;
    const { month } = monthToSearch;
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
      anniversaryResults = await getAnniversary(month, csrf);
      setSearchAnniversaryResults(anniversaryResults.payload.data);
      setSearchBirthdayResults([]);
    } else if (!anniversary) {
      birthdayResults = await getBirthday(month, csrf);
      setSearchBirthdayResults(birthdayResults.payload.data);
      setSearchAnniversaryResults([]);
    } else {
      anniversaryResults = await getAnniversary(month, csrf);
      birthdayResults = await getBirthday(month, csrf);
      setSearchBirthdayResults(birthdayResults.payload.data);
      setSearchAnniversaryResults(anniversaryResults.payload.data);
    }
    setHasSearched(true);
  };

  function onMonthChange(event, newValue) {
    setSelectedMonth(newValue !== null ? newValue : months[currentMonth].month);
  }

  return (
    <div>
      <div className="select-month">
        <Autocomplete
          id="monthSelect"
          options={months}
          value={selectedMonth ? selectedMonth : months[currentMonth].month}
          onChange={onMonthChange}
          getOptionSelected={(option, value) =>
            value ? value.id === option.id : false
          }
          getOptionLabel={(option) =>
            option.month || months[currentMonth].month
          }
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
            if (!selectedMonth) {
              window.snackDispatch({
                type: UPDATE_TOAST,
                payload: {
                  severity: "error",
                  toast: "Must select a month",
                },
              });
              return;
            }
            handleSearch(selectedMonth);
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
          onClick={handleBirthday}
          type="checkbox"
        />
        <label htmlFor="anniversary">Include Anniversaries</label>
        <input
          id="anniversary"
          checked={anniversary}
          onClick={handleAnniversary}
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
