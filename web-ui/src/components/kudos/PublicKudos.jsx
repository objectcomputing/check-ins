import React, { useCallback, useContext, useEffect, useState } from 'react';

import { UPDATE_TOAST } from '../../context/actions';
import { AppContext } from '../../context/AppContext';
import { selectCsrfToken } from '../../context/selectors';
import { sortKudos } from '../../context/util';

import { getRecentKudos } from '../../api/kudos';

import KudosCard from './PublicKudosCard';
import KudosDialog from '../kudos_dialog/KudosDialog';
import SkeletonLoader from '../skeleton_loader/SkeletonLoader';

import StarIcon from '@mui/icons-material/Star';
import { Button, Grid, Typography } from '@mui/material';

import './PublicKudos.css';

const PublicKudos = () => {
  const { state, dispatch } = useContext(AppContext);
  const csrf = selectCsrfToken(state);

  const [kudos, setKudos] = useState(null);
  const [kudosDialogOpen, setKudosDialogOpen] = useState(false);
  const [kudosLoading, setKudosLoading] = useState(false);

  let lastMonth = new Date();
  lastMonth.setMonth(lastMonth.getMonth() - 1);

  const loadKudos = useCallback(async () => {
    setKudosLoading(true);
    const res = await getRecentKudos(csrf);
    if (res.error) return;

    const kudos = res.payload.data;
    setKudos(sortKudos(kudos));
    setKudosLoading(false);
  }, [csrf, dispatch]);

  useEffect(() => {
    loadKudos();
  }, [csrf, dispatch]);

  return (
    <div className="public-kudos">
      <div className="kudos-title">
        <h1>Kudos</h1>
        <Button
          className="kudos-dialog-open"
          startIcon={<StarIcon />}
          onClick={() => setKudosDialogOpen(true)}
        >
          Give Kudos
        </Button>
      </div>
      <KudosDialog
        open={kudosDialogOpen}
        onClose={() => setKudosDialogOpen(false)}
      />
      <Grid container columns={6} spacing={3}>
        <Grid item>
          {kudosLoading ? (
            <div className="kudos-list">
              {Array.from({ length: 5 }).map((_, index) => (
                <SkeletonLoader width="100%" key={index} type="kudos" />
              ))}
            </div>
          ) : !kudosLoading && kudos?.length > 0 ? (
            <div className="kudos-list">
              {kudos.map(k => (<KudosCard key={k.id} kudos={k} />))}
            </div>
          ) : (
            <Typography variant="body2">
              There are currently no kudos
            </Typography>
          )}
        </Grid>
      </Grid>
    </div>
  );
};

export default PublicKudos;
