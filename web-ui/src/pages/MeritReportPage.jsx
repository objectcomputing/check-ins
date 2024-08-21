import React, { useContext, useRef, useState, useEffect } from 'react';

import { Autocomplete, Button, TextField } from '@mui/material';

import { uploadData, downloadData } from '../api/generic';
import { getReviewPeriods } from '../api/reviewperiods';
import { UPDATE_TOAST } from '../context/actions';
import { AppContext } from '../context/AppContext';
import {
  selectCsrfToken,
  selectOrderedMemberFirstName,
} from '../context/selectors';

import './MeritReportPage.css';
import MemberSelector from '../components/member_selector/MemberSelector';
import { useQueryParameters } from '../helpers/query-parameters';

const MeritReportPage = () => {
  const { state, dispatch } = useContext(AppContext);

  const csrf = selectCsrfToken(state);
  const memberProfiles = selectOrderedMemberFirstName(state);

  const [selectedMembers, setSelectedMembers] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [allSearchResults, setAllSearchResults] = useState([]);
  const [editedSearchRequest, setEditedSearchRequest] = useState([]);
  const [selectedFile, setSelectedFile] = useState(null);
  const [reviewPeriodId, setReviewPeriodId] = useState([]);
  const [reviewPeriods, setReviewPeriods] = useState([]);

  const processedQPs = useRef(false);
  useQueryParameters(
    [
      {
        name: 'members',
        default: [],
        value: selectedMembers,
        setter(ids) {
          const selectedMembers = ids.map(id =>
            memberProfiles.find(member => member.id === id)
          );
          setSelectedMembers(selectedMembers);
        },
        toQP() {
          return selectedMembers.map(member => member.id).join(',');
        }
      }
    ],
    [memberProfiles],
    processedQPs
  );

  const selectedMembersCopy = selectedMembers.map(member => ({ ...member }));
  let searchResultsCopy = searchResults.map(result => ({ ...result }));
  const filteredResults = searchResultsCopy.filter(result => {
    return selectedMembersCopy.some(member => {
      return result.name === member.name;
    });
  });

  useEffect(() => {
    const getAllReviewPeriods = async () => {
      const res = await getReviewPeriods(csrf);
      const data =
        res &&
        res.payload &&
        res.payload.data &&
        res.payload.status === 200 &&
        !res.error
          ? res.payload.data
          : null;
      if (data) {
        let periods = data.reduce((result, item) => {
                         if (item.closeDate) {
                           result.push({label: item.closeDate, id: item.id});
                         }
                         return result;
                       }, []);
        setReviewPeriods(periods);
      }
    };
    if (csrf) {
      getAllReviewPeriods();
    }
  }, [csrf, dispatch]);


  const onFileSelected = e => {
    setSelectedFile(e.target.files[0]);
  };

  const upload = async file => {
    if (!file) {
      return;
    }
    let formData = new FormData();
    formData.append('file', file);
    let res = await uploadData("/services/report/data/upload",
                               csrf, formData);
    if (res?.error) {
      let error = res?.error?.response?.data?.message;
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: error
        }
      });
    }
    const data = res?.payload?.data;
    if (data) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: `File was successfully uploaded`
        }
      });
    }
  };

  const download = async () => {
    let selected = selectedMembers.reduce((result, item) => {
                     result.push(item.id);
                     return result;
                   }, []);

    let res = await downloadData("/services/report/data",
                                 csrf, {memberIds: selected,
                                        reviewPeriodId: reviewPeriodId.id});
    if (res?.error) {
      let error = res?.error?.response?.data?.message;
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: error
        }
      });
    }
    const data = res?.payload?.data;
    if (data) {
      console.log(data);
    }
  };

  const onReviewPeriodChange = (event, newValue) => {
    setReviewPeriodId(newValue);
  };

  return (
    <div className="merit-report-page">
      <Button color="primary">
        <label htmlFor="file-upload">
          <h3>Choose A CSV File</h3>
          <input
            accept=".csv"
            id="file-upload"
            onChange={onFileSelected}
            style={{ display: 'none' }}
            type="file"
          />
        </label>
      </Button>

      <div className="buttons">
        {selectedFile && (
          <Button
            color="primary"
            onClick={() => upload(selectedFile)}
          >
            Upload &nbsp;<strong>{selectedFile.name}</strong>
          </Button>
        )}
      </div>
      <Autocomplete
        id="reviewPeriodSelect"
        options={reviewPeriods ? reviewPeriods : []}
        value={reviewPeriodId}
        onChange={onReviewPeriodChange}
        renderInput={params => (
          <TextField
            {...params}
            className="fullWidth"
            label="ReviewPeriod"
            placeholder="Choose review period"
          />
        )}
      />
      <MemberSelector
        className="merit-member-selector"
        onChange={setSelectedMembers}
        selected={selectedMembers}
      />
      <Button color="primary"
              onClick={download}>
        <label htmlFor="download">
          <h3>Generate Report</h3>
        </label>
      </Button>
    </div>
  );
};

export default MeritReportPage;
