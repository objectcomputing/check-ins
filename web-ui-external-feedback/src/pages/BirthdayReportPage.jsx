import React, { useContext, useState } from 'react';

import { AppContext } from '../context/AppContext';

import { FormControlLabel, Switch, Button, TextField } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';

import './BirthdayAnniversaryReportPage.css';

import { getBirthdays } from '../api/birthdayanniversary';
import { UPDATE_TOAST } from '../context/actions';
import SearchBirthdayAnniversaryResults from '../components/search-results/SearchBirthdayAnniversaryResults';
import { sortBirthdays } from '../context/util';
import SkeletonLoader from '../components/skeleton_loader/SkeletonLoader';

import {
  selectCsrfToken,
  selectHasBirthdayReportPermission,
  noPermission,
} from '../context/selectors';
import { useQueryParameters } from '../helpers/query-parameters';

const months = [
  'January',
  'February',
  'March',
  'April',
  'May',
  'June',
  'July',
  'August',
  'September',
  'October',
  'November',
  'December'
];

const BirthdayReportPage = () => {
  const currentMonth = new Date().getMonth();
  const defaultMonths = [months[currentMonth]];

  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);
  const [searchBirthdayResults, setSearchBirthdayResults] = useState([]);
  const [selectedMonths, setSelectedMonths] = useState(defaultMonths);
  const [hasSearched, setHasSearched] = useState(false);
  const [loading, setLoading] = useState(false);
  const [noBirthday, setNoBirthday] = useState(false);

  useQueryParameters([
    {
      name: 'months',
      default: defaultMonths,
      value: selectedMonths,
      setter: setSelectedMonths,
      toQP(value) {
        return value ? value.join(',') : [];
      }
    }
  ]);

  const handleSearch = async monthsToSearch => {
    setLoading(true);
    try {
      const birthdayResults = await getBirthdays(noBirthday ? null :
                                                   monthsToSearch, csrf);
      setSearchBirthdayResults(sortBirthdays(birthdayResults));
      setHasSearched(true);
    } catch(e) {
      window.snackDispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: e,
        }
      });
    }
    setLoading(false);
  };

  function onMonthChange(event, newValue) {
    setSelectedMonths(newValue);
  }

  return selectHasBirthdayReportPermission(state) ? (
    <div>
      <div className="select-month">
        <Autocomplete
          multiple
          id="monthSelect"
          options={months}
          defaultValue={defaultMonths}
          value={selectedMonths}
          onChange={onMonthChange}
          isOptionEqualToValue={(option, value) => {
            return value ? value === option : false;
          }}
          renderInput={params => (
            <TextField
              {...params}
              className="fullWidth"
              label="Select a month"
              placeholder="Choose a month"
            />
          )}
          disabled={noBirthday}
        />
        <FormControlLabel
          control={
            <Switch
              checked={noBirthday}
              onChange={event => {
                const { checked } = event.target;
                setNoBirthday(checked);
              }}
            />
          }
          label="No Birthday Registered"
        />
      </div>
      <div className="birthday-anniversary-search halfWidth">
        <Button
          onClick={() => {
            if (!noBirthday && selectedMonths.length == 0) {
              window.snackDispatch({
                type: UPDATE_TOAST,
                payload: {
                  severity: 'error',
                  toast: 'Must select a month'
                }
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
      <div>
        {
          loading ?
          Array.from({ length: 10 }).map((_, index) => (
                        <SkeletonLoader key={index} type="feedback_requests" />
                     )) :
          <div className="search-results">
            <SearchBirthdayAnniversaryResults
              hasSearched={hasSearched}
              birthday
              results={searchBirthdayResults}
            />
          </div>
        }
      </div>
    </div>
  ) : (
    <h3>{noPermission}</h3>
  );
};

export default BirthdayReportPage;
