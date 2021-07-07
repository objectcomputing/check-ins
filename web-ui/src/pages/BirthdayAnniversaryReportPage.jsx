import React, { useContext, useEffect, useState } from "react";

import { AppContext } from "../context/AppContext";
import CheckinReport from "../components/reports-section/CheckinReport";
import {
  selectCheckinPDLS,
  selectTeamMembersWithCheckinPDL,
} from "../context/selectors";

import { Button, TextField } from "@material-ui/core";
import Autocomplete from "@material-ui/lab/Autocomplete";

import "./CheckinsReportPage.css";

import { getAnniversary, getBirthday } from "../api/birthdayanniversary.js";
import { UPDATE_TOAST } from "../context/actions";
import SearchBirthdayAnniversaryResults from "../components/search-results/SearchBirthdayAnniversaryResults";

 import {
   selectCsrfToken
 } from "../context/selectors";

const BirthdayAnniversaryReportPage = () => {
  const months = ['January', 'February', 'March','April','May','June',"July",'August','September','October','November','December'];
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [selectedPdls, setSelectedPdls] = useState([]);
  const [anniversary, setAnniversary] = useState(true);
  const [birthday, setBirthday] = useState(true);
  const [searchText, setSearchText] = useState("");
  const [searchBirthdayResults, setSearchBirthdayResults] = useState([]);
  const [searchAnniversaryResults, setSearchAnniversaryResults] = useState([]);
  const [searchMonth, setSearchMonth] = useState(null);
  const [allSearchResults, setAllSearchResults] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [showExistingTeam, setShowExistingTeam] = useState(false);
  const [showAdHocTeam, setShowAdHocTeam] = useState(true);
  const currentMonth = new Date().getMonth();
          console.log("month",currentMonth)
  const handleBirthday = () => {
    setBirthday(!birthday);
  };

  const handleAnniversary = () => {
    setAnniversary(!anniversary);
  };

    const handleSearch = async (searchMembers) => {
    let anniversaryResults;
    let birthdayResults;
    console.log(searchMembers);
    if(!birthday && !anniversary){
          console.log("loop0",birthday);
          window.snackDispatch({
            type: UPDATE_TOAST,
            payload: {
              severity: "error",
              toast: "You must select one of the below check boxes",
            },
          });
          return;
        }
    let res;
    if(!birthday) {
      console.log("loop1",birthday);
      console.log(anniversary);
      anniversaryResults = await getAnniversary(searchMembers, csrf);
      setSearchAnniversaryResults(anniversaryResults.payload.data);
      console.log(await getAnniversary(searchMembers, csrf));
      }
    else if(!anniversary)
      {
      console.log("loop2",birthday);
      console.log(anniversary);
      birthdayResults = await getBirthday(searchMembers, csrf);
      setSearchBirthdayResults(birthdayResults.payload.data);
      console.log(birthdayResults);
      }
    else {
     console.log("loop3",birthday);
     anniversaryResults = await getAnniversary(searchMembers, csrf);
     birthdayResults = await getBirthday(searchMembers, csrf);
     setSearchBirthdayResults(birthdayResults.payload.data);
     setSearchAnniversaryResults(anniversaryResults.payload.data);
     console.log(anniversary);
     console.log(anniversaryResults);
    }
    };

    function onMonthChange(event, newValue) {
      setSearchMonth(newValue);
    }

  return (
    <div>
      <div className="filter-pdls-and-members">
        <Autocomplete
          id="monthSelect"
          options={months}
          value={searchMonth ? searchMonth : [months[currentMonth]]}
          onChange={onMonthChange}
          getOptionSelected={(option, value) =>
            value ? value.id === option.id : false
          }
          getOptionLabel={(option) => option}
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
        <div className="skills-search halfWidth">
          <Button
            onClick={() => {
              if (!searchMonth) {
                window.snackDispatch({
                  type: UPDATE_TOAST,
                  payload: {
                    severity: "error",
                    toast: "Must select a month",
                  },
                });
                return;
              }
              handleSearch(searchMonth);
            }}
            color="primary"
          >
            Run Search
          </Button>
        </div>
      <div className="checkbox-row">
        <label htmlFor="birthday">Include Birthdays</label>
        <input id="birthday" checked={birthday} onClick={handleBirthday} type="checkbox" />
        <label htmlFor="anniversary">Include Anniversaries</label>
        <input id="anniversary" checked={anniversary} onClick={handleAnniversary} type="checkbox" />
      </div>
      <div>
        {(
          <div className="search-results">
            <h2>Birthdays and Anniversaries</h2>
              <SearchBirthdayAnniversaryResults searchBirthdayResults = {searchBirthdayResults} searchAnniversaryResults = {searchAnniversaryResults}/>
          </div>
        )}
      </div>
    </div>
  );
};

export default BirthdayAnniversaryReportPage;
