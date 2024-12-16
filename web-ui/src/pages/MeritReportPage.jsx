import React, { useContext, useRef, useState, useEffect } from 'react';

import { Autocomplete, Button, TextField } from '@mui/material';

import { uploadData, initiate } from '../api/generic';
import { getReviewPeriods } from '../api/reviewperiods';
import { UPDATE_TOAST } from '../context/actions';
import { AppContext } from '../context/AppContext';
import {
  selectCsrfToken,
  selectOrderedMemberFirstName,
  selectHasMeritReportPermission,
  noPermission,
} from '../context/selectors';

import './MeritReportPage.css';
import MemberSelector from '../components/member_selector/MemberSelector';
import { useQueryParameters } from '../helpers/query-parameters';

import markdown from 'markdown-builder';

const MeritReportPage = () => {
  const { state, dispatch } = useContext(AppContext);

  const csrf = selectCsrfToken(state);
  const memberProfiles = selectOrderedMemberFirstName(state);

  const [selectedMembers, setSelectedMembers] = useState([]);
  const [searchResults, setSearchResults] = useState([]);
  const [allSearchResults, setAllSearchResults] = useState([]);
  const [editedSearchRequest, setEditedSearchRequest] = useState([]);
  const [selectedCompHist, setSelectedCompHist] = useState(null);
  const [selectedCurrInfo, setSelectedCurrInfo] = useState(null);
  const [selectedPosHist, setSelectedPosHist] = useState(null);
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
                           result.push({label: item.name + " - " +
                                               formatReviewDate(item.closeDate),
                                        id: item.id});
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


  const formatReviewDate = (str) => {
    const date = new Date(Date.parse(str));
    return formatDate(date);
  };

  const onCompHistSelected = e => {
    setSelectedCompHist(e.target.files[0]);
  };

  const onCurrInfoSelected = e => {
    setSelectedCurrInfo(e.target.files[0]);
  };

  const onPosHistSelected = e => {
    setSelectedPosHist(e.target.files[0]);
  };

  const upload = async () => {
    if (!selectedCompHist || !selectedCurrInfo || !selectedPosHist) {
      return;
    }

    const files = [
      {label: 'comp', file: selectedCompHist },
      {label: 'curr', file: selectedCurrInfo },
      {label: 'pos',  file: selectedPosHist  },
    ];

    let formData = new FormData();
    for (let file of files) {
      formData.append(file.label, file.file);
    }
    const res = await uploadData("/services/report/data/upload",
                                 csrf, formData);
    if (res?.error) {
      const error = res?.error?.message;
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: error
        }
      });
    }
    if (res?.payload?.data) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: 'Files were successfully uploaded'
        }
      });
    }
  };

  const createReportMarkdownDocuments = async () => {
    let error;

    // Get the list of selected member ids.
    const selected = selectedMembers.reduce((result, item) => {
                       result.push(item.id);
                       return result;
                     }, []);

    // Check for required parameters before calling the server.
    if (selected.length == 0) {
      error = "Please select one or more members.";
    } else if (!reviewPeriodId || !reviewPeriodId.id) {
      error = "Please select a review period.";
    }

    if (!error) {
      const res = await initiate("/services/report/data/generate",
                                 csrf, {memberIds: selected,
                                        reviewPeriodId: reviewPeriodId.id});
      error = res?.error?.message;
    }

    // Display the error, if there was one.
    if (error) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: error
        }
      });
    } else {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: selected.length == 1 ? 'The report has been generated'
                                      : 'The reports have been generated'
        }
      });
    }
  };

  const uploadDocument = async (directory, name, text) => {
    if (!directory || !name || !text) {
      return;
    }

    let formData = new FormData();
    formData.append('directory', directory);
    formData.append('name', name);
    formData.append('text', text);
    let res = await uploadData("/services/files",
                               csrf, formData);

    if (res?.error) {
      let error = res?.error?.message;
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'error',
          toast: error
        }
      });
    }

    if (res?.payload?.data) {
      dispatch({
        type: UPDATE_TOAST,
        payload: {
          severity: 'success',
          toast: 'Document was successfully uploaded'
        }
      });
    }
  };

  const dateFromArray = (parts) => {
    return (parts ? new Date(parts[0], parts[1] - 1, parts[2]) : null);
  };

  const formatDate = (date) => {
    if (date) {
      // Date.toString() returns something like this: Wed Oct 05 2011
      // We will doctor it up to look like an American date.
      let str = date.toString().slice(4, 15);
      return str.slice(0, 6) + "," + str.slice(6);
    } else {
      return "";
    }
  };

  const onReviewPeriodChange = (event, newValue) => {
    setReviewPeriodId(newValue);
  };

  const checkMark = "✓";

  return selectHasMeritReportPermission(state) ? (
    <div className="merit-report-page">
      <Button color="primary" className="space-between">
        <label htmlFor="file-upload-comp">
          <h3>Compensation History File {selectedCompHist && checkMark}</h3>
          <input
            accept=".csv"
            id="file-upload-comp"
            onChange={onCompHistSelected}
            style={{ display: 'none' }}
            type="file"
          />
        </label>
      </Button>
      <Button color="primary" className="space-between">
        <label htmlFor="file-upload-curr">
          <h3>Current Information File {selectedCurrInfo && checkMark}</h3>
          <input
            accept=".csv"
            id="file-upload-curr"
            onChange={onCurrInfoSelected}
            style={{ display: 'none' }}
            type="file"
          />
        </label>
      </Button>
      <Button color="primary" className="space-between">
        <label htmlFor="file-upload-pos">
          <h3>Position History File {selectedPosHist && checkMark}</h3>
          <input
            accept=".csv"
            id="file-upload-pos"
            onChange={onPosHistSelected}
            style={{ display: 'none' }}
            type="file"
          />
        </label>
      </Button>
      <div className="buttons space-between">
        <Button
          color="primary"
          onClick={() => upload()}
          disabled={!selectedCompHist || !selectedCurrInfo || !selectedPosHist}
        >
          Upload Files
        </Button>
      </div>
      <MemberSelector
        className="merit-member-selector space-between"
        onChange={setSelectedMembers}
        selected={selectedMembers}
      />
      <div className="review-period-section space-between">
        <Autocomplete
          id="reviewPeriodSelect"
          options={reviewPeriods ? reviewPeriods : []}
          value={reviewPeriodId}
          onChange={onReviewPeriodChange}
          renderInput={params => (
            <TextField
              {...params}
              className="fullWidth"
              label="Review Period"
              placeholder="Choose review period"
            />
          )}
        />
      </div>
      <Button color="primary"
              onClick={createReportMarkdownDocuments}>
        <label htmlFor="download">
          <h3>Generate Report</h3>
        </label>
      </Button>
    </div>
  ) : (
    <h3>{noPermission}</h3>
  );
};

export default MeritReportPage;
