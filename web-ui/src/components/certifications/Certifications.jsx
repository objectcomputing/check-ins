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

import { resolve } from '../../api/api.js';
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
    setSelectedCertification(null);
    setSelectedTarget(null);
  };

  const loadCertifications = useCallback(async () => {
    const res = await resolve({
      method: 'GET',
      url: certificationBaseUrl,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      }
    });
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

  const certificationSelect = useCallback(
    (label, setSelected) => (
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
          setBadgeUrl(foundCert.badgeUrl);
          setSelected(foundCert);
        }}
        options={certifications.map(cert => cert.name)}
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
    const res = await resolve({
      method: 'PUT',
      url: certificationBaseUrl + '/' + id,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data: selectedCertification
    });
    if (res.error) return;

    setCertificationMap(map => {
      delete map[id];
      return map;
    });
    setCertifications(certs => certs.filter(c => c.id !== id));
    forceUpdate();
    close();
  }, [certificationMap, certifications, selectedCertification]);

  const mergeCertification = useCallback(async () => {
    const sourceId = selectedCertification.id;
    const targetId = selectedTarget.id;
    const res = await resolve({
      method: 'POST',
      url: certificationBaseUrl + '/merge',
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data: { sourceId, targetId }
    });
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
    const url = adding
      ? certificationBaseUrl
      : certificationBaseUrl + '/' + selectedCertification.id;
    const res = await resolve({
      method: adding ? 'POST' : 'PUT',
      url,
      headers: {
        'X-CSRF-Header': csrf,
        Accept: 'application/json',
        'Content-Type': 'application/json;charset=UTF-8'
      },
      data: { name, badgeUrl }
    });
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
  }, [badgeUrl, certificationMap, name, selectedCertification]);

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
          {certificationSelect('Target Certification', setSelectedTarget)}
          <div className="row">
            <Button disabled={!selectedTarget} onClick={mergeCertification}>
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
