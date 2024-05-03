import React, { useContext, useState } from 'react';

import { AppContext } from '../context/AppContext';

import { Button, TextField } from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';

import './BirthdayAnniversaryReportPage.css';

import { getBirthdays } from '../api/birthdayanniversary';
import { UPDATE_TOAST } from '../context/actions';
import SearchBirthdayAnniversaryResults from '../components/search-results/SearchBirthdayAnniversaryResults';
import { sortBirthdays } from '../context/util';

import { selectCsrfToken } from '../context/selectors';
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
    const birthdayResults = await getBirthdays(monthsToSearch, csrf);
    setSearchBirthdayResults(sortBirthdays(birthdayResults));
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
        />
      </div>
      <div className="birthday-anniversary-search halfWidth">
        <Button
          onClick={() => {
            if (!selectedMonths) {
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
  );
};

export default BirthdayReportPage;
