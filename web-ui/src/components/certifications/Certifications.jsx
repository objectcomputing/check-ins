import PropTypes from 'prop-types';
import React, { useCallback, useContext, useEffect, useState } from 'react';

import {
  Button,
  Dialog,
  DialogContent,
  DialogTitle,
  TextField
} from '@mui/material';
import Autocomplete from '@mui/material/Autocomplete';

import {
  getCertifications,
  createCertification,
  updateCertification,
  mergeCertification,
} from '../../api/certification';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken } from '../../context/selectors';
import ConfirmationDialog from '../dialogs/ConfirmationDialog';

import './Certifications.css';

const certificationBaseUrl = '/services/certification';

const propTypes = {
  forceUpdate: PropTypes.func,
  open: PropTypes.bool,
  onClose: PropTypes.func
};

const Certifications = ({ forceUpdate = () => {}, open, onClose }) => {
  const [adding, setAdding] = useState(true); // true to add, false to edit
  const [badgeUrl, setBadgeUrl] = useState('');
  const [certificationMap, setCertificationMap] = useState({});
  const [certifications, setCertifications] = useState([]);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);
  const [mergeDialogOpen, setMergeDialogOpen] = useState(false);
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [selectedCertification, setSelectedCertification] = useState(null);
  const [selectedTarget, setSelectedTarget] = useState(null);

  const { state } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const close = () => {
    reset();
    onClose();
  };

  const reset = () => {
    setAdding(true);
    setBadgeUrl('');
    setName('');
    setDescription('');
    setSelectedCertification(null);
    setSelectedTarget(null);
  };

  const loadCertifications = useCallback(async () => {
    const res = await getCertifications(csrf);
    if (res.error) return;

    const certs = await res.payload.data;
    setCertifications(certs.sort((c1, c2) => c1.name.localeCompare(c2.name)));

    const certMap = certs.reduce((map, cert) => {
      map[cert.id] = cert;
      return map;
    }, {});
    setCertificationMap(certMap);
  }, [csrf]);

  useEffect(() => {
    if (csrf) loadCertifications();
  }, [csrf]);

  // `exclude` is optional.  If supplied, the certification name that matches
  // will be removed from the list of options.  This is useful when merging
  // certifications.
  const certificationSelect = useCallback(
    (label, setSelected, exclude) => (
      <Autocomplete
        blurOnSelect
        clearOnBlur
        freeSolo
        handleHomeEndKeys
        key={label}
        onChange={(event, value) => {
          if (value === null) return;
          let foundCert = certifications.find(
            cert => cert.name.toUpperCase() === value.toUpperCase()
          );
          if (!foundCert) {
            foundCert = { badgeUrl: '', name: value };
            setCertifications(certs => [...certs, foundCert]);
          }
          setAdding(!Boolean(foundCert));
          setName(foundCert.name);
          setDescription(foundCert.description);
          setBadgeUrl(foundCert.badgeUrl);
          setSelected(foundCert);
        }}
        options={certifications.map(cert => cert.name).filter(name => name !== exclude)}
        renderInput={params => {
          return (
            <TextField
              {...params}
              label={label}
              placeholder="Enter a certification name"
              variant="standard"
            />
          );
        }}
        selectOnFocus
      />
    ),
    [certifications]
  );

  const deleteCertification = useCallback(async () => {
    selectedCertification.active = false;
    const { id } = selectedCertification;
    const res = await updateCertification(id, selectedCertification, csrf);
    if (res.error) return;

    setCertificationMap(map => {
      delete map[id];
      return map;
    });
    setCertifications(certs => certs.filter(c => c.id !== id));
    forceUpdate();
    close();
  }, [certificationMap, certifications, selectedCertification]);

  const mergeSelectedCertification = useCallback(async () => {
    const sourceId = selectedCertification.id;
    const targetId = selectedTarget.id;
    const res = await mergeCertification(sourceId, targetId, csrf);
    if (res.error) return;

    setCertifications(certs => certs.filter(cert => cert.id !== sourceId));
    setCertificationMap(map => {
      delete map[sourceId];
      return map;
    });
    forceUpdate();
    close();
  }, [selectedCertification, selectedTarget]);

  const saveCertification = useCallback(async () => {
    let res;
    const data = { name, description, badgeUrl };
    if (adding) {
      res = await createCertification(data, csrf);
    } else {
      res = await updateCertification(selectedCertification.id, data, csrf);
    }
    if (res.error) return;

    const newCert = res.payload.data;
    certificationMap[newCert.id] = newCert;
    setCertificationMap(certificationMap);
    setCertifications(
      Object.values(certificationMap).sort((c1, c2) =>
        c1.name.localeCompare(c2.name)
      )
    );
    close();
    forceUpdate();
  }, [badgeUrl, certificationMap, name, description, selectedCertification]);

  return (
    <>
      <Dialog
        classes={{ root: 'certification-dialog' }}
        open={open}
        onClose={close}
      >
        <DialogTitle>Manage Certifications</DialogTitle>
        <DialogContent>
          {certificationSelect('Certification', setSelectedCertification)}
          <TextField
            label="Certification Name"
            required
            onChange={e => setName(e.target.value)}
            value={name}
          />
          <TextField
            label="Description"
            required
            onChange={e => setDescription(e.target.value)}
            value={description}
          />
          <TextField
            label="Badge URL"
            required
            onChange={e => setBadgeUrl(e.target.value)}
            value={badgeUrl}
          />

          <div className="row">
            <Button disabled={!name || !badgeUrl} onClick={saveCertification}>
              Save
            </Button>
            <Button
              disabled={!selectedCertification}
              onClick={() => setConfirmDeleteOpen(true)}
            >
              Delete
            </Button>
            <Button
              disabled={!selectedCertification}
              onClick={() => setMergeDialogOpen(true)}
            >
              Merge
            </Button>
          </div>
        </DialogContent>
      </Dialog>

      <ConfirmationDialog
        open={confirmDeleteOpen}
        onYes={deleteCertification}
        question={`Are you sure you want to delete the ${selectedCertification?.name} certification?`}
        setOpen={setConfirmDeleteOpen}
        title="Delete Certification"
      />

      <Dialog
        classes={{ root: 'certification-dialog' }}
        open={mergeDialogOpen}
        onClose={() => setMergeDialogOpen(false)}
      >
        <DialogTitle>
          Merge {selectedCertification?.name} Certification Into
        </DialogTitle>
        <DialogContent>
          {certificationSelect('Target Certification',
                               setSelectedTarget, selectedCertification?.name)}
          <div className="row">
            <Button disabled={!selectedTarget} onClick={mergeSelectedCertification}>
              Merge
            </Button>
            <Button
              disabled={!selectedCertification}
              onClick={() => setMergeDialogOpen(false)}
            >
              Cancel
            </Button>
          </div>
        </DialogContent>
      </Dialog>
    </>
  );
};

Certifications.propTypes = propTypes;

export default Certifications;
