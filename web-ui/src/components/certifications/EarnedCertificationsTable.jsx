import { format } from 'date-fns';
import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

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

import DatePickerField from '../date-picker-field/DatePickerField';
import ConfirmationDialog from '../dialogs/ConfirmationDialog';
import { AppContext } from '../../context/AppContext';
import { selectCurrentUser, selectProfileMap } from '../../context/selectors';
import './EarnedCertificationsTable.css';

const certificationBaseUrl = 'http://localhost:3000/certification';
const earnedCertificationBaseUrl = 'http://localhost:3000/earned-certification';

const formatDate = date => {
  return !date
    ? ''
    : date instanceof Date
      ? format(date, 'yyyy-MM-dd')
      : `${date.$y}-${date.$M + 1}-${date.$D}`;
};

const newEarned = { earnedDate: formatDate(new Date()) };
const tableColumns = [
  'Member',
  'Name',
  'Description',
  'Earned On',
  'Expiration',
  'Certificate Image',
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

  const currentUser = selectCurrentUser(state);
  const profileMap = selectProfileMap(state);
  const profiles = Object.values(profileMap);
  profiles.sort((a, b) => a.name.localeCompare(b.name));

  const loadCertifications = useCallback(async () => {
    try {
      const res = await fetch(certificationBaseUrl);
      const certs = await res.json();
      setCertifications(certs.sort((c1, c2) => c1.name.localeCompare(c2.name)));

      const certMap = certs.reduce((map, cert) => {
        map[cert.id] = cert;
        return map;
      }, {});
      setCertificationMap(certMap);

      res = await fetch(earnedCertificationBaseUrl);
      let earned = await res.json();
      if (onlyMe) {
        earned = earned.filter(cert => cert.memberId === currentUser.id);
      }
      sortEarnedCertifications(earned);
      setEarnedCertifications(earned);
    } catch (err) {
      console.error(err);
    }
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
            label="Badge URL"
            required
            onChange={e => setBadgeUrl(e.target.value)}
            value={badgeUrl}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={cancelCertification}>Cancel</Button>
          <Button
            disabled={
              !certificationName ||
              !badgeUrl ||
              certifications.some(cert => cert.name === certificationName)
            }
            onClick={saveCertification}
          >
            Save
          </Button>
        </DialogActions>
      </Dialog>
    ),
    [badgeUrl, certificationDialogOpen, certificationName]
  );

  const confirmDelete = useCallback(earned => {
    setSelectedEarned(earned);
    setConfirmDeleteOpen(true);
  }, []);

  const deleteEarnedCertification = useCallback(async cert => {
    const url = earnedCertificationBaseUrl + '/' + cert.id;
    try {
      const res = await fetch(url, { method: 'DELETE' });
      setEarnedCertifications(earned => earned.filter(c => c.id !== cert.id));
    } catch (err) {
      console.error(err);
    }
  }, []);

  const earnedCertificationRow = useCallback(
    earned => {
      const profile = profileMap[earned.memberId];
      const { certificateImageUrl, certificationId } = earned;
      const certification = certificationMap[certificationId];
      const { badgeUrl } = certification;
      return (
        <tr key={earned.id}>
          {!onlyMe && <td>{profile?.name ?? 'unknown'}</td>}
          <td>{certificationMap[earned.certificationId]?.name ?? 'unknown'}</td>
          <td>{earned.description}</td>
          <td>{formatDate(new Date(earned.earnedDate))}</td>
          <td>
            {earned.expirationDate
              ? formatDate(new Date(earned.expirationDate))
              : ''}
          </td>
          <td onClick={() => selectImage(earned)} style={{ cursor: 'pointer' }}>
            {certificateImageUrl && <img src={certificateImageUrl} />}
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

          <TextField
            className="fullWidth"
            label="Description"
            placeholder="Description"
            required
            onChange={e =>
              setSelectedEarned({
                ...selectedEarned,
                description: e.target.value
              })
            }
            value={selectedEarned?.description ?? ''}
          />
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
            label="Image URL"
            placeholder="Image URL"
            onChange={e =>
              setSelectedEarned({
                ...selectedEarned,
                certificateImageUrl: e.target.value
              })
            }
            value={selectedEarned?.certificateImageUrl ?? ''}
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
          return earned.description;
        case 'Certificate Image':
          return earned.certificateImageUrl || '';
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
          {selectedEarned?.certificateImageUrl && (
            <img
              src={selectedEarned.certificateImageUrl}
              style={{ width: '100%' }}
            />
          )}
        </DialogContent>
      </Dialog>
    ),
    [imageDialogOpen, selectedEarned]
  );

  const saveCertification = useCallback(async () => {
    try {
      const res = await fetch(certificationBaseUrl, {
        method: 'POST',
        body: JSON.stringify({ badgeUrl, name: certificationName })
      });
      const newCert = await res.json();
      setCertifications(certs =>
        [...certs, newCert].sort((c1, c2) => c1.name.localeCompare(c2.name))
      );
      setCertificationMap(map => {
        map[newCert.id] = newCert;
        return map;
      });
      setSelectedCertification(newCert);
      setCertificationName('');
      forceUpdate();
    } catch (err) {
      console.error(err);
    }
    setCertificationDialogOpen(false);
  }, [certificationName, certifications, selectedCertification]);

  const saveEarnedCertification = useCallback(async () => {
    const { id } = selectedEarned;
    const url = id
      ? `${earnedCertificationBaseUrl}/${id}`
      : earnedCertificationBaseUrl;
    selectedEarned.memberId = selectedProfile?.id || currentUser.id;
    selectedEarned.certificationId = selectedCertification.id;
    try {
      const res = await fetch(url, {
        method: id ? 'PUT' : 'POST',
        body: JSON.stringify(selectedEarned)
      });
      const newEarned = await res.json();
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
    } catch (err) {
      console.error(err);
    }
    setEarnedDialogOpen(false);
  }, [selectedCertification, selectedEarned, selectedProfile]);

  const selectImage = useCallback(earned => {
    if (!earned.certificateImageUrl) return;
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
        // console.log('v1 =', v1, 'v2 =', v2, 'compare =', compare);
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
