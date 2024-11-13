import React, { useContext, useRef, useState, useEffect } from 'react';

import { Autocomplete, Button, TextField } from '@mui/material';

import { uploadData, downloadData } from '../api/generic';
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
                           result.push({label: formatReviewDate(item.closeDate),
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

  const download = async () => {
    let data;
    let error;

    // Get the list of selected member ids.
    let selected = selectedMembers.reduce((result, item) => {
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
      const res = await downloadData("/services/report/data",
                                     csrf, {memberIds: selected,
                                            reviewPeriodId: reviewPeriodId.id});
      error = res?.error?.message;
      data = res?.payload?.data;
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
    }

    return data;
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

  const markdownTitle = (data) => {
    const memberProfile = data.memberProfile;
    const startDate = dateFromArray(data.startDate);
    const endDate = dateFromArray(data.endDate);
    let text = markdown.headers.h1(memberProfile.firstName + " " +
                                   memberProfile.lastName);
    text += memberProfile.title + "\n\n";
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
      text += kudos.message + "\n\n";
      text += "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
              markdown.emphasis.i("Submitted on " + formatDate(date) +
                                  ", by " + kudos.sender) +
              "\n\n\n";
    }
    return text;
  };

  const markdownReviewsImpl = (title, feedbackList, listMembers) => {
    let text = markdown.headers.h1(title);
    for(let feedback of feedbackList) {
      const members = getUniqueMembers(feedback.answers);
      for(let member of Object.keys(members)) {
        if (listMembers) {
          text += member + ": ";
        }
        text += "Submitted - " + formatDate(members[member]) + "\n\n";
      }
      text += "\n";

      const questions = getUniqueQuestions(feedback.answers);
      for(let question of Object.keys(questions)) {
        text += markdown.headers.h4(question) + "\n";
        for(let answer of questions[question]) {
          if (listMembers) {
            text += answer[0] + ": ";
          }
          text += answer[1] + "\n\n";
        }
        text += "\n";
      }
    }
    text += "\n";
    return text;
  }

  const markdownSelfReviews = (data) => {
    return markdownReviewsImpl("Self-Review", data.selfReviews, false);
  }

  const markdownReviews = (data) => {
    return markdownReviewsImpl("Reviews", data.reviews, true);
  };

  const getUniqueMembers = (answers) => {
    let members = {};
    for(let answer of answers) {
      const key = answer.memberName;
      if (!(key in members)) {
        // Put in member name and date
        members[key] = dateFromArray(answer.submitted);
      }
    }
    return members;
  };

  const getAnswerText = (answer) => {
    return answer.answer;
  };

  const getUniqueQuestions = (answers) => {
    let questions = {};
    answers = answers.sort((a, b) => {
      return a.number - b.number;
    });

    for(let answer of answers) {
      const key = answer.question;
      if (!(key in questions)) {
        // Put in member name and answer
        questions[key] = [];
      }
      const text = getAnswerText(answer);
      questions[key].push([answer.memberName, text]);
    }
    return questions;
  };

  const markdownFeedback = (data) => {
    let text = markdown.headers.h1("Feedback");
    const feedbackList = data.feedback;
    for(let feedback of feedbackList) {
      text += markdown.headers.h2("Template: " + feedback.name);
      const members = getUniqueMembers(feedback.answers);
      for(let member of Object.keys(members)) {
        text += member + ": " + formatDate(members[member]) + "\n\n";
      }
      text += "\n";

      const questions = getUniqueQuestions(feedback.answers);
      for(let question of Object.keys(questions)) {
        text += markdown.headers.h4(question) + "\n";
        for(let answer of questions[question]) {
          text += answer[0] + ": " + answer[1] + "\n\n";
        }
        text += "\n";
      }
    }
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
    text += "$" + currentInfo.salary.toFixed(2) + " Base Salary\n\n";
    text += "OCI Range for role: " + currentInfo.range + "\n\n";
    text += "National Range for role: " + currentInfo.nationalRange + "\n\n";
    if (currentInfo.commitments) {
      text += "Commitments: " + currentInfo.commitments + "\n";
    } else {
      text += "No current bonus commitments\n";
    }
    text += "\n";
    return text;
  };

  const prepareCompensationHistory = (data, fn) => {
    return data.compensationHistory.filter(fn).sort((a, b) => {
      for(let i = 0; i < a.startDate.length; i++) {
        if (a.startDate[i] != b.startDate[i]) {
          return b.startDate[i] - a.startDate[i];
        }
      }
      return 0;
    }).slice(0, 3);
  };

  const markdownCompensationHistory = (data) => {
    // Sort them latest to oldest and truncate to the first 3.
    const compBase = prepareCompensationHistory(data, (comp) => !!comp.amount);
    const compTotal = prepareCompensationHistory(data, (comp) => !!comp.totalComp);

    let text = markdown.headers.h2("Compensation History");
    text += markdown.headers.h3("Base Compensation (annual or hourly)");
    text += markdown.lists.ul(compBase,
                (comp) => formatDate(dateFromArray(comp.startDate)) + " - " +
                "$" + parseFloat(comp.amount).toFixed(2));
    text += markdown.headers.h3("Total Compensation")
    text += markdown.lists.ul(compTotal,
                (comp) => {
                    var date = dateFromArray(comp.startDate);
                    date = date.getMonth() === 0 && date.getDate() === 1 ? date.getFullYear() : formatDate(date);
                    return date + " - " + comp.totalComp;
                });
    return text;
  };

  const markdownEmployeeHours = (data) => {
    let text = markdown.headers.h2("Employee Hours");
    let hours = {
      'Contribution Hours': data.hours.contributionHours,
      'PTO Hours': data.hours.ptoHours,
      'Overtime Hours': data.hours.overtimeHours,
      'Billable Utilization': data.hours.billableUtilization,
    };
    text += markdown.lists.ul(Object.keys(hours),
                (key) => key + ": " + hours[key]);
    return text;
  };

  const markdownReviewerNotes = (data) => {
    let text = markdown.headers.h4("Reviewer Notes");
    return text;
  };

  const createReportMarkdownDocuments = async () => {
    const dataSet = await download();
    if (dataSet) {
      for (let data of dataSet) {
        // Generate markdown
        let text = markdownTitle(data);
        text += markdownCurrentInformation(data);
        text += markdownKudos(data);
        text += markdownSelfReviews(data);
        text += markdownReviews(data);
        text += markdownFeedback(data);
        text += markdownTitleHistory(data);
        text += markdownEmployeeHours(data);
        text += markdownCompensation(data);
        text += markdownCompensationHistory(data);
        text += markdownReviewerNotes(data);

        // Store the markdown on the google drive.
        const directory = "merit-reports";
        const fileName = data.memberProfile.workEmail;
        uploadDocument(directory, fileName, text);
      }
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
  ) : (
    <h3>{noPermission}</h3>
  );
};

export default MeritReportPage;
