import React, { useContext, useRef, useState } from 'react';

import { Button } from '@mui/material';

import { uploadFile } from '../api/uploadcsv';
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

  const onFileSelected = e => {
    setSelectedFile(e.target.files[0]);
  };

  const upload = async file => {
    if (!file) {
      return;
    }
    let formData = new FormData();
    formData.append('file', file);
    let res = await uploadFile("/services/report/data/upload",
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


  return (
    <div className="merit-report-page">
      <Button color="primary">
        <label htmlFor="file-upload">
          <h3>Choose A Compensation History CSV File</h3>
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

      <MemberSelector
        className="merit-member-selector"
        onChange={setSelectedMembers}
        selected={selectedMembers}
      />
    </div>
  );
};

export default MeritReportPage;
