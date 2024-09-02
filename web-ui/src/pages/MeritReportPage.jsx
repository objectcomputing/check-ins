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

import markdown from 'markdown-builder';

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
    return res?.payload?.data;
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

  const dateFromArray = (parts) => {
    return new Date(parts[0], parts[1] - 1, parts[2]);
  };

  const formatDate = (date) => {
    // Wed Oct 05 2011
    let str = date.toString().slice(4, 15);
    return str.slice(0, 6) + "," + str.slice(6);
  };

  const markdownTitle = (data) => {
    const memberProfile = data.memberProfile;
    const startDate = dateFromArray(data.startDate);
    const endDate = dateFromArray(data.endDate);
    let text = markdown.headers.h1(memberProfile.firstName + " " +
                                   memberProfile.lastName);
    text += memberProfile.title + "\n";
    text += "Review Period: " +
            formatDate(startDate) + " - " + formatDate(endDate) + "\n\n";
    return text;
  };

  const markdownCurrentInformation = (data) => {
    const memberProfile = data.memberProfile;
    const currentInfo = data.currentInformation;
    const startDate = dateFromArray(memberProfile.startDate);
    const years = (Date.now() - startDate) / (1000 * 60 * 60 * 24 * 365.25);
    let text = markdown.headers.h1("Current Information");
    text += years.toFixed(1) + " years\n\n";
    text += markdown.headers.h2("Biographical Notes");
    text += currentInfo.biography + "\n\n";
    return text;
  };

  const markdownKudos = (data) => {
    const kudosList = data.kudos;
    let text = markdown.headers.h1("Kudos");
    for (let kudos of kudosList) {
      const date = dateFromArray(kudos.dateCreated);
      text += kudos.message + "\n";
      text += markdown.emphasis.i("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
                                  "Submitted on " + formatDate(date)) + "\n\n";
    }
    return text;
  };

  const markdownReviews = (data) => {
    // TODO: Add reviews on server side and fill in here.
    let text = markdown.headers.h1("Self-Review");
    text += "\n";
    text += markdown.headers.h1("Reviews");
    text += "\n";
    return text;
  };

  const markdownFeedback = (data) => {
    // TODO: Add feedback on server side and fill in here.
    let text = markdown.headers.h1("Feedback");
    text += "\n";
    return text;
  };

  const markdownTitleHistory = (data) => {
    // Get the position history sorted latest to earliest
    const posHistory = data.positionHistory.sort((a, b) => {
      for(let i = 0; i < a.length; i++) {
        if (a.date[i] != b.date[i]) {
          return b.date[i] - a.date[i];
        }
      }
      return 0;
    });

    let text = markdown.headers.h2("Title History");
    text += markdown.lists.ul(posHistory,
                              (position) => position.date[0] + " - " +
                                            position.title);
    return text;
  };

  const markdownCompensation = (data) => {
    const currentInfo = data.currentInformation;
    let text = markdown.headers.h2("Compensation and Commitments");
    text += "$" + currentInfo.salary.toFixed(2) + " Base Salary\n";
    text += "OCI Range for role: " + currentInfo.range + "\n\n";
    if (currentInfo.commitments) {
      text += "Commitments: " + currentInfo.commitments + "\n";
    } else {
      text += "No current bonus commitments\n";
    }
    text += "\n";
    return text;
  };

  const markdownCompensationHistory = (data) => {
    // Sort them latest to oldest and truncate to the first 3.
    const compHistory = data.compensationHistory.sort((a, b) => {
      for(let i = 0; i < a.length; i++) {
        if (a.startDate[i] != b.startDate[i]) {
          return b.startDate[i] - a.startDate[i];
        }
      }
      return 0;
    }).slice(0, 3);

    let text = markdown.headers.h2("Compensation History");
    text += markdown.lists.ul(compHistory,
                (comp) => formatDate(dateFromArray(comp.startDate)) + " - " +
                "$" + comp.amount.toFixed(2) + " (base)");
    return text;
  };

  const markdownReviewerNotes = (data) => {
    let text = markdown.headers.h4("Reviewer Notes");
    return text;
  };

  const createReportMarkdownDocuments = async () => {
    const dataSet = await download();
    for (let data of dataSet) {
      // Generate markdown
      let text = markdownTitle(data);
      text += markdownCurrentInformation(data);
      text += markdownKudos(data);
      text += markdownReviews(data);
      text += markdownFeedback(data);
      text += markdownTitleHistory(data);
      text += markdownCompensation(data);
      text += markdownCompensationHistory(data);
      text += markdownReviewerNotes(data);

      // Store the markdown on the google drive.
      const directory = "merit-reports";
      const fileName = data.memberProfile.workEmail;
      uploadDocument(directory, fileName, text);
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
      <MemberSelector
        className="merit-member-selector"
        onChange={setSelectedMembers}
        selected={selectedMembers}
      />
      <div className="review-period-section">
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
      </div>
      <Button color="primary"
              onClick={createReportMarkdownDocuments}>
        <label htmlFor="download">
          <h3>Generate Report</h3>
        </label>
      </Button>
    </div>
  );
};

export default MeritReportPage;
