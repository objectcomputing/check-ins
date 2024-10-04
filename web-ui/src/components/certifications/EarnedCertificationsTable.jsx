import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import { sanitizeUrl } from '@braintree/sanitize-url';
import {
  AddCircleOutline,
  Delete,
  Edit,
  EmojiEvents
} from '@mui/icons-material';
import {
  Autocomplete,
  Button,
  Card,
  CardContent,
  CardHeader,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  TextField,
  Tooltip
} from '@mui/material';

import { resolve } from '../../api/api.js';
import DatePickerField from '../date-picker-field/DatePickerField';
import ConfirmationDialog from '../dialogs/ConfirmationDialog';
import { AppContext } from '../../context/AppContext';
import {
  selectCsrfToken,
  selectCurrentUser,
  selectProfileMap
} from '../../context/selectors';
import { formatDate } from '../../helpers/datetime';
import './EarnedCertificationsTable.css';

const certificationBaseUrl = '/services/certification';
const earnedCertificationBaseUrl = '/services/earned-certification';

const newEarned = { earnedDate: formatDate(new Date()) };
const tableColumns = [
  'Member',
  'Name',
  'Description',
  'Earned On',
  'Expiration',
  'Validation Image',
  'Badge'
];

const propTypes = {
  forceUpdate: PropTypes.func,
  onlyMe: PropTypes.bool
};

const EarnedCertificationsTable = ({
  forceUpdate = () => {},
  onlyMe = false
}) => {
  const { state } = useContext(AppContext);
  const [badgeUrl, setBadgeUrl] = useState('');
  const [certificationDialogOpen, setCertificationDialogOpen] = useState(false);
  const [certificationMap, setCertificationMap] = useState({});
  const [certificationName, setCertificationName] = useState('');
  const [certificationDescription, setCertificationDescription] = useState('');
  const [certifications, setCertifications] = useState([]);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [earnedCertifications, setEarnedCertifications] = useState([]);
  const [earnedDialogOpen, setEarnedDialogOpen] = useState(false);
  const [imageDialogOpen, setImageDialogOpen] = useState(false);
  const [selectedCertification, setSelectedCertification] = useState(null);
  const [selectedEarned, setSelectedEarned] = useState(newEarned);
  const [selectedProfile, setSelectedProfile] = useState(null);
  const [sortAscending, setSortAscending] = useState(true);
  const [sortColumn, setSortColumn] = useState('Member');

  const csrf = selectCsrfToken(state);
  const currentUser = selectCurrentUser(state);
  const profileMap = selectProfileMap(state);
  const profiles = Object.values(profileMap);
  profiles.sort((a, b) => a.name.localeCompare(b.name));

  const loadCertifications = useCallback(async () => {
    let res = await resolve({
      method: 'GET',
      url: certificationBaseUrl,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    const certs = res.payload.data;
    setCertifications(certs.sort((c1, c2) => c1.name.localeCompare(c2.name)));

    const certMap = certs.reduce((map, cert) => {
      map[cert.id] = cert;
      return map;
    }, {});
    setCertificationMap(certMap);

    res = await resolve({
      method: 'GET',
      url: earnedCertificationBaseUrl,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
    if (res.error) return;

    let earned = res.payload.data;
    if (onlyMe) {
      earned = earned.filter(cert => cert.memberId === currentUser.id);
    }
    sortEarnedCertifications(earned);
    setEarnedCertifications(earned);
  }, []);

  useEffect(() => {
    if (profileMap) {
      loadCertifications();
      if (onlyMe) setSelectedProfile(currentUser);
    }
  }, [profileMap]);

  useEffect(() => {
    if (!profileMap) return;
    sortEarnedCertifications(earnedCertifications);
    setEarnedCertifications([...earnedCertifications]);
  }, [profileMap, sortAscending, sortColumn]);

  const addEarnedCertification = useCallback(() => {
    setSelectedEarned(newEarned);
    setEarnedDialogOpen(true);
  }, []);

  const cancelCertification = useCallback(() => {
    setCertificationName('');
    setCertificationDescription('');
    setBadgeUrl('');
    setCertificationDialogOpen(false);
  }, []);

  const cancelEarnedCertification = useCallback(() => {
    setCertificationName('');
    setSelectedCertification(null);
    setSelectedEarned(null);
    setEarnedDialogOpen(false);
  }, []);

  const certificationDialog = useCallback(
    () => (
      <Dialog
        classes={{ root: 'earned-certifications-dialog' }}
        open={certificationDialogOpen}
        onClose={cancelCertification}
      >
        <DialogTitle>Add Certification</DialogTitle>
        <DialogContent>
          <TextField
            label="Certification Name"
            required
            onChange={e => setCertificationName(e.target.value)}
            value={certificationName}
          />
          <TextField
            label="Description"
            required
            onChange={e => setCertificationDescription(e.target.value)}
            value={certificationDescription}
          />
          <TextField
            label="Badge URL"
            onChange={e => setBadgeUrl(e.target.value)}
            value={badgeUrl}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelCertification}>Cancel</Button>
          <Button
            disabled={
              !certificationName ||
              !certificationDescription ||
              certifications.some(cert => cert.name === certificationName)
            }
            onClick={saveCertification}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    ),
    [badgeUrl, certificationDialogOpen, certificationDescription, certificationName]
  );

  const confirmDelete = useCallback(earned => {
    setSelectedEarned(earned);
    setConfirmDeleteOpen(true);
  }, []);

  const deleteEarnedCertification = useCallback(async cert => {
    const res = await resolve({
      method: 'DELETE',
      url: earnedCertificationBaseUrl + '/' + cert.id,
      headers: { 'X-CSRF-Header': csrf }
    });
    if (res.error) return;

    setEarnedCertifications(earned => earned.filter(c => c.id !== cert.id));
  }, []);

  const earnedCertificationRow = useCallback(
    earned => {
      const profile = profileMap[earned.memberId];
      const { validationUrl, certificationId } = earned;
      const certification = certificationMap[certificationId];
      const { badgeUrl } = certification;
      return (
        <tr key={earned.id}>
          {!onlyMe && <td>{profile?.name ?? 'unknown'}</td>}
          <td>{certificationMap[earned.certificationId]?.name ?? 'unknown'}</td>
          <td>{certificationMap[earned.certificationId]?.description ?? 'unknown'}</td>
          <td>{formatDate(new Date(earned.earnedDate))}</td>
          <td>
            {earned.expirationDate
              ? formatDate(new Date(earned.expirationDate))
              : ''}
          </td>
          <td onClick={() => selectImage(earned)} style={{ cursor: 'pointer' }}>
            {validationUrl && <img src={validationUrl} />}
          </td>
          <td>{badgeUrl && <img src={badgeUrl} />}</td>
          <td>
            <Tooltip title="Edit">
              <IconButton
                aria-label="Edit"
                onClick={() => editEarnedCertification(earned)}
              >
                <Edit />
              </IconButton>
            </Tooltip>
            <Tooltip title="Delete">
              <IconButton
                aria-label="Delete"
                onClick={() => confirmDelete(earned)}
              >
                <Delete />
              </IconButton>
            </Tooltip>
          </td>
        </tr>
      );
    },
    [certificationMap, profileMap]
  );

  const earnedDialog = useCallback(
    () => (
      <Dialog
        classes={{ root: 'earned-certifications-dialog' }}
        open={earnedDialogOpen}
        onClose={cancelEarnedCertification}
      >
        <DialogTitle>
          {selectedEarned?.id ? 'Edit' : 'Add'} Earned Certification
        </DialogTitle>
        <DialogContent>
          {!onlyMe && (
            <Autocomplete
              disableClearable
              getOptionLabel={profile => profile.name || ''}
              isOptionEqualToValue={(option, value) => option.id === value.id}
              onChange={(event, profile) => {
                setSelectedProfile({ ...profile });
              }}
              options={profiles}
              renderInput={params => (
                <TextField
                  {...params}
                  className="fullWidth"
                  label="Team Member"
                />
              )}
              value={selectedProfile}
            />
          )}

          <div>
            <Autocomplete
              disableClearable
              getOptionLabel={cert => cert?.name || 'unknown'}
              isOptionEqualToValue={(option, value) => option.id === value.id}
              onChange={(event, cert) => {
                setSelectedCertification({ ...cert });
              }}
              options={certifications}
              renderInput={params => (
                <TextField
                  {...params}
                  className="fullWidth"
                  label="Certification Name"
                />
              )}
              value={selectedCertification}
            />
            <IconButton
              aria-label="Add Certification"
              classes={{ root: 'add-button' }}
              onClick={() => setCertificationDialogOpen(true)}
            >
              <AddCircleOutline />
            </IconButton>
          </div>

          <DatePickerField
            date={new Date(selectedEarned?.earnedDate)}
            label="Earned On"
            setDate={date => {
              setSelectedEarned({
                ...selectedEarned,
                earnedDate: formatDate(date)
              });
            }}
          />
          <DatePickerField
            date={new Date(selectedEarned?.earnedDate)}
            label="Expiration"
            setDate={date => {
              setSelectedEarned({
                ...selectedEarned,
                expirationDate: formatDate(date)
              });
            }}
          />
          <TextField
            className="fullWidth"
            label="Validation URL"
            placeholder="Validation URL"
            onChange={e =>
              setSelectedEarned({
                ...selectedEarned,
                validationUrl: e.target.value
              })
            }
            value={selectedEarned?.validationUrl ?? ''}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelEarnedCertification}>Cancel</Button>
          <Button
            disabled={!selectedProfile || !selectedCertification}
            onClick={saveEarnedCertification}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    ),
    [earnedDialogOpen, selectedCertification, selectedEarned, selectedProfile]
  );

  const tableColumnsToUse = onlyMe ? tableColumns.slice(1) : tableColumns;

  const earnedTable = useCallback(
    () => (
      <Card>
        <CardHeader
          avatar={<EmojiEvents />}
          title="Earned Certifications"
          titleTypographyProps={{ variant: 'h5', component: 'h2' }}
        />
        <CardContent>
          <div className="row">
            <table>
              <thead>
                <tr>
                  {tableColumnsToUse.map(column => (
                    <th
                      key={column}
                      onClick={() => sortTable(column)}
                      style={{ cursor: 'pointer' }}
                    >
                      {column}
                      {sortIndicator(column)}
                    </th>
                  ))}
                  <th key="Actions">Actions</th>
                </tr>
              </thead>
              <tbody>{earnedCertifications.map(earnedCertificationRow)}</tbody>
            </table>
            <IconButton
              aria-label="Add Earned Certification"
              classes={{ root: 'add-button' }}
              onClick={addEarnedCertification}
            >
              <AddCircleOutline />
            </IconButton>
          </div>
        </CardContent>
      </Card>
    ),
    [earnedCertifications, sortAscending, sortColumn]
  );

  const earnedValue = useCallback(
    earned => {
      const certification = certificationMap[earned.certificationId];
      switch (sortColumn) {
        case 'Earned On':
          return earned.earnedDate;
        case 'Expiration':
          return earned.expirationDate || '';
        case 'Description':
          return certificationMap[earned.certificationId]?.description ?? '';
        case 'Validation Image':
          return earned.validationUrl || '';
        case 'Badge':
          return certification.badgeUrl || '';
        case 'Member':
          const profile = profileMap[earned.memberId];
          return profile?.name ?? '';
        case 'Name':
          return certificationMap[earned.certificationId]?.name ?? '';
      }
    },
    [certificationMap, profileMap, sortColumn]
  );

  const editEarnedCertification = useCallback(
    earned => {
      setSelectedEarned(earned);
      setSelectedProfile(profileMap[earned.memberId]);
      setSelectedCertification(certificationMap[earned.certificationId]);
      setEarnedDialogOpen(true);
    },
    [certificationMap, profileMap]
  );

  const imageDialog = useCallback(
    () => (
      <Dialog open={imageDialogOpen} onClose={() => setImageDialogOpen(false)}>
        <DialogTitle>Certification Image</DialogTitle>
        <DialogContent>
          {selectedEarned?.validationUrl && (
            <img
              src={sanitizeUrl(selectedEarned.validationUrl)}
              style={{ width: '100%' }}
            />
          )}
        </DialogContent>
      </Dialog>
    ),
    [imageDialogOpen, selectedEarned]
  );

  const saveCertification = useCallback(async () => {
    const res = await resolve({
      method: 'POST',
      url: certificationBaseUrl,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data: {
        name: certificationName,
        description: certificationDescription,
        badgeUrl
      }
    });
    if (res.error) return;

    const newCert = res.payload.data;
    setCertifications(certs =>
      [...certs, newCert].sort((c1, c2) => c1.name.localeCompare(c2.name))
    );
    setCertificationMap(map => {
      map[newCert.id] = newCert;
      return map;
    });
    setSelectedCertification(newCert);
    setCertificationName('');
    setCertificationDescription('');
    setBadgeUrl('');
    setCertificationDialogOpen(false);
    forceUpdate();
  }, [certificationName, certificationDescription, badgeUrl, certifications, selectedCertification]);

  const saveEarnedCertification = useCallback(async () => {
    selectedEarned.memberId = selectedProfile?.id || currentUser.id;
    selectedEarned.certificationId = selectedCertification.id;
    const { id } = selectedEarned;
    const url = id
      ? `${earnedCertificationBaseUrl}/${id}`
      : earnedCertificationBaseUrl;
    const res = await resolve({
      method: id ? 'PUT' : 'POST',
      url,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data: selectedEarned
    });
    if (res.error) return;

    const newEarned = res.payload.data;
    setEarnedCertifications(earned => {
      if (id) {
        const index = earned.findIndex(c => c.id === id);
        earned[index] = newEarned;
      } else {
        earned.push(newEarned);
      }
      return [...earned];
    });
    setSelectedProfile(null);
    setSelectedCertification(null);
    setEarnedDialogOpen(false);
  }, [selectedCertification, selectedEarned, selectedProfile]);

  const selectImage = useCallback(earned => {
    if (!earned.validationUrl) return;
    setSelectedEarned(earned);
    setImageDialogOpen(true);
  }, []);

  const sortEarnedCertifications = useCallback(
    earned => {
      earned.sort((e1, e2) => {
        const v1 = earnedValue(e1);
        const v2 = earnedValue(e2);
        const compare = sortAscending
          ? v1.localeCompare(v2)
          : v2.localeCompare(v1);
        return compare;
      });
    },
    [sortAscending, sortColumn]
  );

  const sortIndicator = useCallback(
    column => {
      if (column !== sortColumn) return '';
      return ' ' + (sortAscending ? '🔼' : '🔽');
    },
    [sortAscending, sortColumn]
  );

  const sortTable = useCallback(
    column => {
      if (column === sortColumn) {
        setSortAscending(ascending => !ascending);
      } else {
        setSortColumn(column);
        setSortAscending(true);
      }
    },
    [sortAscending, sortColumn]
  );

  return (
    <div id="earned-certifications-table">
      {earnedTable()}

      {imageDialog()}
      {earnedDialog()}
      {certificationDialog()}

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={() => deleteEarnedCertification(selectedEarned)}
        question="Are you sure you want to delete this certification?"
        setOpen={setConfirmDeleteOpen}
        title="Delete Certification"
      />
    </div>
  );
};

EarnedCertificationsTable.propTypes = propTypes;

export default EarnedCertificationsTable;
